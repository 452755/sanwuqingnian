using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;
using MsMath;

using MsCAD.DatabaseServices;

namespace MsCAD.Commands.Draw
{
    internal class Rectangle2Cmd : DrawCmd
    {
        private MsCAD.DatabaseServices.Rectangle2 _rectangle = null;

        /// <summary>
        /// 新增的图元
        /// </summary>
        protected override IEnumerable<Entity> newEntities
        {
            get { return new MsCAD.DatabaseServices.Rectangle2[1] { _rectangle }; }
        }
        
        // 中心点、角度、短边、长边
        private Vector2 _center = new Vector2(0, 0);
        private double _angle = 0.0;
        private double _hwidth = 0.0;
        private double _hheight = 0.0;

        private void UpdateRectangle()
        {

            if (_rectangle == null)
            {
                _rectangle = new MsCAD.DatabaseServices.Rectangle2();
            }
            _rectangle.center = _center;
            _rectangle.angle = _angle;
            _rectangle.hwidth = _hwidth;
            _rectangle.hheight = _hheight;
            _rectangle.layerId = this.document.currentLayerId;
            _rectangle.color = this.document.currentColor;
        }

        /// <summary>
        /// 步骤
        /// </summary>
        private enum Step
        {
            Step1_Center = 1,//中心点
            Step2_Hwidth = 2,//短边
            Step3_Angle = 3,//角度
            Step4_Hheight = 4,//长边
        }
        private Step _step = Step.Step1_Center;

        /// <summary>
        /// 初始化
        /// </summary>
        public override void Initialize()
        {
            base.Initialize();

            _step = Step.Step1_Center;
            this.pointer.mode = UI.Pointer.Mode.Locate;
        }

        public override EventResult OnMouseDown(MouseEventArgs e)
        {
            switch (_step)
            {
                case Step.Step1_Center:
                    if (e.Button == MouseButtons.Left)
                    {
                        _center = this.pointer.currentSnapPoint;
                        UpdateRectangle();
                        _step = Step.Step2_Hwidth;
                    }
                    break;

                case Step.Step2_Hwidth:
                    if (e.Button == MouseButtons.Left)
                    {
                        _hwidth = (_rectangle.center - this.pointer.currentSnapPoint).length;
                        UpdateRectangle();
                        _step = Step.Step3_Angle;
                    }
                    break;

                case Step.Step3_Angle:
                    if (e.Button == MouseButtons.Left)
                    {
                        _angle = -Math.Atan2((this.pointer.currentSnapPoint.y - _rectangle.center.y), (this.pointer.currentSnapPoint.x - _rectangle.center.x)) ;
                        UpdateRectangle();
                        _step = Step.Step4_Hheight;
                    }
                    break;

                case Step.Step4_Hheight:
                    if (e.Button == MouseButtons.Left)
                    {
                        _hheight = (_rectangle.center - this.pointer.currentSnapPoint).length;
                        UpdateRectangle();
                        _mgr.FinishCurrentCommand();
                    }
                    break;
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
            else if (_step == Step.Step2_Hwidth)
            {
                _hwidth = (_rectangle.center - this.pointer.currentSnapPoint).length;
                this.UpdateRectangle();
            }
            else if (_step == Step.Step3_Angle)
            {
                _angle = -Math.Atan2((this.pointer.currentSnapPoint.y - _rectangle.center.y), (this.pointer.currentSnapPoint.x - _rectangle.center.x)) ;
                this.UpdateRectangle();
            }
            else if (_step == Step.Step4_Hheight)
            {
                _hheight = (_rectangle.center - this.pointer.currentSnapPoint).length;
                this.UpdateRectangle();
            }
            return EventResult.Handled;
        }

        public override void OnPaint(Graphics g)
        {
            if (_rectangle != null)
            {
                this.presenter.DrawEntity(g, _rectangle);
            }
        }
    }
}
