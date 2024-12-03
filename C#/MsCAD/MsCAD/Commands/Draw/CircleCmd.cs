using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

using MsCAD.DatabaseServices;

namespace MsCAD.Commands.Draw
{
    internal class CircleCmd : DrawCmd
    {
        //private Circle _circle = null;
        protected Circle CircleObj { get { return (Circle)_entity; } }
        /// <summary>
        /// 新增的图元
        /// </summary>
        protected override IEnumerable<Entity> newEntities
        {
            get { return new Circle[1] { (Circle)_entity }; }
        }

        /// <summary>
        /// 步骤
        /// </summary>
        private enum Step
        {
            Step1_SpecifyCenter = 1,
            Step2_SpecityRadius = 2,
        }
        private Step _step = Step.Step1_SpecifyCenter;

        /// <summary>
        /// 初始化
        /// </summary>
        public override void Initialize()
        {
            base.Initialize();

            //
            _step = Step.Step1_SpecifyCenter;
            this.pointer.mode = UI.Pointer.Mode.Locate;
        }

        public override EventResult OnMouseDown(MouseEventArgs e)
        {
            if (_step == Step.Step1_SpecifyCenter)
            {
                if (e.Button == MouseButtons.Left)
                {
                    _entity = new Circle();
                    CircleObj.center = this.pointer.currentSnapPoint;
                    CircleObj.radius = 0;
                    CircleObj.layerId = this.document.currentLayerId;
                    CircleObj.color = this.document.currentColor;

                    _step = Step.Step2_SpecityRadius;
                }
            }
            else if (_step == Step.Step2_SpecityRadius)
            {
                if (e.Button == MouseButtons.Left)
                {
                    CircleObj.radius = (CircleObj.center - this.pointer.currentSnapPoint).length;
                    CircleObj.layerId = this.document.currentLayerId;
                    CircleObj.color = this.document.currentColor;

                    _mgr.FinishCurrentCommand();
                }
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

            if (CircleObj != null)
            {
                CircleObj.radius = (CircleObj.center - this.pointer.currentSnapPoint).length;
            }

            return EventResult.Handled;
        }

        //public override void OnPaint(Graphics g)
        //{
        //    if (CircleObj != null)
        //    {
        //        Presenter presenter = _mgr.presenter as Presenter;
        //        presenter.DrawEntity(g, CircleObj);
        //    }
        //}
    }
}
