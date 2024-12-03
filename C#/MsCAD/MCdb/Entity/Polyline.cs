using System;
using System.Collections.Generic;
using MsMath;

namespace MsCAD.DatabaseServices
{
    /// <summary>
    /// 多段线，含Polyline【纯直线】和LwPolyline【直线或弧线】
    /// </summary>
    public class Polyline : Entity
    {
        /// <summary>
        /// 类名
        /// </summary>
        public override string className
        {
            get { return "Polyline"; }
        }

        private List<Vector2> _vertices = new List<Vector2>();
        /// <summary>
        /// 凸度，0为直线，非0为arc
        /// </summary>
        public List<double> _BulgeList = new List<double>();
        private bool _closed = false;
        /// <summary>
        /// 0--Polyline，1--LwPolyline
        /// </summary>
        private int _type = 0;

        public int NumberOfVertices
        {
            get
            {
                return _vertices.Count;
            }
        }

        /// <summary>
        /// 是否闭合
        /// </summary>
        public bool closed
        {
            get { return _closed; }
            set { _closed = value; }
        }

        /// <summary>
        /// 绘制函数
        /// </summary>
        public override void Draw(IGraphicsDraw gd)
        {
            int numOfVertices = NumberOfVertices;
            for (int i = 0; i < numOfVertices - 1; ++i)
            {
                if (_BulgeList[i] == 0)
                {
                    gd.DrawLine(GetPointAt(i), GetPointAt(i + 1));
                }
                else
                {

                    Vector2 spnt = GetPointAt(i);
                    Vector2 epnt = GetPointAt(i+1);
                    double b = 0.5 * (1 / _BulgeList[i] - _BulgeList[i]); //bulge为凸度
                    Vector2 _center = new MsMath.Vector2();
                    _center.x = 0.5 * (spnt.x + epnt.x - (epnt.y - spnt.y) * b);
                    _center.y = 0.5 * (spnt.y + epnt.y + (epnt.x - spnt.x) * b);
                    double _radius = (spnt - _center).length;
                    double sAngle = Vector2.SignedAngleInRadian(new Vector2(1, 0), spnt - _center);
                    double eAngle = Vector2.SignedAngleInRadian(new Vector2(1, 0), epnt - _center);
                    if (_BulgeList[i] > 0)
                    {
                        gd.DrawArc(_center, _radius, sAngle, eAngle);
                    }
                    else
                    {
                        gd.DrawArc(_center, _radius, eAngle, sAngle);
                    }    
                }
            }
            //闭合线
            if (closed && numOfVertices > 2)
            {
                if (_BulgeList[numOfVertices - 1] == 0)
                {
                    gd.DrawLine(GetPointAt(numOfVertices - 1), GetPointAt(0));
                }
                else
                {
                    Vector2 spnt = GetPointAt(numOfVertices - 1);
                    Vector2 epnt = GetPointAt(0);
                    double b = 0.5 * (1 / _BulgeList[numOfVertices - 1] - _BulgeList[numOfVertices - 1]); //bulge为凸度
                    Vector2 _center = new MsMath.Vector2();
                    _center.x = 0.5 * (spnt.x + epnt.x - (epnt.y - spnt.y) * b);
                    _center.y = 0.5 * (spnt.y + epnt.y + (epnt.x - spnt.x) * b);

                    double _radius = (spnt - _center).length;

                    double sAngle = Vector2.SignedAngleInRadian(new Vector2(1, 0), spnt - _center);
                    double eAngle = Vector2.SignedAngleInRadian(new Vector2(1, 0), epnt - _center);
                    if (_BulgeList[numOfVertices - 1] > 0)
                    {
                        gd.DrawArc(_center, _radius, sAngle, eAngle);
                    }
                    else
                    {
                        gd.DrawArc(_center, _radius, eAngle, sAngle);
                    }
                }
            }
        }

        public void AddVertexAt(int index, Vector2 point)
        {
            _vertices.Insert(index, point);
        }

        public void RemoveVertexAt(int index)
        {
            _vertices.RemoveAt(index);
        }

        public Vector2 GetPointAt(int index)
        {
            if (index < 0)
                index = 0;
            return _vertices[index];
        }

        public void SetPointAt(int index, Vector2 point)
        {
            _vertices[index] = point;
        }

        /// <summary>
        /// 外围边框
        /// </summary>
        public override Bounding bounding
        {
            get
            {
                if (_vertices.Count > 0)
                {
                    double minX = double.MaxValue;
                    double minY = double.MaxValue;
                    double maxX = double.MinValue;
                    double maxY = double.MinValue;

                    foreach (Vector2 point in _vertices)
                    {
                        minX = point.x < minX ? point.x : minX;
                        minY = point.y < minY ? point.y : minY;

                        maxX = point.x > maxX ? point.x : maxX;
                        maxY = point.y > maxY ? point.y : maxY;
                    }

                    return new Bounding(new Vector2(minX, minY), new Vector2(maxX, maxY));
                }
                else
                {
                    return new Bounding();
                }
            }
        }

