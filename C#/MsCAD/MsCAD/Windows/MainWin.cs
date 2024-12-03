using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Collections.ObjectModel;
using System.Windows.Forms;
using System.IO;
using System.Diagnostics;
using MsCAD.ApplicationServices;
using MsMath;

namespace MsCAD.Windows
{
    public partial class MainWin : Form
    {
        private HawkEyeForm _hawkEyeForm = null;
        private MainWin()
        {
            InitializeComponent();
            SetupToolStripUI();

            string[] args = Environment.GetCommandLineArgs();
            if (args.Length == 2)
            {
                OpenFile(args[1]);
            }
            else
            {
                NewFile();
            }

            Application.Idle += this.OnIdle;

            ////改为窗口显示
            _hawkEyeForm = HawkEyeForm.Instance;

            //在当前窗口显示
            //_canvasHawkEye = new CanvasHawkEye();
        }
        FileStream fs = null;
        /// <summary>
        /// 单例
        /// </summary>
        private static MainWin _instance = null;
        public static MainWin Instance
        {
            get
            {
                if (_instance == null)
                {
                    _instance = new MainWin();
                }
                return _instance;
            }
        }

        private ToolStripMgr _toolStripMgr = new ToolStripMgr();

        /// <summary>
        /// 设置菜单、工具条、状态条
        /// </summary>
        private void SetupToolStripUI()
        {
            // 主菜单
            MenuStrip menuMain = SetupMainMenu();
            this.MainMenuStrip = menuMain;

            // 工具条
            List<ToolStrip> toolStripList = SetupToolbar();

            // Top Panel
            ToolStripPanel topPanel = _toolStripMgr.GetToolStripPanle(DockStyle.Top, true);
            this.Controls.Add(topPanel);

            // 将主菜单和工具条添加到 Top Panel
            for (int i = toolStripList.Count - 1; i >= 0; --i)
            {
                topPanel.Join(toolStripList[i]);
            }
            topPanel.Join(menuMain);
        }

        /// <summary>
        /// 设置主菜单
        /// </summary>
        private MenuStrip SetupMainMenu()
        {
            MenuStrip menuMain = _toolStripMgr.GetMenuStrip("Main", true);

            SetupMainMenu_File(menuMain);

            return menuMain;
        }

        /// <summary>
        /// 文件菜单
        /// </summary>
        private void SetupMainMenu_File(MenuStrip menuMain)
        {
            ToolStripMenuItem menuFile = new ToolStripMenuItem();
            menuFile.Text = "文件";
            menuMain.Items.Add(menuFile);

            // 新建
            ToolStripMenuItem _new = _toolStripMgr.NewMenuItem(
                "file_new",
                "新建...",
                Resource1.file_new.ToBitmap(),
                this.OnFileNew);
            menuFile.DropDownItems.Add(_new);

            // 打开
            ToolStripMenuItem open = _toolStripMgr.NewMenuItem(
                "file_open",
                "打开...",
                Resource1.file_open.ToBitmap(),
                this.OnFileOpen);
            menuFile.DropDownItems.Add(open);

            // 保存
            ToolStripMenuItem save = _toolStripMgr.NewMenuItem(
                "file_save",
                "保存",
                Resource1.file_save.ToBitmap(),
                this.OnFileSave);
            menuFile.DropDownItems.Add(save);

            // 另存为
            ToolStripMenuItem saveas = _toolStripMgr.NewMenuItem(
                "file_saveas",
                "另存为...",
                Resource1.file_saveas.ToBitmap(),
                this.OnFileSaveAs);
            menuFile.DropDownItems.Add(saveas);

            // 保存
            ToolStripMenuItem saveImage = _toolStripMgr.NewMenuItem(
                "file_save",
                "保存成图片",
                Resource1.file_save.ToBitmap(),
                this.OnFileSaveImage);
            menuFile.DropDownItems.Add(saveImage);
            // 保存
            ToolStripMenuItem saveRegion = _toolStripMgr.NewMenuItem(
                "file_save",
                "保存成Region",
                Resource1.file_save.ToBitmap(),
                this.OnFileSaveRegion);
            menuFile.DropDownItems.Add(saveRegion);

            // 生成G代码
            ToolStripMenuItem createG = _toolStripMgr.NewMenuItem(
                "createG",
                "生成G代码...",
                Resource1.file_open.ToBitmap(),
                this.OnCreateGOpen);
            menuFile.DropDownItems.Add(createG);
        }

