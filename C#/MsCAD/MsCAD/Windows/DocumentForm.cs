﻿using System;
using System.Windows.Forms;
using System.Collections.ObjectModel;

using MsCAD.DatabaseServices;
using MsCAD.ApplicationServices;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Threading;

namespace MsCAD.Windows
{
    public partial class DocumentForm : Form
    {
        private ToolStripMgr _toolStripMgr = new ToolStripMgr();
        public ToolStripMgr toolstripMgr
        {
            get { return _toolStripMgr; }
        }

        private Canvas _canvas = null;
        internal Canvas Canvas
        {
            get
            {
                return _canvas;
            }
            set
            {
                _canvas = value;
            }
        }
        private ScreenForm _screenForm = null;
        private Document _document = null;
        internal Document document
        {
            get { return _document; }
        }
        private Presenter _presenter = null;
        internal Presenter presenter
        {
            get { return _presenter; }
        }

        public Database database
        {
            get { return _document.database; }
        }

        public DocumentForm()
        {
            InitializeComponent();

            //
            _canvas = new Canvas();
            _document = new Document();
            _presenter = new Presenter(_canvas, _document);

            _canvas.Dock = DockStyle.Fill;
            this.Controls.Add(_canvas);

            //
            SetupToolStripUI();

            //
            _document.database.layerTable.itemAdded += this.OnAddLayer;
            _document.database.layerTable.itemRemoved += this.OnRemoveLayer;
            _document.currentLayerChanged += this.OnDocumentCurrLayerChanged;
            _document.currentColorChanged += this.OnDocumentCurrColorChanged;


            _canvas.changedScreen -= this.RefreshDiffDataGridView;
            _canvas.changedScreen += this.RefreshDiffDataGridView;

            //
            //Layer layer = new Layer("test");
            //layer.color = MsCAD.Colors.Color.FromRGB(255, 0, 0);
            //_document.database.layerTable.Add(layer);
        }
        public void RefreshDiffDataGridView()
        {
            if (_screenForm != null)
            {
                _screenForm.RefreshDiffDataGridView(_presenter.pointer.currentId);
            }
        }

        /// <summary>
        /// 打开文件
        /// </summary>
        internal bool Open(string fileFullPath)
        {
            bool isOK = _document.database.Open(fileFullPath);//文件解析绘制
            if (isOK == false)
            {
                return false;
            }
            if (_document.database != null && _document.database.fileName != null)
            {
                this.Text = _document.database.fileName;
            }
            return true;
        }
        /// <summary>
        /// 生成G代码
        /// </summary>
        internal ObservableCollection<string> CreateG()
        {
            return _document.database.CreateG();
        }

        /// <summary>
        /// 保存文件
        /// </summary>
        internal void Save()
        {
            _document.database.Save();
            if (_document.database != null
                && _document.database.fileName != null)
            {
                this.Text = _document.database.fileName;
            }
        }

        /// <summary>
        /// 另存为
        /// </summary>
        internal void SaveAs(string fileFullPath, bool rename = false)
        {
            try
            {
                _document.database.SaveAs(fileFullPath, rename);
                if (_document.database != null
                    && _document.database.fileName != null)
                {
                    this.Text = _document.database.fileName;
                }
            }
            catch (Exception ex)
            {
                MainWin.Instance.ShowMsg(TipsState.Error, ex.Message);
            }
        }
        internal void SaveAsImage(string fileFullPath, bool rename = false)
        {
            _document.database.SaveAsImage(fileFullPath, rename);
        }
        internal void SaveRegion(string fileFullPath, bool rename = false)
        {
            _document.database.SaveRegion(fileFullPath, rename);
        }

        /// <summary>
        /// 文件全路径
        /// </summary>
        internal string fileFullPath
        {
            get
            {
                if (_document.database != null
                    && _document.database.fileName != null)
                {
                    return _document.database.fileName;
                }
                else
                {
                    return this.Text;
                }
            }
        }


