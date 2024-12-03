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
    public struct Matrix3
    {
        public double m11;
        public double m12;
        public double m13;
        public double m21;
        public double m22;
        public double m23;
        public double m31;
        public double m32;
        public double m33;
        public Matrix3(double m11 = 0.0, double m12 = 0.0, double m13 = 0.0, double m21 = 0.0, double m22 = 0.0, double m23 = 0.0, double m31 = 0.0, double m32 = 0.0, double m33 = 0.0)
        {
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
        }

        public void Set(double m11 = 0.0, double m12 = 0.0, double m13 = 0.0, double m21 = 0.0, double m22 = 0.0, double m23 = 0.0, double m31 = 0.0, double m32 = 0.0, double m33 = 0.0)
        {
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
        }

        public override string ToString()
        {
            string format = "Matrix3(\r\n{0,10:0.00}, {1,10:0.00}, {2,10:0.00},\r\n{3,10:0.00}, {4,10:0.00}, {5,10:0.00},\r\n{6,10:0.00}, {7,10:0.00}, {8,10:0.00}  )";
            return string.Format(format, new object[] { this.m11, this.m12, this.m13, this.m21, this.m22, this.m23, this.m31, this.m32, this.m33 });
        }

        public double determinant
        {
            get
            {
                return (((((((this.m11 * this.m22) * this.m33) + ((this.m12 * this.m23) * this.m31)) + ((this.m13 * this.m21) * this.m32)) - ((this.m11 * this.m23) * this.m32)) - ((this.m12 * this.m21) * this.m33)) - ((this.m13 * this.m22) * this.m31));
            }
        }
        public Matrix3 inverse
        {
            get
            {
                double determinant = this.determinant;
                if (determinant == 0.0)
                {
                    return identity;
                }
                return new Matrix3
                {
                    m11 = ((this.m22 * this.m33) - (this.m23 * this.m32)) / determinant,
                    m12 = ((this.m13 * this.m32) - (this.m12 * this.m33)) / determinant,
                    m13 = ((this.m12 * this.m23) - (this.m13 * this.m22)) / determinant,
                    m21 = ((this.m23 * this.m31) - (this.m21 * this.m33)) / determinant,
                    m22 = ((this.m11 * this.m33) - (this.m13 * this.m31)) / determinant,
                    m23 = ((this.m13 * this.m21) - (this.m11 * this.m23)) / determinant,
                    m31 = ((this.m21 * this.m32) - (this.m22 * this.m31)) / determinant,
                    m32 = ((this.m12 * this.m31) - (this.m11 * this.m32)) / determinant,
                    m33 = ((this.m11 * this.m22) - (this.m12 * this.m21)) / determinant
                };
            }
        }
        public Matrix3 transpose
        {
            get
            {
                return new Matrix3(this.m11, this.m21, this.m31, this.m12, this.m22, this.m32, this.m13, this.m23, this.m33);
            }
        }
        public Vector2 MultiplyPoint(Vector2 v)
        {
            return new Vector2(((this.m11 * v.x) + (this.m12 * v.y)) + this.m13, ((this.m21 * v.x) + (this.m22 * v.y)) + this.m23);
        }

        public Vector2 MultiplyVector(Vector2 v)
        {
            return new Vector2((this.m11 * v.x) + (this.m12 * v.y), (this.m21 * v.x) + (this.m22 * v.y));
        }

        public static Matrix3 identity
        {
            get
            {
                return new Matrix3(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0);
            }
        }
        public static Matrix3 zero
        {
            get
            {
                return new Matrix3(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            }
        }
        public static Matrix3 Translate(Vector2 v)
        {
            return new Matrix3(1.0, 0.0, v.x, 0.0, 1.0, v.y, 0.0, 0.0, 1.0);
        }

        public static Matrix3 Rotate(double angle)
        {
            return RotateInRadian(Utils.DegreeToRadian(angle));
        }

        public static Matrix3 RotateInRadian(double angle)
        {
            double num = Math.Cos(angle);
            double num2 = Math.Sin(angle);
            return new Matrix3(num, -num2, 0.0, num2, num, 0.0, 0.0, 0.0, 1.0);
        }

        public static Matrix3 Scale(Vector2 v)
        {
            return new Matrix3(v.x, 0.0, 0.0, 0.0, v.y, 0.0, 0.0, 0.0, 1.0);
        }

        public static Matrix3 operator *(Matrix3 a, Matrix3 b)
        {
            return new Matrix3
            {
                m11 = ((a.m11 * b.m11) + (a.m12 * b.m21)) + (a.m13 * b.m31),
                m12 = ((a.m11 * b.m12) + (a.m12 * b.m22)) + (a.m13 * b.m32),
                m13 = ((a.m11 * b.m13) + (a.m12 * b.m23)) + (a.m13 * b.m33),
                m21 = ((a.m21 * b.m11) + (a.m22 * b.m21)) + (a.m23 * b.m31),
                m22 = ((a.m21 * b.m12) + (a.m22 * b.m22)) + (a.m23 * b.m32),
                m23 = ((a.m21 * b.m13) + (a.m22 * b.m23)) + (a.m23 * b.m33),
                m31 = ((a.m31 * b.m11) + (a.m32 * b.m21)) + (a.m33 * b.m31),
                m32 = ((a.m31 * b.m12) + (a.m32 * b.m22)) + (a.m33 * b.m32),
                m33 = ((a.m31 * b.m13) + (a.m32 * b.m23)) + (a.m33 * b.m33)
            };
        }

        public static Vector2 operator *(Matrix3 m, Vector2 v)
        {
            return new Vector2(((m.m11 * v.x) + (m.m12 * v.y)) + m.m13, ((m.m21 * v.x) + (m.m22 * v.y)) + m.m23);
        }
    }
}
