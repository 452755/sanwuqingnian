using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

using MsCAD.ApplicationServices;
using MsCAD.DatabaseServices;
using MsCAD.UI;
using MsMath;

namespace MsCAD.Commands.Modify
{
    /// <summary>
    /// 筛选命令
    /// </summary>
    internal class ChooseCmd : ModifyCmd
    {
        /// <summary>
        /// 操作的图元
        /// </summary>
        private List<Entity> _items = new List<Entity>();//条件筛选的结果
        private List<Entity> _itemsNegative = new List<Entity>();//不是不是筛选的结果
        #region 筛选条件
        private Dictionary<string, double> _data = new Dictionary<string, double>();
        public Dictionary<string, double> Data
        {
            get
            {
                return _data;
            }

            set
            {
                _data = value;
            }
        }
        #endregion
        /// <summary>
        /// 条件筛选
        /// </summary>
        private void InitializeItemsToChoose()
        {
            double _cirRadiusMin = 0;
            double _cirRadiusMax = 0;
            double _rayRadiusMin = 0;
            double _rayRadiusMax = 0;
            double _rayRadianMin = 0;
            double _rayRadianMax = 0;
            double _linMin = 0;
            double _linMax = 0;
            _data.TryGetValue("cirRadiusMin", out _cirRadiusMin);
            _data.TryGetValue("cirRadiusMax", out _cirRadiusMax);
            _data.TryGetValue("rayRadiusMin", out _rayRadiusMin);
            _data.TryGetValue("rayRadiusMax", out _rayRadiusMax);
            _data.TryGetValue("rayRadianMin", out _rayRadianMin);
            _data.TryGetValue("rayRadianMax", out _rayRadianMax);
            _data.TryGetValue("linMin", out _linMin);
            _data.TryGetValue("linMax", out _linMax);

            foreach (DBObject bock in database.blockTable)
            {
                foreach (Object item in ((Block)bock).Items)
                {
                    if ("Circle".Equals(((Entity)item).className))
                    {
                        Circle entity = (Circle)item;
                        double radius = entity.radius;
                        if (radius >   _cirRadiusMin && radius < _cirRadiusMax)
                        {
                            _items.Add(entity);
                        }
                        else
                        {
                            _itemsNegative.Add((Entity)item);
                        }
                    }
                    else if ("Arc".Equals(((Entity)item).className))
                    {
                        Arc entity = (Arc)item;
                        double radius = entity.radius;
                        double radian = 0;
                        if (entity.endAngle < entity.startAngle)
                        {
                            radian = ( 2*Math.PI - entity.startAngle + entity.endAngle) * 180 / Math.PI;
                        }
                        else
                        {
                            radian = (entity.endAngle - entity.startAngle) * 180 / Math.PI;
                        }
                        if (radius > _rayRadiusMin && radius < _rayRadiusMax)
                        {
                            _items.Add(entity);
                        }
                        else if (radian > _rayRadianMin && radian < _rayRadianMax)
                        {
                            _items.Add(entity);
                        }
                        else
                        {
                            _itemsNegative.Add((Entity)item);
                        }
                    }
                    else if ("Line".Equals(((Entity)item).className))
                    {
                        Line entity = (Line)item;
                        double a = Math.Abs(entity.startPoint.x - entity.endPoint.x);
                        double b = Math.Abs(entity.startPoint.y - entity.endPoint.y);
                        double c = Math.Sqrt(a * a + b * b);
                        if (c > _linMin && c < _linMax)
                        {
                            _items.Add(entity);
                        }
                        else
                        {
                            _itemsNegative.Add((Entity)item);
                        }
                    }
                    else if ("Polyline".Equals(((Entity)item).className))//多段线是连续多条直线不是很多条直线连接在一起，故只能选择一个整体
                    {
                        Polyline entity = (Polyline)item;
                        bool closed = entity.closed;
                        bool isLinAdd = false;
                        int iNumber = entity.NumberOfVertices;
                        for (int i = 0; i < iNumber - 2; i++)
                        {
                            Vector2 start = entity.GetPointAt(i);
                            Vector2 end = entity.GetPointAt(i + 1);
                            double a = Math.Abs(start.x - end.x);
                            double b = Math.Abs(start.y - end.y);
                            double c = Math.Sqrt(a * a + b * b);
                            if (c > _linMin && c < _linMax)
                            {
                                isLinAdd = true;
                                break;
                            }
                        }
                        if (closed&& isLinAdd == false)
                        {
                            Vector2 start = entity.GetPointAt(iNumber - 2);
                            Vector2 end = entity.GetPointAt(iNumber - 1);
                            double a = Math.Abs(start.x - end.x);
                            double b = Math.Abs(start.y - end.y);
                            double c = Math.Sqrt(a * a + b * b);
                            if (c > _linMin && c < _linMax)
                            {
                                isLinAdd = true;
                            }
                        }
                        if (isLinAdd)
                        {
                            _items.Add(entity);
                        }
                        else
                        {
                            _itemsNegative.Add((Entity)item);
                        }
                    }
                    else
                    {
                        _itemsNegative.Add((Entity)item);
                    }
                }
            }
        }
        /// <summary>
        /// 差集筛选
        /// </summary>
        private void InitializeItemsToDiff()
        {
            double id = 0;
            double regionState = 0;
            _data.TryGetValue("id", out id);
            _data.TryGetValue("regionState", out regionState);
            foreach (DBObject bock in database.blockTable)
            {
                foreach (Entity item in ((Block)bock).Items)
                {
                    if (double.Parse( item.id.ToString()) == id)
                    {
                        item.regionState = (int)regionState;
                    }
                }
            }
        }
        private void InitializeItemsTypeState()
        {
            double id = 0;
            double regionState = 0;
            _data.TryGetValue("id", out id);
            _data.TryGetValue("regionState", out regionState);
            foreach (DBObject bock in database.blockTable)
            {
                foreach (Entity item in ((Block)bock).Items)
                {
                    if (double.Parse(item.id.ToString()) == id)
                    {
                        _items.Add(item);
                    }
                    else
                    {
                        _itemsNegative.Add(item);
                    }
                }
            }
            _data.Clear();
            _data.Add("isState", 0);
            _data.Add("id", id);
            _data.Add("regionState", regionState);
        }