        /// <summary>
        /// 设置菜单、工具条、状态条
        /// </summary>
        private void SetupToolStripUI()
        {
            //
            MenuStrip menuMain = SetupMainMenu();

            menuMain.Visible = false;
            this.MainMenuStrip = menuMain;
            this.Controls.Add(menuMain);

            //
            SetupToolbar();
        }

        /// <summary>
        /// 设置主菜单
        /// </summary>
        /// <returns></returns>
        private MenuStrip SetupMainMenu()
        {
            MenuStrip mainMenu = _toolStripMgr.GetMenuStrip("Main", true);

            // 菜单: 编辑
            ToolStripMenuItem menuEdit = SetupMainMenu_Edit();
            mainMenu.Items.Add(menuEdit);

            // 菜单: 格式
            ToolStripMenuItem menuFormat = SetupMainMenu_Format();
            mainMenu.Items.Add(menuFormat);

            // 菜单: 绘图
            ToolStripMenuItem menuDraw = SetupMainMenu_Draw();
            mainMenu.Items.Add(menuDraw);

            // 菜单: 修改
            ToolStripMenuItem menuModify = SetupMainMenu_Modify();
            mainMenu.Items.Add(menuModify);

            ToolStripMenuItem menuData = new ToolStripMenuItem();
            menuData.Text = "图形数据";
            menuData.Click += new EventHandler(menuDataClick);
            mainMenu.Items.Add(menuData);

            return mainMenu;
        }



        /// <summary>
        /// 设置菜单: 编辑
        /// </summary>
        private ToolStripMenuItem SetupMainMenu_Edit()
        {
            ToolStripMenuItem menuEdit = new ToolStripMenuItem();
            menuEdit.Text = "编辑";

            // 撤销
            ToolStripMenuItem undo = _toolStripMgr.NewMenuItem(
                "edit_undo",
                "撤销",
                Resource1.edit_undo.ToBitmap(),
                this.OnEditUndo);
            undo.ShortcutKeys = Keys.Control | Keys.Z;
            menuEdit.DropDownItems.Add(undo);

            // 重做
            ToolStripMenuItem redo = _toolStripMgr.NewMenuItem(
                "edit_redo",
                "重做",
                Resource1.edit_redo.ToBitmap(),
                this.OnEditRedo);
            redo.ShortcutKeys = Keys.Control | Keys.Y;
            menuEdit.DropDownItems.Add(redo);

            // 筛选器
            ToolStripMenuItem screen = _toolStripMgr.NewMenuItem(
                "format_layer",
                "筛选",
                Resource1.edit_redo.ToBitmap(),
                this.OnScreen);
            screen.ShortcutKeys = Keys.Control | Keys.F;
            menuEdit.DropDownItems.Add(screen);

            return menuEdit;
        }

        /// <summary>
        /// 设置菜单: 格式
        /// </summary>
        private ToolStripMenuItem SetupMainMenu_Format()
        {
            ToolStripMenuItem menuFormat = new ToolStripMenuItem();
            menuFormat.Text = "格式";
            ToolStripMenuItem menuLayer = _toolStripMgr.NewMenuItem("format_layer", "图层", Resource1.format_layer, this.OnFormatLayer);
            menuFormat.DropDownItems.Add(menuLayer);
            ToolStripMenuItem quicLayer = _toolStripMgr.NewMenuItem("quic_layer", "快速建层", Resource1.format_layer, this.OnQuicLayer);
            menuFormat.DropDownItems.Add(quicLayer);

            return menuFormat;
        }

        /// <summary>
        /// 图层特性管理器
        /// </summary>
        private void OnFormatLayer(object sender, EventArgs e)
        {
            LayersManagementForm.Instance.Show();
        }

        private void OnQuicLayer(object sender, EventArgs e)
        {
            if (this.database.layerTable.Count == 1)
            {
                Layer _layer = new Layer();
                _layer.name = "1";
                _layer.color = MsCAD.Colors.Color.FromRGB(255, 255, 255);
                this.database.layerTable.Add(_layer);//快速添加
                Thread.Sleep(500);
                _toolstripLayerCombo.SelectedIndex = _toolstripLayerCombo.Items.Count - 1;
            }
        }

