using System;

using MsCAD.ApplicationServices;
using MsCAD.DatabaseServices;

namespace MsCAD
{
    internal abstract class EntityRS
    {
        internal abstract bool Cross(Bounding bounding, Entity entity);
        internal virtual bool Window(Bounding bounding, Entity entity)
        {
            return bounding.Contains(entity.bounding);
        }
    }
}
