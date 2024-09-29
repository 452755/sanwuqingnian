using System;

using MsCAD.DatabaseServices;

namespace MsCAD.Commands.Modify.Offset
{
    internal abstract class _OffsetOperation
    {
        public abstract Entity result { get; }

        public abstract bool Do(double value, MsMath.Vector2 refPoint);
    }
}
