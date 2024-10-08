﻿using System;
using System.Configuration;
using System.Design;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Runtime.InteropServices;
using System.Windows;

namespace SwqnUI.Windows.Forms.Common
{
    internal class DpiHelper
    {
        private class DpiAwarenessScope : IDisposable
        {
            private bool dpiAwarenessScopeIsSet;

            private DpiAwarenessContext originalAwareness;

            public DpiAwarenessScope(DpiAwarenessContext awareness)
            {
                if (!EnableDpiChangedHighDpiImprovements)
                {
                    return;
                }

                try
                {
                    if (!CommonUnsafeNativeMethods.TryFindDpiAwarenessContextsEqual(awareness, DpiAwarenessContext.DPI_AWARENESS_CONTEXT_UNSPECIFIED))
                    {
                        originalAwareness = CommonUnsafeNativeMethods.GetThreadDpiAwarenessContext();
                        if (!CommonUnsafeNativeMethods.TryFindDpiAwarenessContextsEqual(originalAwareness, awareness) && !CommonUnsafeNativeMethods.TryFindDpiAwarenessContextsEqual(originalAwareness, DpiAwarenessContext.DPI_AWARENESS_CONTEXT_UNAWARE))
                        {
                            originalAwareness = CommonUnsafeNativeMethods.SetThreadDpiAwarenessContext(awareness);
                            dpiAwarenessScopeIsSet = true;
                        }
                    }
                }
                catch (EntryPointNotFoundException)
                {
                    dpiAwarenessScopeIsSet = false;
                }
            }

            public void Dispose()
            {
                ResetDpiAwarenessContextChanges();
            }

            private void ResetDpiAwarenessContextChanges()
            {
                if (dpiAwarenessScopeIsSet)
                {
                    CommonUnsafeNativeMethods.TrySetThreadDpiAwarenessContext(originalAwareness);
                    dpiAwarenessScopeIsSet = false;
                }
            }
        }

        internal const double LogicalDpi = 96.0;

        private static bool isInitialized = false;

        private static double deviceDpi = 96.0;

        private static double logicalToDeviceUnitsScalingFactor = 0.0;

        private static bool enableHighDpi = false;

        private static string dpiAwarenessValue = null;

        private static InterpolationMode interpolationMode = InterpolationMode.Invalid;

        private static bool isDpiHelperQuirksInitialized = false;

        private static bool enableToolStripHighDpiImprovements = false;

        private static bool enableDpiChangedMessageHandling = false;

        private static bool enableCheckedListBoxHighDpiImprovements = false;

        private static bool enableThreadExceptionDialogHighDpiImprovements = false;

        private static bool enableDataGridViewControlHighDpiImprovements = false;

        private static bool enableSinglePassScalingOfDpiForms = false;

        private static bool enableAnchorLayoutHighDpiImprovements = false;

        private static bool enableMonthCalendarHighDpiImprovements = false;

        private static bool enableDpiChangedHighDpiImprovements = false;

        private static readonly Version dpiChangedMessageHighDpiImprovementsMinimumFrameworkVersion = new Version(4, 8);

        internal static bool EnableDpiChangedHighDpiImprovements
        {
            get
            {
                InitializeDpiHelperForWinforms();
                return enableDpiChangedHighDpiImprovements;
            }
        }

        internal static bool EnableToolStripHighDpiImprovements
        {
            get
            {
                InitializeDpiHelperForWinforms();
                return enableToolStripHighDpiImprovements;
            }
        }

        internal static bool EnableToolStripPerMonitorV2HighDpiImprovements
        {
            get
            {
                if (EnableDpiChangedMessageHandling && enableToolStripHighDpiImprovements)
                {
                    return enableDpiChangedHighDpiImprovements;
                }

                return false;
            }
        }