        /// <summary>
        /// 克隆函数
        /// </summary>
        public override object Clone()
        {
            Polyline polyline = base.Clone() as Polyline;
            polyline._vertices.AddRange(_vertices);
            polyline._closed = _closed;
            polyline._BulgeList = _BulgeList;
            polyline._type = _type;

            return polyline;
        }

        protected override DBObject CreateInstance()
        {
            return new Polyline();
        }

        /// <summary>
        /// 平移
        /// </summary>
        public override void Translate(Vector2 translation)
        {
            for (int i = 0; i < this.NumberOfVertices; ++i)
            {
                _vertices[i] += translation;
            }
        }

        /// <summary>
        /// Transform
        /// </summary>
        public override void TransformBy(Matrix3 transform)
        {
            for (int i = 0; i < this.NumberOfVertices; ++i)
            {
                _vertices[i] = transform * _vertices[i];
            }
        }

        /// <summary>
        /// 对象捕捉点
        /// </summary>
        public override List<ObjectSnapPoint> GetSnapPoints()
        {
            List<ObjectSnapPoint> snapPnts = new List<ObjectSnapPoint>();
            int numOfVertices = this.NumberOfVertices;
            for (int i = 0; i < numOfVertices; ++i)
            {
                snapPnts.Add(new ObjectSnapPoint(ObjectSnapMode.End, GetPointAt(i)));

                if (i != numOfVertices - 1)
                {
                    snapPnts.Add(
                        new ObjectSnapPoint(ObjectSnapMode.Mid, (GetPointAt(i) + GetPointAt(i + 1)) / 2));
                }
            }

            return snapPnts;
        }

        /// <summary>
        /// 获取夹点
        /// </summary>
        public override List<GripPoint> GetGripPoints()
        {
            List<GripPoint> gripPnts = new List<GripPoint>();
            int numOfVertices = NumberOfVertices;
            for (int i = 0; i < numOfVertices; ++i)
            {
                gripPnts.Add(new GripPoint(GripPointType.End, _vertices[i]));
            }
            for (int i = 0; i < numOfVertices - 1; ++i)
            {
                GripPoint midGripPnt = new GripPoint(GripPointType.Mid, (_vertices[i] + _vertices[i+1]) / 2);
                midGripPnt.xData1 = _vertices[i];
                midGripPnt.xData2 = _vertices[i + 1];
                gripPnts.Add(midGripPnt);
            }
            if (_closed && numOfVertices > 2)
            {
                GripPoint midGripPnt = new GripPoint(GripPointType.Mid, (_vertices[0] + _vertices[numOfVertices - 1]) / 2);
                midGripPnt.xData1 = _vertices[0];
                midGripPnt.xData2 = _vertices[numOfVertices - 1];
                gripPnts.Add(midGripPnt);
            }

            return gripPnts;
        }

        /// <summary>
        /// 设置夹点
        /// </summary>
        public override void SetGripPointAt(int index, GripPoint gripPoint, Vector2 newPosition)
        {
            switch (gripPoint.type)
            {
                case GripPointType.End:
                    {
                        this.SetPointAt(index, newPosition);
                    }
                    break;

                case GripPointType.Mid:
                    {
                        int numOfVertices = NumberOfVertices;
                        int i = index - numOfVertices;
                        if (i >= 0 && i <= numOfVertices-1)
                        {
                            int vIndex1st = i;
                            int vIndex2nd = i + 1;
                            if (vIndex2nd == numOfVertices)
                            {
                                vIndex2nd = 0;
                            }
                            Vector2 t = newPosition - gripPoint.position;
                            this.SetPointAt(vIndex1st, (Vector2)gripPoint.xData1 + t);
                            this.SetPointAt(vIndex2nd, (Vector2)gripPoint.xData2 + t);
                        }
                    }
                    break;

                default:
                    break;
            }
        }

        /// <summary>
        /// 写XML
        /// </summary>
        public override void XmlOut(Filer.XmlFiler filer)
        {
            base.XmlOut(filer);

            //
            filer.Write("closed", _closed);
            filer.Write("type", _type);
            //
            string strVertices = "";
            int i = 0;
            foreach (Vector2 vertex in _vertices)
            {
                if (++i > 1)
                {
                    strVertices += "|";
                }
                strVertices += vertex.x.ToString() + ";" + vertex.y.ToString() + ";" + _BulgeList[i - 1];
            }
            filer.Write("vertices", strVertices);
        }

        /// <summary>
        /// 读XML
        /// </summary>
        public override void XmlIn(Filer.XmlFiler filer)
        {
            base.XmlIn(filer);

            //
            filer.Read("closed", out _closed);
            filer.Read("type", out _type);
            //
            string strVertices;
            filer.Read("vertices", out strVertices);
            string[] vts = strVertices.Split('|');
            double x = 0;
            double y = 0;
            string[] xy = null;
            foreach (string vtx in vts)
            {
                xy = vtx.Split(';');
                if (xy.Length != 3)
                {
                    continue;
                }
                if (!double.TryParse(xy[0], out x))
                {
                    continue;
                }
                if (!double.TryParse(xy[1], out y))
                {
                    continue;
                }
                _vertices.Add(new Vector2(x, y));
                _BulgeList.Add(double.Parse(xy[2]));
            }
        }
    }
}
