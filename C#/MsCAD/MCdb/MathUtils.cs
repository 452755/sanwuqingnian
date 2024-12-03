using MsMath;
using System;

namespace MsCAD.DatabaseServices
{
    public class MathUtils
    {
        /// <summary>
        /// 点是否在矩形内
        /// </summary>
        public static bool IsPointInRectangle(Vector2 point, MsMath.Rectangle2 rect)
        {
            Vector2 rectLeftBottom = rect.leftBottom;
            Vector2 rectRightTop = rect.rightTop;

            if (point.x >= rectLeftBottom.x
                && point.x <= rectRightTop.x
                && point.y >= rectLeftBottom.y
                && point.y <= rectRightTop.y)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        /// <summary>
        /// Cross window
        /// https://yal.cc/rectangle-circle-intersection-test/
        /// </summary>
        public static bool BoundingCross(Bounding bounding, Circle circle)
        {
            Vector2 nearestPntOnBound = new Vector2(
                Math.Max(bounding.left, Math.Min(circle.center.x, bounding.right)),
                Math.Max(bounding.bottom, Math.Min(circle.center.y, bounding.top)));

            if (Vector2.Distance(nearestPntOnBound, circle.center) <= circle.radius)
            {
                double bdLeft = bounding.left;
                double bdRight = bounding.right;
                double bdTop = bounding.top;
                double bdBottom = bounding.bottom;

                return Vector2.Distance(new Vector2(bdLeft, bdTop), circle.center) >= circle.radius
                    || Vector2.Distance(new Vector2(bdLeft, bdBottom), circle.center) >= circle.radius
                    || Vector2.Distance(new Vector2(bdRight, bdTop), circle.center) >= circle.radius
                    || Vector2.Distance(new Vector2(bdRight, bdBottom), circle.center) >= circle.radius;
            }
            else
            {
                return false;
            }
        }

        /// <summary>
        /// 值是否在范围内
        /// </summary>
        public static bool IsValueInRange(double value, double min, double max)
        {
            return value >= min && value <= max;
        }

        /// <summary>
        /// 规整化弧度
        /// 返回值范围:[0, 2*PI)
        /// </summary>
        public static double NormalizeRadianAngle(double rad)
        {
            double value = rad % (2 * Utils.PI);
            if (value < -0.001)
                value += 2 * Utils.PI;
            return value;
        }

        /// <summary>
        /// 镜像矩阵
        /// </summary>
        public static Matrix3 MirrorMatrix(Line2 mirrorLine)
        {
            Vector2 lineDir = mirrorLine.direction;
            Matrix3 matPos1 = Matrix3.Translate(-mirrorLine.startPoint);
            double rotAngle = Vector2.SignedAngle(lineDir, new Vector2(1, 0));
            Matrix3 matRot1 = Matrix3.Rotate(rotAngle);

            Matrix3 mirrorMatX = new Matrix3(
                1, 0, 0,
                0, -1, 0,
                0, 0, 1);

            Matrix3 matRot2 = Matrix3.Rotate(-rotAngle);
            Matrix3 matPos2 = Matrix3.Translate(mirrorLine.startPoint);

            return matPos2 * matRot2 * mirrorMatX * matRot1 * matPos1;
        }

        /// <summary>
        /// 在平面中，一个点绕任意点逆时针旋转angle度后的点的坐标
        /// </summary>
        public static Vector2 AntiRotatePoint(Vector2 point, Vector2 center, double angle)
        {
            //假设对图片上任意点(x,y)，绕一个坐标点(rx0,ry0)逆时针旋转a角度后的新的坐标设为(x0, y0)，有公式：
            //x0 = (x - rx0) * cos(a) - (y - ry0) * sin(a) + rx0;
            //y0 = (x - rx0) * sin(a) + (y - ry0) * cos(a) + ry0;
            Vector2 newPoint = new Vector2(0, 0);
            newPoint.x = (point.x - center.x) * Math.Cos(angle) - (point.y - center.y) * Math.Sin(angle) + center.x;
            newPoint.y = (point.x - center.x) * Math.Sin(angle) + (point.y - center.y) * Math.Cos(angle) + center.y;
            return newPoint;
        }


        /// <summary>
        /// 求直线的垂直分段点
        /// </summary>
        /// <param name="Line">原线</param>
        /// <param name="pro">分段点</param>
        /// <param name="distance">距离</param>
        /// <returns>终点坐标</returns>
        public static Vector2 GetVerticalPonit(Line line, double pro, double distance)
        {
            Vector2 start = line.startPoint;
            Vector2 end = line.endPoint;

            double value1 = Math.Sqrt(Math.Abs(start.x - end.x) * Math.Abs(start.x - end.x) + Math.Abs(start.y - end.y) * Math.Abs(start.y - end.y));

            double angle = PointToAngle(start, end);

            double xx = 180 * angle / Math.PI;//转换为角度值45deg

            double value = value1 * pro;
            float x = (float)(Math.Cos(angle) * value + start.x);
            float y = (float)(Math.Sin(angle) * value + start.y);

            Vector2 ResultPoint = new Vector2(0, 0);

            //角度转弧度，加90，以垂直于原线
            var radian = (xx + 90 * Math.PI) / 180;

            //计算新坐标 r 就是两者的距离
            ResultPoint.x = x + distance * Math.Cos(radian);
            ResultPoint.y = y + distance * Math.Sin(radian);
            return ResultPoint;
        }

        /// <summary>
        /// 一条直线的与水平线的角度
        /// </summary>
        /// <param name="AOrigin">起点</param>
        /// <param name="APoint">终点</param>
        /// <returns></returns>
        public static double PointToAngle(Vector2 AOrigin, Vector2 APoint)
        {
            if (APoint.x == AOrigin.x)
            {
                if (APoint.y > AOrigin.y)
                {
                    return Math.PI * 0.5f;
                }
                else
                {
                    return Math.PI * 1.5f;
                }
            }
            else if (APoint.y == AOrigin.y)
            {
                if (APoint.x > AOrigin.x)
                {
                    return 0;
                }
                else
                {
                    return Math.PI;
                }
            }
            else
            {
                double Result = Math.Atan((double)(AOrigin.y - APoint.y) / (AOrigin.x - APoint.x));
                if ((APoint.x < AOrigin.x) && (APoint.y > AOrigin.y))
                {
                    return Result + Math.PI;
                }
                else if ((APoint.x < AOrigin.x) && (APoint.y < AOrigin.y))
                {
                    return Result + Math.PI;
                }
                else if ((APoint.x > AOrigin.x) && (APoint.y < AOrigin.y))
                {
                    return Result + 2 * Math.PI;
                }
                else
                {
                    return Result;
                }
            }
        }


        /// <summary>
        /// 一条直线的与水平线的角度，必定0到180
        /// </summary>
        public static double PointToAngleMin(Vector2 AOrigin, Vector2 APoint)
        {
            double temp = 0;
            if (APoint.x == AOrigin.x)
            {
                if (APoint.y > AOrigin.y)
                {
                    temp = Math.PI * 0.5f;
                }
                else
                {
                    temp = Math.PI * 1.5f;
                }
            }
            else if (APoint.y == AOrigin.y)
            {
                if (APoint.x > AOrigin.x)
                {
                    temp = 0;
                }
                else
                {
                    temp = Math.PI;
                }
            }
            else
            {
                double Result = Math.Atan((double)(AOrigin.y - APoint.y) / (AOrigin.x - APoint.x));
                if ((APoint.x < AOrigin.x) && (APoint.y > AOrigin.y))
                {
                    temp = Result + Math.PI;
                }
                else if ((APoint.x < AOrigin.x) && (APoint.y < AOrigin.y))
                {
                    temp = Result + Math.PI;
                }
                else if ((APoint.x > AOrigin.x) && (APoint.y < AOrigin.y))
                {
                    temp = Result + 2 * Math.PI;
                }
                else
                {
                    temp = Result;
                }
            }
            if (temp < 0)
            {
                temp = temp + 2 * Math.PI;
            }
            temp = temp % Math.PI;

            //if (temp > (Math.PI / 2))
            //{
            //    temp = Math.PI - temp;
            //}
            return temp;
        }

        /// <summary>
        /// 根据中心点、半径、角度，求半径另一端的坐标。注意用的是笛卡尔坐标系  
        /// </summary>  
        /// <param name="center">中心点</param>  
        /// <param name="angle">半径角度</param>  
        /// <param name="radius">半径长度</param>  
        /// <returns>半径另一端的坐标</returns>  
        public static Vector2 CenterRadiusPoint(Vector2 center, double angle, double radius)
        {
            Vector2 p = new Vector2();
            p.y = center.y + radius * Math.Sin(angle);
            p.x = center.x + radius * Math.Cos(angle);
            return p;
        }

        /// <summary>
        /// 一段弧线上的分段点，包括半径伸缩
        /// </summary>
        /// <param name="arc">弧线</param>
        /// <param name="pro">分段点，以起始角度为准</param>
        /// <param name="RadiusPro">半径比例，不变化则填写1</param>
        /// <returns></returns>
        public static Vector2 GetArcPoint(Arc arc, double pro, double RadiusPro)
        {
            double angle = 0;
            if (arc.endAngle > arc.startAngle)
            {
                double temp = arc.endAngle - arc.startAngle;
                angle = arc.startAngle + temp * pro;
            }
            else
            {
                double temp = arc.endAngle - arc.startAngle + Math.PI * 2;
                angle = arc.startAngle + temp * pro;
            }
            return CenterRadiusPoint(arc.center, angle, arc.radius * RadiusPro);
        }

        /// <summary>
        /// 两条直线的交叉点
        /// </summary>
        public static Vector2 GetIntersectionPoint(Line line1, Line line2)
        {
            double x1 = line1.startPoint.x;
            double y1 = line1.startPoint.y;
            double x2 = line1.endPoint.x;
            double y2 = line1.endPoint.y;
            double x3 = line2.startPoint.x;
            double y3 = line2.startPoint.y;
            double x4 = line2.endPoint.x;
            double y4 = line2.endPoint.y;

            var a = (y2 - y1) / (x2 - x1); //需考虑分母不能为0 即x2=x1 l1垂直于x轴
            var b = (y4 - y3) / (x4 - x3); //需考虑分母不能为0 即x4=x3 l2垂直于x轴
            if (a == b)
            {//斜率相同,说明平行 无交点
                throw new Exception("两直线平行,无交点");
            }
            double x, y = 0;
            if (x2 == x1)
            {//L1垂直于x轴  则x=x1=x2 a=infinity 想办法消除a
                x = x1;
                ////(y-y3)/(x-x3)=b 且x=x1 变换得y=bx1-bx3+y3
                y = b * x1 - b * x3 + y3;
                return new Vector2( x, y);
            }
            if (x4 == x3)
            {//L2垂直于x轴 则x=x3=x4 b=infinity 
                x = x3;
                y = a * x - a * x1 + y1;
                return new Vector2(x, y);
            }
            x = (a * x1 - y1 + y3 - b * x3) / (a - b);
            y = a * x - a * x1 + y1;
            return new Vector2(x, y);
        }

        /// <summary>
        /// 计算点到线段的最短距离
        /// </summary>
        public static double pointToLine(Vector2 point, Line line)
        {
            //距离
            double distance = 0;
            //线段的起点与终点
            Vector2 start = new Vector2(line.startPoint.x, line.startPoint.y);
            Vector2 end = new Vector2(line.endPoint.x, line.endPoint.y);
            //点到起点的距离
            double startlength = Vector2.Distance(point, start);
            //点到终点的距离
            double endlength = Vector2.Distance(point, end);
            //线段的长度
            double length = Vector2.Distance(start, end);
            //点到线端两端的距离很小
            if (startlength <= 0.00001 || endlength < 0.00001)
            {
                distance = 0;
                return distance;
            }
            //如果线段很短
            if (length < 0.00001)
            {
                distance = startlength;
                return distance;
            }
            //如果在线段延长线的两边
            if (startlength * startlength >= length * length + endlength * endlength)
            {
                distance = endlength;
                return distance;
            }
            if (length * length + startlength * startlength <= endlength * endlength)
            {
                distance = startlength;
                return distance;
            }
            //最后利用三角形的面积求高（点到垂足的距离）
            double p = (length + startlength + endlength) / 2;
            //求三角形面积
            double area = Math.Sqrt(p * (p - endlength) * (p - startlength) * (p - length));

            distance = 2 * area / length;
            return distance;
        }

        public static double GetDist(double ax, double ay, double bx, double by, double x, double y)
        {
            if ((ax - bx) * (x - bx) + (ay - by) * (y - by) <= 0)
                return Math.Sqrt((x - bx) * (x - bx) + (y - by) * (y - by));

            if ((bx - ax) * (x - ax) + (by - ay) * (y - ay) <= 0)
                return Math.Sqrt((x - ax) * (x - ax) + (y - ay) * (y - ay));

            return Math.Abs((by - ay) * x - (bx - ax) * y + bx * ay - by * ax) /
                Math.Sqrt((ay - by) * (ay - by) + (ax - bx) * (ax - bx));
        }

    }
}
