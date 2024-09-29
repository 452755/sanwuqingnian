using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using MsMath;

namespace MsCAD.Windows
{
    internal partial class Canvas : UserControl, ICanvas, MsCAD.DatabaseServices.IDatabaseObserver
    {
        private IPresenter _presenter = null;
        internal delegate void ChangedCanvas();
        internal event ChangedCanvas changedCanvas;
        internal delegate void ChangedScreen();
        internal event ChangedScreen changedScreen;

        public Canvas()
        {
            InitializeComponent();

            this.SetStyle(ControlStyles.AllPaintingInWmPaint, true);
            this.SetStyle(ControlStyles.UserPaint, true);
            this.SetStyle(ControlStyles.OptimizedDoubleBuffer, true);
        }

        // 画布宽
        public double width
        {
            get { return this.ClientRectangle.Width; }
        }
        // 画布高
        public double height
        {
            get { return this.ClientRectangle.Height; }
        }

        public void AddChild(object child)
        {
            this.Controls.Add((Control)child);
        }

        public void RemoveChild(object child)
        {
            this.Controls.Remove((Control)child);
        }

        /// <summary>
        /// 获取鼠标位置
        /// </summary>
        public Vector2 GetMousePosition()
        {
            Point point = this.PointToClient(Control.MousePosition);
            return new Vector2(point.X, point.Y);
        }

        /// <summary>
        /// 设置鼠标位置
        /// </summary>
        public void SetMousePosition(Vector2 postion)
        {
            Cursor.Position = this.PointToScreen(new Point((int)postion.x, (int)postion.y));
        }

        public void Repaint()
        {
            Invalidate();
        }

        public void Repaint(double x, double y, double width, double height)
        {
            Invalidate(new Rectangle((int)x, (int)y, (int)width, (int)height));
        }

        public void SetPresenter(IPresenter presenter)
        {
            _presenter = presenter;
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            _presenter.OnPaintCanvas(e);
        }

        protected override void OnResize(EventArgs e)
        {
            base.OnResize(e);
            if (_presenter != null)
            {
                _presenter.OnResize(e);
            }
        }

        protected override void OnMouseDown(MouseEventArgs e)
        {
            base.OnMouseDown(e);
            _presenter.OnMouseDown(e);
            if (changedScreen != null)
            {
                changedScreen.Invoke();
            }
        }

        protected override void OnMouseUp(MouseEventArgs e)
        {
            base.OnMouseUp(e);
            _presenter.OnMouseUp(e);
        }

        protected override void OnMouseMove(MouseEventArgs e)
        {
            base.OnMouseMove(e);
            _presenter.OnMouseMove(e);
        }

        protected override void OnMouseDoubleClick(MouseEventArgs e)
        {
            base.OnMouseDoubleClick(e);
            _presenter.OnMouseDoubleClick(e);
        }

        protected override void OnMouseWheel(MouseEventArgs e)
        {
            base.OnMouseWheel(e);
            _presenter.OnMouseWheel(e);
            if (changedCanvas != null)
            {
                changedCanvas.Invoke();
            }
        }

        protected override void OnKeyDown(KeyEventArgs e)
        {
            base.OnKeyDown(e);
            _presenter.OnKeyDown(e);
        }

        protected override void OnKeyUp(KeyEventArgs e)
        {
            base.OnKeyUp(e);
            _presenter.OnKeyUp(e);
        }

        protected override void OnMouseEnter(EventArgs e)
        {
            base.OnMouseEnter(e);

            Cursor.Hide();
        }

        protected override void OnMouseLeave(EventArgs e)
        {
            base.OnMouseLeave(e);

            Cursor.Show();
        }
        protected override bool ProcessCmdKey(ref Message msg, Keys keyData)
        {
            _presenter.OnProcessCmdKey(keyData);
            if (changedCanvas != null)
            {
                changedCanvas.Invoke();
            }
            return base.ProcessCmdKey(ref msg, keyData);
        }
    }
}
