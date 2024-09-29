using System;

using MsCAD.DatabaseServices;

namespace MsCAD.UI
{
    internal abstract class EntityHitter
    {
        internal abstract bool Hit(PickupBox pkbox, Entity entity);
    }
}
