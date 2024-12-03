using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

using MsCAD.ApplicationServices;
using MsCAD.DatabaseServices;
using MsMath;

namespace MsCAD.UI
{
    internal class Pointer
    {
        private Presenter _presenter = null;

        /// <summary>
        /// 模式
        /// </summary>
        internal enum Mode
        {
            /// <summary>默认模式</summary>
            Default = 0,
            /// <summary>选择模式</summary>
            Select = 1,
            /// <summary>定位模式</summary>
            Locate = 2,
            /// <summary>拖动模式</summary>
            Drag = 3,
            /// <summary>鹰眼模式</summary>
            HawkEye = 4,
        }
        private Mode _mode = Mode.Default;
        internal Mode mode
        {
            get { return _mode; }
            set
            {
                if (_mode != value)
                {
                    _mode = value;
                    UpdateBitmap();
                }
            }
        }
        private double _canvasHawkWidth = 0.0;//鹰眼窗口的宽度
        private double _canvasHawkHeight = 0.0;//鹰眼窗口的高度
        private double _canvasWidth = 0.0;//鹰眼窗口的宽度
        private double _canvasHeight = 0.0;//鹰眼窗口的高度
        private double _zoomCanvas = 1;//图元视野的倍率，值越大图形就越大能看到的就更少视野越小，值越小图形就越小能看到的就更多视野越大
        private Vector2 _vHawkEyeStart = new Vector2(0, 0);//鹰眼矩形显示的位置X前一次的位置
        private Vector2 _vHawkEye = new Vector2(0, 0);//鹰眼矩形显示的位置X
        private Vector2 _canvasMousePosition = new Vector2(0, 0);//在视野的鼠标当前坐标
        private Vector2 _screenPanStart = new Vector2(0, 0);//缩放前的原点坐标
        public Vector2 VHawkEyeStart
        {
            get
            {
                return _vHawkEyeStart;
            }

            set
            {
                _vHawkEyeStart = value;
            }
        }

        public Vector2 vHawkEye
        {
            get
            {
                return _vHawkEye;
            }

            set
            {
                _vHawkEye = value;
            }
        }
        internal double canvasHawkWidth
        {
            get
            {
                return _canvasHawkWidth;
            }

            set
            {
                _canvasHawkWidth = value;
            }
        }

        internal double canvasHawkHeight
        {
            get
            {
                return _canvasHawkHeight;
            }

            set
            {
                _canvasHawkHeight = value;
            }
        }
        private string _currentId = "";//当前选中图元ID
        internal string currentId
        {
            get
            {
                return _currentId;
            }

            set
            {
                _currentId = value;
            }
        }

        /// <summary>
        ///  中心位置
        ///  Canvas CSYS
        /// </summary>
        private Vector2 _pos = new Vector2(0, 0);
        internal Vector2 position
        {
            get { return _pos; }
            set { _pos = value; }
        }

        /// <summary>
        /// 捕捉节点位置
        /// Model CSYS
        /// </summary>
        private Vector2 _currSnapPoint = new Vector2(0, 0);
        internal Vector2 currentSnapPoint
        {
            get { return _currSnapPoint; }
        }

        /// <summary>
        /// 尺寸
        /// </summary>
        private PickupBox _pickupBox = null;
        private SelectRectangle _selRect = null;

        private LocateCross _locateCross = null;

        private SnapNodesMgr _snapNodesMgr = null;
        private AnchorsMgr _anchorMgr = null;

        internal int pickupBoxSide
        {
            get { return _pickupBox.side; }
            set
            {
                if (_pickupBox.side != value)
                {
                    _pickupBox.side = value;
                    UpdateBitmap();
                }
            }
        }

        internal int locateCrossLength
        {
            get { return _locateCross.length; }
            set
            {
                if (_locateCross.length != value)
                {
                    _locateCross.length = value;
                    UpdateBitmap();
                }
            }
        }