        internal static bool EnableDpiChangedMessageHandling
        {
            get
            {
                InitializeDpiHelperForWinforms();
                if (enableDpiChangedMessageHandling)
                {
                    DpiAwarenessContext threadDpiAwarenessContext = CommonUnsafeNativeMethods.GetThreadDpiAwarenessContext();
                    return CommonUnsafeNativeMethods.TryFindDpiAwarenessContextsEqual(threadDpiAwarenessContext, DpiAwarenessContext.DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE_V2);
                }

                return false;
            }
        }

        internal static bool EnableCheckedListBoxHighDpiImprovements
        {
            get
            {
                InitializeDpiHelperForWinforms();
                return enableCheckedListBoxHighDpiImprovements;
            }
        }

        internal static bool EnableSinglePassScalingOfDpiForms
        {
            get
            {
                InitializeDpiHelperForWinforms();
                return enableSinglePassScalingOfDpiForms;
            }
        }

        internal static bool EnableThreadExceptionDialogHighDpiImprovements
        {
            get
            {
                InitializeDpiHelperForWinforms();
                return enableThreadExceptionDialogHighDpiImprovements;
            }
        }

        internal static bool EnableDataGridViewControlHighDpiImprovements
        {
            get
            {
                InitializeDpiHelperForWinforms();
                return enableDataGridViewControlHighDpiImprovements;
            }
        }

        internal static bool EnableAnchorLayoutHighDpiImprovements
        {
            get
            {
                InitializeDpiHelperForWinforms();
                return enableAnchorLayoutHighDpiImprovements;
            }
        }

        internal static bool EnableMonthCalendarHighDpiImprovements
        {
            get
            {
                InitializeDpiHelperForWinforms();
                return enableMonthCalendarHighDpiImprovements;
            }
        }

        internal static int DeviceDpi
        {
            get
            {
                Initialize();
                return (int)deviceDpi;
            }
        }

        private static double LogicalToDeviceUnitsScalingFactor
        {
            get
            {
                if (logicalToDeviceUnitsScalingFactor == 0.0)
                {
                    Initialize();
                    logicalToDeviceUnitsScalingFactor = deviceDpi / 96.0;
                }

                return logicalToDeviceUnitsScalingFactor;
            }
        }

        private static InterpolationMode InterpolationMode
        {
            get
            {
                if (interpolationMode == InterpolationMode.Invalid)
                {
                    int num = (int)Math.Round(LogicalToDeviceUnitsScalingFactor * 100.0);
                    if (num % 100 == 0)
                    {
                        interpolationMode = InterpolationMode.NearestNeighbor;
                    }
                    else if (num < 100)
                    {
                        interpolationMode = InterpolationMode.HighQualityBilinear;
                    }
                    else
                    {
                        interpolationMode = InterpolationMode.HighQualityBicubic;
                    }
                }

                return interpolationMode;
            }
        }

        public static bool IsScalingRequired
        {
            get
            {
                Initialize();
                return deviceDpi != 96.0;
            }
        }

        private static void Initialize()
        {
            if (isInitialized)
            {
                return;
            }

            if (IsDpiAwarenessValueSet())
            {
                enableHighDpi = true;
            }
            else
            {
                try
                {
                    string text = ConfigurationManager.AppSettings.Get("EnableWindowsFormsHighDpiAutoResizing");
                    if (!string.IsNullOrEmpty(text) && string.Equals(text, "true", StringComparison.InvariantCultureIgnoreCase))
                    {
                        enableHighDpi = true;
                    }
                }
                catch
                {
                }
            }

            if (enableHighDpi)
            {
                IntPtr dC = UnsafeNativeMethods.GetDC(NativeMethods.NullHandleRef);
                if (dC != IntPtr.Zero)
                {
                    deviceDpi = UnsafeNativeMethods.GetDeviceCaps(new HandleRef(null, dC), 88);
                    UnsafeNativeMethods.ReleaseDC(NativeMethods.NullHandleRef, new HandleRef(null, dC));
                }
            }

            isInitialized = true;
        }

