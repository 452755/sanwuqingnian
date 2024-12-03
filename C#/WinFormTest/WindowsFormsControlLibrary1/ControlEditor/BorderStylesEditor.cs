using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing.Design;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms.Design;
using System.Windows.Forms;
using System.Design;
using SwqnUI.Windows.Forms.Common;

namespace SwqnUI.Windows.Forms.ControlEditor
{
    public sealed class BorderStylesEditor : UITypeEditor
    {
        private class BorderStylesUI : Control
        {
            private class BorderStylesEditorCheckBox : CheckBox
            {
                protected override bool ShowFocusCues => true;

                protected override bool IsInputKey(Keys keyData)
                {
                    if (keyData == Keys.Return || (uint)(keyData - 37) <= 3u)
                    {
                        return true;
                    }

                    return base.IsInputKey(keyData);
                }
            }

            private class ContainerPlaceholder : Control
            {
                public ContainerPlaceholder()
                {
                    BackColor = SystemColors.Control;
                    base.TabStop = false;
                }

                protected override void OnPaint(PaintEventArgs e)
                {
                    Rectangle clientRectangle = base.ClientRectangle;
                    ControlPaint.DrawButton(e.Graphics, clientRectangle, ButtonState.Pushed);
                }
            }

            private ContainerPlaceholder container = new ContainerPlaceholder();

            private CheckBox all = new BorderStylesEditorCheckBox();

            private CheckBox left = new BorderStylesEditorCheckBox();

            private CheckBox right = new BorderStylesEditorCheckBox();

            private CheckBox top = new BorderStylesEditorCheckBox();

            private CheckBox bottom = new BorderStylesEditorCheckBox();

            private CheckBox none = new BorderStylesEditorCheckBox();

            private CheckBox[] upDownOrder;

            private CheckBox[] leftRightOrder;

            private CheckBox[] tabOrder;

            private BorderStylesEditor editor;

            private object value;

            private IWindowsFormsEditorService edSvc;

            private static bool isScalingInitialized = false;

            private const int NONE_HEIGHT = 24;

            private const int NONE_WIDTH = 90;

            private static readonly Size buttonSizeDefault = new Size(20, 20);

            private static readonly Size containerSizeDefault = new Size(90, 90);

            private const int CONTROL_WIDTH = 94;

            private const int CONTROL_HEIGHT = 116;

            private const int OFFSET2X = 2;

            private const int OFFSET2Y = 2;

            private const int NONE_Y = 94;

            private static int noneHeight = 24;

            private static int noneWidth = 90;

            private static Size buttonSize = buttonSizeDefault;

            private static Size containerSize = containerSizeDefault;

            private static int controlWidth = 94;

            private static int controlHeight = 116;

            private static int offset2X = 2;

            private static int offset2Y = 2;

            private static int noneY = 94;

            public object Value => value;

            public BorderStylesUI(BorderStylesEditor editor)
            {
                this.editor = editor;
                upDownOrder = new CheckBox[4] { top, all, bottom, none };
                leftRightOrder = new CheckBox[3] { left, all, right };
                tabOrder = new CheckBox[6] { top, left, all, right, bottom, none };
                if (!isScalingInitialized)
                {
                    if (DpiHelper.IsScalingRequired)
                    {
                        noneHeight = DpiHelper.LogicalToDeviceUnitsY(24);
                        noneWidth = DpiHelper.LogicalToDeviceUnitsX(90);
                        controlHeight = DpiHelper.LogicalToDeviceUnitsY(116);
                        controlWidth = DpiHelper.LogicalToDeviceUnitsX(94);
                        offset2Y = DpiHelper.LogicalToDeviceUnitsY(2);
                        offset2X = DpiHelper.LogicalToDeviceUnitsX(2);
                        noneY = DpiHelper.LogicalToDeviceUnitsY(94);
                        buttonSize = DpiHelper.LogicalToDeviceUnits(buttonSizeDefault);
                        containerSize = DpiHelper.LogicalToDeviceUnits(containerSizeDefault);
                    }

                    isScalingInitialized = true;
                }

                InitializeComponent();
            }

            public void End()
            {
                edSvc = null;
                value = null;
            }

            public virtual BorderStyles GetBorderStyles(CheckBox btn)
            {
                if (top == btn)
                {
                    return BorderStyles.Top;
                }

                if (left == btn)
                {
                    return BorderStyles.Left;
                }

                if (bottom == btn)
                {
                    return BorderStyles.Bottom;
                }

                if (right == btn)
                {
                    return BorderStyles.Right;
                }

                if (all == btn)
                {
                    return BorderStyles.All;
                }

                return BorderStyles.None;
            }

