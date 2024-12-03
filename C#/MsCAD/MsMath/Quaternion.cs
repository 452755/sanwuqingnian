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
    public struct Quaternion
    {
        public double x;
        public double y;
        public double z;
        public double w;
        public Quaternion(double x = 0.0, double y = 0.0, double z = 0.0, double w = 1.0)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        public void Set(double x, double y, double z, double w)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        public void SetIdentity()
        {
            this.Set(0.0, 0.0, 0.0, 1.0);
        }

        public override string ToString()
        {
            return string.Format("Quaternion({0}, {1}, {2}, {3})", new object[] { this.x, this.y, this.z, this.w });
        }

        public double length
        {
            get
            {
                return Math.Sqrt((((this.x * this.x) + (this.y * this.y)) + (this.z * this.z)) + (this.w * this.w));
            }
        }
        public void Normalize()
        {
            double length = this.length;
            if (length != 0.0)
            {
                this.x /= length;
                this.y /= length;
                this.z /= length;
                this.w /= length;
            }
        }

        public Quaternion normalized
        {
            get
            {
                double length = this.length;
                if (length != 0.0)
                {
                    return new Quaternion(this.x / length, this.y / length, this.z / length, this.w / length);
                }
                return new Quaternion(this.x, this.y, this.z, this.w);
            }
        }
        public void Invert()
        {
            this.x = -this.x;
            this.y = -this.y;
            this.z = -this.z;
        }

        public Quaternion inverse
        {
            get
            {
                return new Quaternion(-this.x, -this.y, -this.z, this.w);
            }
        }
        public Matrix4 ToMatrix4()
        {
            return new Matrix4
            {
                m11 = 1.0 - (2.0 * ((this.y * this.y) + (this.z * this.z))),
                m12 = 2.0 * ((this.x * this.y) - (this.z * this.w)),
                m13 = 2.0 * ((this.x * this.z) + (this.y * this.w)),
                m14 = 0.0,
                m21 = 2.0 * ((this.x * this.y) + (this.z * this.w)),
                m22 = 1.0 - (2.0 * ((this.x * this.x) + (this.z * this.z))),
                m23 = 2.0 * ((this.y * this.z) - (this.x * this.w)),
                m24 = 0.0,
                m31 = 2.0 * ((this.x * this.z) - (this.y * this.w)),
                m32 = 2.0 * ((this.y * this.z) + (this.x * this.w)),
                m33 = 1.0 - (2.0 * ((this.x * this.x) + (this.y * this.y))),
                m34 = 0.0,
                m41 = 0.0,
                m42 = 0.0,
                m43 = 0.0,
                m44 = 1.0
            };
        }

        public static Quaternion identity
        {
            get
            {
                return new Quaternion(0.0, 0.0, 0.0, 1.0);
            }
        }
        public static Quaternion AngleAxis(double angle, Vector3 axis)
        {
            return AngleAxisInRadian(Utils.DegreeToRadian(angle), axis);
        }

        public static Quaternion AngleAxisInRadian(double angle, Vector3 axis)
        {
            axis = axis.normalized;
            double num = Math.Sin(angle / 2.0);
            return new Quaternion
            {
                w = Math.Cos(angle / 2.0),
                x = axis.x * num,
                y = axis.y * num,
                z = axis.z * num
            };
        }

        public static Quaternion FromToRotation(Vector3 from, Vector3 to)
        {
            Vector3 normalized = from.normalized;
            Vector3 b = to.normalized;
            double d = Vector3.Dot(normalized, b);
            Vector3 axis = Vector3.Cross(normalized, b);
            if (axis.length != 0.0)
            {
                return AngleAxisInRadian(Math.Acos(d), axis);
            }
            if (d >= 0.0)
            {
                return identity;
            }
            Vector3 vector4 = Vector3.Cross(normalized, new Vector3(1.0, 0.0, 0.0));
            if (Utils.IsEqualZero(vector4.length))
            {
                vector4 = Vector3.Cross(normalized, new Vector3(0.0, 1.0, 0.0));
            }
            return new Quaternion(vector4.x, vector4.y, vector4.z, 0.0);
        }

        public static Quaternion operator *(Quaternion a, Quaternion b)
        {
            return new Quaternion((((a.w * b.x) + (a.x * b.w)) + (a.y * b.z)) - (a.z * b.y), (((a.w * b.y) - (a.x * b.z)) + (a.y * b.w)) + (a.z * b.x), (((a.w * b.z) + (a.x * b.y)) - (a.y * b.x)) + (a.z * b.w), (((a.w * b.w) - (a.x * b.x)) - (a.y * b.y)) - (a.z * b.z));
        }

        public static Vector3 operator *(Quaternion q, Vector3 v)
        {
            double x = q.x;
            double y = q.y;
            double z = q.z;
            double w = q.w;
            double num5 = q.x * q.x;
            double num6 = q.y * q.y;
            double num7 = q.z * q.z;
            double num8 = q.w * q.w;
            return new Vector3((((((num5 + num8) - num6) - num7) * v.x) + ((2.0 * ((x * y) - (z * w))) * v.y)) + ((2.0 * ((x * z) + (y * w))) * v.z), (((2.0 * ((x * y) + (z * w))) * v.x) + ((((num8 - num5) + num6) - num7) * v.y)) + ((2.0 * ((y * z) - (x * w))) * v.z), (((2.0 * ((x * z) - (y * w))) * v.x) + ((2.0 * ((x * w) + (y * z))) * v.y)) + ((((num8 - num5) - num6) + num7) * v.z));
        }
    }
}
