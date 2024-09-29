using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;
using MsCAD.DatabaseServices;
using MsMath;

namespace MsCAD.Commands.Draw
{/// <summary>
 /// 图形绘制
 /// </summary>
    internal class DrawWorld : IGraphicsDraw
    {
        /// <summary>
        /// 绘图图面
        /// </summary>
        private Graphics _g = null;
        public Graphics graphics
        {
            get { return _g; }
            set { _g = value; }
        }

        /// <summary>
        /// 绘图笔
        /// </summary>
        private Pen _pen = null;
        public Pen pen
        {
            get { return _pen; }
            set { 
                _pen = value; 
            }
        }

        /// <summary>
        /// 绘图画刷
        /// </summary>
        private Brush _brush = null;
        public Brush brush
        {
            get { return _brush; }
            set { _brush = value; }
        }

        /// <summary>
        /// Presenter
        /// </summary>
        private Presenter _presenter = null;
        public Presenter presenter
        {
            get { return _presenter; }
        }

        /// <summary>
        /// 构造函数
        /// </summary>
        public DrawWorld(Presenter presenter)
        {
            _presenter = presenter;
        }

        public void DrawLine(Vector2 startPoint, Vector2 endPoint)
        {
            Vector2 startInCanvas = _presenter.ModelToCanvas(startPoint);
            Vector2 endInCanvas = _presenter.ModelToCanvas(endPoint);
            graphics.DrawLine(_pen,
                (float)startInCanvas.x, (float)startInCanvas.y,
                (float)endInCanvas.x, (float)endInCanvas.y);            
        }


        public void FillPolygon( List<Vector2> points)
        {
            List<Vector2> pointsInCanvas = new List<Vector2>(points.Count);
            for (int i = 0; i < points.Count; ++i)
            {
                pointsInCanvas.Add(_presenter.ModelToCanvas(points[i]));
            }

            PointF[] pnts = new PointF[points.Count];
            for (int i = 0; i < points.Count; ++i)
            {
                pnts[i] = new PointF((float)pointsInCanvas[i].x, (float)pointsInCanvas[i].y);
            }
            if (brush == null)
            {
                brush = new SolidBrush(_pen.Color);
            }
            graphics.FillPolygon(brush, pnts);
        }


        public void DrawXLine(Vector2 basePoint, Vector2 direction)
        {
            Vector2 basePnt = _presenter.ModelToCanvas(basePoint);
            Vector2 otherPnt = _presenter.ModelToCanvas(basePoint + direction);
            Vector2 dir = (otherPnt - basePnt).normalized;

            double xk = double.MinValue;
            double yk = double.MinValue;
            if (dir.y != 0)
            {
                double k = basePnt.y / dir.y;
                xk = basePnt.x - k * dir.x;
            }
            if (dir.x != 0)
            {
                double k = basePnt.x / dir.x;
                yk = basePnt.y - k * dir.y;
            }

            if (xk > 0
                || (xk == 0 && dir.x * dir.y >= 0))
            {
                Vector2 spnt = new Vector2(xk, 0);
                if (dir.y < 0)
                {
                    dir = -dir;
                }
                Vector2 epnt = spnt + 10000 * dir;

                graphics.DrawLine(_pen,
                    (float)spnt.x, (float)spnt.y,
                    (float)epnt.x, (float)epnt.y);
            }
            else if (yk > 0
                || (yk == 0 && dir.x * dir.y >= 0))
            {
                Vector2 spnt = new Vector2(0, yk);
                if (dir.x < 0)
                {
                    dir = -dir;
                }
                Vector2 epnt = spnt + 10000 * dir;

                graphics.DrawLine(_pen,
                    (float)spnt.x, (float)spnt.y,
                    (float)epnt.x, (float)epnt.y);
            }
        }

