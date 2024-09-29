using System;

using MsCAD.DatabaseServices;

namespace MsCAD
{
    internal class CircleRS : EntityRS
    {
        internal override bool Cross(Bounding selectBound, Entity entity)
        {
            Circle circle = entity as Circle;
            if (circle == null)
            {
                return false;
            }

            return MathUtils.BoundingCross(selectBound, circle);
        }
    }
}
