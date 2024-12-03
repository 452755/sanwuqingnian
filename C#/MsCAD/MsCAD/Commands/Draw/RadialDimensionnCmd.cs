using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;
using MsCAD.ApplicationServices;
using MsCAD.DatabaseServices;
using MsCAD.UI;
using MsCAD.Windows;
using MsMath;

namespace MsCAD.Commands.Draw
{
    /// <summary>
    /// 创建半径标注
    /// </summary>
    internal class RadialDimensionnCmd : DrawCmd
    {
        /// <summary>临时绘制Paint线</summary>
        private MsCAD.DatabaseServices.Line _currLine = null;
        /// <summary>最终半径主线</summary>
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

        Vector2 _center = new Vector2(0);
        double _radius = 0;

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
            /// <summary>选圆与圆弧</summary>
            Step1_SelectCircleArc = 1,
            /// <summary>设置圆周点位置</summary>
            Step2_SetTextPosition = 2,
        }
        private Step _step = Step.Step1_SelectCircleArc;

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
            _step = Step.Step1_SelectCircleArc;
            this.pointer.mode = UI.Pointer.Mode.Select;
            Windows.MainWin.Instance.ShowMsg(Windows.TipsState.Info, "请选择圆或圆弧！");
        }

        /// <summary>
        /// 结束
        /// </summary>
        public override void Terminate(int num = 0)
        {
            if (_pointInput != null)
            {
                _pointInput.Terminate();
                _pointInput.finish -= this.OnPointInputReturn;
                _pointInput.cancel -= this.OnPointInputReturn;
            }
            base.Terminate();
        }

        public override EventResult OnMouseDown(MouseEventArgs e)
        {
            try
            {
                if (_step == Step.Step1_SelectCircleArc)
                {
                    if (e.Button == MouseButtons.Left)
                    {
                        if (this.presenter.selections.Count == 1)
                        {
                            foreach (Selection sel in this.presenter.selections)
                            {
                                DBObject dbobj = (this.presenter.document as Document).database.GetObject(sel.objectId);
                                if (dbobj != null && (dbobj is Arc || dbobj is Circle))
                                {

                                    if (dbobj is Arc)
                                    {
                                        Arc arc = (Arc)dbobj;
                                        _center = arc.center;
                                        _radius = arc.radius;
                                    }
                                    if (dbobj is Circle)
                                    {
                                        Circle circle = (Circle)dbobj;
                                        _center = circle.center;
                                        _radius = circle.radius;
                                    }
                                    _currLine = new Line();
                                    _currLine.startPoint = _center;
                                    _currLine.layerId = this.document.currentLayerId;
                                    _currLine.color = Colors.Color.FromColor(Color.OrangeRed);
                                    _step = Step.Step2_SetTextPosition;
                                    this.pointer.mode = UI.Pointer.Mode.Locate;
                                    _pointInput = new DynInputPoint(this.presenter, new Vector2(0, 0));
                                    _pointInput.Message = "指定第二个点: ";
                                    this.dynamicInputer.StartInput(_pointInput);
                                    _pointInput.finish += this.OnPointInputReturn;
                                    _pointInput.cancel += this.OnPointInputReturn;
                                }
                                else
                                {
                                    Windows.MainWin.Instance.ShowMsg(Windows.TipsState.Info, "选择对象非圆或圆弧！");
                                    this.presenter.selections.Clear();
                                }
                            }
                        }
                    }
                }
                else if (_step == Step.Step2_SetTextPosition)
                {
                    if (e.Button == MouseButtons.Left)
                    {
                        double Angle = MathUtils.PointToAngle(_center, this.pointer.currentSnapPoint);
                        _currLine.endPoint = MathUtils.CenterRadiusPoint(_center, Angle, _radius);
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
                if (_step == Step.Step2_SetTextPosition)
                {
                    double Angle = MathUtils.PointToAngle(_center, xyRet.value);
                    _currLine.endPoint = MathUtils.CenterRadiusPoint(_center, Angle, _radius);
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
            _mtext.text = "R" + _radius.ToString("f2");
            _mtext.position = MathUtils.GetVerticalPonit(_Line, 0.5, len * 0.05);
            _mtext.layerId = this.document.currentLayerId;
            _mtext.color = Colors.Color.FromColor(Color.OrangeRed);
            _mtext.dimHandle = DimID;
            DocumentForm docForm = MainWin.Instance.ActiveMdiChild as DocumentForm;
            docForm.database.CreateRadialDimensionn(DimID, start, end);
        }


        


    }
}
