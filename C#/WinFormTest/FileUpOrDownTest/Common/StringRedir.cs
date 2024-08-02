using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileUpOrDownTest.Common
{
    internal class StringRedir : StringWriter
    {
        /// <summary>
        /// 当前日志控件（用于记录写入日志）
        /// </summary>
        private TextBox outBox;

        /// <summary>
        /// 日志显示的总行数
        /// </summary>
        private int maxLines = 500;

        /// <summary>
        /// 窗口构造用于将主界面的日志控件传入当前管理类
        /// </summary>
        /// <param name="textBox"></param>
        public StringRedir(ref TextBox textBox)
        {
            outBox = textBox;
        }

        /// <summary>
        /// 重载写入行日志的方法(用于监听Console.WriteLine())
        /// </summary>
        /// <param name="value">日志内容</param>
        public override void WriteLine(string value)
        {
            if (outBox.InvokeRequired)
            {
                outBox.Invoke(new Action(() => 
                {
                    logToTextBoxControl(true, value);
                }));
            }
            else
            {
                logToTextBoxControl(true, value);
            }
        }

        /// <summary>
        /// 重载写入不换行的日志（用于监听Console.Write()）
        /// </summary>
        /// <param name="value"></param>
        public override void Write(string value)
        {
            if (outBox.InvokeRequired)
            {
                outBox.Invoke(new Action(() =>
                {
                    logToTextBoxControl(false, value);
                }));
            }
            else
            {
                logToTextBoxControl(false, value);
            }
        }

        /// <summary>
        /// 将日志写入到TextBox日志控件中
        /// </summary>
        /// <param name="isLineLog">日志是否换行</param>
        /// <param name="logValue">日志内容</param>
        private void logToTextBoxControl(bool isLineLog, string logValue)
        {
            string logContent = logValue;
            if (isLineLog == true)
            {
                logContent += Environment.NewLine;
            }
            outBox.AppendText(logValue);
            outBox.SelectionStart = outBox.Text.Length;
            outBox.ScrollToCaret();
            try
            {
                //获取超过最大值的行数
                int mylength = outBox.GetLineFromCharIndex(outBox.TextLength) - maxLines;
                if (mylength > 0)
                {
                    //从第0行的开始选中
                    outBox.Select(outBox.GetFirstCharIndexFromLine(0), outBox.GetFirstCharIndexFromLine(mylength));
                    outBox.SelectedText = "";
                    outBox.Select(outBox.Text.Length, 0);
                    outBox.ScrollToCaret();
                }
            }
            catch { }
        }
    }
}
