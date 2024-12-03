using System;
using System.Collections.Generic;
using MsMath;

namespace MsCAD.DatabaseServices
{
    /// <summary>
    /// 多段线
    /// </summary>
    public class Rectangle2 : Entity
    {
        /// <summary>
        /// 类名
        /// </summary>
        public override string className
        {
            get { return "Rectangle2"; }
        }
        /// <summary>
        /// 中心点
        /// </summary>
        private Vector2 _center = new Vector2();
        public Vector2 center
        {
            get { return _center; }
            set { _center = value; }
        }
        /// <summary>
        /// 端点集合
        /// </summary>
        private List<Vector2> _endpoint = new List<Vector2>();
        public List<Vector2> endpoint
        {
            get
            {
                if (_endpoint.Count == 0)
                    GetGripPoints();

                return _endpoint;
            }
            set { _endpoint = value; }
        }

        /// <summary>
        /// 角度
        /// </summary>
        private double _angle = 0.0;
        public double angle
        {
            get { return _angle; }
            set
            {
                _angle = MathUtils.NormalizeRadianAngle(value);
            }
        }
        /// <summary>
        /// 短边
        /// </summary>
        private double _hwidth = 0.0;
        public double hwidth
        {
            get { return _hwidth; }
            set { _hwidth = value; }
        }
        /// <summary>
        /// 长边
        /// </summary>
        private double _hheight = 0.0;
        public double hheight
        {
            get { return _hheight; }
            set { _hheight = value; }
        }
        /// <summary>
        /// 绘制函数
        /// </summary>
        public override void Draw(IGraphicsDraw gd)
        {
            gd.DrawRectangle2(_center, _angle, _hwidth, _hheight);
        }
        
        /// <summary>
        /// 外围边框
        /// </summary>
        public override Bounding bounding
        {
            get
            {
                return new Bounding( _center, this.hwidth * 2 , this.hheight * 2 );
            }
        }

        /// <summary>
        /// 克隆函数
        /// </summary>
        public override object Clone()
        {
            Rectangle2 rectangle2 = base.Clone() as Rectangle2;
            rectangle2._center = _center;
            rectangle2._angle = _angle;
            rectangle2._hwidth = _hwidth;
            rectangle2._hheight = _hheight;

            return rectangle2;
        }

        protected override DBObject CreateInstance()
        {
            return new Rectangle2();
        }

        /// <summary>
        /// 平移
        /// </summary>
        public override void Translate(Vector2 translation)
        {
            _center += translation;
        }

        /// <summary>
        /// Transform
        /// </summary>
        public override void TransformBy(Matrix3 transform)
        {
            _center = transform * _center;
        }

        /// <summary>
        /// 对象捕捉点
        /// </summary>
        public override List<ObjectSnapPoint> GetSnapPoints()
        {
            List<ObjectSnapPoint> snapPnts = new List<ObjectSnapPoint>();
            for (int i = 0; i < _endpoint.Count; ++i)
            {
                if (i < 4) snapPnts.Add(new ObjectSnapPoint(ObjectSnapMode.End, _endpoint[i]));
                if (i == 5) snapPnts.Add(new ObjectSnapPoint(ObjectSnapMode.Angle, _endpoint[i]));
                else snapPnts.Add(new ObjectSnapPoint(ObjectSnapMode.Mid, _endpoint[i]));
            }
            return snapPnts;
        }

