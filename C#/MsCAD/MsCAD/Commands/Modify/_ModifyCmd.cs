using System;
using System.Collections.Generic;

using MsCAD.ApplicationServices;
using MsCAD.DatabaseServices;
using MsCAD.UI;

namespace MsCAD.Commands.Modify
{
    internal abstract class ModifyCmd : Command
    {
        /// <summary>
        /// 初始化
        /// </summary>
        public override void Initialize()
        {
            base.Initialize();

            this.pointer.isShowAnchor = false;
        }

        /// <summary>
        /// 结束
        /// </summary>
        public override void Terminate(int num = 0)
        {
            _mgr.presenter.selections.Clear(num);

            base.Terminate();
        }
    }
}
