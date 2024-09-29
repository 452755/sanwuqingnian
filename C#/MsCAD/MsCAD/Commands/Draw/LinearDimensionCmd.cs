using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

using MsCAD.DatabaseServices;
using MsCAD.UI;
using MsCAD.Windows;
using MsMath;

namespace MsCAD.Commands.Draw
{
    /// <summary>
    /// 创建测距标注
    /// </summary>
    internal class LinearDimensionCmd : DrawCmd
    {
        /// <summary>临时绘制Paint线</summary>
        private MsCAD.DatabaseServices.Line _currLine = null;
        /// <summary>最终测距主线</summary>
        private MsCAD.DatabaseServices.Line _Line = null;

        /// <summary>起点端垂直线</summary>
        private MsCAD.DatabaseServices.Line _line0 = null;

        /// <summary>终点端垂直线</summary>
        private MsCAD.DatabaseServices.Line _line1 = null;

        /// <summary>起点端箭头</summary>
        private MsCAD.DatabaseServices.Solid _solid0 = null;

        /// <summary>终点端箭头</summary>
        private MsCAD.DatabaseServices.Solid _solid1 = null;

        private MsCAD.DatabaseServices.Text _mtext = null;

        /// <summary>
        /// 新增的图元
        /// </summary>
        protected override IEnumerable<Entity> newEntities
        {
            get { return new MsCAD.DatabaseServices.Entity[6] { _Line, _line0, _line1, _solid0, _solid1, _mtext }; }
        }

        /// <summary>
        /// 步骤
        /// </summary>
        private enum Step
        {
            /// <summary>选起点</summary>
            Step1_SetStartPoint = 1,
            /// <summary>选终点</summary>
            Step2_SetEndPoint = 2,
        }
        private Step _step = Step.Step1_SetStartPoint;

        /// <summary>
        /// 点动态输入控件
        /// </summary>
        private DynInputPoint _pointInput = null;

        /// <summary>
        /// 初始化
        /// </summary>
        public override void Initialize()
        {
            base.Initialize();

            //
            _step = Step.Step1_SetStartPoint;
            this.pointer.mode = UI.Pointer.Mode.Locate;

            _pointInput = new DynInputPoint(this.presenter, new Vector2(0, 0));
            _pointInput.Message = "指定第一个点: ";
            this.dynamicInputer.StartInput(_pointInput);
            _pointInput.finish += this.OnPointInputReturn;
            _pointInput.cancel += this.OnPointInputReturn;
        }

        /// <summary>
        /// 结束
        /// </summary>
        public override void Terminate(int num = 0)
        {
            _pointInput.Terminate();
            _pointInput.finish -= this.OnPointInputReturn;
            _pointInput.cancel -= this.OnPointInputReturn;

            base.Terminate();
        }

        public override EventResult OnMouseDown(MouseEventArgs e)
        {
            try
            {
                if (_step == Step.Step1_SetStartPoint)
                {
                    if (e.Button == MouseButtons.Left)
                    {
                        _currLine = new Line();
                        _currLine.startPoint = this.pointer.currentSnapPoint;
                        _currLine.layerId = this.document.currentLayerId;
                        _currLine.color = Colors.Color.FromColor(Color.OrangeRed);
                        _pointInput.Message = "指定第二点: ";
                        _step = Step.Step2_SetEndPoint;
                    }
                }
                else if (_step == Step.Step2_SetEndPoint)
                {
                    if (e.Button == MouseButtons.Left)
                    {
                        _currLine.endPoint = this.pointer.currentSnapPoint;
                        this.CreateEnt();
                        _mgr.FinishCurrentCommand();
                    }
                }
            }
            catch (Exception ex)
            {
                MainWin.Instance.ShowMsg(TipsState.Error, ex.Message);
            }
            return EventResult.Handled;
        }


        public override EventResult OnMouseUp(MouseEventArgs e)
        {
            return EventResult.Handled;
        }

        public override EventResult OnMouseMove(MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Middle)
            {
                return EventResult.Handled;
            }

            if (_currLine != null)
            {
                _currLine.endPoint = this.pointer.currentSnapPoint;
            }

            return EventResult.Handled;
        }

