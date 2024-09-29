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
    internal class Angular3PointDimensionCmd : DrawCmd
    {
        /// <summary>被选圆弧</summary>
        private MsCAD.DatabaseServices.Arc _SelectArc = null;

        /// <summary>绘制圆弧</summary>
        private MsCAD.DatabaseServices.Arc _DrawArc = null;


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
            get { return new MsCAD.DatabaseServices.Entity[6] { _DrawArc, _line0, _line1, _solid0, _solid1, _mtext }; }
        }

        /// <summary>
        /// 步骤
        /// </summary>
        private enum Step
        {
            /// <summary>选圆弧</summary>
            Step1_SelectArc = 1,
            /// <summary>选择标注角度</summary>
            Step2_ArcAngle = 2,
            /// <summary>选择标注弧长</summary>
            Step2_ArcLength = 3,
        }
        private Step _step = Step.Step1_SelectArc;



        /// <summary>
        /// 初始化
        /// </summary>
        public override void Initialize()
        {
            base.Initialize();
            //
            _step = Step.Step1_SelectArc;
            this.pointer.mode = UI.Pointer.Mode.Select;
            Windows.MainWin.Instance.ShowMsg(Windows.TipsState.Info, "请选择圆弧！");
        }

        /// <summary>
        /// 结束
        /// </summary>
        public override void Terminate(int num = 0)
        {
            base.Terminate();
        }

        public override EventResult OnMouseDown(MouseEventArgs e)
        {
            try
            {
                if (_step == Step.Step1_SelectArc)
                {
                    if (e.Button == MouseButtons.Left)
                    {
                        if (this.presenter.selections.Count == 1)
                        {
                            foreach (Selection sel in this.presenter.selections)
                            {
                                DBObject dbobj = (this.presenter.document as Document).database.GetObject(sel.objectId);
                                if (dbobj != null && dbobj is Arc)
                                {
                                    _SelectArc = (Arc)dbobj;
                                    _DrawArc = new Arc();
                                    _DrawArc.center = _SelectArc.center;
                                    _DrawArc.radius = _SelectArc.radius * 1.2;
                                    _DrawArc.startAngle = _SelectArc.startAngle;
                                    _DrawArc.endAngle = _SelectArc.endAngle;
                                    _DrawArc.layerId = this.document.currentLayerId;
                                    _DrawArc.color = Colors.Color.FromColor(Color.OrangeRed);

                                    List<string> list = new List<string>();
                                    list.Add("测量角度");
                                    list.Add("测量弧长");
                                    ButtonListForm f = new ButtonListForm("选择标注类型，回车确认", list);
                                    if (f.ShowDialog() == DialogResult.OK)
                                    {
                                        switch (f.SelectdIndex)
                                        {
                                            case 0://圆弧角度                        
                                                _step = Step.Step2_ArcAngle;
                                                break;
                                            case 1://圆弧弧长
                                                _step = Step.Step2_ArcLength;
                                                break;
                                        }
                                        this.CreateEnt();
                                        _mgr.FinishCurrentCommand();
                                    }
                                }
                                else
                                {
                                    Windows.MainWin.Instance.ShowMsg(Windows.TipsState.Info, "选择对象非圆弧！");
                                    this.presenter.selections.Clear();
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                //MainWin.Instance.ShowMsg(TipsState.Error, ex.Message);
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

        }




        private void CreateEnt()
        {
            try
            {
                string DimID = Guid.NewGuid().ToString().Replace("-", "");
                Vector2 start = _DrawArc.startPoint;
                Vector2 end = _DrawArc.endPoint;
                _DrawArc.dimHandle= DimID;
                //测距主线长度
                double len = Math.Sqrt(Math.Pow((end.y - start.y), 2) + Math.Pow((end.x - start.x), 2));

                //起点端垂直线
                _line0 = new Line();
                _line0.startPoint = MathUtils.CenterRadiusPoint(_SelectArc.center, _SelectArc.startAngle, _SelectArc.radius * 1.25);
                _line0.endPoint = MathUtils.CenterRadiusPoint(_SelectArc.center, _SelectArc.startAngle, _SelectArc.radius);
                _line0.layerId = this.document.currentLayerId;
                _line0.color = Colors.Color.FromColor(Color.OrangeRed);
                _line0.dimHandle = DimID;
                //终点端垂直线
                _line1 = new Line();
                _line1.startPoint = MathUtils.CenterRadiusPoint(_SelectArc.center, _SelectArc.endAngle, _SelectArc.radius * 1.25);
                _line1.endPoint = MathUtils.CenterRadiusPoint(_SelectArc.center, _SelectArc.endAngle, _SelectArc.radius);
                _line1.layerId = this.document.currentLayerId;
                _line1.color = Colors.Color.FromColor(Color.OrangeRed);
                _line1.dimHandle = DimID;

                //起点端箭头
                _solid0 = new MsCAD.DatabaseServices.Solid();
                _solid0.vertices.Add(start);
                _solid0.vertices.Add(MathUtils.GetArcPoint(_SelectArc, 0.05, 1.15));
                _solid0.vertices.Add(MathUtils.GetArcPoint(_SelectArc, 0.05, 1.25));
                _solid0.layerId = this.document.currentLayerId;
                _solid0.color = Colors.Color.FromColor(Color.OrangeRed);
                _solid0.dimHandle = DimID;
                //终点端箭头
                _solid1 = new MsCAD.DatabaseServices.Solid();
                _solid1.vertices.Add(end);
                _solid1.vertices.Add(MathUtils.GetArcPoint(_SelectArc, 0.95, 1.15));
                _solid1.vertices.Add(MathUtils.GetArcPoint(_SelectArc, 0.95, 1.25));
                _solid1.layerId = this.document.currentLayerId;
                _solid1.color = Colors.Color.FromColor(Color.OrangeRed);
                _solid1.dimHandle = DimID;

                _mtext = new MsCAD.DatabaseServices.Text();
                _mtext.height = len / 20;
                if (_step == Step.Step2_ArcAngle)
                {
                    _mtext.text = _SelectArc.GetAngle360().ToString("f2") + "°";
                }
                else
                {
                    _mtext.text = "⌒" + _SelectArc.GetArcLength().ToString("f2");
                }

                _mtext.position = _DrawArc.middlePoint;
                _mtext.layerId = this.document.currentLayerId;
                _mtext.color = Colors.Color.FromColor(Color.OrangeRed);
                _mtext.dimHandle = DimID;
                DocumentForm docForm = MainWin.Instance.ActiveMdiChild as DocumentForm;
                docForm.database.CreateAngular3PointDimension(DimID, _DrawArc.center, _DrawArc.startPoint, _DrawArc.endPoint,_DrawArc.radius);
            }
            catch (Exception ex)
            {
                MainWin.Instance.ShowMsg(TipsState.Error, ex.Message);
            }
        }

    }
}
