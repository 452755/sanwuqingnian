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
    public struct Matrix4
    {
        public double m11;
        public double m12;
        public double m13;
        public double m14;
        public double m21;
        public double m22;
        public double m23;
        public double m24;
        public double m31;
        public double m32;
        public double m33;
        public double m34;
        public double m41;
        public double m42;
        public double m43;
        public double m44;
        public Matrix4(double m11 = 0.0, double m12 = 0.0, double m13 = 0.0, double m14 = 0.0, double m21 = 0.0, double m22 = 0.0, double m23 = 0.0, double m24 = 0.0, double m31 = 0.0, double m32 = 0.0, double m33 = 0.0, double m34 = 0.0, double m41 = 0.0, double m42 = 0.0, double m43 = 0.0, double m44 = 0.0)
        {
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m14 = m14;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m24 = m24;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
            this.m34 = m34;
            this.m41 = m41;
            this.m42 = m42;
            this.m43 = m43;
            this.m44 = m44;
        }

        public void Set(double m11 = 0.0, double m12 = 0.0, double m13 = 0.0, double m14 = 0.0, double m21 = 0.0, double m22 = 0.0, double m23 = 0.0, double m24 = 0.0, double m31 = 0.0, double m32 = 0.0, double m33 = 0.0, double m34 = 0.0, double m41 = 0.0, double m42 = 0.0, double m43 = 0.0, double m44 = 0.0)
        {
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m14 = m14;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m24 = m24;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
            this.m34 = m34;
            this.m41 = m41;
            this.m42 = m42;
            this.m43 = m43;
            this.m44 = m44;
        }

        public override string ToString()
        {
            return string.Format("Matrix4(\r\n{0,10:0.00}, {1,10:0.00}, {2,10:0.00}, {3,10:0.00},\r\n{4,10:0.00}, {5,10:0.00}, {6,10:0.00}, {7,10:0.00},\r\n{8,10:0.00}, {9,10:0.00}, {10,10:0.00}, {11,10:0.00},\r\n{12,10:0.00}, {13,10:0.00}, {14,10:0.00}, {15,10:0.00}  )", new object[] { this.m11, this.m12, this.m13, this.m14, this.m21, this.m22, this.m23, this.m24, this.m31, this.m32, this.m33, this.m34, this.m41, this.m42, this.m43, this.m44 });
        }

        public double determinant
        {
            get
            {
                double num = (this.m11 * this.m22) - (this.m12 * this.m21);
                double num2 = (this.m11 * this.m23) - (this.m13 * this.m21);
                double num3 = (this.m11 * this.m24) - (this.m14 * this.m21);
                double num4 = (this.m12 * this.m23) - (this.m13 * this.m22);
                double num5 = (this.m12 * this.m24) - (this.m14 * this.m22);
                double num6 = (this.m13 * this.m24) - (this.m14 * this.m23);
                double num7 = (this.m31 * this.m42) - (this.m32 * this.m41);
                double num8 = (this.m31 * this.m43) - (this.m33 * this.m41);
                double num9 = (this.m31 * this.m44) - (this.m34 * this.m41);
                double num10 = (this.m32 * this.m43) - (this.m33 * this.m42);
                double num11 = (this.m32 * this.m44) - (this.m34 * this.m42);
                double num12 = (this.m33 * this.m44) - (this.m34 * this.m43);
                return ((((((num * num12) - (num2 * num11)) + (num3 * num10)) + (num4 * num9)) - (num5 * num8)) + (num6 * num7));
            }
        }
        public Matrix4 inverse
        {
            get
            {
                double determinant = this.determinant;
                if (determinant == 0.0)
                {
                    return identity;
                }
                return new Matrix4
                {
                    m11 = (((((((this.m23 * this.m34) * this.m42) - ((this.m24 * this.m33) * this.m42)) + ((this.m24 * this.m32) * this.m43)) - ((this.m22 * this.m34) * this.m43)) - ((this.m23 * this.m32) * this.m44)) + ((this.m22 * this.m33) * this.m44)) / determinant,
                    m12 = (((((((this.m14 * this.m33) * this.m42) - ((this.m13 * this.m34) * this.m42)) - ((this.m14 * this.m32) * this.m43)) + ((this.m12 * this.m34) * this.m43)) + ((this.m13 * this.m32) * this.m44)) - ((this.m12 * this.m33) * this.m44)) / determinant,
                    m13 = (((((((this.m13 * this.m24) * this.m42) - ((this.m14 * this.m23) * this.m42)) + ((this.m14 * this.m22) * this.m43)) - ((this.m12 * this.m24) * this.m43)) - ((this.m13 * this.m22) * this.m44)) + ((this.m12 * this.m23) * this.m44)) / determinant,
                    m14 = (((((((this.m14 * this.m23) * this.m32) - ((this.m13 * this.m24) * this.m32)) - ((this.m14 * this.m22) * this.m33)) + ((this.m12 * this.m24) * this.m33)) + ((this.m13 * this.m22) * this.m34)) - ((this.m12 * this.m23) * this.m34)) / determinant,
                    m21 = (((((((this.m24 * this.m33) * this.m41) - ((this.m23 * this.m34) * this.m41)) - ((this.m24 * this.m31) * this.m43)) + ((this.m21 * this.m34) * this.m43)) + ((this.m23 * this.m31) * this.m44)) - ((this.m21 * this.m33) * this.m44)) / determinant,
                    m22 = (((((((this.m13 * this.m34) * this.m41) - ((this.m14 * this.m33) * this.m41)) + ((this.m14 * this.m31) * this.m43)) - ((this.m11 * this.m34) * this.m43)) - ((this.m13 * this.m31) * this.m44)) + ((this.m11 * this.m33) * this.m44)) / determinant,
                    m23 = (((((((this.m14 * this.m23) * this.m41) - ((this.m13 * this.m24) * this.m41)) - ((this.m14 * this.m21) * this.m43)) + ((this.m11 * this.m24) * this.m43)) + ((this.m13 * this.m21) * this.m44)) - ((this.m11 * this.m23) * this.m44)) / determinant,
                    m24 = (((((((this.m13 * this.m24) * this.m31) - ((this.m14 * this.m23) * this.m31)) + ((this.m14 * this.m21) * this.m33)) - ((this.m11 * this.m24) * this.m33)) - ((this.m13 * this.m21) * this.m34)) + ((this.m11 * this.m23) * this.m34)) / determinant,
                    m31 = (((((((this.m22 * this.m34) * this.m41) - ((this.m24 * this.m32) * this.m41)) + ((this.m24 * this.m31) * this.m42)) - ((this.m21 * this.m34) * this.m42)) - ((this.m22 * this.m31) * this.m44)) + ((this.m21 * this.m32) * this.m44)) / determinant,
                    m32 = (((((((this.m14 * this.m32) * this.m41) - ((this.m12 * this.m34) * this.m41)) - ((this.m14 * this.m31) * this.m42)) + ((this.m11 * this.m34) * this.m42)) + ((this.m12 * this.m31) * this.m44)) - ((this.m11 * this.m32) * this.m44)) / determinant,
                    m33 = (((((((this.m12 * this.m24) * this.m41) - ((this.m14 * this.m22) * this.m41)) + ((this.m14 * this.m21) * this.m42)) - ((this.m11 * this.m24) * this.m42)) - ((this.m12 * this.m21) * this.m44)) + ((this.m11 * this.m22) * this.m44)) / determinant,
                    m34 = (((((((this.m14 * this.m22) * this.m31) - ((this.m12 * this.m24) * this.m31)) - ((this.m14 * this.m21) * this.m32)) + ((this.m11 * this.m24) * this.m32)) + ((this.m12 * this.m21) * this.m34)) - ((this.m11 * this.m22) * this.m34)) / determinant,
                    m41 = (((((((this.m23 * this.m32) * this.m41) - ((this.m22 * this.m33) * this.m41)) - ((this.m23 * this.m31) * this.m42)) + ((this.m21 * this.m33) * this.m42)) + ((this.m22 * this.m31) * this.m43)) - ((this.m21 * this.m32) * this.m43)) / determinant,
                    m42 = (((((((this.m12 * this.m33) * this.m41) - ((this.m13 * this.m32) * this.m41)) + ((this.m13 * this.m31) * this.m42)) - ((this.m11 * this.m33) * this.m42)) - ((this.m12 * this.m31) * this.m43)) + ((this.m11 * this.m32) * this.m43)) / determinant,
                    m43 = (((((((this.m13 * this.m22) * this.m41) - ((this.m12 * this.m23) * this.m41)) - ((this.m13 * this.m21) * this.m42)) + ((this.m11 * this.m23) * this.m42)) + ((this.m12 * this.m21) * this.m43)) - ((this.m11 * this.m22) * this.m43)) / determinant,
                    m44 = (((((((this.m12 * this.m23) * this.m31) - ((this.m13 * this.m22) * this.m31)) + ((this.m13 * this.m21) * this.m32)) - ((this.m11 * this.m23) * this.m32)) - ((this.m12 * this.m21) * this.m33)) + ((this.m11 * this.m22) * this.m33)) / determinant
                };
            }
        }
        public Matrix4 transpose
        {
            get
            {
                return new Matrix4(this.m11, this.m21, this.m31, this.m41, this.m12, this.m22, this.m32, this.m42, this.m13, this.m23, this.m33, this.m43, this.m14, this.m24, this.m34, this.m44);
            }
        }
        public Vector3 MultiplyPoint(Vector3 v)
        {
            return new Vector3((((this.m11 * v.x) + (this.m12 * v.y)) + (this.m13 * v.z)) + this.m14, (((this.m21 * v.x) + (this.m22 * v.y)) + (this.m23 * v.z)) + this.m24, (((this.m31 * v.x) + (this.m32 * v.y)) + (this.m33 * v.z)) + this.m34);
        }

        public Vector3 MultiplyVector(Vector3 v)
        {
            return new Vector3(((this.m11 * v.x) + (this.m12 * v.y)) + (this.m13 * v.z), ((this.m21 * v.x) + (this.m22 * v.y)) + (this.m23 * v.z), ((this.m31 * v.x) + (this.m32 * v.y)) + (this.m33 * v.z));
        }

        public static Matrix4 identity
        {
            get
            {
                return new Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);
            }
        }
        public static Matrix4 zero
        {
            get
            {
                return new Matrix4(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            }
        }
        public static Matrix4 Translate(Vector3 v)
        {
            return new Matrix4(1.0, 0.0, 0.0, v.x, 0.0, 1.0, 0.0, v.y, 0.0, 0.0, 1.0, v.z, 0.0, 0.0, 0.0, 1.0);
        }

        public static Matrix4 RotateX(double angle)
        {
            return RotateXInRadian(Utils.DegreeToRadian(angle));
        }

        public static Matrix4 RotateXInRadian(double angle)
        {
            double num = Math.Cos(angle);
            double num2 = Math.Sin(angle);
            return new Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, num, -num2, 0.0, 0.0, num2, num, 0.0, 0.0, 0.0, 0.0, 1.0);
        }

        public static Matrix4 RotateY(double angle)
        {
            return RotateYInRadian(Utils.DegreeToRadian(angle));
        }

        public static Matrix4 RotateYInRadian(double angle)
        {
            double num = Math.Cos(angle);
            double num2 = Math.Sin(angle);
            return new Matrix4(num, 0.0, num2, 0.0, 0.0, 1.0, 0.0, 0.0, -num2, 0.0, num, 0.0, 0.0, 0.0, 0.0, 1.0);
        }

        public static Matrix4 RotateZ(double angle)
        {
            return RotateZInRadian(Utils.DegreeToRadian(angle));
        }

        public static Matrix4 RotateZInRadian(double angle)
        {
            double num = Math.Cos(angle);
            double num2 = Math.Sin(angle);
            return new Matrix4(num, -num2, 0.0, 0.0, num2, num, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);
        }

        public static Matrix4 Scale(Vector3 v)
        {
            return new Matrix4(v.x, 0.0, 0.0, 0.0, 0.0, v.y, 0.0, 0.0, 0.0, 0.0, v.z, 0.0, 0.0, 0.0, 0.0, 1.0);
        }

        public static Matrix4 AngleAxis(double angle, Vector3 axis)
        {
            return AngleAxisInRadian(Utils.DegreeToRadian(angle), axis);
        }

        public static Matrix4 AngleAxisInRadian(double angle, Vector3 axis)
        {
            Vector3 normalized = axis.normalized;
            double x = normalized.x;
            double y = normalized.y;
            double z = normalized.z;
            double num4 = Math.Sin(angle);
            double num5 = Math.Cos(angle);
            double num6 = 1.0 - num5;
            return new Matrix4
            {
                m11 = ((x * x) * num6) + num5,
                m12 = ((x * y) * num6) - (z * num4),
                m13 = ((x * z) * num6) + (y * num4),
                m21 = ((y * x) * num6) + (z * num4),
                m22 = ((y * y) * num6) + num5,
                m23 = ((y * z) * num6) - (x * num4),
                m31 = ((x * z) * num6) - (y * num4),
                m32 = ((y * z) * num6) + (x * num4),
                m33 = ((z * z) * num6) + num5
            };
        }

        public static Matrix4 operator *(Matrix4 a, Matrix4 b)
        {
            return new Matrix4
            {
                m11 = (((a.m11 * b.m11) + (a.m12 * b.m21)) + (a.m13 * b.m31)) + (a.m14 * b.m41),
                m12 = (((a.m11 * b.m12) + (a.m12 * b.m22)) + (a.m13 * b.m32)) + (a.m14 * b.m42),
                m13 = (((a.m11 * b.m13) + (a.m12 * b.m23)) + (a.m13 * b.m33)) + (a.m14 * b.m43),
                m14 = (((a.m11 * b.m14) + (a.m12 * b.m24)) + (a.m13 * b.m34)) + (a.m14 * b.m44),
                m21 = (((a.m21 * b.m11) + (a.m22 * b.m21)) + (a.m23 * b.m31)) + (a.m24 * b.m41),
                m22 = (((a.m21 * b.m12) + (a.m22 * b.m22)) + (a.m23 * b.m32)) + (a.m24 * b.m42),
                m23 = (((a.m21 * b.m13) + (a.m22 * b.m23)) + (a.m23 * b.m33)) + (a.m24 * b.m43),
                m24 = (((a.m21 * b.m14) + (a.m22 * b.m24)) + (a.m23 * b.m34)) + (a.m24 * b.m44),
                m31 = (((a.m31 * b.m11) + (a.m32 * b.m21)) + (a.m33 * b.m31)) + (a.m34 * b.m41),
                m32 = (((a.m31 * b.m12) + (a.m32 * b.m22)) + (a.m33 * b.m32)) + (a.m34 * b.m42),
                m33 = (((a.m31 * b.m13) + (a.m32 * b.m23)) + (a.m33 * b.m33)) + (a.m34 * b.m43),
                m34 = (((a.m31 * b.m14) + (a.m32 * b.m24)) + (a.m33 * b.m34)) + (a.m34 * b.m44),
                m41 = (((a.m41 * b.m11) + (a.m42 * b.m21)) + (a.m43 * b.m31)) + (a.m44 * b.m41),
                m42 = (((a.m41 * b.m12) + (a.m42 * b.m22)) + (a.m43 * b.m32)) + (a.m44 * b.m42),
                m43 = (((a.m41 * b.m13) + (a.m42 * b.m23)) + (a.m43 * b.m33)) + (a.m44 * b.m43),
                m44 = (((a.m41 * b.m14) + (a.m42 * b.m24)) + (a.m43 * b.m34)) + (a.m44 * b.m44)
            };
        }

        public static Vector3 operator *(Matrix4 m, Vector3 v)
        {
            return new Vector3((((m.m11 * v.x) + (m.m12 * v.y)) + (m.m13 * v.z)) + m.m14, (((m.m21 * v.x) + (m.m22 * v.y)) + (m.m23 * v.z)) + m.m24, (((m.m31 * v.x) + (m.m32 * v.y)) + (m.m33 * v.z)) + m.m34);
        }
    }
}
