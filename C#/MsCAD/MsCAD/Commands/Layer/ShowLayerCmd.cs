using System;
using System.Collections.Generic;
using System.Windows.Forms;

using MsCAD.DatabaseServices;
using MsCAD.Windows;

namespace MsCAD.Commands
{
    /// <summary>
    /// 显示图层命令
    /// </summary>
    internal class ShowLayerCmd : Command
    {
        private Layer _layer = null;
        List<string> _data = new List<string>();

        public List<string> data
        {
            get
            {
                return _data;
            }

            set
            {
                _data = value;
            }
        }

        public override void Initialize()
        {
            foreach (Layer item in this.database.layerTable)
            {
                int numState = 0;
                foreach (string layerState in data)
                {
                    if (layerState.Equals(item.name.ToString()))
                    {
                        numState = 1;
                        item.layerState = 0;//显示状态设置为显示
                        break;
                    }
                }
                if (numState == 0)
                    item.layerState = 1;//显示状态设置为yc
            }
            _mgr.FinishCurrentCommand();
        }

        public override void Undo()
        {
            base.Undo();
        }

        public override void Redo()
        {
            base.Redo();
            
        }

        public override void Finish()
        {
            base.Finish();
        }

        public override void Cancel()
        {
            base.Cancel();
        }
    }
}
