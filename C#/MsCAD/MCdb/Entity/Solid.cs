﻿using System;
using System.Collections.Generic;
using MsMath;

namespace MsCAD.DatabaseServices
{
    /// <summary>
    /// 多段线
    /// </summary>
    public class Solid : Entity
    {
        /// <summary>
        /// 类名
        /// </summary>
        public override string className
        {
            get { return "Solid"; }
        }

        private List<Vector2> _vertices = new List<Vector2>();

        public List<Vector2> vertices
        {
            get { return _vertices; }
            set { _vertices = value; }
        }

        private bool _closed = false;

        public int NumberOfVertices
        {
            get
            {
                return _vertices.Count;
            }
        }



        /// <summary>
        /// 绘制函数
        /// </summary>
        public override void Draw(IGraphicsDraw gd)
        {
            int numOfVertices = NumberOfVertices;
            for (int i = 0; i < numOfVertices - 1; ++i)
            {
                gd.DrawLine(GetPointAt(i), GetPointAt(i + 1));
            }

            gd.FillPolygon(_vertices);
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
            Solid solid = base.Clone() as Solid;
            solid._vertices.AddRange(_vertices);
            solid._closed = _closed;

            return solid;
        }

        protected override DBObject CreateInstance()
        {
            return new Solid();
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
                GripPoint midGripPnt = new GripPoint(GripPointType.Mid, (_vertices[i] + _vertices[i + 1]) / 2);
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
                        if (i >= 0 && i <= numOfVertices - 1)
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
            string strVertices = "";
            int i = 0;
            foreach (Vector2 vertex in _vertices)
            {
                if (++i > 1)
                {
                    strVertices += "|";
                }
                strVertices += vertex.x.ToString() + ";" + vertex.y.ToString();
            }
            filer.Write("vertices", strVertices);
        }

        /// <summary>
        /// 读XML
        /// </summary>
        public override void XmlIn(Filer.XmlFiler filer)
        {
            base.XmlIn(filer);
            
            string strVertices;
            filer.Read("vertices", out strVertices);
            string[] vts = strVertices.Split('|');
            double x = 0;
            double y = 0;
            string[] xy = null;
            foreach (string vtx in vts)
            {
                xy = vtx.Split(';');
                if (xy.Length != 2)
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
            }
        }
    }
}
