using System;

using MsCAD.ApplicationServices;
using MsCAD.DatabaseServices;

namespace MsCAD
{
    internal class RayRS : EntityRS
    {
        internal override bool Cross(Bounding selectBound, Entity entity)
        {
            Ray ray = entity as Ray;
            if (ray == null)
            {
                return false;
            }

            return MsCAD.UI.RayHitter.BoundingIntersectWithRay(selectBound, ray);
        }
    }
}
