using System;
using System.Collections.Generic;

using MsCAD.DatabaseServices;
using MsMath;

namespace MsCAD
{
    internal class TextRS : EntityRS
    {
        internal override bool Cross(Bounding selectBound, Entity entity)
        {
            Text text = entity as Text;
            if (text == null)
            {
                return false;
            }

            Bounding textBound = text.bounding;
            if (selectBound.Contains(textBound))
            {
                return true;
            }

            if(selectBound.center.x>= textBound.left && selectBound.center.x <= textBound.right && selectBound.center.y <= textBound.top && selectBound.center.y >= textBound.bottom)
            {
                return true;
            }
            else
            {
                return false;
            }

            //if (textBound.IntersectWith(selectBound))
            //{
            //    return true;
            //}
            //else
            //{
            //    return false;
            //}
        }
    }
}