        /// <summary>
        /// 设置工具条
        /// </summary>
        private List<ToolStrip> SetupToolbar()
        {
            List<ToolStrip> toolStripList = new List<ToolStrip>();

            // 文件
            ToolStrip fileToolstrip = SetupToolbar_File();
            toolStripList.Add(fileToolstrip);

            // 编辑
            ToolStrip editToolstrip = SetupToolbar_Edit();
            toolStripList.Add(editToolstrip);

            // 绘制
            ToolStrip drawToolstrip = SetupToolbar_Draw();
            toolStripList.Add(drawToolstrip);

            // 修改
            ToolStrip modifyToolstrip = SetupToolbar_Modify();
            toolStripList.Add(modifyToolstrip);

            // 图层
            ToolStrip layerToolstrip = SetupToolbar_Layer();
            toolStripList.Add(layerToolstrip);

            // 特性
            ToolStrip propertyToolstrip = SetupToolbar_Property();
            toolStripList.Add(propertyToolstrip);

            return toolStripList;
        }

        /// <summary>
        /// 工具条: 文件
        /// </summary>
        private ToolStrip SetupToolbar_File()
        {
            ToolStrip fileToolstrip = _toolStripMgr.GetToolStrip("File", true);

            // 新建
            ToolStripButton _new = _toolStripMgr.NewToolStripButton("file_new");
            _new.Text = "";
            fileToolstrip.Items.Add(_new);

            // 打开
            ToolStripButton open = _toolStripMgr.NewToolStripButton("file_open");
            open.Text = "";
            fileToolstrip.Items.Add(open);

            // 保存
            ToolStripButton save = _toolStripMgr.NewToolStripButton("file_save");
            save.Text = "";
            fileToolstrip.Items.Add(save);

            // 另存为
            ToolStripButton saveas = _toolStripMgr.NewToolStripButton("file_saveas");
            saveas.Text = "";
            fileToolstrip.Items.Add(saveas);

            return fileToolstrip;
        }

        /// <summary>
        /// 工具条: 编辑
        /// </summary>
        private ToolStrip SetupToolbar_Edit()
        {
            ToolStrip editToolstrip = _toolStripMgr.GetToolStrip("Edit", true);

            return editToolstrip;
        }

        /// <summary>
        /// 工具条: 绘制
        /// </summary>
        private ToolStrip SetupToolbar_Draw()
        {
            ToolStrip drawToolstrip = _toolStripMgr.GetToolStrip("Draw", true);

            return drawToolstrip;
        }

        /// <summary>
        /// 工具条: 修改
        /// </summary>
        private ToolStrip SetupToolbar_Modify()
        {
            ToolStrip modifyToolstrip = _toolStripMgr.GetToolStrip("Modify", true);

            return modifyToolstrip;
        }

        /// <summary>
        /// 工具条: 图层
        /// </summary>
        private ToolStrip SetupToolbar_Layer()
        {
            ToolStrip layerToolstrip = _toolStripMgr.GetToolStrip("Layer", true);

            return layerToolstrip;
        }

        /// <summary>
        /// 工具条: 特性
        /// </summary>
        private ToolStrip SetupToolbar_Property()
        {
            ToolStrip propertyToolstrip = _toolStripMgr.GetToolStrip("Property", true);

            return propertyToolstrip;
        }

