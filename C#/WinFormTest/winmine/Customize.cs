using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace winmine
{
    public partial class Customize : Form
    {
        private int x;
        private int y;
        private int mine;
        private int X 
        {
            set 
            {
                if (value > 30) x = 30;
                else if (value < 9) x = 9;
                else x = value;
            }
        }

        private int Y {
            set
            {
                if (value > 24) y = 24;
                else if (value < 9) y = 9;
                else y = value;
            }
        }

        private int Mine {
            set
            {
                if (value > 200) mine = 200;
                else if (value < 10) mine = 10;
                else mine = value;
            }
        }

        private static Customize customize = new Customize();
        private Dictionary<string, int> dicCustomize = new Dictionary<string, int>();
        private Customize()
        {
            InitializeComponent();
            SetWindowRegion();
        }
        public static Dictionary<string, int> GetCustomize()
        {
            customize.dicCustomize.Clear();
            customize.txtX.Text = "";
            customize.txtY.Text = "";
            customize.txtMine.Text = "";
            customize.ShowDialog();
            return customize.dicCustomize;
        }
        
        private void btnCancel_Click(object sender, EventArgs e)
        {
            dicCustomize = null;
            this.Close();
        }
        
        private void btnDetermine_Click(object sender, EventArgs e)
        {
            string xStr = this.txtX.Text;
            string yStr = this.txtY.Text;
            string mineStr = this.txtMine.Text;
            string message = "";
            if (xStr != null && xStr.Trim() != "" && yStr != null && yStr.Trim() != "" && mineStr != null && mineStr.Trim() != "") 
            {
                if (IsNumeric(xStr) && IsNumeric(yStr) && IsNumeric(mineStr)) 
                {
                    X = Convert.ToInt32(xStr); 
                    Y = Convert.ToInt32(yStr);
                    Mine = Convert.ToInt32(mineStr);
                    if (x * y < mine || x * y == mine) 
                    {
                        Mine = 10;
                    }
                    dicCustomize.Add("宽",x);
                    dicCustomize.Add("高",y);
                    dicCustomize.Add("雷", mine);
                    this.Close();
                }
                else
                {
                    if (!IsNumeric(xStr)) message += "请输入正确的宽\n"; 
                    if (!IsNumeric(yStr)) message += "请输入正确的高\n";
                    if (!IsNumeric(mineStr)) message += "请输入正确的雷数\n";
                    MessageBox.Show(message);
                    this.txtX.Text = "";
                    this.txtY.Text = "";
                    this.txtMine.Text = "";
                }
            }
            else
            {
                if (xStr.Trim() == "") message += "宽不能为空\n";
                if (yStr.Trim() == "") message += "高不能为空\n";
                if (mineStr.Trim() == "") message += "雷的数量不能为空\n";
                MessageBox.Show(message);
            }
        }
        public static bool IsNumeric(string value)
        {
            try
            {
                int.Parse(value);
                return true;
            }
            catch 
            {
                return false;
            }
        }
        /// <summary>
        /// 设置FormBorderStyle:FixedDialog后窗体无法移动，重新实现移动窗体代码
        /// </summary>
        private const int wmParameter = 0x84;
        private const int paramOne = 0x1;
        private const int paramTwo = 0x2;
        protected override void WndProc(ref Message id)
        {
            // 引用消息ID(ref Message ID)
            switch (id.Msg)
            {
                case wmParameter:
                    base.WndProc(ref id);
                    if ((int)id.Result == paramOne)
                        id.Result = (IntPtr)paramTwo;
                    return;
            }
            base.WndProc(ref id);
        }

        private bool isDrag = false;

        private void panel5_MouseDown(object sender, MouseEventArgs e)
        {
            isDrag = true;
        }

        private void panel5_MouseMove(object sender, MouseEventArgs e)
        {
            if (isDrag)
            {
                int xDrag = this.Location.X + e.X; int yDrag = this.Location.Y + e.Y;
                this.Location = new Point(xDrag, yDrag);
            }
        }

        private void panel5_MouseUp(object sender, MouseEventArgs e)
        {
            isDrag = false;
        }

        private void pictureBox2_Click(object sender, EventArgs e)
        {
            dicCustomize = null;
            this.Close();
        }
        private GraphicsPath GetRoundedRectPath(Rectangle rect, int radius)
        {
            int diameter = radius;
            Rectangle arcRect = new Rectangle(rect.Location, new Size(diameter, diameter));
            GraphicsPath path = new GraphicsPath();
            // 左上角  
            path.AddArc(arcRect, 180, 90);
            // 右上角  
            arcRect.X = rect.Right - diameter;
            path.AddArc(arcRect, 270, 90);
            // 右下角  
            arcRect.Y = rect.Bottom - diameter;
            path.AddArc(arcRect, 0, 90);
            // 左下角  
            arcRect.X = rect.Left;
            path.AddArc(arcRect, 90, 90);
            path.CloseFigure();//闭合曲线  
            return path;
        }
        private void frmMain_Resize(object sender, EventArgs e)
        {
            SetWindowRegion();
        }
        public void SetWindowRegion()
        {
            System.Drawing.Drawing2D.GraphicsPath FormPath;
            FormPath = new System.Drawing.Drawing2D.GraphicsPath();
            Rectangle rect = new Rectangle(0, 0, this.Width, this.Height);
            FormPath = GetRoundedRectPath(rect, 10);
            this.Region = new Region(FormPath);
        }
    }
}
