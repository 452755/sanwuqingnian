using Microsoft.Web.WebView2.Core;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace WebView2Test
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
            Initialize();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            
        }

        private void webView21_CoreWebView2InitializationCompleted(object sender, Microsoft.Web.WebView2.Core.CoreWebView2InitializationCompletedEventArgs e)
        {
            webView21.CoreWebView2.Navigate("https://www.baidu.com");
        }

        /// <summary>
        /// WebView2初始化
        /// </summary>
        void Initialize()
        {
            var result = CoreWebView2Environment.CreateAsync(null, Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "cache"), null);
            webView21.EnsureCoreWebView2Async(result.Result);
        }
    }
}