        /// <summary>
        /// 设置菜单: 绘图
        /// </summary>
        private ToolStripMenuItem SetupMainMenu_Draw()
        {
            ToolStripMenuItem menuDraw = new ToolStripMenuItem();
            menuDraw.Text = "绘图";

            // 直线
            ToolStripMenuItem lines = _toolStripMgr.NewMenuItem(
                "draw_lines",
                "直线",
                Resource1.draw_line.ToBitmap(),
                this.OnDrawLines);
            menuDraw.DropDownItems.Add(lines);

            // 射线
            ToolStripMenuItem ray = _toolStripMgr.NewMenuItem(
                "draw_ray",
                "射线",
                Resource1.draw_ray.ToBitmap(),
                this.OnDrawRay);
            menuDraw.DropDownItems.Add(ray);

            // 构造线
            ToolStripMenuItem xline = _toolStripMgr.NewMenuItem(
                "draw_xline",
                "构造线",
                Resource1.draw_xline.ToBitmap(),
                this.OnDrawXLine);
            menuDraw.DropDownItems.Add(xline);

            // 多段线
            ToolStripMenuItem polyline = _toolStripMgr.NewMenuItem(
                "draw_polyline",
                "多段线",
                Resource1.draw_polyline.ToBitmap(),
                this.OnDrawPolyline);
            menuDraw.DropDownItems.Add(polyline);

            // 正多边形
            ToolStripMenuItem polygon = _toolStripMgr.NewMenuItem(
                "draw_polygon",
                "正多边形",
                Resource1.draw_polygon.ToBitmap(),
                this.OnDrawPolygon);
            menuDraw.DropDownItems.Add(polygon);

            // 矩形
            ToolStripMenuItem rectangle = _toolStripMgr.NewMenuItem(
                "draw_rectangle",
                "矩形",
                Resource1.draw_rectangle.ToBitmap(),
                this.OnDrawRectangle);
            menuDraw.DropDownItems.Add(rectangle);
            // 带角度矩形
            ToolStripMenuItem rectangle2 = _toolStripMgr.NewMenuItem(
                "draw_rectangle2",
                "带角度矩形",
                Resource1.draw_rectangle.ToBitmap(),
                this.OnDrawRectangle2);
            menuDraw.DropDownItems.Add(rectangle2);

            // 圆
            ToolStripMenuItem circle = _toolStripMgr.NewMenuItem(
                "draw_circle",
                "圆",
                Resource1.draw_circle_cr.ToBitmap(),
                this.OnDrawCircle);
            menuDraw.DropDownItems.Add(circle);

            // 圆弧
            ToolStripMenuItem arc = _toolStripMgr.NewMenuItem("draw_arc", "圆弧", Resource1.draw_arc_cse.ToBitmap(), this.OnDrawArc);
            menuDraw.DropDownItems.Add(arc);

            // 文本
            ToolStripMenuItem mtext = _toolStripMgr.NewMenuItem("draw_mtext", "文本", Resource1.draw_mtext.ToBitmap(), this.OnDrawMText);
            menuDraw.DropDownItems.Add(mtext);

            // 标注
            ToolStripMenuItem dim = _toolStripMgr.NewMenuItem("draw_dim", "标注", Resource1.draw_dim.ToBitmap(), this.OnDrawDimension);
            menuDraw.DropDownItems.Add(dim);
            return menuDraw;
        }

