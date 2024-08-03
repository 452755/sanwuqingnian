
namespace winmine
{
    partial class Tutorials
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
            this.checkBox2 = new System.Windows.Forms.CheckBox();
            this.button1 = new System.Windows.Forms.Button();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.panel1 = new System.Windows.Forms.Panel();
            this.label2 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.circularButton1 = new winmine.CircularButton();
            this.circularButton2 = new winmine.CircularButton();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.panel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // checkBox2
            // 
            this.checkBox2.AutoSize = true;
            this.checkBox2.Location = new System.Drawing.Point(24, 581);
            this.checkBox2.Name = "checkBox2";
            this.checkBox2.Size = new System.Drawing.Size(89, 19);
            this.checkBox2.TabIndex = 1;
            this.checkBox2.Text = "不再提示";
            this.checkBox2.UseVisualStyleBackColor = true;
            // 
            // button1
            // 
            this.button1.FlatAppearance.BorderColor = System.Drawing.Color.Aqua;
            this.button1.FlatAppearance.MouseDownBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(192)))), ((int)(((byte)(255)))));
            this.button1.FlatAppearance.MouseOverBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(255)))), ((int)(((byte)(192)))));
            this.button1.Font = new System.Drawing.Font("宋体", 14F);
            this.button1.Location = new System.Drawing.Point(490, 581);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(114, 34);
            this.button1.TabIndex = 5;
            this.button1.Text = "知道了";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // pictureBox1
            // 
            this.pictureBox1.BackColor = System.Drawing.SystemColors.Control;
            this.pictureBox1.Location = new System.Drawing.Point(143, 250);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(333, 318);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.pictureBox1.TabIndex = 8;
            this.pictureBox1.TabStop = false;
            // 
            // panel1
            // 
            this.panel1.BackColor = System.Drawing.SystemColors.Control;
            this.panel1.Controls.Add(this.pictureBox1);
            this.panel1.Controls.Add(this.label2);
            this.panel1.Controls.Add(this.label1);
            this.panel1.Controls.Add(this.circularButton1);
            this.panel1.Controls.Add(this.checkBox2);
            this.panel1.Controls.Add(this.button1);
            this.panel1.Controls.Add(this.circularButton2);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.panel1.Location = new System.Drawing.Point(10, 43);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(638, 625);
            this.panel1.TabIndex = 9;
            this.panel1.MouseDown += new System.Windows.Forms.MouseEventHandler(this.panel5_MouseDown);
            this.panel1.MouseMove += new System.Windows.Forms.MouseEventHandler(this.panel5_MouseMove);
            this.panel1.MouseUp += new System.Windows.Forms.MouseEventHandler(this.panel5_MouseUp);
            // 
            // label2
            // 
            this.label2.Font = new System.Drawing.Font("宋体", 14F);
            this.label2.Location = new System.Drawing.Point(15, 87);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(606, 160);
            this.label2.TabIndex = 10;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("宋体", 20F);
            this.label1.Location = new System.Drawing.Point(62, 19);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(491, 68);
            this.label1.TabIndex = 9;
            this.label1.Text = "扫雷怎么玩？这几个技巧必须学\r\n会";
            // 
            // circularButton1
            // 
            this.circularButton1.BorderColor = System.Drawing.Color.Silver;
            this.circularButton1.Location = new System.Drawing.Point(527, 313);
            this.circularButton1.MouseDownBackColor = System.Drawing.Color.Black;
            this.circularButton1.MouseEnterBorderColor = System.Drawing.Color.Blue;
            this.circularButton1.Name = "circularButton1";
            this.circularButton1.Size = new System.Drawing.Size(60, 60);
            this.circularButton1.TabIndex = 6;
            this.circularButton1.Text = " >";
            this.circularButton1.UseVisualStyleBackColor = true;
            this.circularButton1.Click += new System.EventHandler(this.circularButton1_Click);
            // 
            // circularButton2
            // 
            this.circularButton2.BorderColor = System.Drawing.Color.Silver;
            this.circularButton2.Enabled = false;
            this.circularButton2.Location = new System.Drawing.Point(24, 313);
            this.circularButton2.MouseDownBackColor = System.Drawing.Color.Black;
            this.circularButton2.MouseEnterBorderColor = System.Drawing.Color.Blue;
            this.circularButton2.Name = "circularButton2";
            this.circularButton2.Size = new System.Drawing.Size(60, 60);
            this.circularButton2.TabIndex = 7;
            this.circularButton2.Text = "<";
            this.circularButton2.UseVisualStyleBackColor = true;
            this.circularButton2.Click += new System.EventHandler(this.circularButton2_Click);
            // 
            // Tutorials
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 15F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.Blue;
            this.ClientSize = new System.Drawing.Size(658, 678);
            this.Controls.Add(this.panel1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Name = "Tutorials";
            this.Padding = new System.Windows.Forms.Padding(10);
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Tutorials";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Tutorials_FormClosing);
            this.Resize += new System.EventHandler(this.frmMain_Resize);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion
        public System.Windows.Forms.CheckBox checkBox2;
        private System.Windows.Forms.Button button1;
        private winmine.CircularButton circularButton1;
        private winmine.CircularButton circularButton2;
        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
    }
}