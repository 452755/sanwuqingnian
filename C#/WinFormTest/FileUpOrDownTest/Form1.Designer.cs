namespace FileUpOrDownTest
{
    partial class Form1
    {
        /// <summary>
        ///  Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        ///  Clean up any resources being used.
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
        ///  Required method for Designer support - do not modify
        ///  the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            fileDownload = new Button();
            fileUpload = new Button();
            textBoxFilePath = new TextBox();
            textBox1 = new TextBox();
            button1 = new Button();
            直接下载文件 = new Button();
            SuspendLayout();
            // 
            // fileDownload
            // 
            fileDownload.Location = new Point(455, 130);
            fileDownload.Name = "fileDownload";
            fileDownload.Size = new Size(111, 53);
            fileDownload.TabIndex = 0;
            fileDownload.Text = "检查文件是否更新";
            fileDownload.UseVisualStyleBackColor = true;
            fileDownload.Click += fileDownload_Click;
            // 
            // fileUpload
            // 
            fileUpload.Location = new Point(455, 29);
            fileUpload.Name = "fileUpload";
            fileUpload.Size = new Size(111, 53);
            fileUpload.TabIndex = 1;
            fileUpload.Text = "文件上传";
            fileUpload.UseVisualStyleBackColor = true;
            fileUpload.Click += fileUpload_Click;
            // 
            // textBoxFilePath
            // 
            textBoxFilePath.Font = new Font("微软雅黑", 14.25F, FontStyle.Regular, GraphicsUnit.Point);
            textBoxFilePath.Location = new Point(35, 37);
            textBoxFilePath.Name = "textBoxFilePath";
            textBoxFilePath.Size = new Size(372, 33);
            textBoxFilePath.TabIndex = 2;
            // 
            // textBox1
            // 
            textBox1.Location = new Point(33, 96);
            textBox1.Multiline = true;
            textBox1.Name = "textBox1";
            textBox1.Size = new Size(374, 434);
            textBox1.TabIndex = 3;
            // 
            // button1
            // 
            button1.Location = new Point(455, 232);
            button1.Name = "button1";
            button1.Size = new Size(111, 53);
            button1.TabIndex = 4;
            button1.Text = "修改服务端文件指纹";
            button1.UseVisualStyleBackColor = true;
            button1.Click += button1_Click;
            // 
            // 直接下载文件
            // 
            直接下载文件.Location = new Point(455, 340);
            直接下载文件.Name = "直接下载文件";
            直接下载文件.Size = new Size(111, 53);
            直接下载文件.TabIndex = 5;
            直接下载文件.Text = "检查文件是否更新";
            直接下载文件.UseVisualStyleBackColor = true;
            直接下载文件.Click += 直接下载文件_Click;
            // 
            // Form1
            // 
            AutoScaleDimensions = new SizeF(7F, 17F);
            AutoScaleMode = AutoScaleMode.Font;
            ClientSize = new Size(589, 542);
            Controls.Add(直接下载文件);
            Controls.Add(button1);
            Controls.Add(textBox1);
            Controls.Add(textBoxFilePath);
            Controls.Add(fileUpload);
            Controls.Add(fileDownload);
            Name = "Form1";
            Text = "Form1";
            Load += Form1_Load;
            ResumeLayout(false);
            PerformLayout();
        }

        #endregion

        private Button fileDownload;
        private Button fileUpload;
        private TextBox textBoxFilePath;
        private TextBox textBox1;
        private Button button1;
        private Button 直接下载文件;
    }
}
