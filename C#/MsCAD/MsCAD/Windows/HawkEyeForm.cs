using MsCAD.ApplicationServices;
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
using MsMath;

namespace MsCAD.Windows
{
    public partial class HawkEyeForm : Form
    {
        private CanvasHawkEye _canvasHawkEye = null;
        private Document _document = null;
        internal Document document
        {
            get { return _document; }
            set { _document = value; }
        }
        private PresenterHawkEye _presenter = null;
        internal PresenterHawkEye presenter
        {
            get { return _presenter; }
        }

        private Database database
        {
            get { return _document.database; }
        }
        public HawkEyeForm()
        {
            InitializeComponent();

            //this.Owner = MainWin.Instance;
            //DocumentForm activeDocForm = MainWin.Instance.ActiveMdiChild as DocumentForm;
            //if (activeDocForm != null)
            //{
            //    _docForm = activeDocForm;
            //}
            //else
            //{
            //    _docForm = null;
            //}
            //MainWin.Instance.MdiChildActivate += this.OnDocumentFormActivated;

            _canvasHawkEye = new CanvasHawkEye();
            _document = new Document();

        }
        private Vector2 _screenPanHawkEye = new Vector2();//记录鹰眼对应当前视野的坐标
        private double _zoomCanvasHawkEye = -1;//记录当前鹰眼的缩放系数
        public Vector2 screenPanHawkEye
        {
            get { return _screenPanHawkEye; }
            set { _screenPanHawkEye = value; }
        }
        public double zoomCanvasHawkEye
        {
            get { return _zoomCanvasHawkEye; }
            set { _zoomCanvasHawkEye = value; }
        }
        public void HawkEyeInit()
        {
            _presenter = new PresenterHawkEye(_canvasHawkEye, _document);
            _presenter.documentHawk = _document;
            if (_zoomCanvasHawkEye != -1)
            {
                _presenter.pointer.zoomCanvas  = _zoomCanvasHawkEye;
                _presenter.pointer.vHawkEye = _screenPanHawkEye;
            }

            //_canvasHawkEye.Dock = DockStyle.Fill;
            this.Controls.Add(_canvasHawkEye);

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
        /// <summary>
        /// 单例
        /// </summary>
        private static HawkEyeForm _instance = null;
        private static int index = 0;
        internal static HawkEyeForm Instance
        {
            get
            {
                if (_instance == null)
                {
                    index++;
                    _instance = new HawkEyeForm();
                }
                //_instance.location = new point(200, 200);
                return _instance;
            }
        }

        internal CanvasHawkEye CanvasHawkEye
        {
            get
            {
                return _canvasHawkEye;
            }

            set
            {
                _canvasHawkEye = value;
            }
        }
    }
}
