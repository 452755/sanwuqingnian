using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;
using MsCAD.ApplicationServices;
using MsCAD.DatabaseServices;
using MsCAD.UI;
using MsCAD.Windows;
using MsMath;
using System.Linq;

namespace MsCAD.Commands.Draw
{
    /// <summary>
    /// 创建半径标注
    /// </summary>
    internal class Angular2LineDimensionCmd : DrawCmd
    {

        /// <summary>选择直线1</summary>
        private MsCAD.DatabaseServices.Line _SelectLine1 = null;

        /// <summary>选择直线2</summary>
        private MsCAD.DatabaseServices.Line _SelectLine2 = null;

        /// <summary>绘制圆弧</summary>
        private MsCAD.DatabaseServices.Arc _DrawArc = null;

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
            get { return new MsCAD.DatabaseServices.Entity[4] { _DrawArc,  _solid0, _solid1, _mtext }; }
        }

        /// <summary>
        /// 步骤
        /// </summary>
        private enum Step
        {
            /// <summary>选择线1</summary>
            Step1_SelectLine1 = 1,
            /// <summary>选择线2</summary>
            Step1_SelectLine2 = 2,
            /// <summary>选择标注位</summary>
            Step2_Position = 3,
        }
        private Step _step = Step.Step1_SelectLine1;



