﻿using System;
using System.Collections.Generic;
using MsMath;

namespace MsCAD.DatabaseServices
{
    public class Text : Entity
    {
        /// <summary>
        /// 类名
        /// </summary>
        public override string className
        {
            get { return "Text"; }
        }

        /// <summary>
        /// 文本
        /// </summary>
        private string _text = "";
        public string text
        {
            get { return _text; }
            set { _text = value; }
        }

        /// <summary>
        /// 字体高度
        /// </summary>
        private double _height = 3.5;
        public double height
        {
            get { return _height; }
            set
            {
                if (value > 0)
                    _height = value;
            }
        }

        /// <summary>
        /// 字体
        /// </summary>
        private string _font = "";
        public string font
        {
            get { return _font; }
            set { _font = value; }
        }

        /// <summary>
        /// 位置
        /// </summary>
        private Vector2 _position = new Vector2(0, 0);
        public Vector2 position
        {
            get { return _position; }
            set { _position = value; }
        }

        /// <summary>
        /// 对齐方式
        /// </summary>
        private TextAlignment _alignment = TextAlignment.LeftMiddle;
        public TextAlignment alignment
        {
            get { return _alignment; }
            set { _alignment = value; }
        }

        /// <summary>
        /// 外围边框
        /// </summary>
        private Bounding _bounding = new Bounding();
        public override Bounding bounding
        {
            get
            {
                return _bounding;
            }
        }

        public void UpdateBounding(double width, double height)
        {
            switch (_alignment)
            {
                case TextAlignment.LeftBottom:
                    _bounding = new Bounding(_position, new Vector2(_position.x + width, _position.y + height));
                    break;
                case TextAlignment.LeftMiddle://默认
                    _bounding = new Bounding(new Vector2(_position.x - width / 2, _position.y), new Vector2(_position.x + width / 2, _position.y + height));
                    break;
                case TextAlignment.LeftTop:
                    _bounding = new Bounding(_position, new Vector2(_position.x + width, _position.y - height));
                    break;
                case TextAlignment.CenterBottom:
                    _bounding = new Bounding(
                        new Vector2(_position.x - width / 2, _position.y),
                        new Vector2(_position.x + width / 2, _position.y + height));
                    break;
                case TextAlignment.CenterMiddle:
                    _bounding = new Bounding(_position, width, height);
                    break;
                case TextAlignment.CenterTop:
                    _bounding = new Bounding(
                        new Vector2(_position.x - width / 2, _position.y),
                        new Vector2(_position.x + width / 2, _position.y - height));
                    break;
                case TextAlignment.RightBottom:
                    _bounding = new Bounding(_position, new Vector2(_position.x - width, _position.y + height));
                    break;
                case TextAlignment.RightMiddle:
                    _bounding = new Bounding(
                        new Vector2(_position.x, _position.y + height / 2),
                        new Vector2(_position.x - width, _position.y - height / 2));
                    break;
                case TextAlignment.RightTop:
                    _bounding = new Bounding(_position, new Vector2(_position.x - width, _position.y - height));
                    break;
                default:
                    _bounding = new Bounding(_position, 0, 0);
                    break;
            }
        }

        /// <summary>
        /// 构造函数
        /// </summary>
        public Text()
        {
        }

        /// <summary>
        /// 绘制函数
        /// </summary>
        public override void Draw(IGraphicsDraw gd)
        {
            Vector2 size = gd.DrawText(_position, _text, _height, _font, (MsCAD.TextAlignment)_alignment);
            this.UpdateBounding(size.x, size.y);
        }

        /// <summary>
        /// 克隆函数
        /// </summary>
        public override object Clone()
        {
            Text text = base.Clone() as Text;
            text._text = _text;
            text._height = _height;
            text._font = _font;
            text._position = _position;
            text._alignment = _alignment;
            return text;
        }

        protected override DBObject CreateInstance()
        {
            return new Text();
        }

        /// <summary>
        /// 平移
        /// </summary>
        public override void Translate(Vector2 translation)
        {
            _position += translation;
        }

        /// <summary>
        /// Transform
        /// </summary>
        public override void TransformBy(Matrix3 transform)
        {
            _position = transform * _position;
        }

        /// <summary>
        /// 对象捕捉点
        /// </summary>
        public override List<ObjectSnapPoint> GetSnapPoints()
        {
            List<ObjectSnapPoint> snapPnts = new List<ObjectSnapPoint>();
            snapPnts.Add(new ObjectSnapPoint(ObjectSnapMode.End, _position));

            return snapPnts;
        }

        /// <summary>
        /// 获取夹点
        /// </summary>
        public override List<GripPoint> GetGripPoints()
        {
            List<GripPoint> gripPnts = new List<GripPoint>();
            gripPnts.Add(new GripPoint(GripPointType.Undefined, _position));
            return gripPnts;
        }

        /// <summary>
        /// 设置夹点
        /// </summary>
        public override void SetGripPointAt(int index, GripPoint gripPoint, Vector2 newPosition)
        {
            if (index == 0)
            {
                _position = newPosition;
            }
        }

        /// <summary>
        /// 写XML
        /// </summary>
        public override void XmlOut(Filer.XmlFiler filer)
        {
            base.XmlOut(filer);
            filer.Write("position", _position);
            filer.Write("text", _text);
            filer.Write("height", _height);
        }

        /// <summary>
        /// 读XML
        /// </summary>
        public override void XmlIn(Filer.XmlFiler filer)
        {
            base.XmlIn(filer);
            filer.Read("position", out _position);
            filer.Read("text", out _text);
            filer.Read("height", out _height);
        }
    }
}
