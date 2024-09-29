using System;
using System.Collections.Generic;
using MsMath;

namespace MsCAD.DatabaseServices
{
    public class Ellipse : Entity
    {
        /// <summary>
        /// 类名
        /// </summary>
        public override string className
        {
            get { return "Ellipse"; }
        }

        /// <summary>
        /// 圆心
        /// </summary>
        private Vector2 _center = new Vector2(0, 0);
        public Vector2 center
        {
            get { return _center; }
            set { _center = value; }
        }

        /// <summary>
        /// 长轴，当为height
        /// </summary>
        private double _MajorAxis = 0.0;
        public double MajorAxis
        {
            get { return _MajorAxis; }
            set { _MajorAxis = value; }
        }

        /// <summary>
        /// 短轴，当为width
        /// </summary>
        private double _MinorAxis = 0.0;
        public double MinorAxis
        {
            get { return _MinorAxis; }
            set { _MinorAxis = value; }
        }

        /// <summary>
        /// 旋转
        /// </summary>
        private double _Rotation = 0.0;
        public double Rotation
        {
            get { return _Rotation; }
            set { _Rotation = value; }
        }



        /// <summary>
        /// 外围边框
        /// </summary>
        public override Bounding bounding
        {
            get
            {
                return new Bounding(_center, this.MinorAxis, this.MajorAxis);
            }
        }

        /// <summary>
        /// 构造函数
        /// </summary>
        public Ellipse()
        {
        }

        public Ellipse(Vector2 center, double MajorAxis, double MinorAxis, double Rotation)
        {
            _center = center;
            _MajorAxis = MajorAxis;
            _MinorAxis = MinorAxis;
            _Rotation = Rotation;
        }

        /// <summary>
        /// 绘制函数
        /// </summary>
        public override void Draw(IGraphicsDraw gd)
        {
            gd.DrawEllipse(_center, _MajorAxis, _MinorAxis, _Rotation);
        }

        /// <summary>
        /// 克隆函数
        /// </summary>
        public override object Clone()
        {
            Ellipse ellipse = base.Clone() as Ellipse;
            ellipse._center = _center;
            ellipse._MajorAxis = _MajorAxis;
            ellipse._MinorAxis = _MinorAxis;
            ellipse._Rotation = _Rotation;
            return ellipse;
        }

        /// <summary>
        /// 创建圆实例
        /// </summary>
        protected override DBObject CreateInstance()
        {
            return new Circle();
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
            Vector2 pnt = _center + new Vector2(_MajorAxis, 0);

            _center = transform * _center;
            _MajorAxis = (transform * pnt - _center).length;
        }

        /// <summary>
        /// 对象捕捉点
        /// </summary>
        public override List<ObjectSnapPoint> GetSnapPoints()
        {
            List<ObjectSnapPoint> snapPnts = new List<ObjectSnapPoint>();
            snapPnts.Add(new ObjectSnapPoint(ObjectSnapMode.Center, _center));

            return snapPnts;
        }

        /// <summary>
        /// 获取夹点
        /// </summary>
        public override List<GripPoint> GetGripPoints()
        {
            List<GripPoint> gripPnts = new List<GripPoint>();
            gripPnts.Add(new GripPoint(GripPointType.Center, _center));
            gripPnts.Add(new GripPoint(GripPointType.Quad, _center + new Vector2(_MajorAxis, 0)));
            gripPnts.Add(new GripPoint(GripPointType.Quad, _center + new Vector2(0, _MajorAxis)));
            gripPnts.Add(new GripPoint(GripPointType.Quad, _center + new Vector2(-_MajorAxis, 0)));
            gripPnts.Add(new GripPoint(GripPointType.Quad, _center + new Vector2(0, -_MajorAxis)));

            return gripPnts;
        }

        /// <summary>
        /// 设置夹点
        /// </summary>
        public override void SetGripPointAt(int index, GripPoint gripPoint, Vector2 newPosition)
        {
            if (index == 0)
            {
                _center = newPosition;
            }
            else if (index >= 1 && index <= 4)
            {
                _MajorAxis = (newPosition - _center).length;
            }
        }

        /// <summary>
        /// 写XML
        /// </summary>
        public override void XmlOut(Filer.XmlFiler filer)
        {
            base.XmlOut(filer);

            filer.Write("center", _center);
            filer.Write("MajorAxis", _MajorAxis);
            filer.Write("MinorAxis", _MinorAxis);
            filer.Write("Rotation", _Rotation);

        }

        /// <summary>
        /// 读XML
        /// </summary>
        public override void XmlIn(Filer.XmlFiler filer)
        {
            base.XmlIn(filer);

            filer.Read("center", out _center);
            filer.Read("MajorAxis", out _MajorAxis);
            filer.Read("MinorAxis", out _MinorAxis);
            filer.Read("Rotation", out _Rotation);

        }
    }
}
