using System;
using System.Collections.Generic;
using System.Xml;
using MsMath;

namespace MsCAD.DatabaseServices.Filer
{
    /// <summary>
    /// LitCAD XML 文件读写接口类
    /// </summary>
    public abstract class XmlFiler
    {
        /// <summary>
        /// 写文件
        /// </summary>
        /// <param name="name">键名称</param>
        /// <param name="value">键值</param>
        /// <returns>
        /// 成功则返回true
        /// 失败则返回false
        /// </returns>
        public abstract bool Write(string name, string value);
        public abstract bool Write(string name, bool value);
        public abstract bool Write(string name, byte value);
        public abstract bool Write(string name, uint value);
        public abstract bool Write(string name, int value);
        public abstract bool Write(string name, double value);
        public abstract bool Write(string name, Vector2 value);
        public abstract bool Write(string name, MsCAD.Colors.Color color);
        public abstract bool Write(string name, ObjectId value);
        public abstract bool Write(string name, MsCAD.DatabaseServices.LineWeight value);

        /// <summary>
        /// 读文件
        /// </summary>
        /// <param name="name">键名称</param>
        /// <param name="value">读取的键值</param>
        /// <returns>
        /// 成功则返回true
        /// 失败则返回false
        /// </returns>
        public abstract bool Read(string name, out string value);
        public abstract bool Read(string name, out bool value);
        public abstract bool Read(string name, out byte value);
        public abstract bool Read(string name, out uint value);
        public abstract bool Read(string name, out int value);
        public abstract bool Read(string name, out double value);
        public abstract bool Read(string name, out Vector2 value);
        public abstract bool Read(string name, out MsCAD.Colors.Color color);
        public abstract bool Read(string name, out ObjectId value);
        public abstract bool Read(string name, out MsCAD.DatabaseServices.LineWeight value);
    }
}