            private void InitializeComponent()
            {
                base.SetBounds(0, 0, BorderStylesEditor.BorderStylesUI.controlWidth, BorderStylesEditor.BorderStylesUI.controlHeight);
                this.BackColor = System.Drawing.SystemColors.Control;
                this.ForeColor = System.Drawing.SystemColors.ControlText;
                //base.AccessibleName = System.Design.SR.GetString("BorderStylesEditorAccName");
                this.none.Anchor = System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left | System.Windows.Forms.AnchorStyles.Right;
                this.none.Location = new System.Drawing.Point(BorderStylesEditor.BorderStylesUI.offset2X, BorderStylesEditor.BorderStylesUI.noneY);
                this.none.Size = new System.Drawing.Size(BorderStylesEditor.BorderStylesUI.noneWidth, BorderStylesEditor.BorderStylesUI.noneHeight);
                this.none.Text = BorderStyles.None.ToString();
                this.none.TabIndex = 0;
                this.none.TabStop = true;
                this.none.Appearance = System.Windows.Forms.Appearance.Button;
                this.none.Click += new System.EventHandler(OnClick);
                this.none.KeyDown += new System.Windows.Forms.KeyEventHandler(OnKeyDown);
                //this.none.AccessibleName = System.Design.SR.GetString("BorderStylesEditorNoneAccName");
                this.container.Anchor = System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left | System.Windows.Forms.AnchorStyles.Right;
                this.container.Location = new System.Drawing.Point(BorderStylesEditor.BorderStylesUI.offset2X, BorderStylesEditor.BorderStylesUI.offset2Y);
                this.container.Size = BorderStylesEditor.BorderStylesUI.containerSize;
                this.none.Dock = System.Windows.Forms.DockStyle.Bottom;
                this.container.Dock = System.Windows.Forms.DockStyle.Fill;
                this.right.Dock = System.Windows.Forms.DockStyle.Right;
                this.right.Size = BorderStylesEditor.BorderStylesUI.buttonSize;
                this.right.TabIndex = 4;
                this.right.TabStop = true;
                this.right.Text = " ";
                this.right.Appearance = System.Windows.Forms.Appearance.Button;
                this.right.Click += new System.EventHandler(OnClick);
                this.right.KeyDown += new System.Windows.Forms.KeyEventHandler(OnKeyDown);
                //this.right.AccessibleName = System.Design.SR.GetString("BorderStylesEditorRightAccName");
                this.left.Dock = System.Windows.Forms.DockStyle.Left;
                this.left.Size = BorderStylesEditor.BorderStylesUI.buttonSize;
                this.left.TabIndex = 2;
                this.left.TabStop = true;
                this.left.Text = " ";
                this.left.Appearance = System.Windows.Forms.Appearance.Button;
                this.left.Click += new System.EventHandler(OnClick);
                this.left.KeyDown += new System.Windows.Forms.KeyEventHandler(OnKeyDown);
                //this.left.AccessibleName = System.Design.SR.GetString("BorderStylesEditorLeftAccName");
                this.top.Dock = System.Windows.Forms.DockStyle.Top;
                this.top.Size = BorderStylesEditor.BorderStylesUI.buttonSize;
                this.top.TabIndex = 1;
                this.top.TabStop = true;
                this.top.Text = " ";
                this.top.Appearance = System.Windows.Forms.Appearance.Button;
                this.top.Click += new System.EventHandler(OnClick);
                this.top.KeyDown += new System.Windows.Forms.KeyEventHandler(OnKeyDown);
                //this.top.AccessibleName = System.Design.SR.GetString("BorderStylesEditorTopAccName");
                this.bottom.Dock = System.Windows.Forms.DockStyle.Bottom;
                this.bottom.Size = BorderStylesEditor.BorderStylesUI.buttonSize;
                this.bottom.TabIndex = 5;
                this.bottom.TabStop = true;
                this.bottom.Text = " ";
                this.bottom.Appearance = System.Windows.Forms.Appearance.Button;
                this.bottom.Click += new System.EventHandler(OnClick);
                this.bottom.KeyDown += new System.Windows.Forms.KeyEventHandler(OnKeyDown);
                //this.bottom.AccessibleName = System.Design.SR.GetString("BorderStylesEditorBottomAccName");
                this.all.Dock = System.Windows.Forms.DockStyle.Fill;
                this.all.Size = BorderStylesEditor.BorderStylesUI.buttonSize;
                this.all.TabIndex = 3;
                this.all.TabStop = true;
                this.all.Text = " ";
                this.all.Appearance = System.Windows.Forms.Appearance.Button;
                this.all.Click += new System.EventHandler(OnClick);
                this.all.KeyDown += new System.Windows.Forms.KeyEventHandler(OnKeyDown);
                //this.all.AccessibleName = System.Design.SR.GetString("BorderStylesEditorFillAccName");
                base.Controls.Clear();
                base.Controls.AddRange(new System.Windows.Forms.Control[2] { this.container, this.none });
                this.container.Controls.Clear();
                this.container.Controls.AddRange(new System.Windows.Forms.Control[5] { this.all, this.left, this.right, this.top, this.bottom });
            }

