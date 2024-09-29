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
    public struct Rectangle2
    {
        public Vector2 location;
        public double width;
        public double height;
        public Vector2 leftBottom
        {
            get
            {
                return this.location;
            }
        }
        public Vector2 leftTop
        {
            get
            {
                return new Vector2(this.location.x, this.location.y + this.height);
            }
        }
        public Vector2 rightTop
        {
            get
            {
                return new Vector2(this.location.x + this.width, this.location.y + this.height);
            }
        }
        public Vector2 rightBottom
        {
            get
            {
                return new Vector2(this.location.x + this.width, this.location.y);
            }
        }
        public Rectangle2(Vector2 location, double width, double height)
        {
            this.location = location;
            this.width = width;
            this.height = height;
        }

        public Rectangle2(Vector2 point1, Vector2 point2)
        {
            double x = (point1.x < point2.x) ? point1.x : point2.x;
            double y = (point1.y < point2.y) ? point1.y : point2.y;
            this.location = new Vector2(x, y);
            this.width = Math.Abs((double)(point2.x - point1.x));
            this.height = Math.Abs((double)(point2.y - point1.y));
        }

        public override string ToString()
        {
            return string.Format("Rectangle2(({0}, {1}), {2}, {3})", new object[] { this.location.x, this.location.y, this.width, this.height });
        }
    }
}
