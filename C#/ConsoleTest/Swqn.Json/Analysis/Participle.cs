using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Swqn.Json.Analysis
{
    /// <summary>
    /// 分词对象
    /// </summary>
    internal class Participle
    {
        private CharPosition position = CharPosition.Empty;
        private string content = string.Empty;
        private ParticipleType type = ParticipleType.Content;

        /// <summary>
        /// 分词位置
        /// 如果是 <see cref="ParticipleType.Content"/> 类型的分词，则以开始位置作为分词位置
        /// 否则以字符位置作为分词位置
        /// </summary>
        public CharPosition Position { get => position; }
        
        /// <summary>
        /// 分词内容
        /// </summary>
        public string Content { get => content; }

        /// <summary>
        /// 分词类型
        /// </summary>
        public ParticipleType Type { get => type; }

        /// <summary>
        /// 初始化新的分词对象
        /// </summary>
        /// <param name="type">分词类型</param>
        /// <param name="content">分词内容</param>
        /// <param name="position">分词位置</param>
        public Participle(ParticipleType type, string content, CharPosition position) 
        {
            this.type = type;
            this.content = content;
            this.position = position;
        }

        /// <summary>
        /// 初始化新的分词对象
        /// </summary>
        /// <param name="type">分词类型</param>
        /// <param name="content">分词内容</param>
        /// <param name="position">分词位置</param>
        public Participle(ParticipleType type, char content, CharPosition position) : this(type, content.ToString(), position)
        {
            
        }

        /// <summary>
        /// 初始化新的分词对象
        /// </summary>
        /// <param name="type">分词类型</param>
        /// <param name="content">分词内容</param>
        /// <param name="line">开始字符所在行</param>
        /// <param name="column">开始字符所在列</param>
        public Participle(ParticipleType type, string content, int line, int column)
        {
            this.type = type;
            this.content = content;
            this.position = new CharPosition(content[0], line, column);
        }

        /// <summary>
        /// 初始化新的分词对象
        /// </summary>
        /// <param name="type">分词类型</param>
        /// <param name="content">分词内容</param>
        /// <param name="line">分词内容所在行</param>
        /// <param name="column">分词内容所在列</param>
        public Participle(ParticipleType type, char content, int line, int column) 
        {
            this.type = type;
            this.content = content.ToString();
            this.position = new CharPosition(content, line, column);
        }
    }
}