        /// <summary>
        /// 设置菜单: 修改
        /// </summary>
        private ToolStripMenuItem SetupMainMenu_Modify()
        {
            ToolStripMenuItem menuModify = new ToolStripMenuItem();
            menuModify.Text = "修改";

            // 删除
            ToolStripMenuItem erase = _toolStripMgr.NewMenuItem(
                "modify_erase",
                "删除",
                Resource1.modify_erase.ToBitmap(),
                this.OnModifyErase);
            menuModify.DropDownItems.Add(erase);

            // 复制
            ToolStripMenuItem copy = _toolStripMgr.NewMenuItem(
                "modify_copy",
                "复制",
                Resource1.modify_copy.ToBitmap(),
                this.OnModifyCopy);
            menuModify.DropDownItems.Add(copy);

            // 镜像
            ToolStripMenuItem mirror = _toolStripMgr.NewMenuItem(
                "modify_mirror",
                "镜像",
                Resource1.modify_mirror.ToBitmap(),
                this.OnModifyMirror);
            menuModify.DropDownItems.Add(mirror);

            // 偏移
            ToolStripMenuItem offset = _toolStripMgr.NewMenuItem(
                "modify_offset",
                "偏移",
                Resource1.modify_offset.ToBitmap(),
                this.OnModifyOffset);
            menuModify.DropDownItems.Add(offset);

            // 移动
            ToolStripMenuItem move = _toolStripMgr.NewMenuItem("modify_move", "移动", Resource1.modify_move.ToBitmap(), this.OnModifyMove);
            menuModify.DropDownItems.Add(move);

            return menuModify;
        }

        /// <summary>
        /// 设置工具条
        /// </summary>
        private void SetupToolbar()
        {
            SetupToolbar_Edit();
            SetupToolbar_Draw();
            SetupToolbar_Modify();
            SetupToolbar_Layer();
            SetupToolbar_Property();
        }

        /// <summary>
        /// 设置工具条: 编辑
        /// </summary>
        private ToolStripButton _undoToolstripItem = null;
        private ToolStripButton _redoToolstripItem = null;
        private ToolStrip SetupToolbar_Edit()
        {
            ToolStrip editToolstrip = _toolStripMgr.GetToolStrip("Edit");

            // 撤销
            ToolStripButton undo = _toolStripMgr.NewToolStripButton("edit_undo");
            editToolstrip.Items.Add(undo);
            _undoToolstripItem = undo;

            // 重做
            ToolStripButton redo = _toolStripMgr.NewToolStripButton("edit_redo");
            editToolstrip.Items.Add(redo);
            _redoToolstripItem = redo;

            return editToolstrip;
        }

        /// <summary>
        /// 设置工具条: 绘制
        /// </summary>
        private ToolStrip SetupToolbar_Draw()
        {
            ToolStrip drawToolstrip = _toolStripMgr.GetToolStrip("Draw");

            // 直线
            ToolStripButton lines = _toolStripMgr.NewToolStripButton("draw_lines");
            drawToolstrip.Items.Add(lines);

            // 构造线
            ToolStripButton xline = _toolStripMgr.NewToolStripButton("draw_xline");
            drawToolstrip.Items.Add(xline);

            // 多段线
            ToolStripButton polyline = _toolStripMgr.NewToolStripButton("draw_polyline");
            drawToolstrip.Items.Add(polyline);

            // 正多边形
            ToolStripButton polygon = _toolStripMgr.NewToolStripButton("draw_polygon");
            drawToolstrip.Items.Add(polygon);

            // 矩形
            ToolStripButton rectangle = _toolStripMgr.NewToolStripButton("draw_rectangle");
            drawToolstrip.Items.Add(rectangle);
            // 矩形
            ToolStripButton rectangle2 = _toolStripMgr.NewToolStripButton("draw_rectangle2");
            drawToolstrip.Items.Add(rectangle2);

            // 圆
            ToolStripButton circle = _toolStripMgr.NewToolStripButton("draw_circle");
            drawToolstrip.Items.Add(circle);

            // 圆弧
            ToolStripButton arc = _toolStripMgr.NewToolStripButton("draw_arc");
            drawToolstrip.Items.Add(arc);

            // 文字
            ToolStripButton mtext = _toolStripMgr.NewToolStripButton("draw_mtext");
            drawToolstrip.Items.Add(mtext);

            // 标注
            ToolStripButton dim = _toolStripMgr.NewToolStripButton("draw_dim");
            drawToolstrip.Items.Add(dim);

            return drawToolstrip;
        }

