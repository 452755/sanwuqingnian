using System;

using MsCAD.DatabaseServices;
using MsMath;

namespace MsCAD.UI
{
    internal class RayHitter : EntityHitter
    {
        internal override bool Hit(PickupBox pkbox, Entity entity)
        {
            Ray ray = entity as Ray;
            if (ray == null)
                return false;

            Bounding bounding = pkbox.reservedBounding;
            return BoundingIntersectWithRay(bounding, ray);
        }

        internal static bool BoundingIntersectWithRay(Bounding bounding, Ray ray)
        {
            if (!ray.bounding.IntersectWith(bounding))
            {
                return false;
            }

            Vector2 pkPnt1 = new Vector2(bounding.left, bounding.bottom);
            Vector2 pkPnt2 = new Vector2(bounding.left, bounding.top);
            Vector2 pkPnt3 = new Vector2(bounding.right, bounding.top);
            Vector2 pkPnt4 = new Vector2(bounding.right, bounding.bottom);

            double d1 = Vector2.Cross(pkPnt1 - ray.basePoint, ray.direction);
            double d2 = Vector2.Cross(pkPnt2 - ray.basePoint, ray.direction);
            double d3 = Vector2.Cross(pkPnt3 - ray.basePoint, ray.direction);
            double d4 = Vector2.Cross(pkPnt4 - ray.basePoint, ray.direction);

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
