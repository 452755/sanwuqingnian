using System;
using System.Collections.Generic;
using System.Xml;

namespace MsCAD.DatabaseServices
{
    public class BlockTable : DBTable
    {
        /// <summary>
        /// 类名
        /// </summary>
        public override string className
        {
            get { return "BlockTable"; }
        }

        /// <summary>
        /// 构造函数
        /// </summary>
        internal BlockTable(Database db)
            : base(db, Database.BlockTableId)
        {
        }

        /// <summary>
        /// 读XML
        /// </summary>
        public override void XmlIn(Filer.XmlFiler filer)
        {

            //System.Reflection.Assembly[] assemblyArray = AppDomain.CurrentDomain.GetAssemblies();
            Filer.XmlFilerImpl filerImpl = filer as Filer.XmlFilerImpl;

            base.XmlIn(filer);

            XmlNode curXmlNode = filerImpl.curXmlNode;
            Block block = new Block();
            XmlNodeList blocks = curXmlNode.SelectNodes(block.GetType().Name);// FullName, "Block";
            foreach (XmlNode blockNode in blocks)
            {
                if (Count > 0)
                    block = new Block();
                block._dbtable = this;
                filerImpl.curXmlNode = blockNode;
                block.XmlIn(filerImpl);
                this._Add(block);                
            }
            filerImpl.curXmlNode = curXmlNode;
        }
    }
}