        /// <summary>
        /// 设置工具条: 修改
        /// </summary>
        private ToolStrip SetupToolbar_Modify()
        {
            ToolStrip modifyToolstrip = _toolStripMgr.GetToolStrip("Modify");

            // 删除
            ToolStripButton erase = _toolStripMgr.NewToolStripButton("modify_erase");
            modifyToolstrip.Items.Add(erase);

            // 复制
            ToolStripButton copy = _toolStripMgr.NewToolStripButton("modify_copy");
            modifyToolstrip.Items.Add(copy);

            // 镜像
            ToolStripButton mirror = _toolStripMgr.NewToolStripButton("modify_mirror");
            modifyToolstrip.Items.Add(mirror);

            // 偏移
            ToolStripButton offset = _toolStripMgr.NewToolStripButton("modify_offset");
            modifyToolstrip.Items.Add(offset);

            // 移动
            ToolStripButton move = _toolStripMgr.NewToolStripButton("modify_move");
            modifyToolstrip.Items.Add(move);

            return modifyToolstrip;
        }

        /// <summary>
        /// 设置工具条: 图层
        /// </summary>
        private ToolStripComboBox _toolstripLayerCombo;
        private ToolStrip SetupToolbar_Layer()
        {
            ToolStrip layerToolstrip = _toolStripMgr.GetToolStrip("Layer", true);

            // Layer management
            ToolStripButton tsbtnLayerMgr = _toolStripMgr.NewToolStripButton("format_layer");
            layerToolstrip.Items.Add(tsbtnLayerMgr);
            ToolStripButton tsbtnLayerMgr2 = _toolStripMgr.NewToolStripButton("quic_layer");
            layerToolstrip.Items.Add(tsbtnLayerMgr2);
            // Layer combobox
            _toolstripLayerCombo = new ToolStripComboBox();
             _toolstripLayerCombo.DropDownStyle = ComboBoxStyle.DropDownList;
             _toolStripMgr.AddToolStripItem(_toolstripLayerCombo);
             layerToolstrip.Items.Add(_toolstripLayerCombo);

            foreach (DBTableRecord item in _document.database.layerTable)
            {
                Layer layer = item as Layer;
                ToolStripButton layerBtn = new ToolStripButton(layer.name);
                layerBtn.Tag = layer.id;
                _toolstripLayerCombo.Items.Add(layerBtn);
            }

            this.SetLayerComboValue(_document.currentLayerId);
            
            _toolstripLayerCombo.SelectedIndexChanged += this.OnLayerComboSelectedIndexChanged;
            
            return layerToolstrip;
        }

        private void OnLayerComboSelectedIndexChanged(object sender, EventArgs e)
        {
            if (_toolstripLayerCombo.SelectedItem == null)
            {
                return;
            }

            ToolStripButton layerButton = _toolstripLayerCombo.SelectedItem as ToolStripButton;
            ObjectId layerId = (ObjectId)layerButton.Tag;
            if (_document.database.layerTable.Has(layerId))
            {
                _document.currentLayerId = layerId;
                _presenter.pointer.isShowAnchor = true;
            }

        }

        private void OnDocumentCurrLayerChanged(ObjectId last, ObjectId current)
        {
            this.SetLayerComboValue(current);
        }

        private int SetLayerComboValue(ObjectId layerId)
        {
            int index = -1;
            for (int i = 0; i < _toolstripLayerCombo.Items.Count; ++i)
            {
                ToolStripButton layerBtn = _toolstripLayerCombo.Items[i] as ToolStripButton;
                ObjectId oid = (ObjectId)layerBtn.Tag;
                if (oid == layerId)
                {
                    index = i;
                    break;
                }
            }

            _toolstripLayerCombo.SelectedIndex = index;
            return index;
        }

        private void AddLayer(Layer layer)
        {
            ToolStripButton layerBtn = new ToolStripButton(layer.name);
            layerBtn.Tag = layer.id;
            _toolstripLayerCombo.Items.Add(layerBtn);
        }

        private void RemoveLayer(Layer layer)
        {
            for (int i = 0; i < _toolstripLayerCombo.Items.Count; ++i)
            {
                ToolStripButton layerBtn = _toolstripLayerCombo.Items[i] as ToolStripButton;
                ObjectId layerId = (ObjectId)layerBtn.Tag;
                if (layerId == layer.id)
                {
                    _toolstripLayerCombo.Items.RemoveAt(i);
                    return;
                }
            }
        }