        public override void Initialize()
        {
            base.Initialize();

            double _isState = 0;
            _data.TryGetValue("isState", out _isState);
            if (_isState == 0)//筛选数据用
            {
                InitializeItemsToChoose();
                _mgr.FinishCurrentCommandScreen();//显示筛选后的图形
            }
            else if (_isState == 1)//删除功能
            {
                InitializeItemsToChoose();
                _mgr.FinishCurrentCommand();//删除
            }
            //2为了初始给差集列表准备数据，没有其他动作
            else if (_isState == 3)//筛选数据用
            {
                InitializeItemsToDiff();
                _mgr.FinishCurrentCommandScreen();//显示筛选后的图形
            }
            else if (_isState == 4)//更新视野，在点击列表的图元类型时同步更新视野，把视野对应的图元选中
            {
                InitializeItemsTypeState();
                _mgr.FinishCurrentCommandScreen();//显示筛选后的图形
            }
        }

        /// <summary>
        /// 提交到数据库
        /// </summary>
        protected override void Commit()
        {
            double _isState = 0;
            _data.TryGetValue("isState", out _isState);
            if (_isState == 0)
            {
                List<Selection> selection = new List<Selection>();
                foreach (Entity item in _items)
                {
                    Selection sel = new Selection();
                    sel.objectId = item.id;
                    selection.Add(sel);
                }
                if ((_mgr.presenter.document as Document).selections.Count > 0)
                {
                    (_mgr.presenter.document as Document).selections.Clear();
                }
                (_mgr.presenter.document as Document).selections.Add(selection);
            }
            else if (_isState == 1)
            {
                foreach (Entity item in _itemsNegative)
                {
                    item.Erase();
                }
            }
        }

        /// <summary>
        /// 回滚撤销
        /// </summary>
        protected override void Rollback()
        {
            foreach (Entity item in _items)
            {
                _mgr.presenter.AppendEntity(item);
            }
        }

        public override EventResult OnMouseDown(MouseEventArgs e)
        {
            return EventResult.Handled;
        }

        public override EventResult OnMouseUp(MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                if (_mgr.presenter.selections.Count > 0)
                {
                    InitializeItemsToChoose();
                    _mgr.FinishCurrentCommand();
                }
                else
                {
                    _mgr.CancelCurrentCommand();
                }
            }

            return EventResult.Handled;
        }

        public override EventResult OnMouseMove(MouseEventArgs e)
        {
            return EventResult.Handled;
        }

        public override EventResult OnKeyDown(KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Escape)
            {
                _mgr.CancelCurrentCommand();
            }

            return EventResult.Handled;
        }

        public override EventResult OnKeyUp(KeyEventArgs e)
        {
            return EventResult.Handled;
        }

        public override void OnPaint(Graphics g)
        {
        }
    }
}
