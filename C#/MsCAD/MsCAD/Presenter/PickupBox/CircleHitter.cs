using System;
using System.Collections.Generic;

using MsCAD.DatabaseServices;
using MsCAD.UI;

namespace MsCAD.UI
{
    internal class CircleHitter : EntityHitter
    {
        internal override bool Hit(PickupBox pkbox, Entity entity)
        {
            Circle circle = entity as Circle;
            if (circle == null)
                return false;

            return MathUtils.BoundingCross(pkbox.reservedBounding, circle);
        }
    }
}