        private void OnAddLayer(DBTableRecord item)
        {
            Layer layer = item as Layer;
            this.AddLayer(layer);
        }

        private void OnRemoveLayer(DBTableRecord item)
        {
            Layer layer = item as Layer;
            this.RemoveLayer(layer);
        }

        /// <summary>
        /// 设置工具条: 特性
        /// </summary>
        private ToolStripComboBox _toolstripColorCombo;
        private int _colorComboCustomColorIndex = -1;
        private ToolStrip SetupToolbar_Property()
        {
            ToolStrip propertyToolstrip = _toolStripMgr.GetToolStrip("Property", true);

            _toolstripColorCombo = new ToolStripComboBox();
            _toolstripColorCombo.DropDownStyle = ComboBoxStyle.DropDownList;
            _toolStripMgr.AddToolStripItem(_toolstripColorCombo);
            propertyToolstrip.Items.Add(_toolstripColorCombo);

            foreach (MsCAD.Colors.Color color in _document.commonColors)
            {
                ToolStripButton colorBtn = new ToolStripButton(_document.commonColors.GetColorName(color));
                colorBtn.Tag = color;
                _toolstripColorCombo.Items.Add(colorBtn);
            }

            _colorComboCustomColorIndex = _toolstripColorCombo.Items.Count;
            ToolStripButton selectColorBtn = new ToolStripButton("选择颜色...");
            selectColorBtn.Tag = null;
            _toolstripColorCombo.Items.Add(selectColorBtn);

            this.SetColorComboValue(_document.currentColor);

            _toolstripColorCombo.SelectedIndexChanged += this.OnColorComboSelectedIndexChanged;

            return propertyToolstrip;
        }

        private void OnColorComboSelectedIndexChanged(object sender, EventArgs e)
        {
            if (_toolstripColorCombo.SelectedItem == null)
            {
                return;
            }

            ToolStripButton colorButton = _toolstripColorCombo.SelectedItem as ToolStripButton;
            if (colorButton.Tag == null)
            {
                ColorDialog colorDlg = new ColorDialog();
                colorDlg.AllowFullOpen = true;
                colorDlg.SolidColorOnly = true;
                DialogResult dlgRet = colorDlg.ShowDialog();
                if (dlgRet == DialogResult.OK)
                {
                    MsCAD.Colors.Color color = MsCAD.Colors.Color.FromColor(colorDlg.Color);
                    _document.currentColor = color;
                }
                else
                {
                    this.SetColorComboValue(_document.currentColor);
                }
            }
            else
            {
                MsCAD.Colors.Color color = (MsCAD.Colors.Color)colorButton.Tag;
                _document.currentColor = color;
            }
        }

        private void OnDocumentCurrColorChanged(MsCAD.Colors.Color last, MsCAD.Colors.Color current)
        {
            this.SetColorComboValue(current);
        }

        private int SetColorComboValue(MsCAD.Colors.Color color)
        {
            int index = -1;
            for (int i = 0; i < _toolstripColorCombo.Items.Count; ++i)
            {
                ToolStripButton colorBtn = _toolstripColorCombo.Items[i] as ToolStripButton;
                if (colorBtn.Tag != null)
                {
                    MsCAD.Colors.Color itemColor = (MsCAD.Colors.Color)colorBtn.Tag;
                    if (itemColor == color)
                    {
                        index = i;
                        break;
                    }
                }
            }

            if (index == -1)
            {
                ToolStripButton colorBtn = new ToolStripButton(color.Name);
                colorBtn.Tag = color;
                _toolstripColorCombo.Items.Insert(_colorComboCustomColorIndex, colorBtn);
                index = _colorComboCustomColorIndex;
            }

            _toolstripColorCombo.SelectedIndex = index;
            return index;
        }

        private void OnEditUndo(object sender, EventArgs e)
        {
            Commands.Edit.UndoCmd cmd = new Commands.Edit.UndoCmd();
            _presenter.OnCommand(cmd);
        }