        internal static bool IsDpiAwarenessValueSet()
        {
            bool result = false;
            try
            {
                if (string.IsNullOrEmpty(dpiAwarenessValue))
                {
                    dpiAwarenessValue = ConfigurationOptions.GetConfigSettingValue("DpiAwareness");
                }
            }
            catch
            {
            }

            if (!string.IsNullOrEmpty(dpiAwarenessValue))
            {
                switch (dpiAwarenessValue.ToLowerInvariant())
                {
                    case "true":
                    case "system":
                    case "true/pm":
                    case "permonitor":
                    case "permonitorv2":
                        result = true;
                        break;
                }
            }

            return result;
        }

        internal static void InitializeDpiHelperForWinforms()
        {
            Initialize();
            InitializeDpiHelperQuirks();
        }

        internal static void InitializeDpiHelperQuirks()
        {
            if (isDpiHelperQuirksInitialized)
            {
                return;
            }

            try
            {
                if (Environment.OSVersion.Platform == PlatformID.Win32NT && Environment.OSVersion.Version.CompareTo(ConfigurationOptions.RS2Version) >= 0 && IsExpectedConfigValue("DisableDpiChangedMessageHandling", expectedValue: false) && IsDpiAwarenessValueSet() && Application.RenderWithVisualStyles)
                {
                    enableDpiChangedMessageHandling = true;
                }

                if ((IsScalingRequired || enableDpiChangedMessageHandling) && IsDpiAwarenessValueSet())
                {
                    if (IsExpectedConfigValue("CheckedListBox.DisableHighDpiImprovements", expectedValue: false))
                    {
                        enableCheckedListBoxHighDpiImprovements = true;
                    }

                    if (IsExpectedConfigValue("ToolStrip.DisableHighDpiImprovements", expectedValue: false))
                    {
                        enableToolStripHighDpiImprovements = true;
                    }

                    if (IsExpectedConfigValue("Form.DisableSinglePassScalingOfDpiForms", expectedValue: false))
                    {
                        enableSinglePassScalingOfDpiForms = true;
                    }

                    if (IsExpectedConfigValue("DataGridView.DisableHighDpiImprovements", expectedValue: false))
                    {
                        enableDataGridViewControlHighDpiImprovements = true;
                    }

                    if (IsExpectedConfigValue("AnchorLayout.DisableHighDpiImprovements", expectedValue: false))
                    {
                        enableAnchorLayoutHighDpiImprovements = true;
                    }

                    if (IsExpectedConfigValue("MonthCalendar.DisableHighDpiImprovements", expectedValue: false))
                    {
                        enableMonthCalendarHighDpiImprovements = true;
                    }

                    if (ConfigurationOptions.GetConfigSettingValue("DisableDpiChangedHighDpiImprovements") == null)
                    {
                        if (ConfigurationOptions.NetFrameworkVersion.CompareTo(dpiChangedMessageHighDpiImprovementsMinimumFrameworkVersion) >= 0)
                        {
                            enableDpiChangedHighDpiImprovements = true;
                        }
                    }
                    else if (IsExpectedConfigValue("DisableDpiChangedHighDpiImprovements", expectedValue: false))
                    {
                        enableDpiChangedHighDpiImprovements = true;
                    }

                    enableThreadExceptionDialogHighDpiImprovements = true;
                }
            }
            catch
            {
            }

            isDpiHelperQuirksInitialized = true;
        }

        internal static bool IsExpectedConfigValue(string configurationSettingName, bool expectedValue)
        {
            string configSettingValue = ConfigurationOptions.GetConfigSettingValue(configurationSettingName);
            if (!bool.TryParse(configSettingValue, out var result))
            {
                result = false;
            }

            return result == expectedValue;
        }

