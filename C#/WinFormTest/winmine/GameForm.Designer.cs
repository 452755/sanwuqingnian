
namespace winmine
{
    partial class GameForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(GameForm));
            this.MenuStrip = new System.Windows.Forms.MenuStrip();
            this.游戏ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.初级ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.中级ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.高级ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.自定义ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.保存记录ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.查看记录ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
            this.教程ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.关于ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
            this.退出ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.GameInfo = new System.Windows.Forms.Panel();
            this.GameTime = new System.Windows.Forms.Panel();
            this.GameTimeOnesPlace = new System.Windows.Forms.PictureBox();
            this.GameTimeTenPlace = new System.Windows.Forms.PictureBox();
            this.GameTimeHundredPlace = new System.Windows.Forms.PictureBox();
            this.Start = new System.Windows.Forms.Panel();
            this.RemainingMines = new System.Windows.Forms.Panel();
            this.MinesTenPlace = new System.Windows.Forms.PictureBox();
            this.MinesHundredPlace = new System.Windows.Forms.PictureBox();
            this.MinesOnesPlace = new System.Windows.Forms.PictureBox();
            this.Minefield = new System.Windows.Forms.Panel();
            this.GameTimer = new System.Windows.Forms.Timer(this.components);
            this.NumImgList = new System.Windows.Forms.ImageList(this.components);
            this.StartImgList = new System.Windows.Forms.ImageList(this.components);
            this.MineBlockImgList = new System.Windows.Forms.ImageList(this.components);
            this.MenuStrip.SuspendLayout();
            this.GameInfo.SuspendLayout();
            this.GameTime.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.GameTimeOnesPlace)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.GameTimeTenPlace)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.GameTimeHundredPlace)).BeginInit();
            this.RemainingMines.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.MinesTenPlace)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.MinesHundredPlace)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.MinesOnesPlace)).BeginInit();
            this.SuspendLayout();
            // 
            // MenuStrip
            // 
            this.MenuStrip.ImageScalingSize = new System.Drawing.Size(20, 20);
            this.MenuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.游戏ToolStripMenuItem});
            this.MenuStrip.Location = new System.Drawing.Point(0, 0);
            this.MenuStrip.Name = "MenuStrip";
            this.MenuStrip.Size = new System.Drawing.Size(360, 28);
            this.MenuStrip.TabIndex = 0;
            this.MenuStrip.Text = "menuStrip1";
            // 
            // 游戏ToolStripMenuItem
            // 
            this.游戏ToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.初级ToolStripMenuItem,
            this.中级ToolStripMenuItem,
            this.高级ToolStripMenuItem,
            this.自定义ToolStripMenuItem,
            this.toolStripSeparator1,
            this.保存记录ToolStripMenuItem,
            this.查看记录ToolStripMenuItem,
            this.toolStripSeparator2,
            this.教程ToolStripMenuItem,
            this.关于ToolStripMenuItem,
            this.toolStripSeparator3,
            this.退出ToolStripMenuItem});
            this.游戏ToolStripMenuItem.Name = "游戏ToolStripMenuItem";
            this.游戏ToolStripMenuItem.Size = new System.Drawing.Size(53, 24);
            this.游戏ToolStripMenuItem.Text = "游戏";
            this.游戏ToolStripMenuItem.TextDirection = System.Windows.Forms.ToolStripTextDirection.Horizontal;
            // 
            // 初级ToolStripMenuItem
            // 
            this.初级ToolStripMenuItem.Checked = true;
            this.初级ToolStripMenuItem.CheckState = System.Windows.Forms.CheckState.Checked;
            this.初级ToolStripMenuItem.Name = "初级ToolStripMenuItem";
            this.初级ToolStripMenuItem.Size = new System.Drawing.Size(224, 26);
            this.初级ToolStripMenuItem.Text = "初级";
            this.初级ToolStripMenuItem.Click += new System.EventHandler(this.初级ToolStripMenuItem_Click);
            // 
            // 中级ToolStripMenuItem
            // 
            this.中级ToolStripMenuItem.Name = "中级ToolStripMenuItem";
            this.中级ToolStripMenuItem.Size = new System.Drawing.Size(224, 26);
            this.中级ToolStripMenuItem.Text = "中级";
            this.中级ToolStripMenuItem.Click += new System.EventHandler(this.中级ToolStripMenuItem_Click);
            // 
            // 高级ToolStripMenuItem
            // 
            this.高级ToolStripMenuItem.Name = "高级ToolStripMenuItem";
            this.高级ToolStripMenuItem.Size = new System.Drawing.Size(224, 26);
            this.高级ToolStripMenuItem.Text = "高级";
            this.高级ToolStripMenuItem.Click += new System.EventHandler(this.高级ToolStripMenuItem_Click);
            // 
            // 自定义ToolStripMenuItem
            // 
            this.自定义ToolStripMenuItem.Name = "自定义ToolStripMenuItem";
            this.自定义ToolStripMenuItem.Size = new System.Drawing.Size(224, 26);
            this.自定义ToolStripMenuItem.Text = "自定义";
            this.自定义ToolStripMenuItem.Click += new System.EventHandler(this.自定义ToolStripMenuItem_Click);
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            this.toolStripSeparator1.Size = new System.Drawing.Size(221, 6);
            // 
            // 保存记录ToolStripMenuItem
            // 
            this.保存记录ToolStripMenuItem.Name = "保存记录ToolStripMenuItem";
            this.保存记录ToolStripMenuItem.Size = new System.Drawing.Size(224, 26);
            this.保存记录ToolStripMenuItem.Text = "保存记录";
            this.保存记录ToolStripMenuItem.Click += new System.EventHandler(this.保存记录ToolStripMenuItem_Click);
            // 
            // 查看记录ToolStripMenuItem
            // 
            this.查看记录ToolStripMenuItem.Name = "查看记录ToolStripMenuItem";
            this.查看记录ToolStripMenuItem.Size = new System.Drawing.Size(224, 26);
            this.查看记录ToolStripMenuItem.Text = "查看记录";
            this.查看记录ToolStripMenuItem.Click += new System.EventHandler(this.查看记录ToolStripMenuItem_Click);
            // 
            // toolStripSeparator2
            // 
            this.toolStripSeparator2.Name = "toolStripSeparator2";
            this.toolStripSeparator2.Size = new System.Drawing.Size(221, 6);
            // 
            // 教程ToolStripMenuItem
            // 
            this.教程ToolStripMenuItem.Name = "教程ToolStripMenuItem";
            this.教程ToolStripMenuItem.Size = new System.Drawing.Size(224, 26);
            this.教程ToolStripMenuItem.Text = "教程";
            this.教程ToolStripMenuItem.Click += new System.EventHandler(this.教程ToolStripMenuItem_Click);
            // 
            // 关于ToolStripMenuItem
            // 
            this.关于ToolStripMenuItem.Name = "关于ToolStripMenuItem";
            this.关于ToolStripMenuItem.Size = new System.Drawing.Size(224, 26);
            this.关于ToolStripMenuItem.Text = "关于";
            this.关于ToolStripMenuItem.Click += new System.EventHandler(this.关于ToolStripMenuItem_Click);
            // 
            // toolStripSeparator3
            // 
            this.toolStripSeparator3.Name = "toolStripSeparator3";
            this.toolStripSeparator3.Size = new System.Drawing.Size(221, 6);
            // 
            // 退出ToolStripMenuItem
            // 
            this.退出ToolStripMenuItem.Name = "退出ToolStripMenuItem";
            this.退出ToolStripMenuItem.Size = new System.Drawing.Size(224, 26);
            this.退出ToolStripMenuItem.Text = "退出";
            this.退出ToolStripMenuItem.Click += new System.EventHandler(this.退出ToolStripMenuItem_Click);
            // 
            // GameInfo
            // 
            this.GameInfo.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.GameInfo.Controls.Add(this.GameTime);
            this.GameInfo.Controls.Add(this.Start);
            this.GameInfo.Controls.Add(this.RemainingMines);
            this.GameInfo.Location = new System.Drawing.Point(0, 29);
            this.GameInfo.Name = "GameInfo";
            this.GameInfo.Size = new System.Drawing.Size(360, 70);
            this.GameInfo.TabIndex = 1;
            // 
            // GameTime
            // 
            this.GameTime.Controls.Add(this.GameTimeOnesPlace);
            this.GameTime.Controls.Add(this.GameTimeTenPlace);
            this.GameTime.Controls.Add(this.GameTimeHundredPlace);
            this.GameTime.Location = new System.Drawing.Point(265, 10);
            this.GameTime.Name = "GameTime";
            this.GameTime.Size = new System.Drawing.Size(75, 50);
            this.GameTime.TabIndex = 0;
            // 
            // GameTimeOnesPlace
            // 
            this.GameTimeOnesPlace.Location = new System.Drawing.Point(50, 0);
            this.GameTimeOnesPlace.Name = "GameTimeOnesPlace";
            this.GameTimeOnesPlace.Size = new System.Drawing.Size(25, 50);
            this.GameTimeOnesPlace.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.GameTimeOnesPlace.TabIndex = 3;
            this.GameTimeOnesPlace.TabStop = false;
            // 
            // GameTimeTenPlace
            // 
            this.GameTimeTenPlace.Location = new System.Drawing.Point(25, 0);
            this.GameTimeTenPlace.Name = "GameTimeTenPlace";
            this.GameTimeTenPlace.Size = new System.Drawing.Size(25, 50);
            this.GameTimeTenPlace.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.GameTimeTenPlace.TabIndex = 2;
            this.GameTimeTenPlace.TabStop = false;
            // 
            // GameTimeHundredPlace
            // 
            this.GameTimeHundredPlace.Location = new System.Drawing.Point(0, 0);
            this.GameTimeHundredPlace.Name = "GameTimeHundredPlace";
            this.GameTimeHundredPlace.Size = new System.Drawing.Size(25, 50);
            this.GameTimeHundredPlace.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.GameTimeHundredPlace.TabIndex = 1;
            this.GameTimeHundredPlace.TabStop = false;
            // 
            // Start
            // 
            this.Start.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Stretch;
            this.Start.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.Start.Location = new System.Drawing.Point(155, 10);
            this.Start.Name = "Start";
            this.Start.Size = new System.Drawing.Size(50, 50);
            this.Start.TabIndex = 0;
            this.Start.MouseDown += new System.Windows.Forms.MouseEventHandler(this.Start_MouseDown);
            this.Start.MouseUp += new System.Windows.Forms.MouseEventHandler(this.Start_MouseUp);
            // 
            // RemainingMines
            // 
            this.RemainingMines.Controls.Add(this.MinesTenPlace);
            this.RemainingMines.Controls.Add(this.MinesHundredPlace);
            this.RemainingMines.Controls.Add(this.MinesOnesPlace);
            this.RemainingMines.Location = new System.Drawing.Point(20, 10);
            this.RemainingMines.Name = "RemainingMines";
            this.RemainingMines.Size = new System.Drawing.Size(75, 50);
            this.RemainingMines.TabIndex = 0;
            // 
            // MinesTenPlace
            // 
            this.MinesTenPlace.Location = new System.Drawing.Point(25, 0);
            this.MinesTenPlace.Name = "MinesTenPlace";
            this.MinesTenPlace.Size = new System.Drawing.Size(25, 50);
            this.MinesTenPlace.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.MinesTenPlace.TabIndex = 1;
            this.MinesTenPlace.TabStop = false;
            // 
            // MinesHundredPlace
            // 
            this.MinesHundredPlace.Location = new System.Drawing.Point(0, 0);
            this.MinesHundredPlace.Name = "MinesHundredPlace";
            this.MinesHundredPlace.Size = new System.Drawing.Size(25, 50);
            this.MinesHundredPlace.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.MinesHundredPlace.TabIndex = 0;
            this.MinesHundredPlace.TabStop = false;
            // 
            // MinesOnesPlace
            // 
            this.MinesOnesPlace.Location = new System.Drawing.Point(50, 0);
            this.MinesOnesPlace.Name = "MinesOnesPlace";
            this.MinesOnesPlace.Size = new System.Drawing.Size(25, 50);
            this.MinesOnesPlace.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.MinesOnesPlace.TabIndex = 2;
            this.MinesOnesPlace.TabStop = false;
            // 
            // Minefield
            // 
            this.Minefield.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.Minefield.Location = new System.Drawing.Point(0, 100);
            this.Minefield.Name = "Minefield";
            this.Minefield.Size = new System.Drawing.Size(360, 360);
            this.Minefield.TabIndex = 0;
            // 
            // GameTimer
            // 
            this.GameTimer.Interval = 1000;
            this.GameTimer.Tick += new System.EventHandler(this.GameTimer_Tick);
            // 
            // NumImgList
            // 
            this.NumImgList.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("NumImgList.ImageStream")));
            this.NumImgList.TransparentColor = System.Drawing.Color.Transparent;
            this.NumImgList.Images.SetKeyName(0, "d0.gif");
            this.NumImgList.Images.SetKeyName(1, "d1.gif");
            this.NumImgList.Images.SetKeyName(2, "d2.gif");
            this.NumImgList.Images.SetKeyName(3, "d3.gif");
            this.NumImgList.Images.SetKeyName(4, "d4.gif");
            this.NumImgList.Images.SetKeyName(5, "d5.gif");
            this.NumImgList.Images.SetKeyName(6, "d6.gif");
            this.NumImgList.Images.SetKeyName(7, "d7.gif");
            this.NumImgList.Images.SetKeyName(8, "d8.gif");
            this.NumImgList.Images.SetKeyName(9, "d9.gif");
            // 
            // StartImgList
            // 
            this.StartImgList.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("StartImgList.ImageStream")));
            this.StartImgList.TransparentColor = System.Drawing.Color.Transparent;
            this.StartImgList.Images.SetKeyName(0, "face_normal.gif");
            this.StartImgList.Images.SetKeyName(1, "face_fail.gif");
            this.StartImgList.Images.SetKeyName(2, "face_success.gif");
            // 
            // MineBlockImgList
            // 
            this.MineBlockImgList.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("MineBlockImgList.ImageStream")));
            this.MineBlockImgList.TransparentColor = System.Drawing.Color.Transparent;
            this.MineBlockImgList.Images.SetKeyName(0, "0.gif");
            this.MineBlockImgList.Images.SetKeyName(1, "1.gif");
            this.MineBlockImgList.Images.SetKeyName(2, "2.gif");
            this.MineBlockImgList.Images.SetKeyName(3, "3.gif");
            this.MineBlockImgList.Images.SetKeyName(4, "4.gif");
            this.MineBlockImgList.Images.SetKeyName(5, "5.gif");
            this.MineBlockImgList.Images.SetKeyName(6, "6.gif");
            this.MineBlockImgList.Images.SetKeyName(7, "7.gif");
            this.MineBlockImgList.Images.SetKeyName(8, "8.gif");
            this.MineBlockImgList.Images.SetKeyName(9, "mine.gif");
            this.MineBlockImgList.Images.SetKeyName(10, "blood.gif");
            this.MineBlockImgList.Images.SetKeyName(11, "error.gif");
            this.MineBlockImgList.Images.SetKeyName(12, "flag.gif");
            this.MineBlockImgList.Images.SetKeyName(13, "ask.gif");
            this.MineBlockImgList.Images.SetKeyName(14, "blank.gif");
            // 
            // GameForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 15F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.ClientSize = new System.Drawing.Size(360, 460);
            this.Controls.Add(this.Minefield);
            this.Controls.Add(this.GameInfo);
            this.Controls.Add(this.MenuStrip);
            this.DoubleBuffered = true;
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MainMenuStrip = this.MenuStrip;
            this.MaximizeBox = false;
            this.Name = "GameForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "扫雷";
            this.MenuStrip.ResumeLayout(false);
            this.MenuStrip.PerformLayout();
            this.GameInfo.ResumeLayout(false);
            this.GameTime.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.GameTimeOnesPlace)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.GameTimeTenPlace)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.GameTimeHundredPlace)).EndInit();
            this.RemainingMines.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.MinesTenPlace)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.MinesHundredPlace)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.MinesOnesPlace)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.MenuStrip MenuStrip;
        private System.Windows.Forms.Panel GameInfo;
        private System.Windows.Forms.Panel Minefield;
        private System.Windows.Forms.ToolStripMenuItem 游戏ToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem 初级ToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem 中级ToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem 高级ToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem 自定义ToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.Panel GameTime;
        private System.Windows.Forms.Panel Start;
        private System.Windows.Forms.Panel RemainingMines;
        private System.Windows.Forms.PictureBox MinesTenPlace;
        private System.Windows.Forms.PictureBox MinesHundredPlace;
        private System.Windows.Forms.PictureBox MinesOnesPlace;
        private System.Windows.Forms.PictureBox GameTimeOnesPlace;
        private System.Windows.Forms.PictureBox GameTimeTenPlace;
        private System.Windows.Forms.PictureBox GameTimeHundredPlace;
        private System.Windows.Forms.Timer GameTimer;
        private System.Windows.Forms.ImageList NumImgList;
        private System.Windows.Forms.ImageList StartImgList;
        private System.Windows.Forms.ImageList MineBlockImgList;
        private System.Windows.Forms.ToolStripMenuItem 保存记录ToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem 查看记录ToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
        private System.Windows.Forms.ToolStripMenuItem 教程ToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem 关于ToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
        private System.Windows.Forms.ToolStripMenuItem 退出ToolStripMenuItem;
    }
}