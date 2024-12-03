using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace MsCAD.Windows
{
    public partial class ButtonListForm : Form
    {
        List<string> G_list = new List<string>();

        public int SelectdIndex = -1;
        public ButtonListForm(string Title, List<string> list)
        {
            InitializeComponent();
            G_list = list;
            this.Text = Title;
            this.Height = G_list.Count / 2 * 60 + 30 + 40;
        }

        private void ButtonListForm_Load(object sender, EventArgs e)
        {
            this.Controls.Clear();
            for (int i = 0; i < G_list.Count; i++)
            {
                this.button1 = new System.Windows.Forms.Button();
                this.button1.Font = new System.Drawing.Font("微软雅黑", 10F);
                int row = i / 2;
                int col = i % 2;
                this.button1.Location = new System.Drawing.Point(30 + col * 150, 30 + row * 60);
                this.button1.Size = new System.Drawing.Size(120, 30);
                this.button1.Text = "F" + (i + 1).ToString() + "--" + G_list[i];
                this.button1.Tag = i;
                this.Controls.Add(this.button1);
                this.button1.Click += new System.EventHandler(this.button1_Click);
            }
        }

        protected override bool ProcessCmdKey(ref Message msg, Keys keyData)
        {
            try
            {
                int key = (int)keyData;
                if (keyData == Keys.F1 && this.Controls.Count > 0)
                {
                    Button btn = (Button)this.Controls[0];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F2 && this.Controls.Count >1 )
                {
                    Button btn = (Button)this.Controls[1];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F3 && this.Controls.Count >2)
                {
                    Button btn = (Button)this.Controls[2];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F4 && this.Controls.Count > 3)
                {
                    Button btn = (Button)this.Controls[3];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F5 && this.Controls.Count > 4)
                {
                    Button btn = (Button)this.Controls[4];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F6 && this.Controls.Count > 5)
                {
                    Button btn = (Button)this.Controls[5];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F7 && this.Controls.Count > 6)
                {
                    Button btn = (Button)this.Controls[6];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F8 && this.Controls.Count > 7)
                {
                    Button btn = (Button)this.Controls[7];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F9 && this.Controls.Count > 8)
                {
                    Button btn = (Button)this.Controls[8];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F10 && this.Controls.Count > 9)
                {
                    Button btn = (Button)this.Controls[9];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F11 && this.Controls.Count > 10)
                {
                    Button btn = (Button)this.Controls[10];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F12 && this.Controls.Count > 11)
                {
                    Button btn = (Button)this.Controls[11];
                    SelectdIndex = (int)btn.Tag;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }

                if (keyData == Keys.Escape)
                {
                    this.DialogResult = DialogResult.Cancel;
                    return true;
                }
                else
                {
                    return false;
                }
            }
            catch (Exception ex)
            {
                MainWin.Instance.ShowMsg(TipsState.Error, ex.Message);
                return false;
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Button btn = (Button)sender;
            SelectdIndex = (int)btn.Tag;
            this.DialogResult = DialogResult.OK;
        }
    }
}