        private static Bitmap ScaleBitmapToSize(Bitmap logicalImage, Size deviceImageSize)
        {
            Bitmap bitmap = new Bitmap(deviceImageSize.Width, deviceImageSize.Height, logicalImage.PixelFormat);
            using (Graphics graphics = Graphics.FromImage(bitmap)) 
            {
                graphics.InterpolationMode = InterpolationMode;
                RectangleF srcRect = new RectangleF(0f, 0f, logicalImage.Size.Width, logicalImage.Size.Height);
                RectangleF destRect = new RectangleF(0f, 0f, deviceImageSize.Width, deviceImageSize.Height);
                srcRect.Offset(-0.5f, -0.5f);
                graphics.DrawImage(logicalImage, destRect, srcRect, GraphicsUnit.Pixel);
            }
            return bitmap;
        }

        private static Bitmap CreateScaledBitmap(Bitmap logicalImage, int deviceDpi = 0)
        {
            Size deviceImageSize = LogicalToDeviceUnits(logicalImage.Size, deviceDpi);
            return ScaleBitmapToSize(logicalImage, deviceImageSize);
        }

        public static int LogicalToDeviceUnits(int value, int devicePixels = 0)
        {
            if (devicePixels == 0)
            {
                return (int)Math.Round(LogicalToDeviceUnitsScalingFactor * (double)value);
            }

            double num = (double)devicePixels / 96.0;
            return (int)Math.Round(num * (double)value);
        }

        public static double LogicalToDeviceUnits(double value, int devicePixels = 0)
        {
            if (devicePixels == 0)
            {
                return LogicalToDeviceUnitsScalingFactor * value;
            }

            double num = (double)devicePixels / 96.0;
            return num * value;
        }

        public static int LogicalToDeviceUnitsX(int value)
        {
            return LogicalToDeviceUnits(value);
        }

        public static int LogicalToDeviceUnitsY(int value)
        {
            return LogicalToDeviceUnits(value);
        }

        public static Size LogicalToDeviceUnits(Size logicalSize, int deviceDpi = 0)
        {
            return new Size(LogicalToDeviceUnits(logicalSize.Width, deviceDpi), LogicalToDeviceUnits(logicalSize.Height, deviceDpi));
        }

        public static Bitmap CreateResizedBitmap(Bitmap logicalImage, Size targetImageSize)
        {
            if (logicalImage == null)
            {
                return null;
            }

            return ScaleBitmapToSize(logicalImage, targetImageSize);
        }

        public static void ScaleBitmapLogicalToDevice(ref Bitmap logicalBitmap, int deviceDpi = 0)
        {
            if (logicalBitmap != null)
            {
                Bitmap bitmap = CreateScaledBitmap(logicalBitmap, deviceDpi);
                if (bitmap != null)
                {
                    logicalBitmap.Dispose();
                    logicalBitmap = bitmap;
                }
            }
        }

        public static int ConvertToGivenDpiPixel(int value, double pixelFactor)
        {
            int num = (int)Math.Round((double)value * pixelFactor);
            if (num != 0)
            {
                return num;
            }

            return 1;
        }

        public static void ScaleButtonImageLogicalToDevice(Button button)
        {
            if (button != null && button.Image is Bitmap logicalImage)
            {
                Bitmap image = CreateScaledBitmap(logicalImage);
                button.Image.Dispose();
                button.Image = image;
            }
        }

        public static IDisposable EnterDpiAwarenessScope(DpiAwarenessContext awareness)
        {
            return new DpiAwarenessScope(awareness);
        }

        public static T CreateInstanceInSystemAwareContext<T>(Func<T> createInstance)
        {
            using (EnterDpiAwarenessScope(DpiAwarenessContext.DPI_AWARENESS_CONTEXT_SYSTEM_AWARE))
            {
                return createInstance();
            }
        }
    }

    internal enum DpiAwarenessContext
    {
        DPI_AWARENESS_CONTEXT_UNSPECIFIED = 0,
        DPI_AWARENESS_CONTEXT_UNAWARE = -1,
        DPI_AWARENESS_CONTEXT_SYSTEM_AWARE = -2,
        DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE = -3,
        DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE_V2 = -4,
        DPI_AWARENESS_CONTEXT_UNAWARE_GDISCALED = -5
    }
}