        private void OnEditRedo(object sender, EventArgs e)
        {
            Commands.Edit.RedoCmd cmd = new Commands.Edit.RedoCmd();
            _presenter.OnCommand(cmd);
        }
        /// <summary>
        /// 筛选器
        /// </summary>
        private void OnScreen(object sender, EventArgs e)
        {
            try
            {
                _screenForm = ScreenForm.Instance;
                _screenForm.Show();
            }
            catch (Exception)
            {
                MessageBox.Show("绘图操作暂未完成，打开筛选器失败");
            }
        }
        [DllImport("user32.dll")]
        public static extern void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);

        /// <summary>
        /// 删除
        /// </summary>
        private void OnModifyErase(object sender, EventArgs e)
        {
            Commands.Modify.DeleteCmd cmd = new Commands.Modify.DeleteCmd();
            _presenter.OnCommand(cmd);
        }

        /// <summary>
        /// 复制
        /// </summary>
        private void OnModifyCopy(object sender, EventArgs e)
        {
            Commands.Modify.CopyCmd cmd = new Commands.Modify.CopyCmd();
            _presenter.OnCommand(cmd);
        }

        /// <summary>
        /// 镜像
        /// </summary>
        private void OnModifyMirror(object sender, EventArgs e)
        {
            Commands.Modify.MirrorCmd cmd = new Commands.Modify.MirrorCmd();
            _presenter.OnCommand(cmd);
        }

        /// <summary>
        /// 偏移
        /// </summary>
        private void OnModifyOffset(object sender, EventArgs e)
        {
            Commands.Modify.OffsetCmd cmd = new Commands.Modify.OffsetCmd();
            _presenter.OnCommand(cmd);
        }

        /// <summary>
        /// 阵列
        /// </summary>
        private void OnModifyArray(object sender, EventArgs e)
        {
        }

        /// <summary>
        /// 移动
        /// </summary>
        private void OnModifyMove(object sender, EventArgs e)
        {
            MainWin.Instance.ShowMsg(TipsState.Info, "请选择移动对象！");
            Commands.Modify.MoveCmd cmd = new Commands.Modify.MoveCmd();
            _presenter.OnCommand(cmd);
        }

        /// <summary>
        /// 旋转
        /// </summary>
        private void OnModifyRotate(object sender, EventArgs e)
        {
        }

        private void OnDrawLines(object sender, EventArgs e)
        {
            if (!isLayerShow()) return;
            Commands.Draw.LinesChainCmd cmd = new Commands.Draw.LinesChainCmd();
            _presenter.OnCommand(cmd);
        }

        private void OnDrawXLine(object sender, EventArgs e)
        {
            if (!isLayerShow()) return;
            Commands.Draw.XlineCmd cmd = new Commands.Draw.XlineCmd();
            _presenter.OnCommand(cmd);
        }

        private void OnDrawRay(object sender, EventArgs e)
        {
            if (!isLayerShow()) return;
            Commands.Draw.RayCmd cmd = new Commands.Draw.RayCmd();
            _presenter.OnCommand(cmd);
        }

        private void OnDrawPolyline(object sender, EventArgs e)
        {
            if (!isLayerShow()) return;
            Commands.Draw.PolylineCmd cmd = new Commands.Draw.PolylineCmd();
            _presenter.OnCommand(cmd);
        }

        private void OnDrawPolygon(object sender, EventArgs e)
        {
            if (!isLayerShow()) return;
            Commands.Draw.PolygonCmd cmd = new Commands.Draw.PolygonCmd();
            _presenter.OnCommand(cmd);
        }

        private void OnDrawRectangle(object sender, EventArgs e)
        {
            if (!isLayerShow()) return;
            Commands.Draw.RectangleCmd cmd = new Commands.Draw.RectangleCmd();
            _presenter.OnCommand(cmd);
        }
        private void OnDrawRectangle2(object sender, EventArgs e)
        {
            if (!isLayerShow()) return;
            Commands.Draw.Rectangle2Cmd cmd = new Commands.Draw.Rectangle2Cmd();
            _presenter.OnCommand(cmd);
        }

