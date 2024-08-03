using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using winmine.Properties;

namespace winmine
{
    public partial class Tutorials : Form
    {
        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Tutorials));
        private string[] tutorialsTitleStrs = { "", "扫雷怎么玩？这几个技巧必须学\r\n会", "1.点击左键时，出现的数字是什\r\n么意思？", "\t2.点击右键时，出现的小红旗是\r\n什么意思？", "3.在打开的雷块上点击会发生\r\n什么？", "4.当点到雷会发生什么？" };
        private string[] tutorialsContentStrs = { "", "\r\t《扫雷》是一款大众类的益智小游戏，游戏规则是在最短的时间内，根据点击格子出现的数字找出所有非雷格子，同时避免踩雷，踩到一个雷即全盘皆输，但许多玩过扫雷的人却仍然有一些规则不明白。", "\r\t在判断出不是雷的方块上按下左键，可以打开该方块。如果方块上出现数字，则该数字表示其周围3×3区域中的地雷数；如果方块上为空，则可以打开与空相邻的方块；如果不幸触雷，则游戏结束。", "\r\t在判断为地雷的方块上按下右键，可以标记地雷（显示为小红旗），重复一次或两次操作可取消标记。", "\r\t同时按下左键和右键完成双击，当双击位置周围已标记雷数等于该位置数字时操作有效，相当于对该数字周围未打开的方块均进行一次左键单击操作。地雷未标记完全时使用双击无效。若数字周围有标错的地雷，则游戏结束，标错的地雷上会显示一个“ ×”。 小伙伴，你扫雷是否成功过呢？", "\r\t当点击到雷的时候，会展示出所有的雷，点击到的雷会显示为红色状态，标记错的雷会打上×" };
        int tutorialsIndex = 1;
        public Tutorials()
        {
            InitializeComponent();
            this.label1.Text = tutorialsTitleStrs[tutorialsIndex]; 
            this.label2.Text = tutorialsContentStrs[tutorialsIndex];
            this.pictureBox1.Image = ((Image)(resources.GetObject("教程"+ tutorialsIndex.ToString())));
        }

        private void Tutorials_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (this.checkBox2.Visible)
            {
                if (this.checkBox2.Checked)
                {
                    
                    Settings.Default.Tutorials = false;
                }
                else
                {
                    Settings.Default.Tutorials = true;
                }
                Properties.Settings.Default.Save();
            }
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
            FormPath = GetRoundedRectPath(rect, 20);
            this.Region = new Region(FormPath);
        }

        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void circularButton2_Click(object sender, EventArgs e)
        {
            tutorialsIndex--;
            this.label1.Text = tutorialsTitleStrs[tutorialsIndex];
            this.label2.Text = tutorialsContentStrs[tutorialsIndex];
            this.pictureBox1.Image = ((Image)(resources.GetObject("教程" + tutorialsIndex.ToString())));
            if (tutorialsIndex == 1)
            {
                circularButton2.Enabled = false;
            }
            if (circularButton1.Enabled == false) 
            {
                circularButton1.Enabled = true;
            }
        }

        private void circularButton1_Click(object sender, EventArgs e)
        {
            tutorialsIndex++;
            this.label1.Text = tutorialsTitleStrs[tutorialsIndex];
            this.label2.Text = tutorialsContentStrs[tutorialsIndex];
            this.pictureBox1.Image = ((Image)(resources.GetObject("教程" + tutorialsIndex.ToString())));
            if (tutorialsIndex==5)
            {
                circularButton1.Enabled = false;
            }
            if (circularButton2.Enabled == false)
            {
                circularButton2.Enabled = true;
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
    }
}
