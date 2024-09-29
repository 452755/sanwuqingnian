using System;
using System.Collections.Generic;

using MsCAD.DatabaseServices;
using MsCAD.UI;

namespace MsCAD.UI
{
    internal class ArcHitter : EntityHitter
    {
        internal override bool Hit(PickupBox pkbox, Entity entity)
        {
            Arc arc = entity as Arc;
            if (arc == null)
                return false;

            ArcRS arcRS = new ArcRS();
            return arcRS.Cross(pkbox.reservedBounding, arc);
        }
    }
}
