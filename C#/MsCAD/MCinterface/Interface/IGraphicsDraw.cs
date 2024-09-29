using System;
using System.Collections.Generic;
using System.Drawing;
using MsMath;

namespace MsCAD
{
    public enum TextAlignment
    {
        LeftBottom = 0,
        LeftMiddle = 1,
        LeftTop = 2,
        CenterBottom = 3,
        CenterMiddle = 4,
        CenterTop = 5,
        RightBottom = 6,
        RightMiddle = 7,
        RightTop = 8,
    }

    public interface IGraphicsDraw
    {
        void DrawLine(Vector2 startPoint, Vector2 endPoint);

        void DrawXLine(Vector2 basePoint, Vector2 direction);

        void DrawRay(Vector2 basePoint, Vector2 direction);

        void DrawCircle(Vector2 center, double radius);

        void DrawEllipse(Vector2 center, double MajorAxis, double MinorAxis, double Rotation);

        /// <summary>
        /// 绘制圆弧
        /// 以逆时针方式绘制
        /// </summary>
        /// <param name="center">圆弧中心</param>
        /// <param name="radius">圆弧半径</param>
        /// <param name="startAngle">起始角度(弧度)</param>
        /// <param name="endAngle">结束角度(弧度)</param>
        void DrawArc(Vector2 center, double radius, double startAngle, double endAngle);

        void DrawRectangle(Vector2 position, double width, double height);
        /// <summary>
        /// 绘制带角度矩形
        /// </summary>
        /// <param name="center">中心点</param>
        /// <param name="angle">角度</param>
        /// <param name="hwidth">短边</param>
        /// <param name="hheight">长边</param>
        void DrawRectangle2(Vector2 center, double angle,double hwidth, double hheight);

        Vector2 DrawText(Vector2 position, string text, double height, string font, TextAlignment textAlign);


        void FillPolygon( List<Vector2> points);
    }
}