        private bool _isShowAnchor = true;
        internal bool isShowAnchor
        {
            get { return _isShowAnchor; }
            set
            {
                if (_isShowAnchor != value)
                {
                    _anchorMgr.Clear();
                    _isShowAnchor = value;
                    if (_isShowAnchor)
                    {
                        _anchorMgr.Update();
                    }
                }
            }
        }

        /// <summary>
        /// 位图
        /// </summary>
        private Bitmap _bitmap = null;
        private Bitmap _bitmapHawkEye = null;
        private void UpdateBitmap()
        {
            _bitmap = new Bitmap(_locateCross.length, _locateCross.length);
            Graphics graphics = Graphics.FromImage(_bitmap);

            Pen pen = GDIResMgr.Instance.GetPen(Color.White, 1);

            if (_mode == Mode.Default || _mode == Mode.Select)
            {
                graphics.DrawRectangle(pen,
                (_bitmap.Width - _pickupBox.side) / 2, (_bitmap.Height - _pickupBox.side) / 2,
                _pickupBox.side, _pickupBox.side);
            }

            if (_mode == Mode.Default || _mode == Mode.Locate)
            {
                graphics.DrawLine(pen,
                _bitmap.Width / 2, 0,
                _bitmap.Width / 2, _bitmap.Height);
                graphics.DrawLine(pen,
                    0, _bitmap.Height / 2,
                    _bitmap.Width, _bitmap.Height / 2);
            }
            if(_vHawkEye.x == 0 || _vHawkEye.y  == 0 )
            {
                //把矩形框放到中间，鼠标点击的中间（初始位置）
                _vHawkEye.x = canvasHawkWidth / 2 - (canvasHawkWidth / 3) / 2;
                _vHawkEye.y = canvasHawkHeight / 2 - (canvasHawkHeight / 3) / 2;
                _vHawkEyeStart = _vHawkEye;
            }
        }

        /// <summary>
        /// 构造函数
        /// </summary>
        internal Pointer(Presenter presenter)
        {
            _presenter = presenter;

            _pickupBox = new PickupBox(_presenter);
            _pickupBox.side = 20;

            _locateCross = new LocateCross(_presenter);
            _locateCross.length = 60;

            _snapNodesMgr = new SnapNodesMgr(_presenter);
            _anchorMgr = new AnchorsMgr(_presenter);

            UpdateBitmap();
        }

        internal Commands.Command OnMouseDown(MouseEventArgs e)
        {
            _pos.x = e.X;
            _pos.y = e.Y;
            Commands.Command cmd = null;

            switch (_mode)
            {
                case Mode.Default:
                    {
                        if (e.Button == MouseButtons.Left)
                        {
                            if (_anchorMgr.currentGripPoint == null)
                            {
                                _pickupBox.center = _pos;
                                List<Selection> sels = _pickupBox.Select(_presenter.currentBlock);//定位选中点
                                if (sels.Count > 0)
                                {
                                    if (IsShiftKeyDown)
                                    {
                                        (_presenter.document as Document).selections.Remove(sels);
                                    }
                                    else
                                    {
                                        (_presenter.document as Document).selections.Add(sels);
                                    }
                                    _currentId = sels[0].objectId.ToString();
                                }
                                else
                                {
                                    _selRect = new SelectRectangle(_presenter);
                                    _selRect.startPoint = _selRect.endPoint = _pos;
                                }
                            }
                            else
                            {
                                Database db = (_presenter.document as Document).database;
                                Entity entity = db.GetObject(_anchorMgr.currentGripEntityId) as Entity;
                                if (entity != null)
                                {
                                    MsCAD.Commands.GripPointMoveCmd gripMoveCmd = new Commands.GripPointMoveCmd(
                                        entity, _anchorMgr.currentGripPointIndex, _anchorMgr.currentGripPoint);
                                    cmd = gripMoveCmd;
                                }
                            }
                        }
                    }
                    break;

                case Mode.Select:
                    {
                        if (e.Button == MouseButtons.Left)
                        {
                            _pickupBox.center = _pos;
                            List<Selection> sels = _pickupBox.Select(_presenter.currentBlock);
                            if (sels.Count > 0)
                            {
                                if (IsShiftKeyDown)
                                {
                                    (_presenter.document as Document).selections.Remove(sels);
                                }
                                else
                                {
                                    (_presenter.document as Document).selections.Add(sels);
                                }
                            }
                            else
                            {
                                _selRect = new SelectRectangle(_presenter);
                                _selRect.startPoint = _selRect.endPoint = _pos;
                            }
                        }
                    }
                    break;

                case Mode.Locate:
                    _currSnapPoint = _snapNodesMgr.Snap(_pos);
                    break;

                case Mode.Drag:
                    break;

                case Mode.HawkEye:
                    //_vHawkEyeStart.x = _vHawkEye.x + (canvasWidth / 3 + 2) / 2;
                    //_vHawkEyeStart.y = _vHawkEye.y+ (canvasHeight / 3 + 2) / 2;
                    _vHawkEyeStart = _vHawkEye;
                    _vHawkEye.x = _pos.x - (canvasHawkWidth  / 3) /2;
                    _vHawkEye.y = _pos.y - (canvasHawkHeight / 3) /2;
                    break;

                default:
                    break;
            }

            return cmd;
        }

