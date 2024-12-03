using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

using MsCAD.ApplicationServices;
using MsCAD.DatabaseServices;

namespace MsCAD.Windows
{
    public partial class DxfDataForm : Form
    {
        /// <summary>
        /// 构造函数
        /// </summary>
        private DxfDataForm()
        {
            InitializeComponent();
            Init();
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
        }

        /// <summary>
        /// 单例
        /// </summary>
        private static DxfDataForm _instance = null;
        private static int index = 0;
        internal static DxfDataForm Instance
        {
            get
            {
                if (_instance == null)
                {
                    index++;
                    _instance = new DxfDataForm();
                }
                
                return _instance;
            }
        }

        /// <summary>
        /// 文件名
        /// </summary>
        private string _fileName = null;
        public string fileName
        {
            get { return _fileName; }
            set { _fileName = value; }
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
                    this.ReUpdateListView();
                }
            }
            else
            {
                _docForm = null;
                this.Hide();
            }
        }

        private void Init()
        {
           
        }

        private void ReUpdateListView()
        {
            try
            {
                List<EntitiesTotal> listEntities = DxfHelper.Import(fileName);
                this.dgvEntities.DataSource = null;
                this.dgvEntities.DataSource = listEntities;//线列表，数据绑定
                for (int i = 0; i < this.dgvEntities.Columns.Count; i++)
                {
                    this.dgvEntities.Columns[i].AutoSizeMode = DataGridViewAutoSizeColumnMode.AllCells;//自动扩展
                }
                this.dgvEntities.Columns["Id"].Visible = false;
                this.dgvEntities.Columns["TypeName"].Visible = false;
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }

        }




        protected override void OnVisibleChanged(EventArgs e)
        {
            base.OnVisibleChanged(e);

            if (this.Visible)
            {
                this.ReUpdateListView();
            }
            else
            {
                this.dgvEntities.DataSource = null;
                this.dataGridView3.DataSource = null;
            }
        }

        /// <summary>
        /// 第一次显示窗体事件
        /// </summary>
        private void LayersManagementForm_Shown(object sender, EventArgs e)
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

        private void dgvEntities_SelectionChanged(object sender, EventArgs e)
        {
            try
            {
                if (this.dgvEntities.SelectedRows.Count != 1)
                {
                    return;
                }
                DataGridViewRow dr = this.dgvEntities.SelectedRows[0];
                EntitiesTotal entities = new EntitiesTotal();
                entities.Id = dr.Cells["Id"].Value.ToString();
                entities.Name = dr.Cells["Name"].Value.ToString();
                entities.TypeName = dr.Cells["TypeName"].Value.ToString();
               
                this.dataGridView3.DataSource = null;
                this.dataGridView3.DataSource = DxfHelper.GetList(entities);
            }
            catch (Exception ex)
            {

            }
        }
    }
}
