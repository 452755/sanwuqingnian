using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml;

namespace MsCAD.DatabaseServices
{
    public class LayerTable : DBTable
    {
        /// <summary>
        /// 类名
        /// </summary>
        public override string className
        {
            get { return "LayerTable"; }
        }

        /// <summary>
        /// 
        /// </summary>
        private ObjectId _layerZeroId = ObjectId.Null;
        public ObjectId layerZeroId
        {
            get { return _layerZeroId; }
        }

        /// <summary>
        /// 构造函数
        /// </summary>
        internal LayerTable(Database db)
            : base(db, Database.LayerTableId)
        {
            Layer layerZero = new Layer("0");
            this.Add(layerZero);

            _layerZeroId = layerZero.id;
        }

        /// <summary>
        /// 读XML
        /// </summary>
        public override void XmlIn(Filer.XmlFiler filer)
        {
            Filer.XmlFilerImpl filerImpl = filer as Filer.XmlFilerImpl;

            base.XmlIn(filer);

            XmlNode curXmlNode = filerImpl.curXmlNode;
            XmlNodeList layers = curXmlNode.SelectNodes("Layer");
            foreach (XmlNode layerNode in layers)
            {
                Layer layer = new Layer();
                filerImpl.curXmlNode = layerNode;
                layer.XmlIn(filerImpl);
                this._Add(layer);
            }
            filerImpl.curXmlNode = curXmlNode;
        }

        /// <summary>
        /// 本图层，普遍的文本高度
        /// </summary>
        public double _CommonTextHeight = 1;

        public List<double> _TextHeightList = new List<double>();

        public void SetCommonHeight()
        {
            if (_TextHeightList.Count == 0)
            {
                _CommonTextHeight = 1;
            }
            else
            {
                //方式一 借助字典
                Dictionary<double, int> dic = new Dictionary<double, int>();
                _TextHeightList.ForEach(x =>
                {
                    if (dic.ContainsKey(x))
                        dic[x] += 1;
                    else
                        dic[x] = 0;
                });
                var max = dic.Max(x => x.Value);
                List<double> lisDupValues = dic.Where(x => x.Value == max).Select(x => x.Key).ToList();
                _CommonTextHeight = lisDupValues[0];

            }
        }
    }
}
