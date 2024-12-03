
namespace RestaurantTouchSystem.CustomControl.Marquee
{
    partial class TextMarquee
    {
        /// <summary> 
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region 组件设计器生成的代码

        /// <summary> 
        /// 设计器支持所需的方法 - 不要修改
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.tzxPanel4 = new TZXControlLibrary.PanelEx.TzxPanel();
            this.pictureBoxIcon = new System.Windows.Forms.PictureBox();
            this.timer1 = new System.Windows.Forms.Timer(this.components);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxIcon)).BeginInit();
            this.SuspendLayout();
            // 
            // tzxPanel4
            // 
            this.tzxPanel4.BackColor = System.Drawing.Color.Transparent;
            this.tzxPanel4.BorderColor = System.Drawing.Color.Transparent;
            this.tzxPanel4.BorderSize = 0;
            this.tzxPanel4.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tzxPanel4.HasBottomBorder = false;
            this.tzxPanel4.HasLeftBorder = false;
            this.tzxPanel4.HasRightBorder = false;
            this.tzxPanel4.HasTopBorder = false;
            this.tzxPanel4.Location = new System.Drawing.Point(28, 0);
            this.tzxPanel4.Margin = new System.Windows.Forms.Padding(0);
            this.tzxPanel4.Name = "tzxPanel4";
            this.tzxPanel4.Size = new System.Drawing.Size(222, 28);
            this.tzxPanel4.TabIndex = 27;
            this.tzxPanel4.Click += new System.EventHandler(this.tzxPanel4_Click);
            // 
            // pictureBoxIcon
            // 
            this.pictureBoxIcon.BackColor = System.Drawing.Color.Transparent;
            this.pictureBoxIcon.Dock = System.Windows.Forms.DockStyle.Left;
            this.pictureBoxIcon.Image = global::RestaurantTouchSystem.Properties.Resources.收到扫码点单订单信息;
            this.pictureBoxIcon.Location = new System.Drawing.Point(0, 0);
            this.pictureBoxIcon.Margin = new System.Windows.Forms.Padding(0);
            this.pictureBoxIcon.Name = "pictureBoxIcon";
            this.pictureBoxIcon.Size = new System.Drawing.Size(28, 28);
            this.pictureBoxIcon.SizeMode = System.Windows.Forms.PictureBoxSizeMode.CenterImage;
            this.pictureBoxIcon.TabIndex = 1;
            this.pictureBoxIcon.TabStop = false;
            this.pictureBoxIcon.Click += new System.EventHandler(this.pictureBoxIcon_Click);
            // 
            // timer1
            // 
            this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
            // 
            // TextMarquee
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.tzxPanel4);
            this.Controls.Add(this.pictureBoxIcon);
            this.Margin = new System.Windows.Forms.Padding(0);
            this.MinimumSize = new System.Drawing.Size(250, 28);
            this.Name = "TextMarquee";
            this.Size = new System.Drawing.Size(250, 28);
            this.Load += new System.EventHandler(this.Marquee_Load);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBoxIcon)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private TZXControlLibrary.PanelEx.TzxPanel tzxPanel4;
        private System.Windows.Forms.PictureBox pictureBoxIcon;
        private System.Windows.Forms.Timer timer1;
    }
}
