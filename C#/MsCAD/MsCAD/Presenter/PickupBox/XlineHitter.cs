using System;

using MsCAD.DatabaseServices;
using MsMath;

namespace MsCAD.UI
{
    internal class XlineHitter : EntityHitter
    {
        internal override bool Hit(PickupBox pkbox, Entity entity)
        {
            Xline xline = entity as Xline;
            if (xline == null)
                return false;

            Bounding bounding = pkbox.reservedBounding;
            return BoundingIntersectWithXline(bounding, xline);
        }

        internal static bool BoundingIntersectWithXline(Bounding bounding, Xline xline)
        {
            Vector2 pkPnt1 = new Vector2(bounding.left, bounding.bottom);
            Vector2 pkPnt2 = new Vector2(bounding.left, bounding.top);
            Vector2 pkPnt3 = new Vector2(bounding.right, bounding.top);
            Vector2 pkPnt4 = new Vector2(bounding.right, bounding.bottom);

            double d1 = Vector2.Cross(pkPnt1 - xline.basePoint, xline.direction);
            double d2 = Vector2.Cross(pkPnt2 - xline.basePoint, xline.direction);
            double d3 = Vector2.Cross(pkPnt3 - xline.basePoint, xline.direction);
            double d4 = Vector2.Cross(pkPnt4 - xline.basePoint, xline.direction);

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
