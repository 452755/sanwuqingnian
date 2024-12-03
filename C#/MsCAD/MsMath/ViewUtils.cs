using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MsMath
{
    public class ViewUtils
    {
        public static Matrix4 ViewMatrix(Vector3 pos, Vector3 xAxis, Vector3 yAxis)
        {
            Vector3 normalized = xAxis.normalized;
            Vector3 b = yAxis.normalized;
            Vector3 vector3 = Vector3.Cross(normalized, b).normalized;
            Matrix4 matrix = new Matrix4(normalized.x, normalized.y, normalized.z, 0.0, b.x, b.y, b.z, 0.0, vector3.x, vector3.y, vector3.z, 0.0, 0.0, 0.0, 0.0, 1.0);
            Matrix4 matrix2 = new Matrix4(1.0, 0.0, 0.0, -pos.x, 0.0, 1.0, 0.0, -pos.y, 0.0, 0.0, 1.0, -pos.z, 0.0, 0.0, 0.0, 1.0);
            return (matrix * matrix2);
        }
    }
}