        /// <summary>
        /// 新建菜单项
        /// </summary>
        private ToolStripMenuItem NewMenuItem(
            string text,
            Image image,
            EventHandler eventHandler)
        {
            ToolStripMenuItem menuItem = new ToolStripMenuItem();
            menuItem.Text = text;
            menuItem.Image = image;
            menuItem.Click += eventHandler;
            return menuItem;
        }

        /// <summary>
        /// 新建文件
        /// </summary>
        private void OnFileNew(object sender, EventArgs e)
        {
            NewFile();
        }

        private void NewFile()
        {
            DocumentForm docForm = new DocumentForm();
            docForm.Text = GetNextNewFileName();
            docForm.MdiParent = this;
            docForm.WindowState = FormWindowState.Maximized;
            docForm.Show();
        }

        private string GetNextNewFileName()
        {
            string strBase = "new";
            uint id = 1;

            foreach (Form form in this.MdiChildren)
            {
                DocumentForm docForm = form as DocumentForm;
                if (docForm == null)
                {
                    continue;
                }

                string fileName = "";
                MsCAD.DatabaseServices.Database db = docForm.document.database;
                if (db.fileName != null)
                {
                    fileName = System.IO.Path.GetFileNameWithoutExtension(db.fileName);
                }
                else
                {
                    fileName = docForm.Text;
                }
                fileName = fileName.ToLower();

                if (fileName.IndexOf(strBase) == 0)
                {
                    fileName = fileName.Substring(strBase.Length);
                    uint number = 0;
                    if (uint.TryParse(fileName, out number))
                    {
                        if (number >= id)
                        {
                            id = number + 1;
                        }
                    }
                }
            }

            return string.Format("{0}{1}", strBase, id);
        }

        /// <summary>
        /// 打开文件
        /// </summary>
        private void OnFileOpen(object sender, EventArgs e)
        {
            OpenFileDialog ofd = new OpenFileDialog();
            ofd.Filter = "CAD格式(*.dxf)|*.dxf|MsCAD文件(*.mscad)|*.mscad|Gerber文件(*.art;*.gbr)|*.art;*.gbr";
            ofd.ValidateNames = true;
            ofd.CheckPathExists = true;
            ofd.CheckFileExists = true;
            if (ofd.ShowDialog() == DialogResult.OK)
            {
                string strFileFullPath = ofd.FileName;
                OpenFile(strFileFullPath);
            }
            activeDocForm = null;
            this.OnMdiChildActivate(e);
        }

        private void OpenFile(string fileFullPath)
        {
            // 检查是否已经打开
            string fileFullPathLower = fileFullPath.ToLower();
            foreach (Form form in this.MdiChildren)
            {
                DocumentForm childDocForm = form as DocumentForm;
                if (childDocForm == null)
                {
                    continue;
                }

                string strDocPath = childDocForm.fileFullPath.ToLower();
                if (fileFullPathLower == strDocPath)
                {
                    childDocForm.Activate();
                    return;
                }
            }

            // 打开文件
            DocumentForm docForm = new DocumentForm();
            if (fileFullPath != null
                && System.IO.File.Exists(fileFullPath))
            {
                if (docForm.Open(fileFullPath) == false)
                {
                    MessageBox.Show("文件格式不正确");
                    return;
                }
            }
            docForm.MdiParent = this;
            docForm.WindowState = FormWindowState.Maximized;
            docForm.Show();
        }

