using System;
using System.Collections.Generic;
using MsMath;

namespace MsCAD.DatabaseServices
{
    public class Arc : Entity
    {
        /// <summary>
        /// 类名
        /// </summary>
        public override string className
        {
            get { return "Arc"; }
        }

        /// <summary>
        /// 圆心
        /// </summary>
        private MsMath.Vector2 _center = new Vector2(0, 0);
        public Vector2 center
        {
            get { return _center; }
            set { _center = value; }
        }

        /// <summary>
        /// 半径
        /// </summary>
        private double _radius = 0.0;
        public double radius
        {
            get { return _radius; }
            set { _radius = value; }
        }

        /// <summary>
        /// 直径
        /// </summary>
        public double diameter
        {
            get { return _radius * 2; }
        }

        /// <summary>
        /// Start angle in radian
        /// </summary>
        private double _startAngle = 0.0;
        public double startAngle
        {
            get { return _startAngle; }
            set
            {
                _startAngle = MathUtils.NormalizeRadianAngle(value);
            }
        }

        /// <summary>
        /// End angle in radian
        /// </summary>
        private double _endAngle = 0.0;
        public double endAngle
        {
            get { return _endAngle; }
            set
            {
                _endAngle = MathUtils.NormalizeRadianAngle(value);
            }
        }

        /// <summary>
        /// Start point
        /// </summary>
        public Vector2 startPoint
        {
            get
            {
                return _center + Vector2.RotateInRadian(
                     new Vector2(_radius, 0), _startAngle);
            }
        }

        /// <summary>
        /// End point
        /// </summary>
        public Vector2 endPoint
        {
            get
            {
                return _center + Vector2.RotateInRadian(
                    new Vector2(_radius, 0), _endAngle);
            }
        }

        /// <summary>
        /// 圆弧中点
        /// </summary>
        public Vector2 middlePoint
        {
            get
            {
                double angle = 0;
                if (this.endAngle >= this.startAngle)
                {
                    angle = (this.startAngle + this.endAngle) / 2;
                }
                else
                {
                    angle = (this.startAngle + this.endAngle + Utils.PI * 2) / 2;
                }

                return _center + Vector2.RotateInRadian(new Vector2(_radius, 0), angle);
            }
        }

        public double GetAngle360()
        {
            double angle = 0;
            if (this.endAngle > this.startAngle)
            {
                angle = this.endAngle - this.startAngle;
            }
            else
            {
                angle = this.endAngle - this.startAngle + Math.PI * 2;
            }
            return angle / Math.PI * 180;//求角度
        }

        public double GetArcLength()
        {
            double angle = 0;
            if (this.endAngle > this.startAngle)
            {
                angle = this.endAngle - this.startAngle;
            }
            else
            {
                angle = this.endAngle - this.startAngle + Math.PI * 2;
            }
            return radius* angle;//求角度
        }

        /// <summary>
        /// 外围边框
        /// </summary>
        public override Bounding bounding
        {
            get
            {
                List<Vector2> pnts = new List<Vector2>();
                pnts.Add(this.startPoint);
                pnts.Add(this.endPoint);

                for (double ang = 0; ang < Math.PI * 2; ang += Math.PI / 2)
                {
                    if (AngleInRange(ang, _startAngle, _endAngle))
                    {
                        pnts.Add(_center + Vector2.RotateInRadian(
                             new Vector2(_radius, 0), ang));
                    }
                }

                double minX = double.MaxValue;
                double minY = double.MaxValue;
                double maxX = double.MinValue;
                double maxY = double.MinValue;
                foreach (Vector2 pnt in pnts)
                {
                    minX = pnt.x < minX ? pnt.x : minX;
                    minY = pnt.y < minY ? pnt.y : minY;

                    maxX = pnt.x > maxX ? pnt.x : maxX;
                    maxY = pnt.y > maxY ? pnt.y : maxY;
                }

                return new Bounding(new Vector2(minX, minY),
                    new Vector2(maxX, maxY));
            }
        }

        private bool AngleInRange(double angle, double startAngle, double endAngle)
        {
            if (endAngle >= startAngle)
            {
                return angle >= startAngle
                    && angle <= endAngle;
            }
            else
            {
                return angle >= startAngle
                    || angle <= endAngle;
            }
        }

        /// <summary>
        /// 构造函数
        /// </summary>
        public Arc()
        {
        }

        public Arc(Vector2 center, double radius, double startAngle, double endAngle)
        {
            _center = center;
            _radius = radius;
            this.startAngle = startAngle;
            this.endAngle = endAngle;
        }

        /// <summary>
        /// 绘制函数
        /// </summary>
        public override void Draw(IGraphicsDraw gd)
        {
            gd.DrawArc(_center, _radius, startAngle, endAngle);
        }

        /// <summary>
        /// 克隆函数
        /// </summary>
        public override object Clone()
        {
            Arc arc = base.Clone() as Arc;
            arc._center = _center;
            arc._radius = _radius;
            arc._startAngle = _startAngle;
            arc._endAngle = _endAngle;

            return arc;
        }

