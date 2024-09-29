namespace MsCAD.Windows
{
    partial class ScreenForm
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
            this.btnScreen = new System.Windows.Forms.Button();
            this.btnClose = new System.Windows.Forms.Button();
            this.btnDeleteClose = new System.Windows.Forms.Button();
            this.dgvScreen = new System.Windows.Forms.DataGridView();
            this.colType = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.colMin = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.colMax = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.label1 = new System.Windows.Forms.Label();
            this.dgvDifference = new System.Windows.Forms.DataGridView();
            this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
            this.label2 = new System.Windows.Forms.Label();
            ((System.ComponentModel.ISupportInitialize)(this.dgvScreen)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.dgvDifference)).BeginInit();
            this.SuspendLayout();
            // 
            // btnScreen
            // 
            this.btnScreen.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.btnScreen.Location = new System.Drawing.Point(-1, 216);
            this.btnScreen.Margin = new System.Windows.Forms.Padding(2);
            this.btnScreen.Name = "btnScreen";
            this.btnScreen.Size = new System.Drawing.Size(56, 26);
            this.btnScreen.TabIndex = 7;
            this.btnScreen.Text = "筛选";
            this.btnScreen.UseVisualStyleBackColor = true;
            this.btnScreen.Click += new System.EventHandler(this.btnScreen_Click);
            // 
            // btnClose
            // 
            this.btnClose.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.btnClose.Location = new System.Drawing.Point(246, 216);
            this.btnClose.Margin = new System.Windows.Forms.Padding(2);
            this.btnClose.Name = "btnClose";
            this.btnClose.Size = new System.Drawing.Size(56, 26);
            this.btnClose.TabIndex = 6;
            this.btnClose.Text = "关闭";
            this.btnClose.UseVisualStyleBackColor = true;
            this.btnClose.Click += new System.EventHandler(this.btnClose_Click);
            // 
            // btnDeleteClose
            // 
            this.btnDeleteClose.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.btnDeleteClose.Location = new System.Drawing.Point(59, 216);
            this.btnDeleteClose.Margin = new System.Windows.Forms.Padding(2);
            this.btnDeleteClose.Name = "btnDeleteClose";
            this.btnDeleteClose.Size = new System.Drawing.Size(183, 26);
            this.btnDeleteClose.TabIndex = 5;
            this.btnDeleteClose.Text = "删除未选择的图形并关闭";
            this.btnDeleteClose.UseVisualStyleBackColor = true;
            this.btnDeleteClose.Click += new System.EventHandler(this.btnDeleteClose_Click);
            // 
            // dgvScreen
            // 
            this.dgvScreen.AllowUserToAddRows = false;
            this.dgvScreen.AllowUserToDeleteRows = false;
            this.dgvScreen.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.dgvScreen.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvScreen.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.colType,
            this.colMin,
            this.colMax});
            this.dgvScreen.Location = new System.Drawing.Point(0, 24);
            this.dgvScreen.Margin = new System.Windows.Forms.Padding(2);
            this.dgvScreen.Name = "dgvScreen";
            this.dgvScreen.RowHeadersVisible = false;
            this.dgvScreen.RowTemplate.Height = 23;
            this.dgvScreen.Size = new System.Drawing.Size(303, 188);
            this.dgvScreen.TabIndex = 8;
            // 
            // colType
            // 
            this.colType.HeaderText = "类型";
            this.colType.Name = "colType";
            this.colType.ReadOnly = true;
            this.colType.Resizable = System.Windows.Forms.DataGridViewTriState.False;
            this.colType.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
            this.colType.Width = 120;
            // 
            // colMin
            // 
            this.colMin.HeaderText = "最小值";
            this.colMin.Name = "colMin";
            this.colMin.Resizable = System.Windows.Forms.DataGridViewTriState.False;
            this.colMin.Width = 90;
            // 
            // colMax
            // 
            this.colMax.HeaderText = "最大值";
            this.colMax.Name = "colMax";
            this.colMax.Resizable = System.Windows.Forms.DataGridViewTriState.False;
            this.colMax.Width = 90;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(4, 6);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(299, 12);
            this.label1.TabIndex = 9;
            this.label1.Text = "最大最小值为0、相等或最小值大于最大值时不进行筛选";
            // 
            // dgvDifference
            // 
            this.dgvDifference.AllowUserToAddRows = false;
            this.dgvDifference.AllowUserToDeleteRows = false;
            this.dgvDifference.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.dgvDifference.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvDifference.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dataGridViewTextBoxColumn1,
            this.dataGridViewTextBoxColumn2,
            this.dataGridViewTextBoxColumn3});
            this.dgvDifference.Location = new System.Drawing.Point(330, 24);
            this.dgvDifference.Margin = new System.Windows.Forms.Padding(2);
            this.dgvDifference.Name = "dgvDifference";
            this.dgvDifference.RowHeadersVisible = false;
            this.dgvDifference.RowTemplate.Height = 23;
            this.dgvDifference.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgvDifference.Size = new System.Drawing.Size(303, 216);
            this.dgvDifference.TabIndex = 10;
            this.dgvDifference.CellClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvDifference_CellClick);
            this.dgvDifference.CellContentClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvDifference_CellContentClick);
            // 
            // dataGridViewTextBoxColumn1
            // 
            this.dataGridViewTextBoxColumn1.HeaderText = "类型";
            this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
            this.dataGridViewTextBoxColumn1.ReadOnly = true;
            this.dataGridViewTextBoxColumn1.Resizable = System.Windows.Forms.DataGridViewTriState.False;
            this.dataGridViewTextBoxColumn1.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
            this.dataGridViewTextBoxColumn1.Width = 120;
            // 
            // dataGridViewTextBoxColumn2
            // 
            this.dataGridViewTextBoxColumn2.HeaderText = "编号";
            this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
            this.dataGridViewTextBoxColumn2.ReadOnly = true;
            this.dataGridViewTextBoxColumn2.Resizable = System.Windows.Forms.DataGridViewTriState.False;
            this.dataGridViewTextBoxColumn2.Width = 90;
            // 
            // dataGridViewTextBoxColumn3
            // 
            this.dataGridViewTextBoxColumn3.HeaderText = "状态";
            this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
            this.dataGridViewTextBoxColumn3.Resizable = System.Windows.Forms.DataGridViewTriState.False;
            this.dataGridViewTextBoxColumn3.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
            this.dataGridViewTextBoxColumn3.Width = 90;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(328, 6);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(77, 12);
            this.label2.TabIndex = 11;
            this.label2.Text = "差集筛选列表";
            // 
            // ScreenForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(635, 242);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.dgvDifference);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.dgvScreen);
            this.Controls.Add(this.btnScreen);
            this.Controls.Add(this.btnClose);
            this.Controls.Add(this.btnDeleteClose);
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "ScreenForm";
            this.Text = "筛选器";
            this.Shown += new System.EventHandler(this.ScreenForm_Shown);
            ((System.ComponentModel.ISupportInitialize)(this.dgvScreen)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.dgvDifference)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button btnScreen;
        private System.Windows.Forms.Button btnClose;
        private System.Windows.Forms.Button btnDeleteClose;
        private System.Windows.Forms.DataGridView dgvScreen;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.DataGridViewTextBoxColumn colType;
        private System.Windows.Forms.DataGridViewTextBoxColumn colMin;
        private System.Windows.Forms.DataGridViewTextBoxColumn colMax;
        private System.Windows.Forms.DataGridView dgvDifference;
        private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
        private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
        private System.Windows.Forms.DataGridViewCheckBoxColumn dataGridViewTextBoxColumn3;
        private System.Windows.Forms.Label label2;
    }
}