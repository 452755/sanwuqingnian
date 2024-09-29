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
    internal class DrawCanvas : IGraphicsDraw
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
            set { _pen = value; }
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
        public DrawCanvas(Presenter presenter)
        {
            _presenter = presenter;
        }

        public void DrawLine(Vector2 startPoint, Vector2 endPoint)
        {
            graphics.DrawLine(_pen,
                (float)startPoint.x, (float)startPoint.y,
                (float)endPoint.x, (float)endPoint.y);
        }

        public void FillPolygon( List<Vector2> points)
        {
            PointF[] pnts = new PointF[points.Count];
            for (int i = 0; i < points.Count; ++i)
            {
                pnts[i] = new PointF((float)points[i].x, (float)points[i].y);
            }
            if (brush == null)
            {
                brush = new SolidBrush(_pen.Color);
            }
            graphics.FillPolygon(brush, pnts);
        }

        public void DrawXLine(Vector2 basePoint, Vector2 direction)
        {
            direction.Normalize();

            double xk = double.MinValue;
            double yk = double.MinValue;
            if (direction.y != 0)
            {
                double k = basePoint.y / direction.y;
                xk = basePoint.x - k * direction.x;
            }
            if (direction.x != 0)
            {
                double k = basePoint.x / direction.x;
                yk = basePoint.y - k * direction.y;
            }

            if (xk > 0
                || (xk == 0 && direction.x * direction.y >= 0))
            {
                Vector2 spnt = new Vector2(xk, 0);
                if (direction.y < 0)
                {
                    direction = -direction;
                }
                Vector2 epnt = spnt + 10000 * direction;

                graphics.DrawLine(_pen,
                    (float)spnt.x, (float)spnt.y,
                    (float)epnt.x, (float)epnt.y);
            }
            else if (yk > 0
                || (yk == 0 && direction.x * direction.y >= 0))
            {
                Vector2 spnt = new Vector2(0, yk);
                if (direction.x < 0)
                {
                    direction = -direction;
                }
                Vector2 epnt = spnt + 10000 * direction;

                graphics.DrawLine(_pen,
                    (float)spnt.x, (float)spnt.y,
                    (float)epnt.x, (float)epnt.y);
            }
        }

        



        public void DrawRay(Vector2 basePnt, Vector2 dir)
        {
            dir.Normalize();

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
            graphics.DrawEllipse(_pen,
                (float)(center.x - radius), (float)(center.y - radius),
                (float)radius * 2, (float)radius * 2);
        }

        public void DrawEllipse(Vector2 center, double MajorAxis, double MinorAxis, double Rotation)
        {
            float angle = (float)Rotation % 360;
            float w = (float)MinorAxis;
            float h = (float)MajorAxis;
            float cx = (float)center.x - w / 2;
            float cy = (float)center.y - h / 2;
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
            // GDI为顺时针绘制圆弧,而当前函数为逆时针绘制圆弧
            double startAngleInCanvas = MathUtils.NormalizeRadianAngle(-startAngle);
            double endAngleInCanvas = MathUtils.NormalizeRadianAngle(-endAngle);

            //
            double angle = endAngle - startAngle;
            if (endAngle < startAngle)
            {
                angle += Utils.PI * 2;
            }

            graphics.DrawArc(pen,
                (float)(center.x - radius), (float)(center.y - radius),
                (float)radius * 2, (float)radius * 2,
                (float)(startAngleInCanvas * 180.0 / Utils.PI), -(float)(angle * 180.0 / Utils.PI));
        }

        public void DrawRectangle(Vector2 position, double width, double height)
        {
            graphics.DrawRectangle(pen,
                (float)position.x, (float)position.y,
                (float)width, (float)height);
        }
        public void DrawRectangle2(Vector2 center, double angle, double hwidth, double hheight)
        {
            //获取矩形的四个顶点
            Vector2 point1 = new Vector2(center.x - hwidth, center.y + hheight);
            point1 = MathUtils.AntiRotatePoint(point1, center, angle);
            Vector2 point2 = new Vector2(center.x + hwidth, center.y + hheight);
            point2 = MathUtils.AntiRotatePoint(point2, center, angle);
            Vector2 point3 = new Vector2(center.x + hwidth, center.y - hheight);
            point3 = MathUtils.AntiRotatePoint(point3, center, angle);
            Vector2 point4 = new Vector2(center.x - hwidth, center.y - hheight);
            point4 = MathUtils.AntiRotatePoint(point4, center, angle);
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
            int fontHeight = (int)height;
            if (fontHeight <= 0)
            {
                return new Vector2(0, 0);
            }
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
            PointF pos = new PointF((float)position.x, (float)position.y);
            graphics.DrawString(text, font, _brush, pos, format);

            SizeF size = graphics.MeasureString(text, font, pos, format);
            return new Vector2(size.Width, size.Height);
        }

        
    }
}
