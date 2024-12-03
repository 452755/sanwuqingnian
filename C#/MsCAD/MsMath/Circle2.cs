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
    public struct Circle2
    {
        public Vector2 center;
        public double radius;
        public double diameter
        {
            get
            {
                return (this.radius * 2.0);
            }
        }
        public Circle2(Vector2 center, double radius)
        {
            this.center = center;
            this.radius = radius;
        }

        public override string ToString()
        {
            return string.Format("Circle2(({0}, {1}), {2})", this.center.x, this.center.y, this.radius);
        }

        public static Circle2 From3Points(Vector2 pnt1, Vector2 pnt2, Vector2 pnt3)
        {
            Circle2 circle = new Circle2();
            double num = pnt1.x - pnt2.x;
            double num2 = pnt1.y - pnt2.y;
            double num3 = pnt1.x - pnt3.x;
            double num4 = pnt1.y - pnt3.y;
            double num5 = ((((pnt1.x * pnt1.x) - (pnt2.x * pnt2.x)) + (pnt1.y * pnt1.y)) - (pnt2.y * pnt2.y)) / 2.0;
            double num6 = ((((pnt1.x * pnt1.x) - (pnt3.x * pnt3.x)) + (pnt1.y * pnt1.y)) - (pnt3.y * pnt3.y)) / 2.0;
            double num7 = (num2 * num3) - (num * num4);
            if (Math.Abs(num7) < 1E-05)
            {
                circle.center = new Vector2(0.0, 0.0);
                circle.radius = -1.0;
            }
            circle.center.x = -((num4 * num5) - (num2 * num6)) / num7;
            circle.center.y = -((num * num6) - (num3 * num5)) / num7;
            circle.radius = Math.Sqrt(((pnt1.x - circle.center.x) * (pnt1.x - circle.center.x)) + ((pnt1.y - circle.center.y) * (pnt1.y - circle.center.y)));
            return circle;
        }
    }
}
