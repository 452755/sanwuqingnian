using netDxf.Entities;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace MsCAD.DatabaseServices
{
    public static class DxfHelper
    {

        static netDxf.DxfDocument G_DOc_Dxf;

        public static List<EntitiesTotal> Import(string fileName)
        {
            try
            {
                List<netDxf.Entities.Line> lineList1 = new List<netDxf.Entities.Line>();
                List<netDxf.Entities.Arc> arcList1 = new List<netDxf.Entities.Arc>();
                List<netDxf.Entities.Circle> circleList1 = new List<netDxf.Entities.Circle>();
                G_DOc_Dxf = netDxf.DxfDocument.Load(fileName);
                if (G_DOc_Dxf == null)
                {
                    return new List<EntitiesTotal>();
                }

                List<EntitiesTotal> listEntities = new List<EntitiesTotal>();
                Type t = G_DOc_Dxf.GetType();
                PropertyInfo[] pArray = t.GetProperties();
                Array.ForEach<PropertyInfo>(pArray, p =>
                {
                    if (p.PropertyType.Name.Contains("IEnumerable"))
                    {
                        PropertyInfo property = t.GetProperty(p.Name);
                        IEnumerable<object> fieldValue = (IEnumerable<object>)property.GetValue(G_DOc_Dxf, null);
                        if (fieldValue.Count() > 0)
                        {
                            listEntities.Add(new EntitiesTotal() { Name = p.Name, TypeName = p.PropertyType.FullName, ECount = fieldValue.Count() });
                        }
                    }
                });
                return listEntities;
            }
            catch (Exception ex)
            {
                return new List<EntitiesTotal>();
            }
        }


        public static IEnumerable<object> GetList(EntitiesTotal entities)
        {
            Type t = G_DOc_Dxf.GetType();
            PropertyInfo property = t.GetProperty(entities.Name);
            IEnumerable<object> fieldValue = (IEnumerable<object>)property.GetValue(G_DOc_Dxf, null);

            if (entities.TypeName.Contains("netDxf.Entities.Spline"))//样条曲线
            {
                IEnumerable<netDxf.Entities.Spline> list = G_DOc_Dxf.Splines;
                foreach (netDxf.Entities.Spline spline in list)
                {
                    //累加弦长法，向心力，等距取样法，仅适用于已从控制点转换为拟合点创建方法的样条
                    spline.KnotParameterization = SplineKnotParameterization.FitChord;
                }
                return list.ToList();
            }
            else
            {
                return fieldValue.ToList();
            }
        }

        

    }
}
