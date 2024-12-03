using System;
using System.Collections.Generic;

using MsCAD.Colors;

namespace MsCAD.DatabaseServices
{
    /// <summary>
    /// 图层
    /// </summary>
    public class Layer : DBTableRecord
    {
        /// <summary>
        /// 类名
        /// </summary>
        public override string className
        {
            get { return "Layer"; }
        }

        /// <summary>
        /// 颜色
        /// </summary>
        private Color _color = Color.FromRGB(255, 255, 255);
        public Color color
        {
            get { return _color; }
            set
            {
                if (value.colorMethod == ColorMethod.ByColor)
                {
                    _color = value;
                }
                else
                {
                    throw new System.Exception("Layer set color exception.");
                }
            }
        }
        /// <summary>
        /// 图层状态:0显示1隐藏
        /// </summary>
        private int _layerState = 0;
        public int layerState
        {
            get { return _layerState; }
            set { _layerState = value; }
        }

        public System.Drawing.Color colorValue
        {
            get
            {
                return System.Drawing.Color.FromArgb(_color.r, _color.g, _color.b);
            }
        }

        /// <summary>
        /// 线宽
        /// </summary>
        private LineWeight _lineWeight = LineWeight.ByLineWeightDefault;
        public LineWeight lineWeight
        {
            get { return _lineWeight; }
            set { _lineWeight = value; }
        }

        /// <summary>
        /// 构造函数
        /// </summary>
        public Layer(string name = "")
        {
            _name = name;
        }

        /// <summary>
        /// 克隆函数
        /// </summary>
        public override object Clone()
        {
            Layer layer = base.Clone() as Layer;
            layer._color = _color;
            layer._lineWeight = _lineWeight;

            return layer;
        }

        protected override DBObject CreateInstance()
        {
            return new Layer();
        }

        /// <summary>
        /// 写XML
        /// </summary>
        public override void XmlOut(Filer.XmlFiler filer)
        {
            base.XmlOut(filer);

            filer.Write("color", _color);
            filer.Write("lineWeight", _lineWeight);
        }

        /// <summary>
        /// 读XML
        /// </summary>
        public override void XmlIn(Filer.XmlFiler filer)
        {
            base.XmlIn(filer);

            filer.Read("color", out _color);
            filer.Read("lineWeight", out _lineWeight);
        }
    }
}
