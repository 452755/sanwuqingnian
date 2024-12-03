using System;
using System.Collections.Generic;

using MsCAD.DatabaseServices;
using MsCAD.UI;
using MsMath;

namespace MsCAD.UI
{
    internal class PolylineHitter : EntityHitter
    {
        internal override bool Hit(PickupBox pkbox, Entity entity)
        {
            Polyline polyline = entity as Polyline;
            if (polyline == null)
                return false;

            Bounding pkBounding = pkbox.reservedBounding;
            for (int i = 0; i < polyline.NumberOfVertices - 1; ++i)
            {
                if (polyline._BulgeList[i] == 0)
                {
                    Line2 line = new Line2(polyline.GetPointAt(i), polyline.GetPointAt(i + 1));

                    if (LineHitter.BoundingIntersectWithLine(pkBounding, line))
                    {
                        return true;
                    }
                }
                else
                {
                    continue;
                }
            }

            return false;
        }
    }
}
