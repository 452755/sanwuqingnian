using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Collections;

namespace WindowsFormsApp1
{
    internal static class Program
    {
        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.Run(new Form1());

            //List<int> ss = new List<int>() { 1,2,3,4,5};

            //ss.Output();

            //Console.ReadKey();
        }

        //public static void Output<T>(this T list) where T : IList 
        //{
        //    foreach (var item in list)
        //    {
        //        Console.WriteLine(item.ToString());
        //    }
        //}
    }
}
