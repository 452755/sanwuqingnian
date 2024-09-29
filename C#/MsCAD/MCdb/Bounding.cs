using System;
using System.Collections.Generic;
using System.Linq;
using MsMath;

namespace MsCAD.DatabaseServices
{
    /// <summary>
    /// 矩形包围框
    /// </summary>
    public struct Bounding
    {
        /// <summary>
        /// Center
        /// </summary>
        public Vector2 center
        {
            get
            { 
                return new Vector2(
                    (_left + _right) / 2,
                    (_bottom + _top) / 2); 
            }
        }

        /// <summary>
        /// Width
        /// </summary>
        private double _width;
        public double width
        {
            get { return _width; }
            //set { _width = value; }
        }

        /// <summary>
        /// Height
        /// </summary>
        private double _height;
        public double height
        {
            get { return _height; }
            //set { _height = value; }
        }

        /// <summary>
        /// Left
        /// </summary>
        private double _left;
        public double left
        {
            get { return _left; }
        }

        /// <summary>
        /// Right
        /// </summary>
        private double _right;
        public double right
        {
            get { return _right; }
        }

        /// <summary>
        /// Top
        /// </summary>
        private double _top;
        public double top
        {
            get { return _top; }
        }

        /// <summary>
        /// Bottom
        /// </summary>
        private double _bottom;
        public double bottom
        {
            get { return _bottom; }
        }

        /// <summary>
        /// Constructor
        /// </summary>
        public Bounding(Vector2 point1, Vector2 point2)
        {
            if (point1.x < point2.x)
            {
                _left = point1.x;
                _right = point2.x;
            }
            else
            {
                _left = point2.x;
                _right = point1.x;
            }

            if (point1.y < point2.y)
            {
                _bottom = point1.y;
                _top = point2.y;
            }
            else
            {
                _bottom = point2.y;
                _top = point1.y;
            }

            _width = _right - _left;
            _height = _top - _bottom;
        }

        public Bounding(Vector2 center, double width, double height)
        {
            _left = center.x - width / 2;
            _right = center.x + width / 2;
            _bottom = center.y - height / 2;
            _top = center.y + height / 2;
            _width = _right - _left;
            _height = _top - _bottom;
        }

        /// <summary>
        /// Check whether contains bounding
        /// </summary>
        public bool Contains(Bounding bounding)
        {
            return this.Contains(bounding.left, bounding.bottom)
                && this.Contains(bounding.right, bounding.top);
        }

        /// <summary>
        /// Check whether contains point
        /// </summary>
        public bool Contains(Vector2 point)
        {
            return this.Contains(point.x, point.y);
        }

        /// <summary>
        /// Check whether contains point: (x, y)
        /// </summary>
        public bool Contains(double x, double y)
        {
            return x >= this.left
                && x <= this.right
                && y >= this.bottom
                && y <= this.top;
        }

        /// <summary>
        /// Check whether intersect with bounding
        /// </summary>
        public bool IntersectWith(Bounding bd)
        {
            bool b1 = (bd.left >= this.left && bd.left <= this.right)
                || (bd.right >= this.left && bd.right <= this.right)
                || (bd.left <= this.left && bd.right >= this.right);

            if (b1)
            {
                bool b2 = (this.bottom >= bd.bottom && this.bottom <= bd.top)
                    || (this.top >= bd.bottom && this.top <= bd.top)
                    || (this.bottom <= this.bottom && this.top >= bd.top);
                if (b2)
                {
                    return true;
                }
            }
            return false;

        }

        private bool ValueInRange(double value, double min, double max)
        {
            return value >= min && value <= max;
        }


        /// <summary>
        /// 判断点是否在多边形内.
        /// ----------原理----------
        /// 注意到如果从P作水平向左的射线的话，如果P在多边形内部，那么这条射线与多边形的交点必为奇数，
        /// 如果P在多边形外部，则交点个数必为偶数(0也在内)。
        /// </summary>
        public bool IsInPolygon( List<Vector2> polygonPoints)
        {
            bool inside = false;
            int pointCount = polygonPoints.Count;
            Vector2 p1, p2;
            for (int i = 0, j = pointCount - 1; i < pointCount; j = i, i++)
            {
                p1 = polygonPoints[i];
                p2 = polygonPoints[j];
                if (this.center.y < p2.y)
                {
                    if (p1.y <= this.center.y)
                    {
                        if ((this.center.y - p1.y) * (p2.x - p1.x) > (this.center.x - p1.x) * (p2.y - p1.y))
                        {
                            inside = !inside;
                        }
                    }
                }
                else if (this.center.y < p1.y)
                {
                    if ((this.center.y - p1.y) * (p2.x - p1.x) < (this.center.x - p1.x) * (p2.y - p1.y))
                    {
                        inside = !inside;
                    }
                }
            }
            return inside;
        }

        /// <summary>
        /// 获取鼠标点选与多段线中距离最短的线
        /// </summary>
        public Line GetCloseLine(Polyline polyline)
        {
            Dictionary<Line, double> list = new Dictionary<Line, double>();
            for (int i = 0; i < polyline.NumberOfVertices - 1; ++i)
            {
                if (polyline._BulgeList[i] == 0)
                {
                    Line line = new Line(polyline.GetPointAt(i), polyline.GetPointAt(i + 1));
                    double dist = MathUtils.GetDist(line.startPoint.x, line.startPoint.y, line.endPoint.x, line.endPoint.y, this.center.x, this.center.y);
                    list.Add( line, dist);
                }
                else
                {
                    continue;
                }
            }
            return list.First(v => v.Value == list.Min(x => x.Value)).Key;
        }
    }
}