        protected override DBObject CreateInstance()
        {
            return new Arc();
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
            Vector2 spnt = transform * this.startPoint;
            Vector2 epnt = transform * this.endPoint;
            Vector2 mpnt = transform * this.middlePoint;

            _center = transform * _center;
            _radius = (spnt - _center).length;

            double sAngle = Vector2.SignedAngleInRadian(
                new Vector2(1, 0),
                spnt - _center);
            double eAngle = Vector2.SignedAngleInRadian(
                new Vector2(1, 0),
                epnt - _center);

            if (Vector2.SignedAngle(spnt - _center, mpnt - _center) >= 0)
            {
                this.startAngle = sAngle;
                this.endAngle = eAngle;
            }
            else
            {
                this.startAngle = eAngle;
                this.endAngle = sAngle;
            }
        }

        /// <summary>
        /// 对象捕捉点
        /// </summary>
        public override List<ObjectSnapPoint> GetSnapPoints()
        {
            List<ObjectSnapPoint> snapPnts = new List<ObjectSnapPoint>();
            snapPnts.Add(new ObjectSnapPoint(ObjectSnapMode.Center, _center));
            snapPnts.Add(new ObjectSnapPoint(ObjectSnapMode.End, startPoint));
            snapPnts.Add(new ObjectSnapPoint(ObjectSnapMode.End, endPoint));
            snapPnts.Add(new ObjectSnapPoint(ObjectSnapMode.Mid, middlePoint));

            return snapPnts;
        }

        /// <summary>
        /// 获取夹点
        /// </summary>
        public override List<GripPoint> GetGripPoints()
        {
            List<GripPoint> gripPnts = new List<GripPoint>();
            //
            gripPnts.Add(new GripPoint(GripPointType.Center, _center));
            //
            GripPoint startGripPnt = new GripPoint(GripPointType.End, startPoint);
            startGripPnt.xData1 = middlePoint;
            gripPnts.Add(startGripPnt);
            //
            GripPoint endGripPnt = new GripPoint(GripPointType.End, endPoint);
            endGripPnt.xData1 = middlePoint;
            gripPnts.Add(endGripPnt);
            //
            gripPnts.Add(new GripPoint(GripPointType.Mid, middlePoint));

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
            else if (index >= 1 && index <= 3)
            {
                Vector2 startPoint = this.startPoint;
                Vector2 endPoint = this.endPoint;
                Vector2 middlePoint = this.middlePoint;

                if (index == 1)
                {
                    startPoint = newPosition;
                    middlePoint = (Vector2)gripPoint.xData1;
                }
                else if (index == 2)
                {
                    endPoint = newPosition;
                    middlePoint = (Vector2)gripPoint.xData1;
                }
                else if (index == 3)
                {
                    middlePoint = newPosition;
                }

                Circle2 newCircle = Circle2.From3Points(
                    startPoint, middlePoint, endPoint);
                if (newCircle.radius > 0)
                {
                    Vector2 xPositive = new Vector2(1, 0);
                    double startAngle = Vector2.SignedAngleInRadian(xPositive,
                        startPoint - newCircle.center);
                    double endAngle = Vector2.SignedAngleInRadian(xPositive,
                        endPoint - newCircle.center);
                    double middleAngle = Vector2.SignedAngleInRadian(xPositive,
                        middlePoint - newCircle.center);
                    startAngle = MathUtils.NormalizeRadianAngle(startAngle);
                    endAngle = MathUtils.NormalizeRadianAngle(endAngle);
                    middleAngle = MathUtils.NormalizeRadianAngle(middleAngle);

                    _center = newCircle.center;
                    _radius = newCircle.radius;
                    if (AngleInArcRange(middleAngle, startAngle, endAngle))
                    {
                        this.startAngle = startAngle;
                        this.endAngle = endAngle;
                    }
                    else
                    {
                        this.startAngle = endAngle;
                        this.endAngle = startAngle;
                    }
                }
            }
        }

        private bool AngleInArcRange(double angle, double startAngle, double endAngle)
        {
            if (endAngle >= startAngle)
            {
                return angle >= startAngle
                    && angle <= endAngle;
            }
            else
            {
                return angle >= startAngle
                    || angle <= endAngle;
            }
        }

        /// <summary>
        /// 写XML
        /// </summary>
        public override void XmlOut(Filer.XmlFiler filer)
        {
            base.XmlOut(filer);

            filer.Write("center", _center);
            filer.Write("radius", _radius);
            filer.Write("startAngle", _startAngle);
            filer.Write("endAngle", _endAngle);
        }

        /// <summary>
        /// 读XML
        /// </summary>
        public override void XmlIn(Filer.XmlFiler filer)
        {
            base.XmlIn(filer);

            filer.Read("center", out _center);
            filer.Read("radius", out _radius);
            filer.Read("startAngle", out _startAngle);
            filer.Read("endAngle", out _endAngle);
        }
    }
}