        internal void OnMouseUp(MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left)
            {
                if (_selRect != null)
                {
                    List<Selection> sels = _selRect.Select(_presenter.currentBlock);
                    if (sels.Count > 0)
                    {
                        if (IsShiftKeyDown)
                        {
                            (_presenter.document as Document).selections.Remove(sels);
                        }
                        else
                        {
                            (_presenter.document as Document).selections.Add(sels);
                        }
                    }
                }
                _selRect = null;
            }
        }

        internal void OnMouseMove(MouseEventArgs e)
        {
            _pos.x = e.X;
            _pos.y = e.Y;

            switch (_mode)
            {
                case Mode.Default:
                    if (_selRect != null)
                    {
                        _selRect.endPoint = _pos;
                        //_presenter.RepaintCanvas();
                    }
                    else
                    {
                        _currSnapPoint = _anchorMgr.Snap(_pos);
                    }
                    break;

                case Mode.Select:
                    if (_selRect != null)
                    {
                        _selRect.endPoint = _pos;
                        //_presenter.RepaintCanvas();
                    }
                    break;

                case Mode.Locate:
                    _currSnapPoint = _snapNodesMgr.Snap(_pos);
                    break;

                case Mode.Drag:
                    break;

                default:
                    break;
            }

            _presenter.RepaintCanvas();
        }

        internal void OnMouseDoubleClick(MouseEventArgs e)
        {
            switch (mode)
            {
                case Mode.Default:
                    if (e.Button == MouseButtons.Left)
                    {
                        if (_anchorMgr.currentGripPoint == null)
                        {
                            _pickupBox.center = _pos;
                            List<Selection> sels = _pickupBox.Select(_presenter.currentBlock);
                            if (sels.Count > 0)
                            {
                                foreach (Selection sel in sels)
                                {
                                    DBObject dbobj = (_presenter.document as Document).database.GetObject(sel.objectId);
                                    if (dbobj != null && dbobj is Text)
                                    {
                                        MsCAD.Commands.Text.EditCmd cmd = new MsCAD.Commands.Text.EditCmd();
                                        cmd.entText = dbobj as Text;
                                        _presenter.OnCommand(cmd);
                                        //cmd.Initialize();

                                        (_presenter.document as Document).selections.Clear();
                                    }
                                }
                            }
                        }
                    }
                    break;

                default:
                    break;
            }
        }

        internal bool OnKeyDown(KeyEventArgs e)
        {

            return false;
        }

        internal bool OnKeyUp(KeyEventArgs e)
        {
            return false;
        }