        /// <summary>
        /// 保存文件
        /// </summary>
        private void OnFileSave(object sender, EventArgs e)
        {
            DocumentForm activeDocForm = this.ActiveMdiChild as DocumentForm;
            if (activeDocForm == null)
            {
                return;
            }

            MsCAD.DatabaseServices.Database db = activeDocForm.document.database;
            if (db.fileName == null)
            {
                SaveFileDialog savedialog = new SaveFileDialog();
                savedialog.Title = "保存";
                savedialog.Filter = "CAD格式(*.dxf)|*.dxf|MsCAD文件(*.mscad)|*.mscad";
                savedialog.FilterIndex = 0;
                savedialog.RestoreDirectory = true;
                savedialog.CheckPathExists = true;
                savedialog.FileName = activeDocForm.Text;
                if (savedialog.ShowDialog() == DialogResult.OK)
                {
                    string fileFullPath = savedialog.FileName;
                    activeDocForm.SaveAs(fileFullPath, true);
                }
            }
            else
            {
                activeDocForm.Save();
            }
        }
        /// <summary>
        /// 保存文件
        /// </summary>
        private void OnFileSaveImage(object sender, EventArgs e)
        {
            DocumentForm activeDocForm = this.ActiveMdiChild as DocumentForm;
            if (activeDocForm == null)
            {
                return;
            }
            
            SaveFileDialog savedialog = new SaveFileDialog();
            savedialog.Title = "保存";
            savedialog.Filter = "图像(*.png)|*.png";
            savedialog.FilterIndex = 0;
            savedialog.RestoreDirectory = true;
            savedialog.CheckPathExists = true;
            if (activeDocForm.Text.Contains("."))
                savedialog.FileName = activeDocForm.Text.Substring(0, (activeDocForm.Text).LastIndexOf('.'))+".png";
            else
                savedialog.FileName = activeDocForm.Text;

            if (savedialog.ShowDialog() == DialogResult.OK)
            {
                string fileFullPath = savedialog.FileName;
                //保存视野显示的图形
                Bitmap mGraphics = activeDocForm.presenter.graphicsSave;
                //mGraphics.Save(fileFullPath);
                //获取数据，提取区域，把区域放到窗口上，再把窗口转成图像，之后保存
                activeDocForm.SaveAsImage(fileFullPath);

                //Graphics mGraphics = activeDocForm.presenter.graphicsSave;
                //Image imgTemp = new Bitmap((int)mGraphics.VisibleClipBounds.Size.Width, (int)mGraphics.VisibleClipBounds.Height, mGraphics);
                //Graphics g = Graphics.FromImage(imgTemp);
                //g.DrawImage(imgTemp,0,0,(int)mGraphics.VisibleClipBounds.Size.Width, (int)mGraphics.VisibleClipBounds.Height);
                //imgTemp.Save(fileFullPath);
                //g.Dispose();
            }
        }
        /// <summary>
        /// 保存文件
        /// </summary>
        private void OnFileSaveRegion(object sender, EventArgs e)
        {
            DocumentForm activeDocForm = this.ActiveMdiChild as DocumentForm;
            if (activeDocForm == null)
            {
                return;
            }

            SaveFileDialog savedialog = new SaveFileDialog();
            savedialog.Title = "保存";
            savedialog.Filter = "区域(*.hobj)|*.hobj";
            savedialog.FilterIndex = 0;
            savedialog.RestoreDirectory = true;
            savedialog.CheckPathExists = true;
            if(activeDocForm.Text.Contains("."))
                savedialog.FileName = activeDocForm.Text.Substring(0, (activeDocForm.Text).LastIndexOf('.')) + ".hobj";
            else
                savedialog.FileName = activeDocForm.Text;

            if (savedialog.ShowDialog() == DialogResult.OK)
            {
                string fileFullPath = savedialog.FileName;
                activeDocForm.SaveRegion(fileFullPath, true);
            }
        }

