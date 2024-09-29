using System;
using System.Collections;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Drawing;
using System.Drawing.Design;
using System.Drawing.Drawing2D;
using System.Globalization;
using System.Runtime.InteropServices;
using System.Security.Permissions;
using System.Text;
using System.Windows.Forms;
// using System.Windows.Forms.Automation;
// using System.Windows.Forms.Internal;
using System.Windows.Forms.Layout;

namespace WindowsFormsApp1
{
    public class CustomComboBox : UserControl
    {
        private int requestedHeight;
        private bool integralHeightAdjust;

        private static readonly object EVENT_MULTILINECHANGED = new object();

        protected override Size DefaultSize => new Size(100, PreferredHeight);

        private bool autoSize = true;
        [Category("CatBehavior")]
        [DefaultValue(true)]
        [Localizable(true)]
        [Description("TextBoxAutoSizeDescr")]
        [RefreshProperties(RefreshProperties.Repaint)]
        [Browsable(false)]
        [EditorBrowsable(EditorBrowsableState.Never)]
        public override bool AutoSize
        {
            get
            {
                return autoSize;
            }
            set
            {
                if (autoSize != value)
                {
                    autoSize = value;
                    if (!Multiline)
                    {
                        SetStyle(ControlStyles.FixedHeight, value);
                        AdjustHeight(returnIfAnchored: false);
                    }

                    OnAutoSizeChanged(EventArgs.Empty);
                }
            }
        }

        [Category("CatPropertyChanged")]
        [Description("Multiline 属性值改变时发生")]
        public event EventHandler MultilineChanged
        {
            add
            {
                base.Events.AddHandler(EVENT_MULTILINECHANGED, value);
            }
            remove
            {
                base.Events.RemoveHandler(EVENT_MULTILINECHANGED, value);
            }
        }

        private bool multiline = false;

        [Category("CatBehavior")]
        [DefaultValue(false)]
        [Localizable(true)]
        [Description("是否多行展示")]
        [RefreshProperties(RefreshProperties.All)]
        public virtual bool Multiline
        {
            get
            {
                return multiline;
            }
            set
            {
                if (multiline == value)
                {
                    return;
                }

                base.SuspendLayout();
                
                multiline = value;
                if (value)
                {
                    SetStyle(ControlStyles.FixedHeight, value: false);
                }
                else
                {
                    SetStyle(ControlStyles.FixedHeight, AutoSize);
                }

                RecreateHandle();
                AdjustHeight(returnIfAnchored: false);
                OnMultilineChanged(EventArgs.Empty);
                
                base.ResumeLayout();
                
            }
        }

        protected override CreateParams CreateParams
        {
            [SecurityPermission(SecurityAction.LinkDemand, Flags = SecurityPermissionFlag.UnmanagedCode)]
            get
            {
                CreateParams createParams = base.CreateParams;
                //switch (characterCasing)
                //{
                //    case CharacterCasing.Lower:
                //        createParams.Style |= 16;
                //        break;
                //    case CharacterCasing.Upper:
                //        createParams.Style |= 8;
                //        break;
                //}

                //HorizontalAlignment horizontalAlignment = RtlTranslateHorizontal(textAlign);
                //createParams.ExStyle &= -4097;
                //switch (horizontalAlignment)
                //{
                //    case HorizontalAlignment.Left:
                //        createParams.Style |= 0;
                //        break;
                //    case HorizontalAlignment.Center:
                //        createParams.Style |= 1;
                //        break;
                //    case HorizontalAlignment.Right:
                //        createParams.Style |= 2;
                //        break;
                //}

                //if (Multiline)
                //{
                //    if ((scrollBars & ScrollBars.Horizontal) == ScrollBars.Horizontal && textAlign == HorizontalAlignment.Left && !base.WordWrap)
                //    {
                //        createParams.Style |= 1048576;
                //    }

                //    if ((scrollBars & ScrollBars.Vertical) == ScrollBars.Vertical)
                //    {
                //        createParams.Style |= 2097152;
                //    }
                //}

                //if (useSystemPasswordChar)
                //{
                //    createParams.Style |= 32;
                //}

                return createParams;
            }
        }

        public CustomComboBox()
        {
            SetStyle(ControlStyles.FixedHeight, autoSize);
            SetStyle(ControlStyles.UserPaint, true);
            SetStyle(ControlStyles.StandardClick | ControlStyles.StandardDoubleClick | ControlStyles.UseTextForAccessibility, value: false);
            requestedHeight = base.Height;
        }

