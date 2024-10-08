﻿using System;
using System.Collections.Generic;

using MsCAD.Colors;
using MsMath;

namespace MsCAD.DatabaseServices
{
    public abstract class Entity : DBObject
    {
        /// <summary>
        /// 类名
        /// </summary>
        public override string className
        {
            get { return "Entity"; }
        }

        /// <summary>
        /// 外围边框
        /// </summary>
        public abstract Bounding bounding
        {
            get;
        }

        /// <summary>
        /// 颜色
        /// </summary>
        private Color _color = Color.ByLayer;
        public Color color
        {
            get { return _color; }
            set { _color = value; }
        }

        public System.Drawing.Color colorValue
        {
            get
            {
                switch (_color.colorMethod)
                {
                    case ColorMethod.ByBlock:
                        if (this.parent != null && this.parent is BlockReference)
                        {
                            BlockReference blockRef = this.parent as BlockReference;
                            return blockRef.colorValue;
                        }
                        else
                        {
                            return System.Drawing.Color.FromArgb(_color.r, _color.g, _color.b);
                        }

                    case ColorMethod.ByLayer:
                        Database db = this.database;
                        if (db != null && this.layer != "0" && db.layerTable.Has(this.layer))
                        {
                            Layer layer = db.layerTable[this.layer] as Layer;
                            return layer.colorValue;
                        }
                        else
                        {
                            return System.Drawing.Color.FromArgb(_color.r, _color.g, _color.b);
                        }

                    case ColorMethod.ByColor:
                    case ColorMethod.None:
                    default:
                        return System.Drawing.Color.FromArgb(_color.r, _color.g, _color.b);
                }
            }
        }

        /// <summary>
        /// 线宽
        /// </summary>
        private LineWeight _lineWeight = LineWeight.ByLayer;
        public LineWeight lineWeight
        {
            get { return _lineWeight; }
            set { _lineWeight = value; }
        }

        /// <summary>
        /// 图层
        /// </summary>
        private ObjectId _layerId = ObjectId.Null;
        public ObjectId layerId
        {
            get { return _layerId; }
            set { _layerId = value; }
        }

        public string layer
        {
            get
            {
                Database db = this.database;
                if (db != null 
                    && _layerId != ObjectId.Null
                    && db.layerTable.Has(_layerId))
                {
                    Layer layerRecord = db.GetObject(_layerId) as Layer;
                    return layerRecord.name;
                }
                return "";
            }
            set
            {
                Database db = this.database;
                if (db != null
                    && db.layerTable.Has(value))
                {
                    _layerId = db.layerTable[value].id;
                }
            }
        }
        /// <summary>
        /// 区域状态，0并集，1差集
        /// </summary>
        private int _regionState = 0;
        public int regionState
        {
            get { return _regionState; }
            set { _regionState = value; }
        }

        /// <summary>
        /// 对象Handle
        /// </summary>
        private string _entHandle = null;
        public string entHandle
        {
            get { return _entHandle; }
            set { _entHandle = value; }
        }


        /// <summary>
        /// 标注Handle
        /// </summary>
        private string _dimHandle = null;
        public string dimHandle
        {
            get { return _dimHandle; }
            set { _dimHandle = value; }
        }


        /// <summary>
        /// 绘制函数
        /// </summary>
        public virtual void Draw(IGraphicsDraw gd)
        {
        }

        /// <summary>
        /// 克隆函数
        /// </summary>
        public override object Clone()
        {
            Entity entity = base.Clone() as Entity;
            entity._color = _color;
            entity._layerId = _layerId;
            return entity;
        }

        /// <summary>
        /// 平移
        /// </summary>
        public abstract void Translate(Vector2 translation);

        /// <summary>
        /// Transform
        /// 前提: 变换不改变图元整体形状
        /// </summary>
        public abstract void TransformBy(Matrix3 transform);

        /// <summary>
        /// 移除
        /// </summary>
        protected override void _Erase()
        {
            if (_parent != null)
            {
                Block block = _parent as Block;
                block.RemoveEntity(this);
            }
        }

        /// <summary>
        /// 对象捕捉
        /// </summary>
        public virtual List<ObjectSnapPoint> GetSnapPoints()
        {
            return null;
        }

        /// <summary>
        /// 获取夹点
        /// </summary>
        public virtual List<GripPoint> GetGripPoints()
        {
            return null;
        }

        /// <summary>
        /// 设置夹点
        /// </summary>
        public virtual void SetGripPointAt(int index, GripPoint gripPoint, Vector2 newPosition)
        {
        }

        /// <summary>
        /// 写XML
        /// </summary>
        public override void XmlOut(Filer.XmlFiler filer)
        {
            base.XmlOut(filer);

            filer.Write("color", _color);
            filer.Write("lineWeight", _lineWeight.ToString());
            filer.Write("layer", _layerId);
            filer.Write("regionState", _regionState);
            filer.Write("entHandle", _entHandle);
            filer.Write("dimHandle", _dimHandle);
        }

        /// <summary>
        /// 读XML
        /// </summary>
        public override void XmlIn(Filer.XmlFiler filer)
        {
            base.XmlIn(filer);

            filer.Read("color", out _color);
            filer.Read("lineWeight", out _lineWeight);
            filer.Read("layer", out _layerId);
            filer.Read("regionState", out _regionState);
            filer.Read("entHandle", out _entHandle);
            filer.Read("dimHandle", out _dimHandle);
        }
    }
}
