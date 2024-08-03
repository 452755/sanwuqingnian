using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace winmine
{
    public class CircularButton : Button
    {
        public CircularButton() 
        {
            this.Size = new Size(40,40);
            this.Text = "";
            this.Paint += CircularButton_Paint;
            this.MouseEnter += CircularButton_MouseEnter;
            this.MouseLeave += CircularButton_MouseLeave;
            this.GotFocus += CircularButton_GotFocus;
            this.LostFocus += CircularButton_LostFocus;
        }

        private void CircularButton_LostFocus(object sender, EventArgs e)
        {
            
        }

        private void CircularButton_GotFocus(object sender, EventArgs e)
        {
            
        }

        private void CircularButton_MouseLeave(object sender, EventArgs e)
        {
            Color color = this.MouseEnterBorderColor;
            this.MouseEnterBorderColor = this.BorderColor;
            this.BorderColor = color;
        }

        private void CircularButton_MouseEnter(object sender, EventArgs e)
        {
            Color color = this.MouseEnterBorderColor;
            this.MouseEnterBorderColor = this.BorderColor;
            this.BorderColor = color;
        }

        private void CircularButton_Paint(object sender, PaintEventArgs e)
        {
            Graphics graphics = e.Graphics;
            Pen pen = new Pen(BorderColor, 2f);
            graphics.DrawEllipse(pen, new RectangleF(4f, 4f, this.Size.Width - 8, this.Size.Height - 8));
            System.Drawing.Drawing2D.GraphicsPath g = new System.Drawing.Drawing2D.GraphicsPath(System.Drawing.Drawing2D.FillMode.Alternate);
            //向路径中填充棋子大小椭圆
            g.AddEllipse(new Rectangle(3, 3, this.Size.Width-6, this.Size.Height-6));
            //将椭圆路径和棋子进行关联
            this.Region = new Region(g);
            //释放路径资源
            g.Dispose();
        }
        private Color borderColor = Color.Silver;
        private Color mouseDownBackColor = Color.Black;
        private Color mouseEnterBorderColor = Color.Blue;
        /// < summary >
        /// 鼠标按下的背景颜色
        /// </ summary >
        public Color MouseDownBackColor
        {
            get
            {
                return this.mouseDownBackColor;
            }
            set
            {
                this.mouseDownBackColor = value;
                this.Invalidate();
            }
        }

        public Color BorderColor {
            get
            {
                return this.borderColor;
            }
            set
            {
                this.borderColor = value;
                this.Invalidate();
            }
        }

        public Color MouseEnterBorderColor
        {
            get
            {
                return this.mouseEnterBorderColor;
            }
            set
            {
                this.mouseEnterBorderColor = value;
                //this.Invalidate();
            }
        }

        private const int WM_PAINT = 0xF;
        protected override void WndProc(ref Message m)
        {
            base.WndProc(ref m);
            if (m.Msg == WM_PAINT)
            {
                //DrawBtn();
            }
        }

        private void DrawBtn() 
        {
            System.Drawing.Drawing2D.GraphicsPath g = new System.Drawing.Drawing2D.GraphicsPath(System.Drawing.Drawing2D.FillMode.Alternate);
            //向路径中填充棋子大小椭圆
            g.AddEllipse(new Rectangle(15, 15, 36, 36));
            //将椭圆路径和棋子进行关联
            this.Region = new Region(g);
            //释放路径资源
            g.Dispose();
        }
        
    }
}