        public void DrawRay(Vector2 basePoint, Vector2 direction)
        {
            Vector2 basePnt = _presenter.ModelToCanvas(basePoint);
            Vector2 otherPnt = _presenter.ModelToCanvas(basePoint + direction);
            Vector2 dir = (otherPnt - basePnt).normalized;

            double xk = double.MinValue;
            double yk = double.MinValue;
            if (basePnt.x > 0 && basePnt.x < 10000
                && basePnt.y > 0 && basePnt.y < 10000)
            {
                xk = 1;
                yk = 1;
            }
            else
            {
                if (dir.y != 0)
                {
                    double k = -basePnt.y / dir.y;
                    if (k >= 0)
                    {
                        xk = basePnt.x + k * dir.x;
                    }
                }
                if (dir.x != 0)
                {
                    double k = -basePnt.x / dir.x;
                    if (k >= 0)
                    {
                        yk = basePnt.y + k * dir.y;
                    }
                }

            }

            if (xk > 0
                || (xk == 0 && dir.x * dir.y >= 0)
                || yk > 0
                || (yk == 0 && dir.x * dir.y >= 0))
            {

                Vector2 epnt = basePnt + 10000 * dir;

                graphics.DrawLine(_pen,
                    (float)basePnt.x, (float)basePnt.y,
                    (float)epnt.x, (float)epnt.y);
            }
        }

        public void DrawCircle(Vector2 center, double radius)
        {
            Vector2 centerInCanvas = _presenter.ModelToCanvas(center);
            double radiusInCanvas = _presenter.ModelToCanvas(radius);
            graphics.DrawEllipse(_pen,
                (float)(centerInCanvas.x - radiusInCanvas), (float)(centerInCanvas.y - radiusInCanvas),
                (float)radiusInCanvas * 2, (float)radiusInCanvas * 2);
        }


        public void DrawEllipse(Vector2 center, double MajorAxis, double MinorAxis, double Rotation)
        {
            Vector2 centerIC = _presenter.ModelToCanvas(center);
            double MajorAxisIC = _presenter.ModelToCanvas(MajorAxis);
            double MinorAxisIC = _presenter.ModelToCanvas(MinorAxis);

            double angle = 0;
            //float angle = (float)Rotation % 360;
            float w = (float)MajorAxisIC;
            float h = (float)MinorAxisIC;
            float cx = (float)centerIC.x - w / 2;
            float cy = (float)centerIC.y - h / 2;
            //将graphics坐标原点移到矩形中心点
            graphics.TranslateTransform(w / 2 + cx, h / 2 + cy);
            //graphics旋转相应的角度(绕当前原点)
            graphics.RotateTransform(-(float)Rotation);
            //恢复graphics在水平和垂直方向的平移(沿当前原点)
            graphics.TranslateTransform(-w / 2 - cx, -h / 2 - cy);
            //画出矩形或椭圆当前位置
            graphics.DrawEllipse(_pen, new RectangleF(cx, cy, w, h));
            //重至绘图的所有变换
            graphics.ResetTransform();
            graphics.Save();
        }

        /// <summary>
        /// 绘制圆弧,逆时针从startAngle到endAngle
        /// </summary>
        public void DrawArc(Vector2 center, double radius, double startAngle, double endAngle)
        {
            //
            Vector2 centerInCanvas = _presenter.ModelToCanvas(center);
            double radiusInCanvas = _presenter.ModelToCanvas(radius);

            // GDI为顺时针绘制圆弧,而当前函数为逆时针绘制圆弧
            double startAngleInCanvas = MathUtils.NormalizeRadianAngle(-startAngle);
            double endAngleInCanvas = MathUtils.NormalizeRadianAngle(-endAngle);

            //
            double angle = endAngle - startAngle;
            if (endAngle < startAngle)
            {
                angle += Utils.PI * 2;
            }

            //
            if (radiusInCanvas > 0)
            {
                graphics.DrawArc(pen,
                (float)(centerInCanvas.x - radiusInCanvas), (float)(centerInCanvas.y - radiusInCanvas),
                (float)radiusInCanvas * 2, (float)radiusInCanvas * 2,
                (float)(startAngleInCanvas * 180.0 / Utils.PI), -(float)(angle * 180.0 / Utils.PI));
            }
        }

        public void DrawRectangle(Vector2 position, double width, double height)
        {
            double widthInCanvas = _presenter.ModelToCanvas(width);
            double heightInCanvas = _presenter.ModelToCanvas(height);
            Vector2 posInCanvas = _presenter.ModelToCanvas(position);
            posInCanvas.y -= heightInCanvas;

            graphics.DrawRectangle(pen,
                (float)posInCanvas.x, (float)posInCanvas.y,
                (float)widthInCanvas, (float)heightInCanvas);
        }

