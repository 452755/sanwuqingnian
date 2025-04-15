using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace FileUpOrDownTest
{
    public partial class Form2 : Form
    {
        public Form2()
        {
            InitializeComponent();
            PropGridTest propGridTest = new PropGridTest();
            this.propertyGrid1.SelectedObject = propGridTest;
        }
    }

    public class PropGridTest 
    {
        [Browsable(true)]
        [Category("分组1")]
        public string Name { get; set; }

        [Browsable(true)]
        [Category("分组1")]
        public string School { get; set; }

        [Browsable(true)]
        [Category("分组2")]
        public int Age { get; set; }

        [Browsable(true)]
        [Category("分组2")]
        public int Heig { get; set; }
    }
}
