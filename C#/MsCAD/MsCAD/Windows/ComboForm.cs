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
    public partial class ComboForm : Form
    {
        List<string> G_list = new List<string>();

        public int SelectdIndex = -1;
        public ComboForm(string Title, List<string> list)
        {
            InitializeComponent();
            G_list = list;
            this.Text = Title;
        }

        private void ComboForm_Load(object sender, EventArgs e)
        {
            foreach (string str in G_list)
            {
                this.comboBox1.Items.Add(str);
            }
            this.comboBox1.SelectedIndex = 0;
        }

        protected override bool ProcessCmdKey(ref Message msg, Keys keyData)
        {
            try
            {
                int key = (int)keyData;
                if (keyData == Keys.Enter)//空格
                {
                    SelectdIndex = this.comboBox1.SelectedIndex;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                else if(keyData == Keys.Escape)
                {
                    SelectdIndex = this.comboBox1.SelectedIndex;
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
                return false;
            }
        }

    }
}