            private void OnClick(object sender, EventArgs eventargs)
            {
                BorderStyles dock = GetBorderStyles((CheckBox)sender);
                if (dock >= BorderStyles.None)
                {
                    value = dock;
                }

                Teardown();
            }

            protected override void OnGotFocus(EventArgs e)
            {
                base.OnGotFocus(e);
                for (int i = 0; i < tabOrder.Length; i++)
                {
                    if (tabOrder[i].Checked)
                    {
                        tabOrder[i].Focus();
                        break;
                    }
                }
            }

            private void OnKeyDown(object sender, KeyEventArgs e)
            {
                Keys keyCode = e.KeyCode;
                Control control = null;
                switch (keyCode)
                {
                    default:
                        return;
                    case Keys.Up:
                    case Keys.Down:
                        {
                            if (sender == left || sender == right)
                            {
                                sender = all;
                            }

                            int num = upDownOrder.Length - 1;
                            for (int j = 0; j <= num; j++)
                            {
                                if (upDownOrder[j] == sender)
                                {
                                    control = ((keyCode != Keys.Up) ? upDownOrder[Math.Min(j + 1, num)] : upDownOrder[Math.Max(j - 1, 0)]);
                                    break;
                                }
                            }

                            break;
                        }
                    case Keys.Left:
                    case Keys.Right:
                        {
                            int num = leftRightOrder.Length - 1;
                            for (int k = 0; k <= num; k++)
                            {
                                if (leftRightOrder[k] == sender)
                                {
                                    control = ((keyCode != Keys.Left) ? leftRightOrder[Math.Min(k + 1, num)] : leftRightOrder[Math.Max(k - 1, 0)]);
                                    break;
                                }
                            }

                            break;
                        }
                    case Keys.Tab:
                        {
                            for (int i = 0; i < tabOrder.Length; i++)
                            {
                                if (tabOrder[i] == sender)
                                {
                                    i += (((e.Modifiers & Keys.Shift) == 0) ? 1 : (-1));
                                    i = ((i < 0) ? (i + tabOrder.Length) : (i % tabOrder.Length));
                                    control = tabOrder[i];
                                    break;
                                }
                            }

                            break;
                        }
                    case Keys.Return:
                        InvokeOnClick((CheckBox)sender, EventArgs.Empty);
                        return;
                }

                e.Handled = true;
                if (control != null && control != sender)
                {
                    control.Focus();
                }
            }

            public void Start(IWindowsFormsEditorService edSvc, object value)
            {
                this.edSvc = edSvc;
                this.value = value;
                if (value is BorderStyles)
                {
                    BorderStyles borderStyles = (BorderStyles)value;
                    none.Checked = false;
                    top.Checked = false;
                    left.Checked = false;
                    right.Checked = false;
                    bottom.Checked = false;
                    all.Checked = false;
                    switch (borderStyles)
                    {
                        case BorderStyles.None:
                            none.Checked = true;
                            break;
                        case BorderStyles.Top:
                            top.Checked = true;
                            break;
                        case BorderStyles.Left:
                            left.Checked = true;
                            break;
                        case BorderStyles.Right:
                            right.Checked = true;
                            break;
                        case BorderStyles.Bottom:
                            bottom.Checked = true;
                            break;
                        case BorderStyles.All:
                            all.Checked = true;
                            break;
                    }
                }
            }

            private void Teardown()
            {
                edSvc.CloseDropDown();
            }
        }

        private BorderStylesUI borderStylesUI;

        public override object EditValue(ITypeDescriptorContext context, IServiceProvider provider, object value)
        {
            if (provider != null)
            {
                IWindowsFormsEditorService windowsFormsEditorService = (IWindowsFormsEditorService)provider.GetService(typeof(IWindowsFormsEditorService));
                if (windowsFormsEditorService != null)
                {
                    if (borderStylesUI == null)
                    {
                        borderStylesUI = DpiHelper.CreateInstanceInSystemAwareContext(() => new BorderStylesUI(this));
                    }

                    borderStylesUI.Start(windowsFormsEditorService, value);
                    windowsFormsEditorService.DropDownControl(borderStylesUI);
                    value = borderStylesUI.Value;
                    borderStylesUI.End();
                }
            }

            return value;
        }

        public override UITypeEditorEditStyle GetEditStyle(ITypeDescriptorContext context)
        {
            return UITypeEditorEditStyle.DropDown;
        }
    }
}
