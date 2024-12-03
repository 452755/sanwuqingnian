using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.Drawing.Design;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace RestaurantTouchSystem.CustomControl.Marquee
{
    /// <summary>
    /// 动画出现方向
    /// </summary>
    [Description("动画出现方向")]
    public enum AnimationOrientation
    {
        /// <summary>
        ///从上边出现
        /// </summary>
        Top,
        /// <summary>
        /// 从下边出现
        /// </summary>
        Bottom,
        /// <summary>
        /// 从右边出现
        /// </summary>
        Right
    }

    public partial class TextMarquee : UserControl
    {
        #region 自定义属性

        /// <summary>
        /// 是否自动隐藏
        /// </summary>
        [Description("是否自动隐藏"), DefaultValue(true)]
        public bool AutoHiddle { get; set; } = true;

        /// <summary>
        /// 每个项的执行次数，为0时无限循环
        /// </summary>
        [Description("每个项的执行次数，为0时无限循环"), DefaultValue(1)]
        public int Frequency { get; set; } = 1;

        /// <summary>
        /// 图标
        /// </summary>
        [Description("图标")]
        public Image Icon { get; set; }

        /// <summary>
        /// 是否显示图标
        /// </summary>
        [Description("是否显示图标"), DefaultValue(true)]
        public bool IsShowIcon { get; set; } = true;

        /// <summary>
        /// 每个项的动画执行时长，单位(毫秒)，默认1000毫秒
        /// </summary>
        [Description("每个项的动画执行时长，单位(毫秒)"), DefaultValue(1000)]
        public int ItemTime { get; set; } = 1000;

        /// <summary>
        /// 动画执行的间隔, 单位(毫秒)，默认50毫秒
        /// </summary>
        [Description("动画执行的间隔, 单位(毫秒)"), DefaultValue(50)]
        public int IntervalTime { get; set; } = 50;

        /// <summary>
        /// 每个项的动画执行时长，单位(毫秒)，默认1000毫秒
        /// </summary>
        [Description("每个项的动画等待时长，单位(毫秒)"), DefaultValue(0)]
        public int ItemAwaitTime { get; set; } = 0;

        /// <summary>
        /// 动画出现的方向
        /// </summary>
        private AnimationOrientation orientation = AnimationOrientation.Right;
        /// <summary>
        /// 动画出现方向
        /// </summary>
        [DefaultValue(AnimationOrientation.Right)]
        [Description("动画出现方向")]
        public AnimationOrientation Orientation
        {
            get { return this.orientation; }
            set
            {
                if (this.orientation == value)
                    return;
                this.orientation = value;
            }
        }

        /// <summary>
        /// 文字的颜色
        /// </summary>
        [Description("文字的颜色"), DefaultValue("239, 119, 110")]
        public Color TextColor { get; set; } = System.Drawing.Color.FromArgb(((int)(((byte)(239)))), ((int)(((byte)(112)))), ((int)(((byte)(110)))));

        /// <summary>
        /// 最后一项是否自动停止播放
        /// </summary>
        [Description("最后一项是否自动停止播放"), DefaultValue(false)]
        public bool LastItemRemainingIsStop { get; set; } = false;
        #endregion

        #region 重写属性
        public override Size MinimumSize 
        {
            get { return base.MinimumSize; }
            set 
            {
                if (value.Height < 28 || value.Width < 250) 
                {
                    return;
                }
                base.MinimumSize = value; 
            }
        }


        #endregion

        #region 重写事件
        public new event EventHandler Click;
        #endregion
        public TextMarquee()
        {
            InitializeComponent();
        }

        private void Marquee_Load(object sender, EventArgs e)
        {
            this.pictureBoxIcon.Visible = this.IsShowIcon;
            this.pictureBoxIcon.Image = this.Icon;
            this.timer1.Interval = this.IntervalTime;
            this.timer1.Enabled = true;
            this.timer1.Start();
            handleVisible();
        }

        /// <summary>
        /// 处理控件是否显示
        /// </summary>
        private void handleVisible() 
        {
            if (AutoHiddle && !IsDesignMode()) 
            {
                if (this.tzxPanel4.Controls.Count <= 0)
                {
                    base.Visible = false;
                    this.timer1.Stop();
                }
                else
                {
                    base.Visible = true;
                    this.pictureBoxIcon.Image = this.Icon;
                    this.pictureBoxIcon.Visible = this.IsShowIcon;
                }
            }
            else
            {
                if (this.tzxPanel4.Controls.Count <= 0)
                {
                    this.timer1.Stop();
                }
            }
        }

        public static bool IsDesignMode() {
            bool returnFlag = false;

            if (LicenseManager.UsageMode == LicenseUsageMode.Designtime) {
                returnFlag = true;
            }
            else if (Process.GetCurrentProcess().ProcessName == "devenv")
            {
                returnFlag = true;
            }
            
            return returnFlag;
        }   

        private void timer1_Tick(object sender, EventArgs e)
        {
            if (this.tzxPanel4.Controls.Count > 0) 
            {
                Control removeControl = null;

                bool isFullyDisplayed = false;

                // 计算文本的移动距离
                switch (this.Orientation)
                {
                    case AnimationOrientation.Top:
                        {
                            int moveDistance = (this.tzxPanel4.Height * 2) * IntervalTime / (ItemTime - ItemAwaitTime);
                            bool isAwait = false;
                            foreach (Control item in this.tzxPanel4.Controls)
                            {
                                item.Location = new Point(item.Location.X, item.Location.Y + moveDistance);
                                Dictionary<string, int> labelTag = (Dictionary<string, int>)item.Tag;
                                if (item.Location.Y > this.tzxPanel4.Height)
                                {
                                    int frequency = labelTag["frequency"];
                                    frequency++;
                                    if (this.Frequency > 0 && frequency == this.Frequency)
                                    {
                                        removeControl = item;
                                    }
                                    else
                                    {
                                        labelTag["frequency"] = frequency;
                                        labelTag["await"] = 0;
                                        item.Tag = labelTag;
                                        int locationY = 0;
                                        if (this.tzxPanel4.Controls.Count > 1)
                                        {
                                            locationY = 0 - ((this.tzxPanel4.Controls.Count - 1) * this.tzxPanel4.Height);
                                        }
                                        else
                                        {
                                            locationY = 0 - (this.tzxPanel4.Controls.Count * this.tzxPanel4.Height);
                                        }
                                        item.Location = new Point(item.Location.X, locationY);
                                    }
                                }
                                else if (item.Location.Y >= 0)
                                {
                                    int awaitCout = this.ItemAwaitTime / this.IntervalTime;
                                    int awaitnum = labelTag["await"];
                                    if (awaitnum < awaitCout)
                                    {
                                        awaitnum++;
                                        labelTag["await"] = awaitnum;
                                        item.Location = new Point(item.Location.X, item.Location.Y - moveDistance);
                                        item.Tag = labelTag;
                                        isAwait = true;
                                    }
                                    else if (awaitnum == awaitCout) 
                                    {
                                        awaitnum++;
                                        labelTag["await"] = awaitnum;
                                        item.Location = new Point(item.Location.X, item.Location.Y - moveDistance);
                                        item.Tag = labelTag;
                                        isAwait = true;
                                        isFullyDisplayed = true;
                                    }
                                    else
                                    {
                                        isAwait = false;
                                    }
                                }
                                else if (isAwait) 
                                {
                                    item.Location = new Point(item.Location.X, item.Location.Y - moveDistance);
                                }
                            }
                        }
                        break;
                    case AnimationOrientation.Bottom:
                        {
                            int moveDistance = (this.tzxPanel4.Height * 2) * IntervalTime / (ItemTime - ItemAwaitTime);
                            bool isAwait = false;
                            foreach (Control item in this.tzxPanel4.Controls)
                            {
                                item.Location = new Point(item.Location.X, item.Location.Y - moveDistance);
                                Dictionary<string, int> labelTag = (Dictionary<string, int>)item.Tag;
                                // 滚动完毕
                                if (item.Location.Y < (0 - this.tzxPanel4.Height))
                                {
                                    int frequency = labelTag["frequency"];
                                    frequency++;
                                    if (this.Frequency > 0 && frequency == this.Frequency)
                                    {
                                        removeControl = item;
                                    }
                                    else
                                    {
                                        labelTag["frequency"] = frequency;
                                        labelTag["await"] = 0;
                                        item.Tag = labelTag;
                                        int locationY = 0;
                                        if (this.tzxPanel4.Controls.Count > 1)
                                        {
                                            locationY = (this.tzxPanel4.Controls.Count - 1) * this.tzxPanel4.Height;
                                        }
                                        else
                                        {
                                            locationY = this.tzxPanel4.Controls.Count * this.tzxPanel4.Height;
                                        }
                                        item.Location = new Point(item.Location.X, locationY);
                                    }
                                }
                                // 刚好显示完成 判断是否需要进行等待
                                else if (item.Location.Y <= 0)
                                {
                                    int awaitCout = this.ItemAwaitTime / this.IntervalTime;
                                    int awaitnum = labelTag["await"];
                                    if (awaitnum < awaitCout)
                                    {
                                        awaitnum++;
                                        labelTag["await"] = awaitnum;
                                        item.Location = new Point(item.Location.X, item.Location.Y + moveDistance);
                                        item.Tag = labelTag;
                                        isAwait = true;
                                    }
                                    else if (awaitnum == awaitCout) 
                                    {
                                        awaitnum++;
                                        labelTag["await"] = awaitnum;
                                        item.Location = new Point(item.Location.X, item.Location.Y + moveDistance);
                                        item.Tag = labelTag;
                                        isAwait = true;
                                        isFullyDisplayed = true;
                                    }
                                    else
                                    {
                                        isAwait = false;
                                    }
                                }
                                // 还没开始显示，判断是否需要等待
                                else if (isAwait) 
                                {
                                    item.Location = new Point(item.Location.X, item.Location.Y + moveDistance);
                                }
                            }
                        }
                        break;
                    case AnimationOrientation.Right:
                    default:
                        {
                            int moveDistance = (this.tzxPanel4.Width * 2) * IntervalTime / (ItemTime - ItemAwaitTime);
                            bool isAwait = false;
                            foreach (Control item in this.tzxPanel4.Controls)
                            {
                                item.Location = new Point(item.Location.X - moveDistance, item.Location.Y);
                                Dictionary<string, int> labelTag = (Dictionary<string, int>)item.Tag;
                                if (item.Location.X <= (0 - this.tzxPanel4.Width))
                                {
                                    int frequency = labelTag["frequency"];
                                    frequency++;
                                    if (this.Frequency > 0 && frequency == this.Frequency)
                                    {
                                        removeControl = item;
                                    }
                                    else
                                    {
                                        labelTag["frequency"] = frequency;
                                        labelTag["await"] = 0;
                                        item.Tag = labelTag;
                                        int locationX = 0;
                                        if (this.tzxPanel4.Controls.Count > 1)
                                        {
                                            locationX = (this.tzxPanel4.Controls.Count - 1) * this.tzxPanel4.Width;
                                        }
                                        else
                                        {
                                            locationX = this.tzxPanel4.Controls.Count * this.tzxPanel4.Width;
                                        }
                                        item.Location = new Point(locationX, item.Location.Y);
                                    }
                                }
                                else if (item.Location.X <= 0)
                                {
                                    int awaitCout = this.ItemAwaitTime / this.IntervalTime;
                                    int awaitnum = labelTag["await"];
                                    if (awaitnum < awaitCout)
                                    {
                                        awaitnum++;
                                        labelTag["await"] = awaitnum;
                                        item.Location = new Point(item.Location.X + moveDistance, item.Location.Y);
                                        item.Tag = labelTag;
                                        isAwait = true;
                                    }
                                    else if (awaitnum == awaitCout) 
                                    {
                                        awaitnum++;
                                        labelTag["await"] = awaitnum;
                                        item.Location = new Point(item.Location.X + moveDistance, item.Location.Y);
                                        item.Tag = labelTag;
                                        isAwait = true;
                                        isFullyDisplayed = true;
                                    }
                                    else
                                    {
                                        isAwait = false;
                                    }
                                }
                                else if (isAwait) 
                                {
                                    item.Location = new Point(item.Location.X + moveDistance, item.Location.Y);
                                }
                            }
                        }
                        break;
                }

                if (removeControl != null) 
                {
                    this.tzxPanel4.Controls.Remove(removeControl);
                }

                if (LastItemRemainingIsStop && isFullyDisplayed && this.tzxPanel4.Controls.Count == 1)
                {
                    this.timer1.Stop();
                }

                this.handleVisible();
            }
        }

        /// <summary>
        /// 添加文本
        /// </summary>
        /// <param name="text">要添加的文本</param>
        public void AddText(string text) 
        {
            Label label = new Label();
            label.AutoSize = false;
            label.ForeColor = this.TextColor;
            label.Name = "label";
            label.Size = new Size(this.tzxPanel4.Width, this.tzxPanel4.Height);
            label.Text = text;
            label.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            
            label.Click += Label_Click;

            Dictionary<string, int> labelTag = new Dictionary<string, int>();
            labelTag.Add("frequency", 0);
            labelTag.Add("await", 0);
            label.Tag = labelTag;

            Point lastPoint = new Point(0, 0);
            // 如果文本多于零条，获取最后一条文本
            if (this.tzxPanel4.Controls.Count > 0) 
            {
                Control control = this.tzxPanel4.Controls[this.tzxPanel4.Controls.Count - 1];
                lastPoint = control.Location;
            }
            // 计算当前新增的文本的位置
            switch (this.Orientation)
            {
                case AnimationOrientation.Top:
                    label.Location = new Point(lastPoint.X, lastPoint.Y - this.tzxPanel4.Height);
                    break;
                case AnimationOrientation.Bottom:
                    label.Location = new Point(lastPoint.X, lastPoint.Y + this.tzxPanel4.Height);
                    break;
                case AnimationOrientation.Right:
                default:
                    label.Location = new Point(lastPoint.X + this.tzxPanel4.Width, lastPoint.Y);
                    break;
            }

            this.tzxPanel4.Controls.Add(label);

            this.handleVisible();

            if (this.timer1.Enabled == false) 
            {
                this.timer1.Start();
            }
        }

        public List<string> GetCurrentItems() 
        {
            List<string> items = new List<string>();
            foreach (Control item in this.tzxPanel4.Controls)
            {
                items.Add(item.Text);
            }
            return items;
        }

        /// <summary>
        /// 清空文本
        /// </summary>
        public void ClearText() 
        {
            this.tzxPanel4.Controls.Clear();
            this.handleVisible();
            this.timer1.Stop();
        }

        private void pictureBoxIcon_Click(object sender, EventArgs e)
        {
            if (this.Click != null) 
            {
                this.Click(this, EventArgs.Empty);
            }
        }

        private void Label_Click(object sender, EventArgs e)
        {
            if (this.Click != null)
            {
                this.Click(this, EventArgs.Empty);
            }
        }

        private void tzxPanel4_Click(object sender, EventArgs e)
        {
            if (this.Click != null)
            {
                this.Click(this, EventArgs.Empty);
            }
        }
    }
}
