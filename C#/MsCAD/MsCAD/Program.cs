using MsCAD.DatabaseServices;
using MsMath;
using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace MsCAD.ApplicationServices
{
    internal static class Program
    {
        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        static void Main()
        {
            CreateEnt();
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(Windows.MainWin.Instance);
        }

        private static  void CreateEnt()
        {
            string DimID = Guid.NewGuid().ToString().Replace("-", "");
            Vector2 start = new Vector2(-20.15,-12.06);
            Vector2 end = new Vector2(-20.15, 12.06);
            //测距主线
            Line _Line = new Line();
            _Line.startPoint = start;
            _Line.endPoint = end;

            _Line.dimHandle = DimID;
            //测距主线长度
            double len = Math.Sqrt(Math.Pow((end.y - start.y), 2) + Math.Pow((end.x - start.x), 2));

            //起点端垂直线
            Line _line0 = new Line();
            _line0.startPoint = GetVerticalPonit(_Line, 0.1, len * 0.05);
            _line0.endPoint = GetVerticalPonit(_Line, 0.1, -len * 0.05);

            _line0.dimHandle = DimID;
            //终点端垂直线
            Line _line1 = new Line();
            _line1.startPoint = GetVerticalPonit(_Line, 0.9, len * 0.05);
            _line1.endPoint = GetVerticalPonit(_Line, 0.9, -len * 0.05);

            _line1.dimHandle = DimID;


        }


        /// <param name="Line">原线</param>
        /// <param name="pro">分段点</param>
        /// <param name="distance">距离</param>
        /// <returns>终点坐标</returns>
        private static Vector2 GetVerticalPonit(Line line, double pro, double distance)
        {
            Vector2 start = line.startPoint;
            Vector2 end = line.endPoint;

            double value1 = Math.Sqrt(Math.Abs(start.x - end.x) * Math.Abs(start.x - end.x) + Math.Abs(start.y - end.y) * Math.Abs(start.y - end.y));


            double deltaY = end.y - start.y;
            double deltaX = end.x - start.x;

            double k2 = deltaY / deltaX;
            double k = (double)(start.y - end.y) / (double)(start.x - end.x);
            double k1 = Math.Acos((start.x * end.x + start.y * end.y) / (Math.Sqrt(start.x * start.x + start.y * start.y) * Math.Sqrt(end.x * end.x + end.y * end.y)));
            double angle = Math.Atan(k2);
            double xx = 180 * angle / Math.PI;//转换为角度值45deg
            //if (double.IsInfinity(k))
            //{
            //    angle = -angle;
            //}
            
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
    }
}
