using System;

using MsCAD.ApplicationServices;
using MsCAD.DatabaseServices;
using MsMath;

namespace MsCAD
{
    internal class PolylineRS : EntityRS
    {
        internal override bool Cross(Bounding selectBound, Entity entity)
        {
            Polyline polyline = entity as Polyline;
            if (polyline == null)
            {
                return false;
            }

            Bounding polylineBound = polyline.bounding;
            if (selectBound.Contains(polylineBound))
            {
                return true;
            }

            MsMath.Rectangle2 selRect = new MsMath.Rectangle2(
                new Vector2(selectBound.left, selectBound.bottom),
                new Vector2(selectBound.right, selectBound.top));

            Line2 rectLine1 = new Line2(selRect.leftBottom, selRect.leftTop);
            Line2 rectLine2 = new Line2(selRect.leftTop, selRect.rightTop);
            Line2 rectLine3 = new Line2(selRect.rightTop, selRect.rightBottom);
            Line2 rectLine4 = new Line2(selRect.rightBottom, selRect.leftBottom);

            for (int i = 1; i < polyline.NumberOfVertices; ++i)
            {
                Vector2 spnt = polyline.GetPointAt(i - 1);
                Vector2 epnt = polyline.GetPointAt(i);
                Line2 line2 = new Line2(spnt, epnt);
                Vector2 intersection = new Vector2();
                if (Line2.Intersect(rectLine1, line2, ref intersection)
                    || Line2.Intersect(rectLine2, line2, ref intersection)
                    || Line2.Intersect(rectLine3, line2, ref intersection)
                    || Line2.Intersect(rectLine4, line2, ref intersection))
                {
                    return true;
                }
            }

            return false;
        }
    }
}
