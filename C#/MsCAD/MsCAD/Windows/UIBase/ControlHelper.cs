// ***********************************************************************
// Assembly         : HZH_Controls
// Created          : 08-08-2019
//
// ***********************************************************************
// <copyright file="ControlHelper.cs">
//     Copyright by Huang Zhenghui(黄正辉) All, QQ group:568015492 QQ:623128629 Email:623128629@qq.com
// </copyright>
//
// Blog: https://www.cnblogs.com/bfyx
// GitHub：https://github.com/kwwwvagaa/NetWinformControl
// gitee：https://gitee.com/kwwwvagaa/net_winform_custom_control.git
//
// If you use this code, please keep this note.
// ***********************************************************************
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Linq;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using System.Windows.Forms;


namespace MsCAD.Windows
{
    /// <summary>
    /// Class ControlHelper.
    /// </summary>
    public static class ControlHelper
    {
        

        /// <summary>
        /// Sets the foreground window.
        /// </summary>
        /// <param name="hWnd">The h WND.</param>
        /// <returns><c>true</c> if XXXX, <c>false</c> otherwise.</returns>
        [DllImport("user32.dll")]
        public static extern bool SetForegroundWindow(IntPtr hWnd);


        /// <summary>
        /// 相对于屏幕显示的位置
        /// </summary>
        /// <param name="screen">窗体需要显示的屏幕</param>
        /// <param name="left">left</param>
        /// <param name="top">top</param>
        /// <returns></returns>
        public static Point GetScreenLocation(Screen screen,int left,int top)
        {
            return new Point(screen.Bounds.Left + left, screen.Bounds.Top + top);
        }
    }
}
