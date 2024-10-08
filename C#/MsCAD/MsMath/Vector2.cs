﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Drawing;

namespace MsMath
{
    using System;
    using System.Runtime.InteropServices;

    [StructLayout(LayoutKind.Sequential)]
    public struct Vector2
    {
        public double x;
        public double y;

        public Vector2(double x = 0.0, double y = 0.0)
        {
            this.x = x;
            this.y = y;
        }

        public void Set(double newX, double newY)
        {
            this.x = newX;
            this.y = newY;
        }

        public override string ToString()
        {
            return string.Format("Vector2({0}, {1})", this.x, this.y);
        }

        public override bool Equals(object obj)
        {
            return ((obj is Vector2) && this.Equals((Vector2)obj));
        }

        public bool Equals(Vector2 rhs)
        {
            return (Utils.IsEqual(this.x, rhs.x) && Utils.IsEqual(this.y, rhs.y));
        }

        public override int GetHashCode()
        {
            return (this.x.GetHashCode() ^ this.y.GetHashCode());
        }

        public double length
        {
            get
            {
                return Math.Sqrt((this.x * this.x) + (this.y * this.y));
            }
        }
        public double lengthSqrd
        {
            get
            {
                return ((this.x * this.x) + (this.y * this.y));
            }
        }
        public void Normalize()
        {
            double length = this.length;
            if (length != 0.0)
            {
                this.x /= length;
                this.y /= length;
            }
        }

        public Vector2 normalized
        {
            get
            {
                double length = this.length;
                if (length != 0.0)
                {
                    return new Vector2(this.x / length, this.y / length);
                }
                return this;
            }
        }
        public static double Dot(Vector2 a, Vector2 b)
        {
            return ((a.x * b.x) + (a.y * b.y));
        }

        public static double Cross(Vector2 a, Vector2 b)
        {
            return ((a.x * b.y) - (a.y * b.x));
        }

        public static double Angle(Vector2 a, Vector2 b)
        {
            return Utils.RadianToDegree(AngleInRadian(a, b));
        }

        public static double AngleInRadian(Vector2 a, Vector2 b)
        {
            double num = a.length * b.length;
            if (num == 0.0)
            {
                return 0.0;
            }
            double num2 = Dot(a, b) / num;
            return Math.Acos(Utils.Clamp(num2, -1.0, 1.0));
        }

        public static double SignedAngle(Vector2 from, Vector2 to)
        {
            return Utils.RadianToDegree(SignedAngleInRadian(from, to));
        }

        public static double SignedAngleInRadian(Vector2 from, Vector2 to)
        {
            double num = AngleInRadian(from, to);
            if (Cross(from, to) < 0.0)
            {
                num = -num;
            }
            return num;
        }

        public static double Distance(Vector2 a, Vector2 b)
        {
            Vector2 vector = b - a;
            return vector.length;
        }

        public static Vector2 Rotate(Vector2 v, double angle)
        {
            return RotateInRadian(v, Utils.DegreeToRadian(angle));
        }

        public static Vector2 Rotate(Vector2 point, Vector2 basePoint, double angle)
        {
            return RotateInRadian(point, basePoint, Utils.DegreeToRadian(angle));
        }

        public static Vector2 RotateInRadian(Vector2 v, double rad)
        {
            double x = (v.x * Math.Cos(rad)) - (v.y * Math.Sin(rad));
            return new Vector2(x, (v.x * Math.Sin(rad)) + (v.y * Math.Cos(rad)));
        }

        public static Vector2 RotateInRadian(Vector2 point, Vector2 basePoint, double rad)
        {
            double num = Math.Cos(rad);
            double num2 = Math.Sin(rad);
            double x = (((point.x * num) - (point.y * num2)) + (basePoint.x * (1.0 - num))) + (basePoint.y * num2);
            return new Vector2(x, (((point.x * num2) + (point.y * num)) + (basePoint.y * (1.0 - num))) + (basePoint.x * num2));
        }

        public static Vector2 operator +(Vector2 a, Vector2 b)
        {
            return new Vector2(a.x + b.x, a.y + b.y);
        }

        public static Vector2 operator -(Vector2 a, Vector2 b)
        {
            return new Vector2(a.x - b.x, a.y - b.y);
        }

        public static Vector2 operator -(Vector2 a)
        {
            return new Vector2(-a.x, -a.y);
        }

        public static Vector2 operator *(Vector2 a, double d)
        {
            return new Vector2(a.x * d, a.y * d);
        }

        public static Vector2 operator *(double d, Vector2 a)
        {
            return new Vector2(a.x * d, a.y * d);
        }

        public static Vector2 operator /(Vector2 a, double d)
        {
            return new Vector2(a.x / d, a.y / d);
        }

        public static bool operator ==(Vector2 lhs, Vector2 rhs)
        {
            return lhs.Equals(rhs);
        }

        public static bool operator !=(Vector2 lhs, Vector2 rhs)
        {
            return !(lhs == rhs);
        }


        public PointF ToPointF()
        {
            return new PointF((float)x, (float)y);
        }
    }
}
