
namespace winmine
{
    partial class Customize
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
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.txtX = new System.Windows.Forms.TextBox();
            this.txtY = new System.Windows.Forms.TextBox();
            this.txtMine = new System.Windows.Forms.TextBox();
            this.panel5 = new System.Windows.Forms.Panel();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.thisTitle = new System.Windows.Forms.Label();
            this.btnDetermine = new System.Windows.Forms.Button();
            this.btnCancel = new System.Windows.Forms.Button();
            this.pictureBox2 = new System.Windows.Forms.PictureBox();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox2)).BeginInit();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.BackColor = System.Drawing.SystemColors.Control;
            this.label1.Font = new System.Drawing.Font("宋体", 14F);
            this.label1.Location = new System.Drawing.Point(30, 63);
            this.label1.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(274, 24);
            this.label1.TabIndex = 2;
            this.label1.Text = "请 输 入 宽 （9~30）：";
            this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.BackColor = System.Drawing.SystemColors.Control;
            this.label2.Font = new System.Drawing.Font("宋体", 14F);
            this.label2.Location = new System.Drawing.Point(30, 108);
            this.label2.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(274, 24);
            this.label2.TabIndex = 3;
            this.label2.Text = "请 输 入 高 （9~24）：";
            this.label2.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.BackColor = System.Drawing.SystemColors.Control;
            this.label3.Font = new System.Drawing.Font("宋体", 14F);
            this.label3.Location = new System.Drawing.Point(30, 153);
            this.label3.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(274, 24);
            this.label3.TabIndex = 4;
            this.label3.Text = "请输入雷数（10~200）：";
            this.label3.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // txtX
            // 
            this.txtX.Font = new System.Drawing.Font("宋体", 14F);
            this.txtX.Location = new System.Drawing.Point(310, 60);
            this.txtX.Margin = new System.Windows.Forms.Padding(2);
            this.txtX.Name = "txtX";
            this.txtX.Size = new System.Drawing.Size(70, 34);
            this.txtX.TabIndex = 5;
            this.txtX.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // txtY
            // 
            this.txtY.Font = new System.Drawing.Font("宋体", 14F);
            this.txtY.Location = new System.Drawing.Point(310, 105);
            this.txtY.Margin = new System.Windows.Forms.Padding(2);
            this.txtY.Name = "txtY";
            this.txtY.Size = new System.Drawing.Size(70, 34);
            this.txtY.TabIndex = 6;
            this.txtY.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // txtMine
            // 
            this.txtMine.Font = new System.Drawing.Font("宋体", 14F);
            this.txtMine.Location = new System.Drawing.Point(310, 150);
            this.txtMine.Margin = new System.Windows.Forms.Padding(2);
            this.txtMine.Name = "txtMine";
            this.txtMine.Size = new System.Drawing.Size(70, 34);
            this.txtMine.TabIndex = 7;
            this.txtMine.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // panel5
            // 
            this.panel5.BackColor = System.Drawing.SystemColors.Control;
            this.panel5.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.panel5.Location = new System.Drawing.Point(10, 36);
            this.panel5.Name = "panel5";
            this.panel5.Size = new System.Drawing.Size(380, 213);
            this.panel5.TabIndex = 10;
            this.panel5.MouseDown += new System.Windows.Forms.MouseEventHandler(this.panel5_MouseDown);
            this.panel5.MouseMove += new System.Windows.Forms.MouseEventHandler(this.panel5_MouseMove);
            this.panel5.MouseUp += new System.Windows.Forms.MouseEventHandler(this.panel5_MouseUp);
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = global::winmine.Properties.Resources.mine;
            this.pictureBox1.Location = new System.Drawing.Point(10, 10);
            this.pictureBox1.Margin = new System.Windows.Forms.Padding(0);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(24, 24);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.pictureBox1.TabIndex = 0;
            this.pictureBox1.TabStop = false;
            // 
            // thisTitle
            // 
            this.thisTitle.AutoSize = true;
            this.thisTitle.Font = new System.Drawing.Font("宋体", 9F);
            this.thisTitle.Location = new System.Drawing.Point(47, 14);
            this.thisTitle.Name = "thisTitle";
            this.thisTitle.Size = new System.Drawing.Size(37, 15);
            this.thisTitle.TabIndex = 1;
            this.thisTitle.Text = "扫雷";
            // 
            // btnDetermine
            // 
            this.btnDetermine.BackColor = System.Drawing.SystemColors.Control;
            this.btnDetermine.Font = new System.Drawing.Font("宋体", 14F);
            this.btnDetermine.Location = new System.Drawing.Point(50, 200);
            this.btnDetermine.Name = "btnDetermine";
            this.btnDetermine.Size = new System.Drawing.Size(100, 40);
            this.btnDetermine.TabIndex = 0;
            this.btnDetermine.Text = "确认";
            this.btnDetermine.UseVisualStyleBackColor = false;
            this.btnDetermine.Click += new System.EventHandler(this.btnDetermine_Click);
            // 
            // btnCancel
            // 
            this.btnCancel.BackColor = System.Drawing.SystemColors.Control;
            this.btnCancel.Font = new System.Drawing.Font("宋体", 14F);
            this.btnCancel.Location = new System.Drawing.Point(250, 200);
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(100, 40);
            this.btnCancel.TabIndex = 1;
            this.btnCancel.Text = "取消";
            this.btnCancel.UseVisualStyleBackColor = false;
            this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
            // 
            // pictureBox2
            // 
            this.pictureBox2.Image = global::winmine.Properties.Resources.window_close;
            this.pictureBox2.Location = new System.Drawing.Point(340, 10);
            this.pictureBox2.Name = "pictureBox2";
            this.pictureBox2.Size = new System.Drawing.Size(50, 24);
            this.pictureBox2.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.pictureBox2.TabIndex = 11;
            this.pictureBox2.TabStop = false;
            this.pictureBox2.Click += new System.EventHandler(this.pictureBox2_Click);
            // 
            // Customize
            // 
            this.AcceptButton = this.btnDetermine;
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 15F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.BackColor = System.Drawing.Color.Blue;
            this.ClientSize = new System.Drawing.Size(400, 259);
            this.Controls.Add(this.pictureBox2);
            this.Controls.Add(this.thisTitle);
            this.Controls.Add(this.pictureBox1);
            this.Controls.Add(this.txtMine);
            this.Controls.Add(this.txtY);
            this.Controls.Add(this.txtX);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.btnCancel);
            this.Controls.Add(this.btnDetermine);
            this.Controls.Add(this.panel5);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Name = "Customize";
            this.Padding = new System.Windows.Forms.Padding(10);
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Resize += new System.EventHandler(this.frmMain_Resize);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox2)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.TextBox txtX;
        private System.Windows.Forms.TextBox txtY;
        private System.Windows.Forms.TextBox txtMine;
        private System.Windows.Forms.Panel panel5;
        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.Label thisTitle;
        private System.Windows.Forms.Button btnDetermine;
        private System.Windows.Forms.Button btnCancel;
        private System.Windows.Forms.PictureBox pictureBox2;
    }
}