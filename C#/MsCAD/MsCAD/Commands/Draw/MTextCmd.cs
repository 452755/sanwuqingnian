using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

using MsCAD.DatabaseServices;
using MsCAD.UI;
using MsMath;

namespace MsCAD.Commands.Draw
{
    /// <summary>
    /// 创建MText对象
    /// </summary>
    internal class MTextCmd : DrawCmd
    {
        private MsCAD.DatabaseServices.Text _mtext = null;

        /// <summary>
        /// 新增的图元
        /// </summary>
        protected override IEnumerable<Entity> newEntities
        {
            get { return new MsCAD.DatabaseServices.Text[1] { _mtext }; }
        }

        /// <summary>
        /// 字体高度
        /// </summary>
        private double _height = 3.5;

        /// <summary>
        /// 定位点
        /// </summary>
        private Vector2 _position = new Vector2(0, 0);

        private string _text = "";

        

        /// <summary>
        /// 步骤
        /// </summary>
        private enum Step
        {
            // 文本高度
            Step1_Height = 1,
            // 输入文本
            Step2_Text = 2,
            // 文本坐标
            Step3_Position = 3,
        }
        private Step _step = Step.Step1_Height;

        /// <summary>
        /// 初始化
        /// </summary>
        public override void Initialize()
        {
            base.Initialize();

            _step = Step.Step1_Height;
            this.pointer.mode = UI.Pointer.Mode.Locate;

            DynInputDouble sidesCntInput = new DynInputDouble(this.presenter, 3.5);
            sidesCntInput.Message = "请输入文本高度，再回车确认： ";
            this.dynamicInputer.StartInput(sidesCntInput);
            sidesCntInput.finish += this.OnHeightInputReturn;
            sidesCntInput.cancel += this.OnHeightInputReturn;
        }

        /// <summary>
        /// 边数输入结束事件响应
        /// </summary>
        private void OnHeightInputReturn(DynInputCtrl sender, DynInputResult result)
        {
            DynInputResult<double> ret = result as DynInputResult<double>;
            if (ret != null && ret.status == DynInputStatus.OK)
            {
                _height = (uint)ret.value;
                _step = Step.Step2_Text;

                DynInputString icInput = new DynInputString(this.presenter, "");
                icInput.Message = "请输入文本，再回车确认：";
                this.dynamicInputer.StartInput(icInput);
                icInput.finish += this.OnTextInputReturn;
                icInput.cancel += this.OnTextInputReturn;
            }
            else
            {
                _mgr.CancelCurrentCommand();
            }

            sender.finish -= this.OnHeightInputReturn;
            sender.cancel -= this.OnHeightInputReturn;
        }

        /// <summary>
        /// 内接于圆还是外切于圆
        /// 输入结束事件响应
        /// </summary>
        private void OnTextInputReturn(DynInputCtrl sender, DynInputResult result)
        {
            DynInputResult<string> ret = result as DynInputResult<string>;
            if (ret != null && ret.status == DynInputStatus.OK)
            {
                _text = ret.value.Trim();
                _step = Step.Step3_Position;
                Windows.MainWin.Instance.ShowMsg(Windows.TipsState.Info, "鼠标点选文本坐标！");
            }
            else
            {
                _mgr.CancelCurrentCommand();
            }

            sender.finish -= this.OnTextInputReturn;
            sender.cancel -= this.OnTextInputReturn;
        }

        public override EventResult OnMouseDown(MouseEventArgs e)
        {
            switch (_step)
            {
                case Step.Step3_Position:
                    if (e.Button == MouseButtons.Left)
                    {
                        _position = this.pointer.currentSnapPoint;
                        this.UpdateMText();
                        _mgr.FinishCurrentCommand();
                    }
                    break;
            }
            return EventResult.Handled;
        }

        /// <summary>
        /// 刷新文本
        /// </summary>
        private void UpdateMText()
        {
            if (_mtext == null)
            {
                _mtext = new MsCAD.DatabaseServices.Text();
            }
            _mtext.height = this._height;
            _mtext.text = this._text;
            _mtext.position = this._position;


            _mtext.layerId = this.document.currentLayerId;
            _mtext.color = this.document.currentColor;
        }

        public override EventResult OnMouseUp(MouseEventArgs e)
        {
            return EventResult.Handled;
        }


        public override void OnPaint(Graphics g)
        {
            if (_mtext != null)
            {
                this.presenter.DrawEntity(g, _mtext);
            }
        }
    }
}
