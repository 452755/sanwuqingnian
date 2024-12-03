using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Swqn.Json.Analysis
{
    /// <summary>
    /// 字符位置
    /// </summary>
    internal struct CharPosition
    {
        public static readonly CharPosition Empty = new CharPosition(char.MinValue, 0, 0);

        private char character = char.MinValue;
        private int line = 0;
        private int column = 0;

        /// <summary>
        /// 字符
        /// </summary>
        public char Character { get => character; }  // 字符
        /// <summary>
        /// 行号
        /// </summary>
        public int Line { get => line; }  // 行号
        /// <summary>
        /// 列号
        /// </summary>
        public int Column { get => column; }  // 列号

        /// <summary>
        /// 初始化新的字符位置对象
        /// </summary>
        /// <param name="character">字符</param>
        /// <param name="lineNumber">字符所在行</param>
        /// <param name="columnNumber">字符所在列</param>
        public CharPosition(char character, int lineNumber, int columnNumber)
        {
            this.character = character;
            this.line = lineNumber;
            this.column = columnNumber;
        }

        public override string ToString()
        {
            return $"Character: {Character}, Line: {Line}, Column: {Column}";
        }
    }
}