        private void OnDrawCircle(object sender, EventArgs e)
        {
            if (!isLayerShow()) return;
            Commands.Draw.CircleCmd cmd = new Commands.Draw.CircleCmd();
            _presenter.OnCommand(cmd);
        }

        private void OnDrawArc(object sender, EventArgs e)
        {
            if (!isLayerShow()) return;
            Commands.Draw.ArcCmd cmd = new Commands.Draw.ArcCmd();
            _presenter.OnCommand(cmd);
        }

        private void OnDrawMText(object sender, EventArgs e)
        {
            if (!isLayerShow()) return;
            this.presenter.selections.Clear();
            _document.selections.Clear();//先清空已选对象
            Commands.Draw.MTextCmd cmd = new Commands.Draw.MTextCmd();
            _presenter.OnCommand(cmd);
        }

        private void OnDrawDimension(object sender, EventArgs e)
        {
            if (!isLayerShow()) return;
            this.presenter.selections.Clear();
            this._document.selections.Clear();//先清空已选对象
            List<string> list = new List<string>();
            list.Add("测距对齐");
            list.Add("半径标注");
            list.Add("弧角弧长");
            list.Add("直线夹角");
            ButtonListForm f = new ButtonListForm("选择标注类型，回车确认", list);
            if (f.ShowDialog() == DialogResult.OK)
            {
                switch (f.SelectdIndex)
                {
                    case 0://测距标注                        
                        Commands.Draw.LinearDimensionCmd cmd0 = new Commands.Draw.LinearDimensionCmd();
                        _presenter.OnCommand(cmd0);
                        break;
                    case 1://半径标注
                        Commands.Draw.RadialDimensionnCmd cmd1 = new Commands.Draw.RadialDimensionnCmd();
                        _presenter.OnCommand(cmd1);
                        break;
                    case 2://弧角弧长
                        Commands.Draw.Angular3PointDimensionCmd cmd2 = new Commands.Draw.Angular3PointDimensionCmd();
                        _presenter.OnCommand(cmd2);
                        break;
                    case 3://直线夹角
                        Commands.Draw.Angular2LineDimensionCmd cmd3 = new Commands.Draw.Angular2LineDimensionCmd();
                        _presenter.OnCommand(cmd3);
                        break;
                }
            }
        }




        private bool isLayerShow()
        {
            LayerTable layerTable = _document.database.layerTable;
            foreach (Layer item in layerTable)
            {
                if (double.Parse(item.layerState.ToString()) == 0 &&         //该层是显示状态
                    (_document.currentLayerId.ToString()).Equals(item.id.ToString()))   //这个图元是这层的
                {
                    return true;
                }
            }
            MessageBox.Show("当前图层为隐藏状态，取消绘图");
            return false;
        }



        protected override void OnFormClosing(FormClosingEventArgs e)
        {
            //if (m_data.IsDirty)
            //{
            //    string s = "Save Changes to " + Path.GetFileName(m_filename) + "?";
            //    DialogResult result = MessageBox.Show(this, s, Program.AppName, MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            //    if (result == DialogResult.Cancel)
            //    {
            //        e.Cancel = true;
            //        return;
            //    }
            //    if (result == DialogResult.Yes)
            //        Save();
            //}
            _toolStripMgr.DisableAll();
            base.OnFormClosing(e);
        }

        internal void UpdateUI()
        {
            ToolStripMenuItem menuUndo = _toolStripMgr.GetMenuItem("edit_undo");
            menuUndo.Enabled = _presenter.canUndo;
            _undoToolstripItem.Enabled = _presenter.canUndo;

            ToolStripMenuItem menuRedo = _toolStripMgr.GetMenuItem("edit_redo");
            menuRedo.Enabled = _presenter.canRedo;
            _redoToolstripItem.Enabled = _presenter.canRedo;
        }


        private void menuDataClick(object sender, EventArgs e)
        {
            if (database != null && database.fileName != null)
            {
                DxfDataForm.Instance.fileName = database.fileName;
                DxfDataForm.Instance.ShowDialog();
            }
            else
            {
                MessageBox.Show("未打开文件");
            }
        }
    }
}
