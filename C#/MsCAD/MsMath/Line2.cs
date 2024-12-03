using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MsMath
{
    using System;
    using System.Runtime.InteropServices;

    [StructLayout(LayoutKind.Sequential)]
    public struct Line2
    {
        public Vector2 startPoint;
        public Vector2 endPoint;
        public Line2(Vector2 startPnt, Vector2 endPnt)
        {
            this.startPoint = startPnt;
            this.endPoint = endPnt;
        }

        public Vector2 centerPoint
        {
            get
            {
                return new Vector2((this.startPoint.x + this.endPoint.x) / 2.0, (this.startPoint.y + this.endPoint.y) / 2.0);
            }
        }
        public Vector2 direction
        {
            get
            {
                Vector2 vector = this.endPoint - this.startPoint;
                return vector.normalized;
            }
        }
        public double length
        {
            get
            {
                Vector2 vector = this.endPoint - this.startPoint;
                return vector.length;
            }
        }
        public override string ToString()
        {
            return string.Format("Line2(({0}, {1}), ({2}, {3})", new object[] { this.startPoint.x, this.startPoint.y, this.endPoint.x, this.endPoint.y });
        }

        public static bool Intersect(Line2 line1st, Line2 line2nd, ref Vector2 intersection)
        {
            Vector2 startPoint = line1st.startPoint;
            Vector2 a = line1st.endPoint - line1st.startPoint;
            Vector2 vector3 = line2nd.startPoint;
            Vector2 b = line2nd.endPoint - line2nd.startPoint;
            double x = Vector2.Cross(a, b);
            if (!Utils.IsEqualZero(x))
            {
                double num2 = Vector2.Cross(vector3 - startPoint, b) / x;
                double num3 = Vector2.Cross(vector3 - startPoint, a) / x;
                if (((num2 >= 0.0) && (num2 <= 1.0)) && ((num3 >= 0.0) && (num3 <= 1.0)))
                {
                    intersection = startPoint + (num2 * a);
                    return true;
                }
            }
            return false;
        }
    }
}