        [Category("CatLayout")]
        [Browsable(false)]
        [EditorBrowsable(EditorBrowsableState.Advanced)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        [Description("TextBoxPreferredHeightDescr")]
        public int PreferredHeight
        {
            get
            {
                int num = base.FontHeight;
                if (BorderStyle != 0)
                {
                    num += DpiHelper.GetBorderSizeForDpi((int)DpiHelper.GetDeviceDpi(this.Handle)).Height * 4 + 3;
                }

                return num;
            }
        }

        private void AdjustHeight(bool returnIfAnchored)
        {
            if (returnIfAnchored && (Anchor & (AnchorStyles.Top | AnchorStyles.Bottom)) == (AnchorStyles.Top | AnchorStyles.Bottom))
            {
                return;
            }

            int val = requestedHeight;
            try
            {
                if (autoSize && !multiline)
                {
                    base.Height = PreferredHeight;
                    return;
                }

                int num = base.Height;
                if (multiline)
                {
                    base.Height = Math.Max(val, PreferredHeight + 2);
                }

                integralHeightAdjust = true;
                try
                {
                    base.Height = val;
                }
                finally
                {
                    integralHeightAdjust = false;
                }
            }
            finally
            {
                requestedHeight = val;
            }
        }

        protected virtual void OnMultilineChanged(EventArgs e)
        {
            (base.Events[EVENT_MULTILINECHANGED] as EventHandler)?.Invoke(this, e);
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);

            Graphics g = e.Graphics;
            g.SmoothingMode = SmoothingMode.AntiAlias;
            Rectangle rect = new Rectangle(0, 0, this.Width - 1, this.Height - 1);

            using (GraphicsPath path = new GraphicsPath())
            {
                int radius = 30;
                path.AddArc(rect.X, rect.Y, radius, radius, 180, 90);
                path.AddArc(rect.X + rect.Width - radius, rect.Y, radius, radius, 270, 90);
                path.AddArc(rect.X + rect.Width - radius, rect.Y + rect.Height - radius, radius, radius, 0, 90);
                path.AddArc(rect.X, rect.Y + rect.Height - radius, radius, radius, 90, 90);
                path.CloseFigure();

                using (Pen pen = new Pen(Color.Gray, 1))
                {
                    g.DrawPath(pen, path);
                }
            }
        }
    }

    public static class DpiHelper
    {
        private const int SM_CXSIZEFRAME = 32; // 窗口大小调整边框宽度
        private const int SM_CYSIZEFRAME = 33; // 窗口大小调整边框高度

        [DllImport("user32.dll")]
        private static extern int GetSystemMetricsForDpi(int nIndex, uint dpi);

        [DllImport("shcore.dll")]
        private static extern int GetDpiForMonitor(IntPtr hmonitor, int dpiType, out uint dpiX, out uint dpiY);

        private const int MONITOR_DEFAULTTONEAREST = 2;

        [DllImport("user32.dll")]
        private static extern IntPtr MonitorFromWindow(IntPtr hwnd, uint dwFlags);

        public static Size GetBorderSizeForDpi(Form form)
        {
            uint dpiX = GetDeviceDpi(form.Handle);

            int borderWidth = GetSystemMetricsForDpi(SM_CXSIZEFRAME, dpiX);
            int borderHeight = GetSystemMetricsForDpi(SM_CYSIZEFRAME, dpiX);

            return new Size(borderWidth, borderHeight);
        }

        public static Size GetBorderSizeForDpi(Control control) 
        {
            uint dpiX = GetDeviceDpi(control.Handle);

            int borderWidth = GetSystemMetricsForDpi(SM_CXSIZEFRAME, dpiX);
            int borderHeight = GetSystemMetricsForDpi(SM_CYSIZEFRAME, dpiX);

            return new Size(borderWidth, borderHeight);
        }

        public static Size GetBorderSizeForDpi(int deviceDpi)
        {
            int borderWidth = GetSystemMetricsForDpi(SM_CXSIZEFRAME, (uint)deviceDpi);
            int borderHeight = GetSystemMetricsForDpi(SM_CYSIZEFRAME, (uint)deviceDpi);

            return new Size(borderWidth, borderHeight);
        }

        public static uint GetDeviceDpi(IntPtr handle) 
        {
            uint dpiX = 0;
            IntPtr hMonitor = MonitorFromWindow(handle, MONITOR_DEFAULTTONEAREST);
            GetDpiForMonitor(hMonitor, 0, out dpiX, out _);
            return dpiX;
        }
    }
}
