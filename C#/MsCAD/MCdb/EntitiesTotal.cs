using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MsCAD.DatabaseServices
{
    public class EntitiesTotal
    {
        public string Id { get; set; }= Guid.NewGuid().ToString().Replace("-", "");

        public string Name { get; set; }

        public string TypeName { get; set; }

        public int ECount { get; set; }
    }
}