        public override EventResult OnKeyDown(KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Escape)
            {
                _mgr.CancelCurrentCommand();
            }
            return EventResult.Handled;
        }

        public override EventResult OnKeyUp(KeyEventArgs e)
        {
            return EventResult.Handled;
        }

        public override void OnPaint(Graphics g)
        {

            if (_currLine != null)
            {
                Presenter presenter = _mgr.presenter as Presenter;
                presenter.DrawEntity(g, _currLine);
            }
        }

        private void OnPointInputReturn(DynInputCtrl sender, DynInputResult retult)
        {
            try
            {
                DynInputResult<Vector2> xyRet = retult as DynInputResult<Vector2>;
                if (xyRet == null || xyRet.status == DynInputStatus.Cancel)
                {
                    _mgr.CancelCurrentCommand();
                    return;
                }

                if (_step == Step.Step1_SetStartPoint)
                {
                    _currLine = new Line();
                    _currLine.layerId = this.document.currentLayerId;
                    _currLine.color = Colors.Color.FromColor(Color.OrangeRed);
                    _currLine.startPoint = xyRet.value;
                    _pointInput.Message = "指定第二点:";
                    _step = Step.Step2_SetEndPoint;
                }
                else if (_step == Step.Step2_SetEndPoint)
                {
                    _currLine.endPoint = xyRet.value;
                    this.CreateEnt();
                    _mgr.FinishCurrentCommand();
                }
            }
            catch (Exception ex)
            {
                MainWin.Instance.ShowMsg(TipsState.Error, ex.Message);
            }
        }


        private void CreateEnt()
        {
            try
            {
                string DimID = Guid.NewGuid().ToString().Replace("-", "");
                Vector2 start = _currLine.startPoint;
                Vector2 end = _currLine.endPoint;
                //测距主线
                _Line = new Line();
                _Line.startPoint = start;
                _Line.endPoint = end;
                _Line.layerId = this.document.currentLayerId;
                _Line.color = Colors.Color.FromColor(Color.OrangeRed);
                _Line.dimHandle = DimID;
                //测距主线长度
                double len = Math.Sqrt(Math.Pow((end.y - start.y), 2) + Math.Pow((end.x - start.x), 2));

                //起点端垂直线
                _line0 = new Line();
                _line0.startPoint = MathUtils.GetVerticalPonit(_Line, 0, len * 0.05);
                _line0.endPoint = MathUtils.GetVerticalPonit(_Line, 0, -len * 0.05);
                _line0.layerId = this.document.currentLayerId;
                _line0.color = Colors.Color.FromColor(Color.OrangeRed);
                _line0.dimHandle = DimID;
                //终点端垂直线
                _line1 = new Line();
                _line1.startPoint = MathUtils.GetVerticalPonit(_Line, 1, len * 0.05);
                _line1.endPoint = MathUtils.GetVerticalPonit(_Line, 1, -len * 0.05);
                _line1.layerId = this.document.currentLayerId;
                _line1.color = Colors.Color.FromColor(Color.OrangeRed);
                _line1.dimHandle = DimID;

                //起点端箭头
                _solid0 = new MsCAD.DatabaseServices.Solid();
                _solid0.vertices.Add(start);
                _solid0.vertices.Add(MathUtils.GetVerticalPonit(_Line, 0.1, len * 0.05));
                _solid0.vertices.Add(MathUtils.GetVerticalPonit(_Line, 0.1, -len * 0.05));
                _solid0.layerId = this.document.currentLayerId;
                _solid0.color = Colors.Color.FromColor(Color.OrangeRed);

                _solid0.dimHandle = DimID;
                //终点端箭头
                _solid1 = new MsCAD.DatabaseServices.Solid();
                _solid1.vertices.Add(end);
                _solid1.vertices.Add(MathUtils.GetVerticalPonit(_Line, 0.9, len * 0.05));
                _solid1.vertices.Add(MathUtils.GetVerticalPonit(_Line, 0.9, -len * 0.05));
                _solid1.layerId = this.document.currentLayerId;
                _solid1.color = Colors.Color.FromColor(Color.OrangeRed);
                _solid1.dimHandle = DimID;

                _mtext = new MsCAD.DatabaseServices.Text();
                _mtext.height = len / 20;
                _mtext.text = len.ToString("f2");
                _mtext.position = MathUtils.GetVerticalPonit(_Line, 0.5, len * 0.05);
                _mtext.layerId = this.document.currentLayerId;
                _mtext.color = Colors.Color.FromColor(Color.OrangeRed);
                _mtext.dimHandle = DimID;
                DocumentForm docForm = MainWin.Instance.ActiveMdiChild as DocumentForm;
                docForm.database.CreateLinearDimension(DimID, start, end);
            }
            catch (Exception ex)
            {
                MainWin.Instance.ShowMsg(TipsState.Error, ex.Message);
            }
        }


        


        


    }
}