        internal void OnPaint(Graphics graphics,List<string > _isPelShow)
        {
            if (_isShowAnchor)
            {
                _anchorMgr.OnPaint(graphics, _isPelShow);
            }

            switch (_mode)
            {
                case Mode.Default:
                    {
                        if (_selRect != null)
                        {
                            _selRect.OnPaint(graphics);
                        }
                        else
                        {
                            Vector2 currSnapPointInCanvas = _presenter.ModelToCanvas(_currSnapPoint);
                            graphics.DrawImage(_bitmap,
                                (float)(currSnapPointInCanvas.x - _bitmap.Width / 2),
                                (float)(currSnapPointInCanvas.y - _bitmap.Height / 2));
                        }
                    }
                    break;

                case Mode.Select:
                    if (_selRect != null)
                    {
                        _selRect.OnPaint(graphics);
                    }
                    else
                    {
                        graphics.DrawImage(_bitmap,
                            (float)(_pos.x - _bitmap.Width / 2),
                            (float)(_pos.y - _bitmap.Height / 2));
                    }
                    break;

                case Mode.Locate:
                    {
                        Vector2 currSnapPointInCanvas = _presenter.ModelToCanvas(_currSnapPoint);
                        graphics.DrawImage(_bitmap,
                            (float)(currSnapPointInCanvas.x - _bitmap.Width / 2),
                            (float)(currSnapPointInCanvas.y - _bitmap.Height / 2));

                        _presenter.canvasDraw.graphics = graphics;
                        _snapNodesMgr.OnPaint(_presenter.canvasDraw);
                    }
                    break;

                case Mode.Drag:
                    break;

                case Mode.HawkEye:
                    if (_selRect != null)
                    {
                        _selRect.OnPaint(graphics);
                    }
                    else
                    {
                        _bitmapHawkEye = new Bitmap((int)canvasHawkWidth, (int)canvasHawkHeight);
                        Graphics Graphics = Graphics.FromImage(_bitmapHawkEye);
                        Pen pen = GDIResMgr.Instance.GetPen(Color.Red, 1);
                        Graphics.DrawRectangle(pen, 0, 0, (int)(canvasHawkWidth / 3 ), (int)(canvasHawkHeight / 3 ));
                        Graphics.DrawLine(pen, (int)canvasHawkWidth / 12, (int)canvasHawkHeight / 6,
                            (int)(canvasHawkWidth / 6 + canvasHawkWidth / 12), (int)canvasHawkHeight / 6);
                        Graphics.DrawLine(pen, (int)canvasHawkWidth / 6, (int)canvasHawkHeight / 12, 
                            (int)canvasHawkWidth / 6, (int)(canvasHawkHeight / 6+ canvasHawkHeight / 12));


                        graphics.DrawImage(_bitmapHawkEye,
                                (float)(_vHawkEye.x),
                                (float)(_vHawkEye.y + 2));
                    }
                    break;

                default:
                    break;
            }
        }

        internal void OnSelectionChanged()
        {
            if (_isShowAnchor)
            {
                _anchorMgr.Update();
            }
        }

        internal void UpdateGripPoints()
        {
            _anchorMgr.Clear();
            if (_isShowAnchor)
            {
                _anchorMgr.Update();
            }
        }

        private bool IsShiftKeyDown
        {
            get
            {
                return (Control.ModifierKeys & Keys.Shift) == Keys.Shift;
            }
        }

        public double zoomCanvas
        {
            get
            {
                return _zoomCanvas;
            }

            set
            {
                _zoomCanvas = value;
            }
        }

        public double canvasWidth
        {
            get
            {
                return _canvasWidth;
            }

            set
            {
                _canvasWidth = value;
            }
        }

        public double canvasHeight
        {
            get
            {
                return _canvasHeight;
            }

            set
            {
                _canvasHeight = value;
            }
        }

        public Vector2 canvasMousePosition
        {
            get
            {
                return _canvasMousePosition;
            }

            set
            {
                _canvasMousePosition = value;
            }
        }
    }
}