        /// <summary>
        /// 获取夹点
        /// </summary>
        public override List<GripPoint> GetGripPoints()
        {
            List<GripPoint> gripPnts = new List<GripPoint>();

            gripPnts.Add(new GripPoint(GripPointType.Center, _center));

            if (_endpoint == null) _endpoint = new List<Vector2>();
            else _endpoint.Clear();

            Vector2 point1 = new Vector2(_center.x - hwidth, _center.y + hheight);
            point1 = AntiRotatePoint(point1, _center, -angle);
            gripPnts.Add(new GripPoint(GripPointType.End, point1));
            _endpoint.Add(point1);

            Vector2 point2 = new Vector2(_center.x + hwidth, _center.y + hheight);
            point2 = AntiRotatePoint(point2, _center, -angle);
            gripPnts.Add(new GripPoint(GripPointType.End, point2));
            _endpoint.Add(point2);

            Vector2 point3 = new Vector2(_center.x + hwidth, _center.y - hheight);
            point3 = AntiRotatePoint(point3, _center, -angle);
            gripPnts.Add(new GripPoint(GripPointType.End, point3));
            _endpoint.Add(point3);

            Vector2 point4 = new Vector2(_center.x - hwidth, _center.y - hheight);
            point4 = AntiRotatePoint(point4, _center, -angle);
            gripPnts.Add(new GripPoint(GripPointType.End, point4));
            _endpoint.Add(point4);

            Vector2 point5 = new Vector2(_center.x, _center.y + hheight);
            point5 = AntiRotatePoint(point5, _center, -angle);
            GripPoint midGripPnt = new GripPoint(GripPointType.Mid, point5);
            midGripPnt.xData1 = point1;
            midGripPnt.xData2 = point2;
            gripPnts.Add(midGripPnt);
            _endpoint.Add(point5);

            Vector2 point6 = new Vector2(_center.x + hwidth, _center.y);
            point6 = AntiRotatePoint(point6, _center, -angle);
            midGripPnt = new GripPoint(GripPointType.Angle, point6);
            midGripPnt.xData1 = point2;
            midGripPnt.xData2 = point3;
            gripPnts.Add(midGripPnt);
            _endpoint.Add(point6);

            Vector2 point7 = new Vector2(_center.x, _center.y - hheight);
            point7 = AntiRotatePoint(point7, _center, -angle);
            midGripPnt = new GripPoint(GripPointType.Mid, point7);
            midGripPnt.xData1 = point3;
            midGripPnt.xData2 = point4;
            gripPnts.Add(midGripPnt);
            _endpoint.Add(point7);

            Vector2 point8 = new Vector2(_center.x - hwidth, _center.y);
            point8 = AntiRotatePoint(point8, _center, -angle);
            midGripPnt = new GripPoint(GripPointType.Mid, point8);
            midGripPnt.xData1 = point4;
            midGripPnt.xData2 = point1;
            gripPnts.Add(midGripPnt);
            _endpoint.Add(point8);

            return gripPnts;
        }
        /// <summary>
        /// 在平面中，一个点绕任意点逆时针旋转angle度后的点的坐标
        /// </summary>
        internal static Vector2 AntiRotatePoint(Vector2 point, Vector2 center, double angle)
        {
            //假设对图片上任意点(x,y)，绕一个坐标点(rx0,ry0)逆时针旋转a角度后的新的坐标设为(x0, y0)，有公式：
            //x0 = (x - rx0) * cos(a) - (y - ry0) * sin(a) + rx0;
            //y0 = (x - rx0) * sin(a) + (y - ry0) * cos(a) + ry0;
            Vector2 newPoint = new Vector2(0, 0);
            newPoint.x = (point.x - center.x) * Math.Cos(angle) - (point.y - center.y) * Math.Sin(angle) + center.x;
            newPoint.y = (point.x - center.x) * Math.Sin(angle) + (point.y - center.y) * Math.Cos(angle) + center.y;
            return newPoint;
        }

        /// <summary>
        /// 设置夹点
        /// </summary>
        public override void SetGripPointAt(int index, GripPoint gripPoint, Vector2 newPosition)
        {
            //switch (gripPoint.type)
            //{
            //    case GripPointType.End:
            //        {
            //            this.SetPointAt(index, newPosition);
            //        }
            //        break;

            //    case GripPointType.Mid:
            //        {
            //            int numOfVertices = NumberOfVertices;
            //            int i = index - numOfVertices;
            //            if (i >= 0 && i <= numOfVertices-1)
            //            {
            //                int vIndex1st = i;
            //                int vIndex2nd = i + 1;
            //                if (vIndex2nd == numOfVertices)
            //                {
            //                    vIndex2nd = 0;
            //                }
            //                LitMath.Vector2 t = newPosition - gripPoint.position;
            //                this.SetPointAt(vIndex1st, (LitMath.Vector2)gripPoint.xData1 + t);
            //                this.SetPointAt(vIndex2nd, (LitMath.Vector2)gripPoint.xData2 + t);
            //            }
            //        }
            //        break;

            //    default:
            //        break;
            //}
        }

        /// <summary>
        /// 写XML
        /// </summary>
        public override void XmlOut(Filer.XmlFiler filer)
        {
            base.XmlOut(filer);
            
            filer.Write("center", _center);
            filer.Write("angle", _angle);
            filer.Write("hwidth", _hwidth);
            filer.Write("hheight", _hheight);
        }

        /// <summary>
        /// 读XML
        /// </summary>
        public override void XmlIn(Filer.XmlFiler filer)
        {
            base.XmlIn(filer);

            filer.Read("center",out _center);
            filer.Read("angle", out _angle);
            filer.Read("hwidth", out _hwidth);
            filer.Read("hheight", out _hheight);
        }
    }
}
