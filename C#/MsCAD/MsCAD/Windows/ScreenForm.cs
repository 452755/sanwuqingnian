using MsCAD.DatabaseServices;
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
    public partial class ScreenForm : Form
    {
        internal Commands.Modify.ChooseCmd cmd;
        public ScreenForm()
        {
            InitializeComponent();
            InitializeDataGridView();//设置默认数据

            this.Owner = MainWin.Instance;
            DocumentForm activeDocForm = MainWin.Instance.ActiveMdiChild as DocumentForm;
            if (activeDocForm != null)
            {
                _docForm = activeDocForm;
            }
            else
            {
                _docForm = null;
            }
            MainWin.Instance.MdiChildActivate += this.OnDocumentFormActivated;

            cmd = new Commands.Modify.ChooseCmd();
            cmd.Data.Add("isState", 0);

            _docForm.presenter.OnCommand(cmd);
            InitializeDiffDataGridView();//显示图元数据
        }
        /// <summary>
        /// 文档
        /// </summary>
        private DocumentForm _docForm = null;
        /// <summary>
        /// 文档窗口激活相应函数
        /// </summary>
        private void OnDocumentFormActivated(object sender, EventArgs e)
        {
            DocumentForm docForm = MainWin.Instance.ActiveMdiChild as DocumentForm;
            if (docForm != null)
            {
                _docForm = docForm; ;
                if (this.Visible)
                {
                    //this.ReUpdateListView();
                }
            }
            else
            {
                _docForm = null;
                this.Hide();
            }
        }
        private void InitializeDataGridView()
        {
            int index = dgvScreen.Rows.Add();
            dgvScreen.Rows[index].Cells[0].Value = "圆形半径";
            dgvScreen.Rows[index].Cells[1].Value = "0";
            dgvScreen.Rows[index].Cells[2].Value = "0";
            index = dgvScreen.Rows.Add();
            dgvScreen.Rows[index].Cells[0].Value = "圆弧半径";
            dgvScreen.Rows[index].Cells[1].Value = "0";
            dgvScreen.Rows[index].Cells[2].Value = "0";
            index = dgvScreen.Rows.Add();
            dgvScreen.Rows[index].Cells[0].Value = "圆弧弧度(度)";
            dgvScreen.Rows[index].Cells[1].Value = "0";
            dgvScreen.Rows[index].Cells[2].Value = "0";
            index = dgvScreen.Rows.Add();
            dgvScreen.Rows[index].Cells[0].Value = "直线长度";
            dgvScreen.Rows[index].Cells[1].Value = "0";
            dgvScreen.Rows[index].Cells[2].Value = "0";
        }
        private void InitializeDiffDataGridView()
        {
            foreach (DBObject bock in cmd.database.blockTable)
            {
                foreach (Entity item in ((Block)bock).Items)
                {
                    int index = dgvDifference.Rows.Add();
                    dgvDifference.Rows[index].Cells[0].Value = item.className;
                    dgvDifference.Rows[index].Cells[1].Value = item.id.ToString();
                    if (item.regionState == 0)
                        dgvDifference.Rows[index].Cells[2].Value = false;
                    else
                        dgvDifference.Rows[index].Cells[2].Value = true;
                    dgvDifference.Rows[index].Selected = false;
                }
            }
        }
        public void RefreshDiffDataGridView(string id)
        {
            if (this.IsHandleCreated == true)
            {
                dgvDifference.Rows.Clear();
                InitializeDiffDataGridView();
                for (int i = 0; i < dgvDifference.Rows.Count; i++)
                {
                    if (id.Equals(dgvDifference.Rows[i].Cells[1].Value.ToString()))
                    {
                        dgvDifference.Rows[i].Selected = true;
                        return;
                    }
                }
            }
        }
        /// <summary>
        /// 单例
        /// </summary>
        private static ScreenForm _instance = null;
        private static int index = 0;
        internal static ScreenForm Instance
        {
            get
            {
                if (_instance == null)
                {
                    index++;
                    _instance = new ScreenForm();
                }

                return _instance;
            }
        }
        private void btnScreen_Click(object sender, EventArgs e)
        {
            Operation(0);
        }

        private void btnDeleteClose_Click(object sender, EventArgs e)
        {
            Operation(1);
            this.Close();
        }
        private void Operation(double isState)
        {
            try
            {
                double cirRadiusMin = double.Parse((string)dgvScreen.Rows[0].Cells[1].Value);
                double cirRadiusMax = double.Parse((string)dgvScreen.Rows[0].Cells[2].Value);
                double rayRadiusMin = double.Parse((string)dgvScreen.Rows[1].Cells[1].Value);
                double rayRadiusMax = double.Parse((string)dgvScreen.Rows[1].Cells[2].Value);
                double rayRadianMin = double.Parse((string)dgvScreen.Rows[2].Cells[1].Value);
                double rayRadianMax = double.Parse((string)dgvScreen.Rows[2].Cells[2].Value);
                double linMin = double.Parse((string)dgvScreen.Rows[3].Cells[1].Value);
                double linMax = double.Parse((string)dgvScreen.Rows[3].Cells[2].Value);

                if (rayRadianMin < 0 || rayRadianMin > 360 ||
                    rayRadianMax < 0 || rayRadianMax > 360)
                {
                    MessageBox.Show("弧度度数设置有误");
                    return;
                }

                cmd = new Commands.Modify.ChooseCmd();
                cmd.Data.Clear();
                cmd.Data.Add("cirRadiusMin", cirRadiusMin);
                cmd.Data.Add("cirRadiusMax", cirRadiusMax);
                cmd.Data.Add("rayRadiusMin", rayRadiusMin);
                cmd.Data.Add("rayRadiusMax", rayRadiusMax);
                cmd.Data.Add("rayRadianMin", rayRadianMin);
                cmd.Data.Add("rayRadianMax", rayRadianMax);
                cmd.Data.Add("linMin", linMin);
                cmd.Data.Add("linMax", linMax);
                cmd.Data.Add("isState", isState);
                _docForm.presenter.OnCommand(cmd);

            }
            catch (Exception ex)
            {
                MainWin.Instance.ShowMsg(TipsState.Error, ex.Message);
                MessageBox.Show("筛选数据设置有误");
            }
        }

        private void btnClose_Click(object sender, EventArgs e)
        {
            this.Close();
        }
        /// <summary>
        /// 第一次显示窗体事件
        /// </summary>
        private void ScreenForm_Shown(object sender, EventArgs e)
        {
            if (index == 1)
            {
                this.StartPosition = FormStartPosition.Manual;
                this.Location = new Point(
                    MainWin.Instance.Location.X + MainWin.Instance.Width / 2 - this.Width / 2,
                    MainWin.Instance.Location.Y + MainWin.Instance.Height / 2 - this.Height / 2);
            }
            else
            {
                this.StartPosition = FormStartPosition.Manual;
                this.Location = _location;
            }
        }

        private static Point _location;
        protected override void OnFormClosing(FormClosingEventArgs e)
        {
            _location = this.Location;
            MainWin.Instance.MdiChildActivate -= this.OnDocumentFormActivated;
            base.OnFormClosing(e);
            _instance = null;
        }

        private void dgvDifference_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.ColumnIndex == 2)//单击复选框时
            {
                DataGridViewCell dCell = dgvDifference.Rows[e.RowIndex].Cells[e.ColumnIndex];
                int mID = int.Parse(dgvDifference.Rows[dCell.RowIndex].Cells[1].Value.ToString());
                cmd = new Commands.Modify.ChooseCmd();
                cmd.Data.Clear();
                cmd.Data.Add("isState", 3);
                cmd.Data.Add("id", mID);

                //(bool)dCell.FormattedValue != (bool)dCell.EditedFormattedValue)//选中前 != 选中后
                if (!(bool)dCell.EditedFormattedValue)
                    cmd.Data.Add("regionState", 0);
                else
                    cmd.Data.Add("regionState", 1);

                _docForm.presenter.OnCommand(cmd);
            }
        }

        private void dgvDifference_CellClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.ColumnIndex == 0 || e.ColumnIndex == 1)//单击类型时
            {
                DataGridViewCell dCell = dgvDifference.Rows[e.RowIndex].Cells[e.ColumnIndex];
                int mID = int.Parse(dgvDifference.Rows[dCell.RowIndex].Cells[1].Value.ToString());
                cmd = new Commands.Modify.ChooseCmd();
                cmd.Data.Clear();
                cmd.Data.Add("isState", 4);
                cmd.Data.Add("id", mID);
                _docForm.presenter.OnCommand(cmd);
            }
        }
    }
}
