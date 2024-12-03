using MsCAD.ApplicationServices;
using MsCAD.DatabaseServices;
using MsCAD.UI;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using MsMath;

namespace MsCAD
{
    class PresenterHawkEye : Presenter
    {
        public PresenterHawkEye(ICanvas canvas, Document doc)
            : base(canvas, doc)
        {

        }
        private Bitmap _bufferBitmap = null;
        private bool _bufferBitmapToRedraw = true;
        public override void OnPaintCanvas(PaintEventArgs e)
        {
            int canvasHawkWidth = (int)canvas.width;
            int canvasHawkHeight = (int)canvas.height;
            Rectangle clipRectangle = e.ClipRectangle;
            Rectangle canvasRectangle = new Rectangle(0, 0, canvasHawkWidth, canvasHawkHeight);

            if (_bufferBitmap == null)
            {
                clipRectangle = canvasRectangle;
                _bufferBitmap = new Bitmap(canvasHawkWidth, canvasHawkHeight);
                _bufferBitmapToRedraw = true;
                
                screenPan = new Vector2(canvasHawkWidth/3, -canvasHawkHeight/3);//把绘制图元的坐标系原点移动到中间
            }
            zoom = 0.03 * pointer.zoomCanvas ;//放大倍率
            _bufferBitmapToRedraw = true;
            if (_bufferBitmapToRedraw)
            {
                _bufferBitmapToRedraw = false;

                Graphics graphics = Graphics.FromImage(_bufferBitmap);
                graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;

                // 绘制背景
                graphics.Clear(Color.FromArgb(0, 99, 99));
                worldDraw.graphics = graphics;
                canvasDraw.graphics = graphics;

                // 绘制数据库图元对象
                Block modelSpace = documentHawk.database.blockTable[documentHawk.currentBlockName] as Block;
                foreach (Entity entity in modelSpace)
                {
                    worldDraw.pen = GetPen(entity);//采用
                    if (entity is Text || entity is Solid)
                    {
                        worldDraw.brush = GetBrush(entity);
                    }
                    entity.Draw(worldDraw);
                }
            }
            worldDraw.graphics = e.Graphics;
            canvasDraw.graphics = e.Graphics;

            // 双缓冲:将图片绘制到画布
            e.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
            e.Graphics.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.NearestNeighbor;
            e.Graphics.PixelOffsetMode = System.Drawing.Drawing2D.PixelOffsetMode.Half;
            e.Graphics.DrawImage(_bufferBitmap, clipRectangle, clipRectangle, GraphicsUnit.Pixel);

            pointer.canvasHawkWidth  = canvasHawkWidth;
            pointer.canvasHawkHeight =canvasHawkHeight;
            pointer.mode = Pointer.Mode.HawkEye;
            // 绘制Pointer
            List<string> _isPelShow = new List<string>();
            pointer.isShowAnchor = false;
            pointer.OnPaint(e.Graphics, _isPelShow);
            
            return;
        }
    }
}
