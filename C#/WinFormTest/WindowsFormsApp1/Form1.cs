using System;
using System.Drawing;
using System.Threading;
using System.Windows;
using System.Windows.Forms;
using SkiaSharp;
using SkiaSharp.Views.Desktop;
using static System.Windows.Forms.VisualStyles.VisualStyleElement;

namespace WindowsFormsApp1
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
            this.textBox1.Multiline = true;
            this.textBox1.Dock = DockStyle.None;
            this.Dock = DockStyle.Fill;
            this.button1.FlatAppearance
        }

        private void button1_Click(object sender, EventArgs e)
        {
            System.Windows.Forms.Design.DockEditor dockEditor = new System.Windows.Forms.Design.DockEditor();
            System.Drawing.Size size = new System.Drawing.Size();
            this.customComboBox1.Multiline = !this.customComboBox1.Multiline;
        }
    }
}
