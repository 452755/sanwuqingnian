using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SwqnUI.Windows.Forms.Common
{
    [Flags]
    public enum BorderStyles
    {
        None = 0b_0000_0000,    // 0
        Top = 0b_0000_0001,     // 1
        Bottom = 0b_0000_0010,  // 2
        Left = 0b_0000_0100,    // 4
        Right = 0b_0000_1000,   // 8
        All = Top | Bottom | Left | Right,
    }
}