        /// <summary>
        /// 初始化
        /// </summary>
        public override void Initialize()
        {
            base.Initialize();
            //
            _step = Step.Step1_SelectLine1;
            this.pointer.mode = UI.Pointer.Mode.Select;
            Windows.MainWin.Instance.ShowMsg(Windows.TipsState.Info, "请选择第一条直线！");
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
                if (_step == Step.Step1_SelectLine1 && e.Button == MouseButtons.Left && this.presenter.selections.Count >= 1)
                {
                    foreach (Selection sel in this.presenter.selections)
                    {
                        DBObject dbobj = (this.presenter.document as Document).database.GetObject(sel.objectId);
                        if (dbobj != null && (dbobj is Line || dbobj is Polyline))
                        {
                            if (dbobj is Line)
                            {
                                _SelectLine1 = (Line)dbobj;
                            }
                            else
                            {
                                Polyline pl = (Polyline)dbobj;
                                PickupBox pb = new PickupBox(this.presenter);
                                pb.center = new Vector2(e.X, e.Y);
                                pb.UpdateReservedBounding();
                                _SelectLine1 = pb.reservedBounding.GetCloseLine(pl);
                            }
                            _step = Step.Step1_SelectLine2;
                            this.pointer.mode = UI.Pointer.Mode.Select;
                            Windows.MainWin.Instance.ShowMsg(Windows.TipsState.Info, "请选择第二条直线！");
                            return EventResult.Handled; ;
                        }
                    }
                    Windows.MainWin.Instance.ShowMsg(Windows.TipsState.Info, "选择对象非直线！");
                    this.presenter.selections.Clear();
                }
                else if (_step == Step.Step1_SelectLine2 && e.Button == MouseButtons.Left && this.presenter.selections.Count >= 1)
                {
                    foreach (Selection sel in this.presenter.selections)
                    {
                        DBObject dbobj = (this.presenter.document as Document).database.GetObject(sel.objectId);
                        if (dbobj.id == _SelectLine1.id)
                        {
                            continue;
                        }
                        if (dbobj != null && (dbobj is Line || dbobj is Polyline))
                        {
                            if (dbobj is Line)
                            {
                                _SelectLine2 = (Line)dbobj;
                            }
                            else
                            {
                                Polyline pl = (Polyline)dbobj;
                                PickupBox pb = new PickupBox(this.presenter);
                                pb.center = new Vector2(e.X, e.Y);
                                pb.UpdateReservedBounding();
                                _SelectLine2 = pb.reservedBounding.GetCloseLine(pl);
                            }

                            double angle1 = MathUtils.PointToAngleMin(_SelectLine1.startPoint, _SelectLine1.endPoint);//0-180
                            double angle2 = MathUtils.PointToAngleMin(_SelectLine2.startPoint, _SelectLine2.endPoint);//0-180
                            if (angle1 == angle2)
                            {
                                Windows.MainWin.Instance.ShowMsg(Windows.TipsState.Info, "线一与线二平行！");
                                this.presenter.selections.Remove(sel.objectId);
                            }
                            else
                            {

                                //固定以逆时针，绘制startAngle 到 endAngle
                                double CommonTextHeight = (this.presenter.document as Document).database.layerTable._CommonTextHeight;
                                if (angle1 > angle2)
                                {
                                    double temp = angle1;
                                    angle1 = angle2;
                                    angle2 = temp;
                                }

                                TempDrawForm f = new TempDrawForm(angle1, angle2);
                                if (f.ShowDialog() == DialogResult.OK)
                                {
                                    Vector2 center = MathUtils.GetIntersectionPoint(_SelectLine1, _SelectLine2);
                                    _DrawArc = new Arc();
                                    _DrawArc.center = center;
                                    _DrawArc.radius = CommonTextHeight * 4;

                                    _DrawArc.startAngle = f.SetAngle1;
                                    _DrawArc.endAngle = f.SetAngle2;
                                    _DrawArc.layerId = this.document.currentLayerId;
                                    _DrawArc.color = Colors.Color.FromColor(Color.OrangeRed);
                                    this.CreateEnt();
                                    _mgr.FinishCurrentCommand();
                                }
                                else
                                {
                                    _mgr.CancelCurrentCommand();
                                }
                            }
                            return EventResult.Handled;
                        }
                    }
                    Windows.MainWin.Instance.ShowMsg(Windows.TipsState.Info, "选择对象非直线！");
                    this.presenter.selections.Clear();
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
            if (_SelectLine1 != null)
            {
                Presenter presenter = _mgr.presenter as Presenter;
                Pen pen = new Pen(Color.Linen);
                pen.Width = 3;
                presenter.DrawEntity(g, _SelectLine1, pen);
            }
        }




        private void CreateEnt()
        {
            try
            {
                string DimID = Guid.NewGuid().ToString().Replace("-", "");
                Vector2 start = _DrawArc.startPoint;
                Vector2 end = _DrawArc.endPoint;
                _DrawArc.dimHandle = DimID;
                //测距主线长度
                double len = Math.Sqrt(Math.Pow((end.y - start.y), 2) + Math.Pow((end.x - start.x), 2));

                //起点端箭头
                _solid0 = new MsCAD.DatabaseServices.Solid();
                _solid0.vertices.Add(start);
                _solid0.vertices.Add(MathUtils.GetArcPoint(_DrawArc, 0.05, 1.05));
                _solid0.vertices.Add(MathUtils.GetArcPoint(_DrawArc, 0.05, 0.95));
                _solid0.layerId = this.document.currentLayerId;
                _solid0.color = Colors.Color.FromColor(Color.OrangeRed);
                _solid0.dimHandle = DimID;
                //终点端箭头
                _solid1 = new MsCAD.DatabaseServices.Solid();
                _solid1.vertices.Add(end);
                _solid1.vertices.Add(MathUtils.GetArcPoint(_DrawArc, 0.95, 1.05));
                _solid1.vertices.Add(MathUtils.GetArcPoint(_DrawArc, 0.95, 0.95));
                _solid1.layerId = this.document.currentLayerId;
                _solid1.color = Colors.Color.FromColor(Color.OrangeRed);
                _solid1.dimHandle = DimID;

                _mtext = new MsCAD.DatabaseServices.Text();
                _mtext.height = len / 5; 
                _mtext.text = _DrawArc.GetAngle360().ToString("f2") + "°";

                _mtext.position = _DrawArc.middlePoint;
                _mtext.layerId = this.document.currentLayerId;
                _mtext.color = Colors.Color.FromColor(Color.OrangeRed);
                _mtext.dimHandle = DimID;
                DocumentForm docForm = MainWin.Instance.ActiveMdiChild as DocumentForm;
                docForm.database.CreateAngular2LineDimension(DimID, _SelectLine1, _SelectLine2, _DrawArc.radius);
            }
            catch (Exception ex)
            {
                MainWin.Instance.ShowMsg(TipsState.Error, ex.Message);
            }
        }

    }
}
