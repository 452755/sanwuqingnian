using System;
using System.Collections.Generic;

using MsCAD.DatabaseServices;
using MsCAD.UI;
using MsMath;

namespace MsCAD.UI
{
    internal class SolidHitter : EntityHitter
    {
        internal override bool Hit(PickupBox pkbox, Entity entity)
        {
            Solid solid = entity as Solid;
            if (solid == null)
            {
                return false;
            }
            Bounding pkBounding = pkbox.reservedBounding;
            for (int i = 0; i < solid.NumberOfVertices; ++i)
            {
                Line2 line = new Line2(
                    solid.GetPointAt(i - 1),
                    solid.GetPointAt(i));

                if (LineHitter.BoundingIntersectWithLine(pkBounding, line))
                {
                    return true;
                }
            }
            if (pkBounding.IsInPolygon(solid.vertices))
            {
                return true;
            }
            else
            {
                return false;
            }
        }



    }
}
