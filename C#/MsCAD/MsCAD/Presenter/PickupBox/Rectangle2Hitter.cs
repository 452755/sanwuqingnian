using System;
using System.Collections.Generic;

using MsCAD.DatabaseServices;
using MsCAD.UI;

namespace MsCAD.UI
{
    internal class Rectangle2Hitter : EntityHitter
    {
        internal override bool Hit(PickupBox pkbox, Entity entity)
        {
            Rectangle2 rectangle2 = entity as Rectangle2;
            if (rectangle2 == null)
                return false;

            Bounding pkBounding = pkbox.reservedBounding;
            for (int i = 1; i < rectangle2.endpoint.Count; ++i)
            {
                MsMath.Line2 line = new MsMath.Line2(
                    rectangle2.endpoint[i-1],
                    rectangle2.endpoint[i]);

                if (LineHitter.BoundingIntersectWithLine(pkBounding, line))
                {
                    return true;
                }
            }
            return false;
        }
    }
}
