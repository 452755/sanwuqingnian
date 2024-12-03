using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Swqn.Json.Analysis
{
    /// <summary>
    /// 分词类型
    /// </summary>
    internal enum ParticipleType
    {
        /// <summary>
        /// 对象开始符 {
        /// </summary>
        ObjectStart = 0,
        /// <summary>
        /// 对象结束符 }
        /// </summary>
        ObjectEnd = 1,
        /// <summary>
        /// 数组开始符 [
        /// </summary>
        ArrayStart = 2,
        /// <summary>
        /// 数组结束符 ]
        /// </summary>
        ArrayEnd = 3,
        /// <summary>
        /// 键值符 :
        /// </summary>
        KVSymbol = 4,
        /// <summary>
        /// 元素分割符 ,
        /// </summary>
        ElementSeparator = 5,
        /// <summary>
        /// 内容
        /// </summary>
        Content = 6
    }
}