        /// <summary>
        /// 文件另存为
        /// </summary>
        private void OnFileSaveAs(object sender, EventArgs e)
        {
            DocumentForm activeDocForm = this.ActiveMdiChild as DocumentForm;
            if (activeDocForm == null)
            {
                return;
            }

            SaveFileDialog savedialog = new SaveFileDialog();
            savedialog.Title = "另存为";
            savedialog.Filter = "CAD格式(*.dxf)|*.dxf|MsCAD文件(*.mscad)|*.mscad";
            savedialog.FilterIndex = 0;
            savedialog.RestoreDirectory = true;
            savedialog.CheckPathExists = true;
            savedialog.FileName = "";
            if (savedialog.ShowDialog() == DialogResult.OK)
            {
                string fileFullPath = savedialog.FileName;
                activeDocForm.SaveAs(fileFullPath);
            }
        }
        /// <summary>
        /// 生成G代码
        /// </summary>
        private void OnCreateGOpen(object sender, EventArgs e)
        {
            DocumentForm activeDocForm = this.ActiveMdiChild as DocumentForm;
            if (activeDocForm == null)
            {
                return;
            }

            ObservableCollection<string> GCode = activeDocForm.document.database.CreateG();
            if (GCode == null)
                return;
            var saveFileDialog = new SaveFileDialog { FileName = Path.GetFileNameWithoutExtension("D:\\NF\\CAD\\001") + ".txt", Filter = "TXT Files|*.txt" };
            using (var sw = new StreamWriter(saveFileDialog.FileName))
            {
                foreach (var line in GCode)
                {
                    sw.WriteLine(line);
                }
                Process.Start(saveFileDialog.FileName);
            }
        }
        private DocumentForm activeDocForm = null;
        private int hawkEyeWidth = 200;
        private int hawkEyeHeight = 0;
        /// <summary>
        /// 子窗体激活事件
        /// </summary>
        protected override void OnMdiChildActivate(EventArgs e)
        {
            base.OnMdiChildActivate(e);

            activeDocForm = this.ActiveMdiChild as DocumentForm;
            foreach (Control ctrl in Controls)
            {
                if (ctrl is ToolStripPanel)
                    ((ToolStripPanel)ctrl).SuspendLayout();
            }
            if (activeDocForm != null)
            {
                ToolStripManager.RevertMerge(_toolStripMgr.GetToolStrip("Edit"));
                ToolStripManager.Merge(activeDocForm.toolstripMgr.GetToolStrip("Edit"), _toolStripMgr.GetToolStrip("Edit"));

                ToolStripManager.RevertMerge(_toolStripMgr.GetToolStrip("Draw"));
                ToolStripManager.Merge(activeDocForm.toolstripMgr.GetToolStrip("Draw"), _toolStripMgr.GetToolStrip("Draw"));

                ToolStripManager.RevertMerge(_toolStripMgr.GetToolStrip("Modify"));
                ToolStripManager.Merge(activeDocForm.toolstripMgr.GetToolStrip("Modify"), _toolStripMgr.GetToolStrip("Modify"));

                ToolStripManager.RevertMerge(_toolStripMgr.GetToolStrip("Layer"));
                ToolStripManager.Merge(activeDocForm.toolstripMgr.GetToolStrip("Layer"), _toolStripMgr.GetToolStrip("Layer"));

                ToolStripManager.RevertMerge(_toolStripMgr.GetToolStrip("Property"));
                ToolStripManager.Merge(activeDocForm.toolstripMgr.GetToolStrip("Property"), _toolStripMgr.GetToolStrip("Property"));
                
                if (activeDocForm.document.database.zoomCanvasHawkEye != -1)
                {
                    _hawkEyeForm.zoomCanvasHawkEye = activeDocForm.document.database.zoomCanvasHawkEye;
                    _hawkEyeForm.screenPanHawkEye = activeDocForm.document.database.screenPanHawkEye;
                }
                hawkEyeHeight = (int)(200 * activeDocForm.Canvas.height / activeDocForm.Canvas.width);
                _hawkEyeForm.CanvasHawkEye.Size = new Size(hawkEyeWidth, hawkEyeHeight);//让鹰眼和视野等比例显示
                _hawkEyeForm.Location = new Point(1000, 90);
                _hawkEyeForm.document = activeDocForm.document;//设置数据
                _hawkEyeForm.TopMost = true;
                _hawkEyeForm.HawkEyeInit();


                _hawkEyeForm.CanvasHawkEye.changedHawk -= this.OnSelectionChangedHawk;//先清再加，去除重复添加，有多个子窗口时OnMdiChildActivate会执行多次
                _hawkEyeForm.CanvasHawkEye.changedHawk += this.OnSelectionChangedHawk;//
                activeDocForm.Canvas.changedCanvas -= this.OnSelectionChangedCanvas;
                activeDocForm.Canvas.changedCanvas += this.OnSelectionChangedCanvas;

                _hawkEyeForm.Show();

                _hawkEyeForm.CanvasHawkEye.Invalidate();
            }
            else
            {
                _hawkEyeForm.Visible = false;
            }
            foreach (Control ctrl in Controls)
            {
                if (ctrl is ToolStripPanel)
                    ((ToolStripPanel)ctrl).ResumeLayout();
            }
        }
        public void OnSelectionChangedHawk()//更新视野
        {
            Vector2 vHawkEyeStart = _hawkEyeForm.presenter.pointer.VHawkEyeStart;
            Vector2 vHawkEye = _hawkEyeForm.presenter.pointer.vHawkEye;
            Vector2 screenPan = activeDocForm.presenter.screenPan;
            activeDocForm.presenter.screenPan = new Vector2(
                screenPan.x - (vHawkEye.x- vHawkEyeStart.x)/0.03, 
                screenPan.y - (vHawkEye.y - vHawkEyeStart.y) /0.03 );

            activeDocForm.document.database.screenPanHawkEye = _hawkEyeForm.presenter.pointer.vHawkEye;
            activeDocForm.document.database.zoomCanvasHawkEye = _hawkEyeForm.presenter.pointer.zoomCanvas;
            activeDocForm.Canvas.Invalidate();
        }
        private Vector2 vHawkEyeStart = new Vector2(0,0);
        public void OnSelectionChangedCanvas()//更新鹰眼
        {
            Vector2 vHawkEye = _hawkEyeForm.presenter.pointer.vHawkEye;//相当于原点，当前鹰眼矩形框显示的位置
            Vector2 screenPanStart = activeDocForm.presenter.screenPanStart;//视野的位移前原点坐标
            Vector2 screenPan = activeDocForm.presenter.screenPan;//视野的位移后的坐标
            double zoom = activeDocForm.presenter.zoom;//视野缩放后的倍率
            _hawkEyeForm.presenter.pointer.zoomCanvas = zoom;
            _hawkEyeForm.presenter.pointer.vHawkEye = new Vector2(
                vHawkEye.x - (screenPan.x - screenPanStart.x) * 0.03, 
                vHawkEye.y - (screenPan.y - screenPanStart.y) * 0.03);
            _hawkEyeForm.CanvasHawkEye.Invalidate();
        }
        public void OnSelectionChangedScreen()
        {

        }
        /// <summary>
        /// 空闲事件
        /// </summary>
        private void OnIdle(object sender, EventArgs e)
        {
            DocumentForm currActiveDocForm = this.ActiveMdiChild as DocumentForm;
            if (currActiveDocForm != null)
            {
                currActiveDocForm.UpdateUI();
            }
        }
        private void MainWin_Resize(object sender, EventArgs e)
        {
            if (this.WindowState == FormWindowState.Minimized)
            {
                _hawkEyeForm.Visible = false;
            }
            else if (this.WindowState == FormWindowState.Maximized)
            {
                _hawkEyeForm.Visible = true;
            }
        }

        /// <summary>
        /// 0--Error，1--Success，2--Info，3--Warning
        /// </summary>
        /// <param name="msg"></param>
        public void ShowMsg(TipsState ts,string msg)
        {
            switch(ts)
            {
                case TipsState.Error:
                    FrmTips.ShowTipsError(this, msg);
                    break;

                case TipsState.Info:
                    FrmTips.ShowTipsInfo(this, msg);
                    break;

                case TipsState.Success:
                    FrmTips.ShowTipsSuccess(this, msg);
                    break;

                case TipsState.Warning:
                    FrmTips.ShowTipsWarning(this, msg);
                    break;
            }            
        }
    }
}
