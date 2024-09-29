using System;

using MsCAD.DatabaseServices;
using MsMath;

namespace MsCAD.DBUtils
{
    internal class ArcUtils
    {
        public static Vector2 ArcMiddlePoint(Arc arc)
        {
            double angle = 0;
            if (arc.endAngle >= arc.startAngle)
            {
                angle = (arc.startAngle + arc.endAngle) / 2;
            }
            else
            {
                angle = (arc.startAngle + arc.endAngle + Utils.PI * 2) / 2;
            }
            return arc.center + Vector2.RotateInRadian(
                 new Vector2(arc.radius, 0), angle);
        }
    }
}
