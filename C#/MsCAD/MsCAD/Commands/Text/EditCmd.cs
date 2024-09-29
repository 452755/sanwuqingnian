using MsCAD.UI;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

using db = MsCAD.DatabaseServices;

namespace MsCAD.Commands.Text
{
    /// <summary>
    /// 文本编辑命令
    /// </summary>
    internal class EditCmd : Command
    {
        /// <summary>
        /// 文本
        /// </summary>
        private db.Text _entText = null;
        private db.Text _original = null;
        private db.Text _result = null;

        internal db.Text entText
        {
            set
            {
                _entText = value;
                _original = _entText.Clone() as db.Text;
                _result = _entText.Clone() as db.Text;
            }
        }

        /// <summary>
        /// 步骤
        /// </summary>
        private enum Step
        {
            // 输入文本
            Step1_Text = 1,
            // 文本高度
            Step2_Height = 2,
            

        }
        private Step _step = Step.Step1_Text;

        /// <summary>
        /// 初始化
        /// </summary>
        public override void Initialize()
        {
            base.Initialize();

            DynInputString icInput = new DynInputString(this.presenter, _original.text);
            icInput.Message = "编辑文本，回车确认";
            this.dynamicInputer.StartInput(icInput);
            icInput.finish += this.OnIcInputReturn;
            icInput.cancel += this.OnIcInputReturn;
        }


        /// <summary>
        /// 内接于圆还是外切于圆
        /// 输入结束事件响应
        /// </summary>
        private void OnIcInputReturn(DynInputCtrl sender, DynInputResult result)
        {
            DynInputResult<string> ret = result as DynInputResult<string>;
            if (ret != null && ret.status == DynInputStatus.OK)
            {
                _result.text = ret.value;
                _step = Step.Step2_Height;
                DynInputDouble sidesCntInput = new DynInputDouble(this.presenter, _original.height);
                sidesCntInput.Message = "请输入文本高度，回车确认";
                this.dynamicInputer.StartInput(sidesCntInput);
                sidesCntInput.finish += this.OnHeightInputReturn;
                sidesCntInput.cancel += this.OnHeightInputReturn;

            }
            else
            {
                _mgr.CancelCurrentCommand();
            }

            sender.finish -= this.OnIcInputReturn;
            sender.cancel -= this.OnIcInputReturn;
        }

        /// <summary>
        /// 边数输入结束事件响应
        /// </summary>
        private void OnHeightInputReturn(DynInputCtrl sender, DynInputResult result)
        {
            DynInputResult<double> ret = result as DynInputResult<double>;
            if (ret != null && ret.status == DynInputStatus.OK)
            {
                _result.height = (uint)ret.value;
                _mgr.FinishCurrentCommand();
            }
            else
            {
                _mgr.CancelCurrentCommand();
            }

            sender.finish -= this.OnHeightInputReturn;
            sender.cancel -= this.OnHeightInputReturn;
        }

        /// <summary>
        /// 结束
        /// </summary>
        public override void Terminate(int num = 0)
        {
            _mgr.presenter.selections.Clear();

            base.Terminate();
        }

        /// <summary>
        /// 提交到数据库
        /// </summary>
        protected override void Commit()
        {
            _entText.text = _result.text;
            _entText.font = _result.font;
            _entText.height = _result.height;
            _entText.alignment = _result.alignment;
        }

        /// <summary>
        /// 回滚撤销
        /// </summary>
        protected override void Rollback()
        {
            _entText.text = _original.text;
            _entText.font = _original.font;
            _entText.height = _original.height;
            _entText.alignment = _original.alignment;
        }
    }
}
