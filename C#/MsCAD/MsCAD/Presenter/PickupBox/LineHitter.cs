using System;

using MsCAD.DatabaseServices;
using MsMath;

namespace MsCAD.UI
{
    internal class LineHitter : EntityHitter
    {
        internal override bool Hit(PickupBox pkbox, Entity entity)
        {
            Line line = entity as Line;
            if (line == null)
                return false;

            Bounding pkBounding = pkbox.reservedBounding;
            return LineHitter.BoundingIntersectWithLine(
                pkBounding,
                new Line2(line.startPoint, line.endPoint));
        }

        internal static bool BoundingIntersectWithLine(Bounding bounding, Line2 line)
        {
            Bounding lineBound = new Bounding(line.startPoint, line.endPoint);
            if (!bounding.IntersectWith(lineBound))
            {
                return false;
            }

            if (bounding.Contains(line.startPoint)
                || bounding.Contains(line.endPoint))
            {
                return true;
            }

            Vector2 pkPnt1 = new Vector2(bounding.left, bounding.bottom);
            Vector2 pkPnt2 = new Vector2(bounding.left, bounding.top);
            Vector2 pkPnt3 = new Vector2(bounding.right, bounding.top);
            Vector2 pkPnt4 = new Vector2(bounding.right, bounding.bottom);

            double d1 = Vector2.Cross(line.startPoint - pkPnt1, line.endPoint - pkPnt1);
            double d2 = Vector2.Cross(line.startPoint - pkPnt2, line.endPoint - pkPnt2);
            double d3 = Vector2.Cross(line.startPoint - pkPnt3, line.endPoint - pkPnt3);
            double d4 = Vector2.Cross(line.startPoint - pkPnt4, line.endPoint - pkPnt4);

            if (d1 * d2 <= 0 || d1 * d3 <= 0 || d1 * d4 <= 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