        public void DrawRectangle2(Vector2 center, double angle, double hwidth, double hheight)
        {
            Vector2 point = _presenter.ModelToCanvas(center); ;
            //获取矩形的四个顶点
            Vector2 point1 = new Vector2(point.x - _presenter.ModelToCanvas(hwidth), point.y + _presenter.ModelToCanvas(hheight));
            point1 = MathUtils.AntiRotatePoint(point1, point, angle);
            Vector2 point2 = new Vector2(point.x + _presenter.ModelToCanvas(hwidth), point.y + _presenter.ModelToCanvas(hheight));
            point2 = MathUtils.AntiRotatePoint(point2, point, angle);
            Vector2 point3 = new Vector2(point.x + _presenter.ModelToCanvas(hwidth), point.y - _presenter.ModelToCanvas(hheight));
            point3 = MathUtils.AntiRotatePoint(point3, point, angle);
            Vector2 point4 = new Vector2(point.x - _presenter.ModelToCanvas(hwidth), point.y - _presenter.ModelToCanvas(hheight));
            point4 = MathUtils.AntiRotatePoint(point4, point, angle);
            //4个顶点画4条线
            graphics.DrawLine(_pen,
                (float)point1.x, (float)point1.y,
                (float)point2.x, (float)point2.y);
            graphics.DrawLine(_pen,
                (float)point2.x, (float)point2.y,
                (float)point3.x, (float)point3.y);
            graphics.DrawLine(_pen,
                (float)point3.x, (float)point3.y,
                (float)point4.x, (float)point4.y);
            graphics.DrawLine(_pen,
                (float)point4.x, (float)point4.y,
                (float)point1.x, (float)point1.y);
        }
        public Vector2 DrawText(Vector2 position, string text, double height, string fontName, TextAlignment textAlign)
        {
            fontName = "Txt";
            int fontHeight = (int)_presenter.ModelToCanvas(height);
            if (fontHeight <= 0)
            {
                return new Vector2(0, 0);
            }
            position = _presenter.ModelToCanvas(position);
            string fontFamily = fontName == "" ? "Arial" : fontName;

            FontStyle fontStyle = FontStyle.Regular;
            Font font = new Font(fontFamily, (int)fontHeight, fontStyle);
            StringFormat format = new StringFormat();
            format.Alignment = StringAlignment.Near;
            format.LineAlignment = StringAlignment.Far;
            switch (textAlign)
            {
                case TextAlignment.LeftBottom:
                    format.Alignment = StringAlignment.Near;
                    format.LineAlignment = StringAlignment.Far;
                    break;

                case TextAlignment.LeftMiddle:
                    format.Alignment = StringAlignment.Near;
                    format.LineAlignment = StringAlignment.Center;
                    break;

                case TextAlignment.LeftTop:
                    format.Alignment = StringAlignment.Near;
                    format.LineAlignment = StringAlignment.Near;
                    break;

                case TextAlignment.CenterBottom:
                    format.Alignment = StringAlignment.Center;
                    format.LineAlignment = StringAlignment.Far;
                    break;

                case TextAlignment.CenterMiddle:
                    format.Alignment = StringAlignment.Center;
                    format.LineAlignment = StringAlignment.Center;
                    break;

                case TextAlignment.CenterTop:
                    format.Alignment = StringAlignment.Center;
                    format.LineAlignment = StringAlignment.Near;
                    break;

                case TextAlignment.RightBottom:
                    format.Alignment = StringAlignment.Far;
                    format.LineAlignment = StringAlignment.Far;
                    break;

                case TextAlignment.RightMiddle:
                    format.Alignment = StringAlignment.Far;
                    format.LineAlignment = StringAlignment.Center;
                    break;

                case TextAlignment.RightTop:
                    format.Alignment = StringAlignment.Far;
                    format.LineAlignment = StringAlignment.Near;
                    break;
            }

            if (brush != null)
            {
                brush = new SolidBrush(_pen.Color);
            }

            PointF pos = new PointF((float)position.x, (float)position.y);
            SizeF size = graphics.MeasureString(text, font, pos, format);
            double w = _presenter.CanvasToModel(size.Width);
            double h = _presenter.CanvasToModel(size.Height);            
            PointF pos1 = new PointF((float)position.x - size.Width / 2, (float)position.y);//水平居中
            graphics.DrawString(text, font, brush, pos1, format);

           
            return new Vector2(w, h);
        }
    }
}
