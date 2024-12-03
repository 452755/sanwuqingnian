using System;

using MsCAD.ApplicationServices;
using MsCAD.DatabaseServices;

namespace MsCAD
{
    internal class XlineRS : EntityRS
    {
        internal override bool Cross(Bounding selectBound, Entity entity)
        {
            Xline xline = entity as Xline;
            if (xline == null)
            {
                return false;
            }

            return MsCAD.UI.XlineHitter.BoundingIntersectWithXline(selectBound, xline);
        }
    }
}
