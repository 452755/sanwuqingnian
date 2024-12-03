using System;
using System.Collections.Generic;
using System.Xml;
using netDxf;
using MsCAD.Colors;
using netDxf.Entities;
using System.IO;
using System.Collections.ObjectModel;
using HalconDotNet;
using GerberVS;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Linq;

namespace MsCAD.DatabaseServices
{
    public class Database
    {
        /// <summary>
        /// 块表
        /// </summary>
        private BlockTable _blockTable = null;
        public BlockTable blockTable
        {
            get { return _blockTable; }
        }
        public static ObjectId BlockTableId
        {
            get { return new ObjectId(TableIds.BlockTableId); }
        }

        /// <summary>
        /// 图层表
        /// </summary>
        private LayerTable _layerTable = null;
        public static ObjectId LayerTableId
        {
            get { return new ObjectId(TableIds.LayerTableId); }
        }
        public LayerTable layerTable
        {
            get { return _layerTable; }
        }

        /// <summary>
        /// ID
        /// </summary>
        private Dictionary<ObjectId, DBObject> _dictId2Object = null;
        internal ObjectId currentMaxId
        {
            get
            {
                if (_dictId2Object == null || _dictId2Object.Count == 0)
                {
                    return ObjectId.Null;
                }
                else
                {
                    ObjectId id = ObjectId.Null;
                    foreach (KeyValuePair<ObjectId, DBObject> kvp in _dictId2Object)
                    {
                        if (kvp.Key.CompareTo(id) > 0)
                        {
                            id = kvp.Key;
                        }
                    }
                    return id;
                }
            }
        }

        private ObjectIdMgr _idMgr = null;

        /// <summary>
        /// 文件名
        /// </summary>
        private string _fileName = null;
        public string fileName
        {
            get { return _fileName; }
        }

        public Dictionary<ObjectId, DBObject> DictId2Object
        {
            get
            {
                return _dictId2Object;
            }
        }
        private LibGerberVS gerberLib = null;
        private GerberProject project = null;

        List<netDxf.Entities.Dimension> G_DimList = new List<netDxf.Entities.Dimension>();

        /// <summary>
        /// 构造函数
        /// </summary>
        public Database()
        {
            _dictId2Object = new Dictionary<ObjectId, DBObject>();
            _idMgr = new ObjectIdMgr(this);

            _blockTable = new BlockTable(this);
            Block modelSpace = new Block();
            modelSpace.name = "ModelSpace";
            _blockTable.Add(modelSpace);
            IdentifyDBTable(_blockTable);

            _layerTable = new LayerTable(this);
            IdentifyDBTable(_layerTable);


            gerberLib = new LibGerberVS();
            project = gerberLib.CreateNewProject();

        }

        /// <summary>
        /// 通过ID获取数据库对象
        /// </summary>
        public DBObject GetObject(ObjectId oid)
        {
            if (_dictId2Object.ContainsKey(oid))
            {
                return _dictId2Object[oid];
            }
            else
            {
                return null;
            }
        }

        /// <summary>
        /// 打开文件
        /// </summary>
        /// <param name="fileFullPath">文件路径</param>
        public bool Open(string fileFullPath)
        {
            if (_fileName == null || _fileName == "")
            {
                if (fileFullPath.EndsWith(".mscad", StringComparison.InvariantCultureIgnoreCase))
                    XmlIn(fileFullPath);
                else if (fileFullPath.EndsWith(".dxf", StringComparison.InvariantCultureIgnoreCase))
                    DxfIn(fileFullPath);
                else if (fileFullPath.EndsWith(".art", StringComparison.InvariantCultureIgnoreCase) || fileFullPath.EndsWith(".gbr", StringComparison.InvariantCultureIgnoreCase))
                    GerberIn(fileFullPath);
                else
                    return false;
            }
            return true;
        }
        /// <summary>
        /// 生成G代码
        /// </summary>
        public ObservableCollection<string> CreateG()
        {
            return createG();
        }

        /// <summary>
        /// 保存文件
        /// </summary>
        public void Save()
        {
            if (_fileName != null
                && System.IO.File.Exists(_fileName))
            {
                if (_fileName.EndsWith(".mscad", StringComparison.InvariantCultureIgnoreCase))
                    XmlOut(_fileName);
                else if (_fileName.EndsWith(".dxf", StringComparison.InvariantCultureIgnoreCase))
                    DxfOut(_fileName);
            }
        }

        /// <summary>
        /// 另存为
        /// </summary>
        /// <param name="fileFullPath">文件路径</param>
        /// <param name="rename">是否重命名</param>
        public void SaveAs(string fileFullPath, bool rename = false)
        {
            if (fileFullPath.EndsWith(".mscad", StringComparison.InvariantCultureIgnoreCase))
                XmlOut(fileFullPath);
            else if (fileFullPath.EndsWith(".dxf", StringComparison.InvariantCultureIgnoreCase))
                DxfOut(fileFullPath);

            if (rename)
            {
                _fileName = fileFullPath;
            }
        }
        #region 写XML文件
        /// <summary>
        /// 写XML文件
        /// </summary>
        /// <param name="xmlFileFullPath">XML文件全路径</param>
        internal void XmlOut(string xmlFileFullPath)
        {
            Filer.XmlFilerImpl xmlFilerImpl = new Filer.XmlFilerImpl();

            //
            xmlFilerImpl.NewSubNodeAndInsert("Database");
            {
                // block table
                xmlFilerImpl.NewSubNodeAndInsert(_blockTable.className);
                _blockTable.XmlOut(xmlFilerImpl);
                xmlFilerImpl.Pop();

                // layer table
                xmlFilerImpl.NewSubNodeAndInsert(_layerTable.className);
                _layerTable.XmlOut(xmlFilerImpl);
                xmlFilerImpl.Pop();
            }
            xmlFilerImpl.Pop();

            //
            xmlFilerImpl.Save(xmlFileFullPath);
        }
        #endregion
        #region 写Dxf文件
        /// <summary>
        /// 写Dxf文件
        /// </summary>
        /// <param name="dxfFileFullPath">DXF文件全路径</param>
        internal void DxfOut(string dxfFileFullPath)
        {
            DxfDocument dxf = getDxfDocument();
            if (dxf != null)
            {
                dxf.Save(dxfFileFullPath);
            }
        }
        private DxfDocument getDxfDocument()
        {
            DxfDocument dxf = null;
            //获取当前所有数据
            Filer.XmlFilerImpl xmlFilerImpl = new Filer.XmlFilerImpl();
            xmlFilerImpl.NewSubNodeAndInsert("Database");
            {
                // block table
                xmlFilerImpl.NewSubNodeAndInsert(_blockTable.className);
                _blockTable.XmlOut(xmlFilerImpl);
                xmlFilerImpl.Pop();

                // layer table
                xmlFilerImpl.NewSubNodeAndInsert(_layerTable.className);
                _layerTable.XmlOut(xmlFilerImpl);
                xmlFilerImpl.Pop();
            }
            xmlFilerImpl.Pop();

            var gCode = new ObservableCollection<string>();
            XmlNode dbNode = xmlFilerImpl.xmldoc.SelectSingleNode("Database");
            if (dbNode == null)
            {
                return dxf;
            }
            XmlNode blockTblNode = dbNode.SelectSingleNode(_blockTable.className);//块表
            if (blockTblNode == null)
            {
                return dxf;
            }
            XmlNodeList blocks = blockTblNode.SelectNodes("Block");
            dxf = new DxfDocument();
            foreach (var dim in G_DimList)//先清除标注子控件
            {
                dim.Block.Entities.Clear();
            }
            foreach (XmlNode blockNode in blocks)
            {
                XmlNode entitiesNode = blockNode.SelectSingleNode("entities");
                if (entitiesNode != null && entitiesNode.ChildNodes != null)
                {
                    foreach (XmlNode entityNode in entitiesNode.ChildNodes)
                    {
                        if (entityNode.Name.Equals("Line"))//直线
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 startPoint, endPoint;
                            node.Read("startPoint", out startPoint);
                            node.Read("endPoint", out endPoint);

                            netDxf.Entities.Line line = new netDxf.Entities.Line();
                            Vector3 sVector = new Vector3();
                            sVector.X = startPoint.x;
                            sVector.Y = startPoint.y;
                            line.StartPoint = sVector;
                            Vector3 eVector = new Vector3();
                            eVector.X = endPoint.x;
                            eVector.Y = endPoint.y;
                            line.EndPoint = eVector;

                            AddEntity(line, node, dxf);

                        }
                        else if (entityNode.Name.Equals("Polyline"))//折线
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            bool closed;
                            string vertices;
                            int type = 0;
                            node.Read("closed", out closed);
                            node.Read("vertices", out vertices);
                            node.Read("type", out type);
                            if (type == 0)
                            {
                                netDxf.Entities.Polyline polyline = new netDxf.Entities.Polyline();
                                string[] sVertices = vertices.Split('|');
                                int i = 0;
                                foreach (string sXY in sVertices)
                                {
                                    string[] sXYValue = sXY.Split(';');
                                    if (sXYValue.Length == 3)
                                    {
                                        PolylineVertex polylineVertex = new PolylineVertex();
                                        Vector3 vector = new Vector3();
                                        vector.X = double.Parse(sXYValue[0]);
                                        vector.Y = double.Parse(sXYValue[1]);
                                        polylineVertex.Position = vector;
                                        polyline.Vertexes.Add(polylineVertex);
                                    }
                                }
                                polyline.IsClosed = closed;

                                AddEntity(polyline, node, dxf);
                            }
                            else
                            {
                                netDxf.Entities.LwPolyline polyline = new netDxf.Entities.LwPolyline();
                                string[] sVertices = vertices.Split('|');
                                int i = 0;
                                foreach (string sXY in sVertices)
                                {
                                    string[] sXYValue = sXY.Split(';');
                                    if (sXYValue.Length == 3)
                                    {
                                        LwPolylineVertex polylineVertex = new LwPolylineVertex();
                                        Vector2 vector = new Vector2();
                                        vector.X = double.Parse(sXYValue[0]);
                                        vector.Y = double.Parse(sXYValue[1]);
                                        polylineVertex.Position = vector;
                                        polylineVertex.Bulge= double.Parse(sXYValue[2]);
                                        polyline.Vertexes.Add(polylineVertex);
                                    }
                                }
                                polyline.IsClosed = closed;

                                AddEntity(polyline, node, dxf);
                            }
                        }
                        else if (entityNode.Name.Equals("Circle"))//圆形
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 center;
                            double radius;
                            node.Read("center", out center);
                            node.Read("radius", out radius);
                            netDxf.Entities.Circle circle = new netDxf.Entities.Circle();
                            Vector3 vCenter = new Vector3();
                            vCenter.X = center.x;
                            vCenter.Y = center.y;
                            circle.Center = vCenter;
                            circle.Radius = radius;

                            AddEntity(circle, node, dxf);
                        }
                        else if (entityNode.Name.Equals("Arc"))//圆弧
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 center;
                            double radius, startAngle, endAngle;
                            node.Read("center", out center);
                            node.Read("radius", out radius);
                            node.Read("startAngle", out startAngle);
                            node.Read("endAngle", out endAngle);
                            netDxf.Entities.Arc arc = new netDxf.Entities.Arc();
                            Vector3 vCenter = new Vector3();
                            vCenter.X = center.x;
                            vCenter.Y = center.y;
                            arc.Center = vCenter;
                            arc.Radius = radius;
                            arc.StartAngle = startAngle * 180 / Math.PI;
                            arc.EndAngle = endAngle * 180 / Math.PI;

                            AddEntity(arc, node, dxf);
                        }
                        else if (entityNode.Name.Equals("Xline"))//射线
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 basePoint, direction;
                            node.Read("basePoint", out basePoint);
                            node.Read("direction", out direction);
                            netDxf.Entities.XLine xLine = new netDxf.Entities.XLine();
                            Vector3 vBasePoint = new Vector3();
                            vBasePoint.X = basePoint.x;
                            vBasePoint.Y = basePoint.y;
                            xLine.Origin = vBasePoint;
                            Vector3 vDirection = new Vector3();
                            vDirection.X = direction.x;
                            vDirection.Y = direction.y;
                            xLine.Direction = vDirection;

                            AddEntity(xLine, node, dxf);
                        }
                        else if (entityNode.Name.Equals("Ray"))//构造线
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 basePoint, direction;
                            node.Read("basePoint", out basePoint);
                            node.Read("direction", out direction);
                            netDxf.Entities.Ray ray = new netDxf.Entities.Ray();
                            Vector3 vBasePoint = new Vector3();
                            vBasePoint.X = basePoint.x;
                            vBasePoint.Y = basePoint.y;
                            ray.Origin = vBasePoint;
                            Vector3 vDirection = new Vector3();
                            vDirection.X = direction.x;
                            vDirection.Y = direction.y;
                            ray.Direction = vDirection;

                            AddEntity(ray, node, dxf);
                        }
                        else if (entityNode.Name.Equals("Text"))//文本
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 position = new MsMath.Vector2(0, 0);
                            string text = "";
                            double height = 3.5;
                            node.Read("position", out position);
                            node.Read("text", out text);
                            node.Read("height", out height);
                            netDxf.Entities.MText mtext = new netDxf.Entities.MText();
                            Vector3 sVector = new Vector3();
                            sVector.X = position.x;
                            sVector.Y = position.y;
                            mtext.Position = sVector;
                            mtext.Value = text;
                            mtext.Height = height;
                            mtext.AttachmentPoint = MTextAttachmentPoint.MiddleCenter;//暂时默认
                            mtext.DrawingDirection = MTextDrawingDirection.LeftToRight;//暂时默认

                            AddEntity(mtext, node, dxf);
                        }
                        else if (entityNode.Name.Equals("Solid"))//三四角形
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            string vertices;
                            node.Read("vertices", out vertices);
                            netDxf.Entities.Solid solid = new netDxf.Entities.Solid();
                            string[] sVertices = vertices.Split('|');

                            for (int i = 0; i < sVertices.Length; i++)
                            {
                                string[] sXYValue = sVertices[i].Split(';');
                                if (sXYValue.Length == 2)
                                {
                                    Vector2 vector = new Vector2();
                                    vector.X = double.Parse(sXYValue[0]);
                                    vector.Y = double.Parse(sXYValue[1]);
                                    switch (i)
                                    {
                                        case 0:
                                            solid.FirstVertex = vector;
                                            break;
                                        case 1:
                                            solid.SecondVertex = vector;
                                            break;
                                        case 2:
                                            solid.ThirdVertex = vector;
                                            solid.FourthVertex = vector;
                                            break;
                                    }
                                }
                            }
                            AddEntity(solid, node, dxf);
                        }
                        else if (entityNode.Name.Equals("Ellipse"))//椭圆
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 center;
                            double _MajorAxis;
                            double _MinorAxis;
                            double _Rotation;
                            node.Read("center", out center);
                            node.Read("MajorAxis", out _MajorAxis);
                            node.Read("MinorAxis", out _MinorAxis);
                            node.Read("Rotation", out _Rotation);
                            netDxf.Entities.Ellipse ellipse = new netDxf.Entities.Ellipse();
                            Vector3 vCenter = new Vector3();
                            vCenter.X = center.x;
                            vCenter.Y = center.y;
                            ellipse.Center = vCenter;
                            ellipse.MajorAxis = _MajorAxis;
                            ellipse.MinorAxis = _MinorAxis;
                            ellipse.Rotation = _Rotation;
                            AddEntity(ellipse, node, dxf);
                        }
                    }
                }
            }
            //最后添加标准
            foreach (var ent in G_DimList)
            {
                netDxf.Entities.Dimension dimClone = (netDxf.Entities.Dimension)ent.Clone();
                dimClone.Block = (netDxf.Blocks.Block)ent.Block.Clone(Guid.NewGuid().ToString().Replace("-", ""));
                if (dimClone.Block.Entities.Count > 0)
                {
                    dimClone.UserText = null;
                    dxf.AddEntity(dimClone);
                }
            }
            return dxf;
        }


        void AddEntity(EntityObject obj, Filer.XmlFilerImpl node, DxfDocument dxf)
        {
            LineWeight mLineWeight = new LineWeight();
            node.Read("lineWeight", out mLineWeight);

            MsCAD.Colors.Color _color = MsCAD.Colors.Color.ByLayer;
            node.Read("color", out _color);

            obj.Color = new AciColor(_color.r, _color.g, _color.b);

            obj.Lineweight = (netDxf.Lineweight)mLineWeight;

            string dimHandle;
            node.Read("dimHandle", out dimHandle);

            if (string.IsNullOrEmpty(dimHandle))
            {
                dxf.AddEntity(obj);
            }
            else
            {
                netDxf.Entities.Dimension item = G_DimList.Find(x => x.Handle == dimHandle || (x.UserText != null && x.UserText == dimHandle));
                if (item == null)
                {
                    dxf.AddEntity(obj);
                }
                else
                {
                    string entHandle;
                    node.Read("entHandle", out entHandle);
                    item.Block.Entities.Add(obj);

                }
            }
        }





        #endregion
        #region 写png图像和Region的.hobj文件
        /// <summary>
        /// png另存为
        /// </summary>
        /// <param name="fileFullPath">文件路径</param>
        /// <param name="rename">是否重命名</param>
        public void SaveAsImage(string fileFullPath, bool rename = false)
        {
            //获取得区域，区域在窗口上显示，后保存
            HObject ho_image;
            HObject allRegion = null;

            HOperatorSet.GenEmptyObj(out ho_image);

            HObject ho_NewImage, ho_ImageResult;

            HOperatorSet.GenEmptyObj(out ho_NewImage);
            HOperatorSet.GenEmptyObj(out ho_ImageResult);
            if (HDevWindowStack.IsOpen())
            {
                HOperatorSet.SetDraw(HDevWindowStack.GetActive(), "margin");
            }
            ho_NewImage.Dispose();
            HOperatorSet.GenImageConst(out ho_NewImage, "byte", 800, 600);//创建一张空的图像
            getCanvasRegion(out allRegion);//获取画布的所有图元并转成区域
            ho_ImageResult.Dispose();
            HOperatorSet.PaintRegion(allRegion, ho_NewImage, out ho_ImageResult, 255, "fill");//在图像上画区域
            HOperatorSet.WriteImage(ho_ImageResult, "png", 0, fileFullPath);//保存图像
            ho_NewImage.Dispose();
            allRegion.Dispose();
            ho_ImageResult.Dispose();
        }
        /// <summary>
        /// 区域另存为
        /// </summary>
        /// <param name="fileFullPath">文件路径</param>
        /// <param name="rename">是否重命名</param>
        public void SaveRegion(string fileFullPath, bool rename = false)
        {
            HObject ho_image;
            HObject allRegion = null;

            HOperatorSet.GenEmptyObj(out ho_image);

            HObject ho_NewImage, ho_ImageResult;

            HOperatorSet.GenEmptyObj(out ho_NewImage);
            HOperatorSet.GenEmptyObj(out ho_ImageResult);
            if (HDevWindowStack.IsOpen())
            {
                HOperatorSet.SetDraw(HDevWindowStack.GetActive(), "margin");
            }
            ho_NewImage.Dispose();
            HOperatorSet.GenImageConst(out ho_NewImage, "byte", 800, 400);//创建一张空的图像
            getCanvasRegion(out allRegion);//获取画布的所有图元并转成区域
            ho_ImageResult.Dispose();
            HOperatorSet.PaintRegion(allRegion, ho_NewImage, out ho_ImageResult, 255, "fill");//在图像上画区域
            HOperatorSet.WriteRegion(allRegion, fileFullPath);//保存区域
            ho_NewImage.Dispose();
            ho_ImageResult.Dispose();
            allRegion.Dispose();
        }
        /// <summary>
        /// 读取数据生成区域
        /// </summary>
        /// <param name="allRegion">区域</param>
        /// <returns></returns>
        private bool getCanvasRegion(out HObject allRegion)
        {
            //获取当前所有数据
            Filer.XmlFilerImpl xmlFilerImpl = new Filer.XmlFilerImpl();
            xmlFilerImpl.NewSubNodeAndInsert("Database");
            {
                // block table
                xmlFilerImpl.NewSubNodeAndInsert(_blockTable.className);
                _blockTable.XmlOut(xmlFilerImpl);
                xmlFilerImpl.Pop();

                // layer table
                xmlFilerImpl.NewSubNodeAndInsert(_layerTable.className);
                _layerTable.XmlOut(xmlFilerImpl);
                xmlFilerImpl.Pop();
            }
            xmlFilerImpl.Pop();


            HObject genRegion = null;
            HObject uniRegion = null;
            HObject difRegion = null;
            HOperatorSet.GenEmptyObj(out allRegion);//区域集合
            HOperatorSet.GenEmptyObj(out genRegion);//当前生成的区域
            HOperatorSet.GenEmptyObj(out uniRegion);//并集的区域
            HOperatorSet.GenEmptyObj(out difRegion);//差集的区域

            XmlNode dbNode = xmlFilerImpl.xmldoc.SelectSingleNode("Database");
            if (dbNode == null)
            {
                return false;
            }
            XmlNode blockTblNode = dbNode.SelectSingleNode(_blockTable.className);//块表
            if (blockTblNode == null)
            {
                return false;
            }
            XmlNodeList blocks = blockTblNode.SelectNodes("Block");
            foreach (XmlNode blockNode in blocks)
            {
                XmlNode entitiesNode = blockNode.SelectSingleNode("entities");
                if (entitiesNode != null && entitiesNode.ChildNodes != null)
                {
                    foreach (XmlNode entityNode in entitiesNode.ChildNodes)
                    {
                        if (entityNode.Name.Equals("Line"))//直线
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 startPoint, endPoint;
                            node.Read("startPoint", out startPoint);
                            node.Read("endPoint", out endPoint);
                            genRegion.Dispose();
                            HOperatorSet.GenRegionLine(out genRegion, 280 - startPoint.y * 10, startPoint.x * 10 + 280, 280 - endPoint.y * 10, endPoint.x * 10 + 280);
                            //HOperatorSet.ConcatObj(allRegion, genRegion, out allRegion);

                            int regionState = 0;//获取状态判断差集和并集
                            node.Read("regionState", out regionState);
                            if (regionState == 0)
                                HOperatorSet.Union2(uniRegion, genRegion, out uniRegion);
                            else
                                HOperatorSet.Union2(difRegion, genRegion, out difRegion);
                        }
                        else if (entityNode.Name.Equals("Polyline"))//折线
                        {
                            HTuple hv_Rows, hv_Cols, hv_Row = new HTuple(), hv_Column = new HTuple();

                            hv_Rows = new HTuple();
                            hv_Cols = new HTuple();
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            bool closed;
                            string vertices, startX = "", startY = "";
                            node.Read("closed", out closed);
                            node.Read("vertices", out vertices);
                            string[] sVertices = vertices.Split('|');//getCanvasRegion
                            int i = 0;
                            foreach (string sXY in sVertices)
                            {
                                string[] sXYValue = sXY.Split(';');
                                if (sXYValue.Length == 3)
                                {
                                    if (i == 0)
                                    {
                                        i = 1;
                                        startX = sXYValue[0];
                                        startY = sXYValue[1];
                                    }
                                    hv_Row = double.Parse(sXYValue[0]) * 10 + 280;
                                    hv_Column = 280 - double.Parse(sXYValue[1]) * 10;

                                    hv_Rows = hv_Rows.TupleConcat(hv_Row);
                                    hv_Cols = hv_Cols.TupleConcat(hv_Column);
                                }
                            }
                            if (closed || !startX.Equals("") || !startY.Equals(""))
                            {
                                hv_Row = double.Parse(startX) * 10 + 280;
                                hv_Column = 280 - double.Parse(startY) * 10;

                                hv_Rows = hv_Rows.TupleConcat(hv_Row);
                                hv_Cols = hv_Cols.TupleConcat(hv_Column);
                            }
                            genRegion.Dispose();
                            HOperatorSet.GenRegionPolygonFilled(out genRegion, hv_Cols, hv_Rows);
                            //HOperatorSet.ConcatObj(allRegion, genRegion, out allRegion);

                            int regionState = 0;//获取状态判断差集和并集
                            node.Read("regionState", out regionState);
                            if (regionState == 0)
                                HOperatorSet.Union2(uniRegion, genRegion, out uniRegion);
                            else
                                HOperatorSet.Union2(difRegion, genRegion, out difRegion);
                        }
                        else if (entityNode.Name.Equals("Circle"))//圆形
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 center;
                            string radius;
                            node.Read("center", out center);
                            node.Read("radius", out radius);
                            genRegion.Dispose();
                            HOperatorSet.GenCircle(out genRegion, (HTuple)280 - center.y * 10, (HTuple)center.x * 10 + 280, (HTuple)double.Parse(radius) * 10);
                            //HOperatorSet.ConcatObj(allRegion, genRegion, out allRegion);

                            int regionState = 0;//获取状态判断差集和并集
                            node.Read("regionState", out regionState);
                            if (regionState == 0)
                                HOperatorSet.Union2(uniRegion, genRegion, out uniRegion);
                            else
                                HOperatorSet.Union2(difRegion, genRegion, out difRegion);

                        }
                        else if (entityNode.Name.Equals("Arc"))//圆弧
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 center;
                            double radius, startAngle, endAngle;
                            node.Read("center", out center);
                            node.Read("radius", out radius);
                            node.Read("startAngle", out startAngle);
                            node.Read("endAngle", out endAngle);
                        }
                    }
                }
            }

            HOperatorSet.Difference(uniRegion, difRegion, out allRegion);
            genRegion.Dispose();
            uniRegion.Dispose();
            difRegion.Dispose();
            return true;
        }
        #endregion
        #region 生成G代码
        /// <summary>
        /// 生成G代码
        /// </summary>
        internal ObservableCollection<string> createG()
        {
            //获取当前所有数据
            Filer.XmlFilerImpl xmlFilerImpl = new Filer.XmlFilerImpl();
            xmlFilerImpl.NewSubNodeAndInsert("Database");
            {
                // block table
                xmlFilerImpl.NewSubNodeAndInsert(_blockTable.className);
                _blockTable.XmlOut(xmlFilerImpl);
                xmlFilerImpl.Pop();

                // layer table
                xmlFilerImpl.NewSubNodeAndInsert(_layerTable.className);
                _layerTable.XmlOut(xmlFilerImpl);
                xmlFilerImpl.Pop();
            }
            xmlFilerImpl.Pop();

            var gCode = new ObservableCollection<string>();
            XmlNode dbNode = xmlFilerImpl.xmldoc.SelectSingleNode("Database");
            if (dbNode == null)
            {
                return gCode;
            }
            XmlNode blockTblNode = dbNode.SelectSingleNode(_blockTable.className);//块表
            if (blockTblNode == null)
            {
                return gCode;
            }
            XmlNodeList blocks = blockTblNode.SelectNodes("Block");
            gCode.Add("O0001");
            gCode.Add("G91G28Z0");
            gCode.Add("G90G40G49G80G98");
            gCode.Add("G21G0G54G17");
            gCode.Add("T1 M6");
            gCode.Add("S30000 M03");
            gCode.Add("M08");
            foreach (XmlNode blockNode in blocks)
            {
                XmlNode entitiesNode = blockNode.SelectSingleNode("entities");
                if (entitiesNode != null && entitiesNode.ChildNodes != null)
                {
                    foreach (XmlNode entityNode in entitiesNode.ChildNodes)
                    {
                        if (entityNode.Name.Equals("Line"))//直线
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 startPoint, endPoint;
                            node.Read("startPoint", out startPoint);
                            node.Read("endPoint", out endPoint);

                            gCode.Add("G00 X" + startPoint.x.ToString() + " Y" + endPoint.y.ToString());
                            gCode.Add("G01 X" + endPoint.x.ToString() + " Y" + endPoint.y.ToString() + " F100");
                        }
                        else if (entityNode.Name.Equals("Polyline"))//折线
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            bool closed;
                            string vertices, startX = "", startY = "";
                            node.Read("closed", out closed);
                            node.Read("vertices", out vertices);
                            string[] sVertices = vertices.Split('|');//ObservableCollection
                            int i = 0;
                            foreach (string sXY in sVertices)
                            {
                                string[] sXYValue = sXY.Split(';');
                                if (sXYValue.Length == 3)
                                {
                                    if (i == 0)
                                    {
                                        i = 1;
                                        startX = sXYValue[0];
                                        startY = sXYValue[1];
                                        gCode.Add("G00 X" + startX + " Y" + startY);
                                    }
                                    else
                                    {
                                        gCode.Add("G01 X" + sXYValue[0] + " Y" + sXYValue[1] + " F100");
                                    }
                                }
                            }
                            if (closed || !startX.Equals("") || !startY.Equals(""))
                            {
                                gCode.Add("G01 X" + startX + " Y" + startY + " F100");
                            }
                        }
                        else if (entityNode.Name.Equals("Circle"))//圆形
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 center;
                            string radius;
                            node.Read("center", out center);
                            node.Read("radius", out radius);
                            gCode.Add("G00 X" + center.x.ToString() + " Y" + center.y.ToString());
                            gCode.Add("G02 X" + center.x.ToString() + " Y" + center.y.ToString() + " R" + radius + " F100");
                        }
                        else if (entityNode.Name.Equals("Arc"))//圆弧
                        {
                            Filer.XmlFilerImpl node = new Filer.XmlFilerImpl(entityNode);
                            MsMath.Vector2 center;
                            double radius, startAngle, endAngle;
                            node.Read("center", out center);
                            node.Read("radius", out radius);
                            node.Read("startAngle", out startAngle);
                            node.Read("endAngle", out endAngle);
                            //算出圆弧的起始点
                            double sX = center.x + radius * Math.Cos(startAngle);
                            double sY = center.y + radius * Math.Sin(startAngle);
                            double eX = center.x + radius * Math.Cos(endAngle);
                            double eY = center.y + radius * Math.Sin(endAngle);
                            //圆弧插补参考：https://www.sohu.com/a/290283091_120087664
                            gCode.Add("G00 X" + center.x.ToString() + " Y" + center.y.ToString());//运动到起点
                            if (startAngle > endAngle)//圆弧插补
                            {
                                gCode.Add("G02 G90 X" + eX.ToString() + " Y" + eY.ToString() + " I" + (center.x - sX).ToString() + " J" + (center.y - sY).ToString() + " R" + radius);
                            }
                            else
                            {
                                gCode.Add("G03 G90 X" + eX.ToString() + " Y" + eY.ToString() + " I" + (center.x - sX).ToString() + " J" + (center.y - sY).ToString() + " R" + radius);
                            }
                        }
                    }
                }
            }

            gCode.Add("G91 G28 Z0");
            gCode.Add("M09");
            gCode.Add("M05");
            gCode.Add("M30");
            return gCode;
        }
        #endregion
        #region 读XML文件
        /// <summary>
        /// 读XML文件
        /// </summary>
        internal bool XmlIn(string xmlFileFullPath)
        {
            Filer.XmlFilerImpl xmlFilerImpl = new Filer.XmlFilerImpl();
            xmlFilerImpl.Load(xmlFileFullPath);

            //
            XmlDocument xmldoc = xmlFilerImpl.xmldoc;
            XmlNode dbNode = xmldoc.SelectSingleNode("Database");
            if (dbNode == null)
            {
                return false;
            }
            xmlFilerImpl.curXmlNode = dbNode;

            //
            ClearLayerTable();
            ClearBlockTable();

            // layer table
            XmlNode layerTblNode = dbNode.SelectSingleNode(_layerTable.GetType().Name);//className
            if (layerTblNode == null)
            {
                return false;
            }
            xmlFilerImpl.curXmlNode = layerTblNode;
            _layerTable.XmlIn(xmlFilerImpl);

            // block table
            XmlNode blockTblNode = dbNode.SelectSingleNode(_blockTable.GetType().Name);//className
            if (blockTblNode == null)
            {
                return false;
            }
            xmlFilerImpl.curXmlNode = blockTblNode;
            _blockTable.XmlIn(xmlFilerImpl);

            //
            _fileName = xmlFileFullPath;
            _idMgr.reset();
            return true;
        }
        #endregion 
        #region 解析DXF文件
        private const string AcDbCircle = "AcDbCircle";
        private const string AcDbLine = "AcDbLine";
        private const string AcDbPolyline = "AcDbPolyline";
        private const string AcDbArc = "AcDbArc";
        private const string XStartMarker = " 10";
        private const string XEndMarker = " 11";
        private const string YStartMarker = " 20";
        private const string YEndMarker = " 21";
        private const string ZStartMarker = " 30";
        private const string ZEndMarker = " 31";
        private const string Radius = " 40";
        private const string StartAngle = " 50";
        private const string EndAngle = " 51";
        private const string End100 = "100";
        private const string End0 = "  0";



        /// <summary>
        /// 使用netDXF解析DXF文件
        /// </summary>
        internal bool DxfIn(string dxfFileFullPath)
        {
            Filer.XmlFilerImpl xmlFilerImpl = new Filer.XmlFilerImpl();
            _idMgr = new ObjectIdMgr(this);
            ObjectId layerId = _idMgr.NextId;


            xmlFilerImpl.NewSubNodeAndInsert("Database");//添加数据头
            {
                // 添加块表数据
                xmlFilerImpl.NewSubNodeAndInsert("BlockTable"); //添加块表头数据
                {
                    xmlFilerImpl.Write("id", 1);//添加块表头id
                    xmlFilerImpl.NewSubNodeAndInsert("Block"); //添加Block
                    {
                        xmlFilerImpl.Write("id", _idMgr.NextId);//添加Block的id
                        xmlFilerImpl.Write("name", "ModelSpace"); //添加Block的name
                        xmlFilerImpl.NewSubNodeAndInsert("entities"); //添加entities
                        {
                            //使用netDxf工具来读取DXF
                            DxfDocument dxfDocument = DxfDocument.Load(dxfFileFullPath);
                            if (dxfDocument != null)
                            {
                                IEnumerable<netDxf.Entities.Circle> circle = (IEnumerable<netDxf.Entities.Circle>)dxfDocument.Circles.GetEnumerator();
                                foreach (netDxf.Entities.Circle item in circle)
                                {
                                    EntityToXml(xmlFilerImpl, item, layerId);
                                }
                                IEnumerable<netDxf.Entities.Arc> arc = (IEnumerable<netDxf.Entities.Arc>)dxfDocument.Arcs.GetEnumerator();
                                foreach (netDxf.Entities.Arc item in arc)
                                {
                                    EntityToXml(xmlFilerImpl, item, layerId);
                                }
                                IEnumerable<netDxf.Entities.Line> lines = (IEnumerable<netDxf.Entities.Line>)dxfDocument.Lines.GetEnumerator();
                                foreach (netDxf.Entities.Line item in lines)
                                {
                                    EntityToXml(xmlFilerImpl, item, layerId);
                                }
                                IEnumerable<netDxf.Entities.Polyline> polyline = (IEnumerable<netDxf.Entities.Polyline>)dxfDocument.Polylines.GetEnumerator();
                                foreach (netDxf.Entities.Polyline item in polyline)
                                {
                                    EntityToXml(xmlFilerImpl, item, layerId);
                                }
                                IEnumerable<netDxf.Entities.LwPolyline> lwPolyline = (IEnumerable<netDxf.Entities.LwPolyline>)dxfDocument.LwPolylines.GetEnumerator();
                                foreach (netDxf.Entities.LwPolyline item in lwPolyline)
                                {
                                    EntityToXml(xmlFilerImpl, item, layerId);
                                }

                                IEnumerable<netDxf.Entities.XLine> xLine = (IEnumerable<netDxf.Entities.XLine>)dxfDocument.XLines.GetEnumerator();
                                foreach (netDxf.Entities.XLine item in xLine)
                                {
                                    EntityToXml(xmlFilerImpl, item, layerId);
                                }
                                IEnumerable<netDxf.Entities.Ray> ray = (IEnumerable<netDxf.Entities.Ray>)dxfDocument.Rays.GetEnumerator();
                                foreach (netDxf.Entities.Ray item in ray)
                                {
                                    EntityToXml(xmlFilerImpl, item, layerId);
                                }
                                IEnumerable<netDxf.Entities.MText> mtext = (IEnumerable<netDxf.Entities.MText>)dxfDocument.MTexts.GetEnumerator();
                                foreach (netDxf.Entities.MText item in mtext)
                                {
                                    EntityToXml(xmlFilerImpl, item, layerId);
                                }
                                IEnumerable<netDxf.Entities.Solid> solid = (IEnumerable<netDxf.Entities.Solid>)dxfDocument.Solids.GetEnumerator();
                                foreach (netDxf.Entities.Solid item in solid)
                                {
                                    EntityToXml(xmlFilerImpl, item, layerId);
                                }

                                IEnumerable<netDxf.Entities.Ellipse> ellipse = (IEnumerable<netDxf.Entities.Ellipse>)dxfDocument.Ellipses.GetEnumerator();
                                foreach (netDxf.Entities.Ellipse item in ellipse)
                                {
                                    EntityToXml(xmlFilerImpl, item, layerId);
                                }
                                //填充图案
                                //IEnumerable<netDxf.Entities.Hatch> hatch = (IEnumerable<netDxf.Entities.Hatch>)dxfDocument.Hatches.GetEnumerator();
                                //foreach (netDxf.Entities.Hatch item in hatch)
                                //{
                                //    EntityToXml(xmlFilerImpl, item, layerId);
                                //}

                                IEnumerable<netDxf.Entities.Dimension> dimension = (IEnumerable<netDxf.Entities.Dimension>)dxfDocument.Dimensions.GetEnumerator();
                                G_DimList = dimension.ToList();
                                foreach (netDxf.Entities.Dimension item in dimension)
                                {
                                    string str2 = item.GetType().FullName;
                                    if (item.Block != null && item.Block.Entities != null)
                                    {
                                        foreach (var obj in item.Block.Entities)
                                        {
                                            EntityToXml(xmlFilerImpl, obj, layerId, obj.Handle, item);
                                        }
                                    }
                                }
                            }

                            #region File.ReadAllLines直接解析DXF
                            //string[] _dxfContent = File.ReadAllLines(dxfFileFullPath);
                            //for (var i = 0; i < _dxfContent.Length; i++)
                            //{
                            //    if (string.Equals(_dxfContent[i], AcDbCircle, StringComparison.CurrentCultureIgnoreCase))
                            //    {
                            //        //圆和圆弧解析
                            //        CircleArc(ref i, _dxfContent, ref xmlFilerImpl, layerId);
                            //    }
                            //    else if (string.Equals(_dxfContent[i], AcDbLine, StringComparison.CurrentCultureIgnoreCase))
                            //    {
                            //        //直线解析
                            //        Line(ref i, _dxfContent, ref xmlFilerImpl, layerId);
                            //    }
                            //    else if (string.Equals(_dxfContent[i], AcDbPolyline, StringComparison.CurrentCultureIgnoreCase))
                            //    {
                            //        //折线解析
                            //        Polyline(ref i, _dxfContent, ref xmlFilerImpl, layerId);
                            //    }
                            //}
                            #endregion
                        }
                        xmlFilerImpl.Pop();//结束entities
                    }
                    xmlFilerImpl.Pop();//结束Block
                }
                xmlFilerImpl.Pop();//结束BlockTable
                                   // 添加图层表数据
                xmlFilerImpl.NewSubNodeAndInsert("LayerTable");
                {
                    xmlFilerImpl.Write("id", 2);//添加
                    xmlFilerImpl.NewSubNodeAndInsert("Layer");
                    {
                        xmlFilerImpl.Write("id", layerId);
                        xmlFilerImpl.Write("name", 0);
                        xmlFilerImpl.Write("color", Colors.Color.FromRGB(255, 255, 255));
                        xmlFilerImpl.Write("lineWeight", LineWeight.ByLineWeightDefault);
                    }
                    xmlFilerImpl.Pop();//结束Layer
                }
                xmlFilerImpl.Pop();//结束LayerTable
            }
            xmlFilerImpl.Pop();//结束Database

            XmlDocument xmldoc = xmlFilerImpl.xmldoc;
            XmlNode dbNode = xmldoc.SelectSingleNode("Database");
            if (dbNode == null)
            {
                return false;
            }
            xmlFilerImpl.curXmlNode = dbNode;

            //
            ClearLayerTable();
            ClearBlockTable();

            // layer table
            XmlNode layerTblNode = dbNode.SelectSingleNode(_layerTable.className);
            if (layerTblNode == null)
            {
                return false;
            }
            xmlFilerImpl.curXmlNode = layerTblNode;
            _layerTable.XmlIn(xmlFilerImpl);//打开文件
            _layerTable.SetCommonHeight();
            // block table
            XmlNode blockTblNode = dbNode.SelectSingleNode(_blockTable.className);
            if (blockTblNode == null)
            {
                return false;
            }
            xmlFilerImpl.curXmlNode = blockTblNode;
            _blockTable.XmlIn(xmlFilerImpl);

            xmlFilerImpl.Save(Path.ChangeExtension(dxfFileFullPath, "mscad"));
            //
            _fileName = dxfFileFullPath;
            _idMgr.reset();
            return true;
        }

        /// <summary>
        /// Circle，Arc，Line，Polyline，Xline，Ray，Text，Solid
        /// </summary>
        internal void EntityToXml(Filer.XmlFilerImpl xmlFilerImpl, EntityObject ent, ObjectId layerId, string EntHandle = null, netDxf.Entities.Dimension dim = null)
        {
            if (ent.GetType() == typeof(netDxf.Entities.Circle))
            {
                netDxf.Entities.Circle item = ent as netDxf.Entities.Circle;
                xmlFilerImpl.NewSubNodeAndInsert("Circle");
                {
                    xmlFilerImpl.Write("center", new MsMath.Vector2(item.Center.X, item.Center.Y));
                    xmlFilerImpl.Write("radius", item.Radius);
                }
            }
            else if (ent.GetType() == typeof(netDxf.Entities.Arc))
            {
                netDxf.Entities.Arc item = ent as netDxf.Entities.Arc;
                xmlFilerImpl.NewSubNodeAndInsert("Arc");
                {
                    xmlFilerImpl.Write("center", new MsMath.Vector2(item.Center.X, item.Center.Y));
                    xmlFilerImpl.Write("radius", item.Radius);
                    xmlFilerImpl.Write("startAngle", item.StartAngle / 180 * Math.PI);
                    xmlFilerImpl.Write("endAngle", item.EndAngle / 180 * Math.PI);
                }
            }
            else if (ent.GetType() == typeof(netDxf.Entities.Line))
            {
                netDxf.Entities.Line item = ent as netDxf.Entities.Line;
                xmlFilerImpl.NewSubNodeAndInsert("Line");
                {
                    xmlFilerImpl.Write("startPoint", new MsMath.Vector2(item.StartPoint.X, item.StartPoint.Y));
                    xmlFilerImpl.Write("endPoint", new MsMath.Vector2(item.EndPoint.X, item.EndPoint.Y));
                }
            }
            else if (ent.GetType() == typeof(netDxf.Entities.Polyline))
            {
                netDxf.Entities.Polyline item = ent as netDxf.Entities.Polyline;
                string vertices = "";
                foreach (PolylineVertex vertex in item.Vertexes)
                {
                    vertices += string.Concat(vertex.Position.X) + ";" + string.Concat(vertex.Position.Y) + ";0|";
                }
                vertices = vertices.Substring(0, vertices.Length - 1);
                xmlFilerImpl.NewSubNodeAndInsert("Polyline");
                {
                    xmlFilerImpl.Write("closed", item.IsClosed);
                    xmlFilerImpl.Write("type", 0);//0--Polyline，1--LwPolyline
                    xmlFilerImpl.Write("vertices", vertices);
                }
            }
            else if (ent.GetType() == typeof(netDxf.Entities.LwPolyline))
            {
                netDxf.Entities.LwPolyline item = ent as netDxf.Entities.LwPolyline;
                string vertices = "";
                foreach (LwPolylineVertex vertex in item.Vertexes)
                {
                    vertices += string.Concat(vertex.Position.X) + ";" + string.Concat(vertex.Position.Y) + ";" + vertex.Bulge + "|";
                }
                vertices = vertices.Substring(0, vertices.Length - 1);
                xmlFilerImpl.NewSubNodeAndInsert("Polyline");
                {
                    xmlFilerImpl.Write("closed", item.IsClosed);
                    xmlFilerImpl.Write("type", 1);//0--Polyline，1--LwPolyline
                    xmlFilerImpl.Write("vertices", vertices);
                }
            }
            else if (ent.GetType() == typeof(netDxf.Entities.XLine))
            {
                netDxf.Entities.XLine item = ent as netDxf.Entities.XLine;
                xmlFilerImpl.NewSubNodeAndInsert("Xline");
                {
                    xmlFilerImpl.Write("basePoint", new MsMath.Vector2(item.Origin.X, item.Origin.Y));
                    xmlFilerImpl.Write("direction", new MsMath.Vector2(item.Direction.X, item.Direction.Y));
                }
            }
            else if (ent.GetType() == typeof(netDxf.Entities.Ray))
            {
                netDxf.Entities.Ray item = ent as netDxf.Entities.Ray;
                xmlFilerImpl.NewSubNodeAndInsert("Ray");
                {
                    xmlFilerImpl.Write("basePoint", new MsMath.Vector2(item.Origin.X, item.Origin.Y));
                    xmlFilerImpl.Write("direction", new MsMath.Vector2(item.Direction.X, item.Direction.Y));
                }
            }
            else if (ent.GetType() == typeof(netDxf.Entities.MText))
            {
                netDxf.Entities.MText item = ent as netDxf.Entities.MText;
                xmlFilerImpl.NewSubNodeAndInsert("Text");
                {
                    xmlFilerImpl.Write("position", new MsMath.Vector2(item.Position.X, item.Position.Y));
                    item.Value = item.Value.Replace(@"\A1;", "");
                    if (dim != null && !string.IsNullOrEmpty(dim.UserText))
                    {
                        string[] rep = dim.UserText.Replace("<>", "#").Split('#');
                        foreach (string str in rep)
                        {
                            if(!string.IsNullOrEmpty(str))
                            {
                                item.Value = item.Value.Replace(str, "");
                            }                            
                        }
                        xmlFilerImpl.Write("text", item.Value);
                        
                    }
                    else
                    {
                        xmlFilerImpl.Write("text", item.Value);
                    }
                    xmlFilerImpl.Write("height", item.Height);
                    _layerTable._TextHeightList.Add(item.Height);
                }
            }
            else if (ent.GetType() == typeof(netDxf.Entities.Solid))
            {
                netDxf.Entities.Solid item = ent as netDxf.Entities.Solid;
                string vertices = "";
                vertices += string.Concat(item.FirstVertex.X) + ";" + string.Concat(item.FirstVertex.Y) + "|";
                vertices += string.Concat(item.SecondVertex.X) + ";" + string.Concat(item.SecondVertex.Y) + "|";
                vertices += string.Concat(item.ThirdVertex.X) + ";" + string.Concat(item.ThirdVertex.Y) + "|";
                vertices += string.Concat(item.FourthVertex.X) + ";" + string.Concat(item.FourthVertex.Y) + "|";

                vertices = vertices.Substring(0, vertices.Length - 1);
                xmlFilerImpl.NewSubNodeAndInsert("Solid");
                {
                    xmlFilerImpl.Write("vertices", vertices);
                }
            }
            else if (ent.GetType() == typeof(netDxf.Entities.Ellipse))
            {
                netDxf.Entities.Ellipse item = ent as netDxf.Entities.Ellipse;
                xmlFilerImpl.NewSubNodeAndInsert("Ellipse");
                {
                    List<Vector2> temp = item.PolygonalVertexes(4);
                    xmlFilerImpl.Write("center", new MsMath.Vector2(item.Center.X, item.Center.Y));
                    xmlFilerImpl.Write("MajorAxis", item.MajorAxis);
                    xmlFilerImpl.Write("MinorAxis", item.MinorAxis);
                    xmlFilerImpl.Write("Rotation", item.Rotation);
                    xmlFilerImpl.Write("entHandle", EntHandle);
                }
            }
            else
            {
                return;
            }
            //通用节点
            xmlFilerImpl.Write("id", _idMgr.NextId);
            if (ent.Color.IsByBlock && dim != null)
            {
                netDxf.Tables.Layer layer = dim.Layer;
                if (dim.Color.IsByLayer && layer != null)
                {
                    xmlFilerImpl.Write("color", "ByLayer:" + layer.Color.R.ToString() + "," + layer.Color.G.ToString() + "," + layer.Color.B.ToString());
                }
                else
                {
                    xmlFilerImpl.Write("color", "ByLayer:" + dim.Color.R.ToString() + "," + dim.Color.G.ToString() + "," + dim.Color.B.ToString());
                }
            }
            else
            {
                netDxf.Tables.Layer layer = ent.Layer;
                if (ent.Color.IsByLayer && layer != null)
                {
                    xmlFilerImpl.Write("color", "ByLayer:" + layer.Color.R.ToString() + "," + layer.Color.G.ToString() + "," + layer.Color.B.ToString());
                }
                else
                {
                    xmlFilerImpl.Write("color", "ByLayer:" + ent.Color.R.ToString() + "," + ent.Color.G.ToString() + "," + ent.Color.B.ToString());
                }
            }
            
            xmlFilerImpl.Write("lineWeight", Enum.GetName(typeof(LineWeight), (int)ent.Lineweight));
            xmlFilerImpl.Write("layer", layerId);
            xmlFilerImpl.Write("entHandle", EntHandle);
            if (dim != null)
            {
                xmlFilerImpl.Write("dimHandle", dim.Handle);
            }
            xmlFilerImpl.Pop();//结束
        }


        #region 直接读取DXF文件，判断相应字符串解析
        /// <summary>
        /// 圆形和圆弧的解析
        /// </summary>
        /// <param name="i">下标</param>
        /// <param name="_dxfContent">数据</param>
        /// <param name="xmlFilerImpl">XML数据</param>
        /// <param name="layerId">层ID</param>
        /// <returns></returns>
        internal bool CircleArc(ref int i, string[] _dxfContent, ref Filer.XmlFilerImpl xmlFilerImpl, ObjectId layerId)
        {
            try
            {
                double xStart = 0,
                    xEnd = 0,
                    yStart = 0,
                    yEnd = 0,
                    zStart = 0,
                    zEnd = 0,
                    radius = 0,
                    startAngle = 0,
                    endAngle = 0;
                string name = "";
                name = "Circle";
            gotoStart:
                i = i + 1;
                string isCs = _dxfContent[i];
                if (_dxfContent[i] == XStartMarker)
                {
                    xStart = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == XEndMarker)
                {
                    xEnd = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == YStartMarker)
                {
                    yStart = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == YEndMarker)
                {
                    yEnd = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == ZStartMarker)
                {
                    zStart = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == ZEndMarker)
                {
                    zEnd = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == Radius)
                {
                    radius = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == StartAngle)
                {
                    startAngle = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == EndAngle)
                {
                    endAngle = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                if (_dxfContent[i] == End100 || _dxfContent[i + 1].Equals(AcDbArc))
                {
                    name = "Arc";
                    i = i + 1;
                    goto gotoStart;
                }
                else if (!_dxfContent[i].Equals(End0))
                {
                    string isCs2 = _dxfContent[i];
                    i = i + 1;
                    goto gotoStart;
                }
                xmlFilerImpl.NewSubNodeAndInsert(name);
                {
                    xmlFilerImpl.Write("id", _idMgr.NextId);
                    xmlFilerImpl.Write("color", "ByLayer:255,255,255");
                    xmlFilerImpl.Write("lineWeight", LineWeight.ByLayer);
                    xmlFilerImpl.Write("layer", layerId);
                    xmlFilerImpl.Write("center", new MsMath.Vector2(xStart, yStart));
                    xmlFilerImpl.Write("radius", radius);
                    if (name == "Arc")
                    {
                        //度转弧度
                        startAngle = startAngle / 180 * Math.PI;
                        endAngle = endAngle / 180 * Math.PI;
                        xmlFilerImpl.Write("startAngle", startAngle);
                        xmlFilerImpl.Write("endAngle", endAngle);
                    }
                }
                xmlFilerImpl.Pop();//结束
            }
            catch (Exception ex)
            {

                return false;
            }
            return true;
        }
        /// <summary>
        /// 直线的解析
        /// </summary>
        /// <param name="i">下标</param>
        /// <param name="_dxfContent">数据</param>
        /// <param name="xmlFilerImpl">XML数据</param>
        /// <param name="layerId">层ID</param>
        /// <returns></returns>
        internal bool Line(ref int i, string[] _dxfContent, ref Filer.XmlFilerImpl xmlFilerImpl, ObjectId layerId)
        {
            try
            {
                //直线解析
                double xStart = 0,
                    xEnd = 0,
                    yStart = 0,
                    yEnd = 0,
                    zStart = 0,
                    zEnd = 0;
            gotoStart:
                i = i + 1;
                string isCs = _dxfContent[i];
                if (_dxfContent[i] == XStartMarker)
                {
                    xStart = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == XEndMarker)
                {
                    xEnd = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == YStartMarker)
                {
                    yStart = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == YEndMarker)
                {
                    yEnd = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == ZStartMarker)
                {
                    zStart = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                else if (_dxfContent[i] == ZEndMarker)
                {
                    zEnd = Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1);
                }
                if (!_dxfContent[i].Equals(End0))
                {
                    string isCs2 = _dxfContent[i];
                    i = i + 1;
                    goto gotoStart;
                }
                xmlFilerImpl.NewSubNodeAndInsert("Line");
                {
                    xmlFilerImpl.Write("id", _idMgr.NextId);
                    xmlFilerImpl.Write("color", "ByLayer:255,255,255");
                    xmlFilerImpl.Write("lineWeight", LineWeight.ByLayer);
                    xmlFilerImpl.Write("layer", layerId);
                    xmlFilerImpl.Write("startPoint", new MsMath.Vector2(xStart, yStart));
                    xmlFilerImpl.Write("endPoint", new MsMath.Vector2(xEnd, yEnd));
                }
                xmlFilerImpl.Pop();//结束Line
            }
            catch (Exception ex)
            {
                return false;
            }
            return true;
        }
        /// <summary>
        /// 折线的解析
        /// </summary>
        /// <param name="i">下标</param>
        /// <param name="_dxfContent">数据</param>
        /// <param name="xmlFilerImpl">XML数据</param>
        /// <param name="layerId">层ID</param>
        /// <returns></returns>
        internal bool Polyline(ref int i, string[] _dxfContent, ref Filer.XmlFilerImpl xmlFilerImpl, ObjectId layerId)
        {
            try
            {
                string vertices = "";
                bool closed = false;
            gotoStart:
                i = i + 1;
                string isCs = _dxfContent[i];
                if (_dxfContent[i] == XStartMarker || _dxfContent[i + 2] == YStartMarker)
                {
                    vertices += Math.Round(Convert.ToDouble(_dxfContent[i + 1]), 1).ToString() + ";" +
                        Math.Round(Convert.ToDouble(_dxfContent[i + 3]), 1).ToString() + "|";
                    i = i + 3;
                }
                if (_dxfContent[i].Equals(" 70"))
                {
                    if (_dxfContent[i].Equals("     0"))
                    {
                        closed = false;
                    }
                    else if (_dxfContent[i].Equals("     1"))
                    {
                        closed = true;
                    }
                    i = i + 1;
                    goto gotoStart;
                }
                else if (_dxfContent[i].Equals(" 90") && _dxfContent[i].Equals(" 43"))
                {
                    i = i + 1;
                    goto gotoStart;
                }
                else if (!_dxfContent[i].Equals(End0))
                {
                    goto gotoStart;
                }
                vertices = vertices.Substring(0, vertices.Length - 1);

                xmlFilerImpl.NewSubNodeAndInsert("Polyline");
                {
                    xmlFilerImpl.Write("id", _idMgr.NextId);
                    xmlFilerImpl.Write("color", "ByLayer:255,0,255");
                    xmlFilerImpl.Write("lineWeight", LineWeight.ByLayer);
                    xmlFilerImpl.Write("layer", layerId);
                    xmlFilerImpl.Write("closed", closed);
                    xmlFilerImpl.Write("vertices", vertices);
                }
                xmlFilerImpl.Pop();//结束
            }
            catch (Exception ex)
            {
                return false;
            }
            return true;
        }
        #endregion
        #endregion
        #region 解析Gerber的art文件
        /// <summary>
        /// 解析Gerber的art文件
        /// </summary>
        /// <returns></returns>
        internal bool GerberIn(string dxfFileFullPath)
        {
            Filer.XmlFilerImpl xmlFilerImpl = new Filer.XmlFilerImpl();
            _idMgr = new ObjectIdMgr(this);
            ObjectId layerId = _idMgr.NextId;

            xmlFilerImpl.NewSubNodeAndInsert("Database");//添加数据头
            {
                // 添加块表数据
                xmlFilerImpl.NewSubNodeAndInsert("BlockTable"); //添加块表头数据
                {
                    xmlFilerImpl.Write("id", 1);//添加块表头id
                    xmlFilerImpl.NewSubNodeAndInsert("Block"); //添加Block
                    {
                        xmlFilerImpl.Write("id", _idMgr.NextId);//添加Block的id
                        xmlFilerImpl.Write("name", "ModelSpace"); //添加Block的name
                        xmlFilerImpl.NewSubNodeAndInsert("entities"); //添加entities
                        {
                            //添加图元
                            try
                            {
                                gerberLib.OpenLayerFromFilename(project, dxfFileFullPath);
                                GerberImage gerberImage = project.FileInfo[0].Image;
                                Collection<GerberNet> gerberNetList = project.FileInfo[0].Image.GerberNetList;
                                float dx, dy;
                                float startX, startY, stopX, stopY;
                                int repeatX = 1, repeatY = 1;
                                float repeatDistanceX = 0.0f, repeatDistanceY = 0.0f;
                                PointF startPoint, endPoint;
                                int netListIndex = 0;
                                GerberNet currentNet = null;
                                for (netListIndex = 0; netListIndex < gerberNetList.Count; GetNextRenderObject(gerberNetList, ref netListIndex))
                                {
                                    currentNet = gerberNetList[netListIndex];
                                    for (int rx = 0; rx < repeatX; rx++)
                                    {
                                        for (int ry = 0; ry < repeatY; ry++)
                                        {
                                            float stepAndRepeatX = rx * repeatDistanceX;
                                            float stepAndRepeatY = ry * repeatDistanceY;
                                            startX = (float)currentNet.StartX + stepAndRepeatX;
                                            startY = (float)currentNet.StartY + stepAndRepeatY;
                                            stopX = (float)currentNet.StopX + stepAndRepeatX;
                                            stopY = (float)currentNet.StopY + stepAndRepeatY;
                                            switch (currentNet.ApertureState)
                                            {
                                                case GerberApertureState.On:
                                                    switch (currentNet.Interpolation)
                                                    {
                                                        case GerberInterpolation.LinearX10:
                                                        case GerberInterpolation.LinearX01:
                                                        case GerberInterpolation.LinearX001:
                                                        case GerberInterpolation.LinearX1:
                                                            switch (gerberImage.ApertureArray[currentNet.Aperture].ApertureType)
                                                            {
                                                                case GerberApertureType.Circle:
                                                                    xmlFilerImpl.NewSubNodeAndInsert("Line");
                                                                    {
                                                                        xmlFilerImpl.Write("id", _idMgr.NextId);
                                                                        xmlFilerImpl.Write("color", "ByLayer:255,255,255");
                                                                        xmlFilerImpl.Write("lineWeight", LineWeight.ByLineWeightDefault);
                                                                        xmlFilerImpl.Write("layer", layerId);
                                                                        xmlFilerImpl.Write("startPoint", new MsMath.Vector2(startX, startY));
                                                                        xmlFilerImpl.Write("endPoint", new MsMath.Vector2(stopX, stopY));
                                                                    }
                                                                    xmlFilerImpl.Pop();//结束Line
                                                                    break;
                                                                case GerberApertureType.Rectangle:
                                                                    dx = (float)(gerberImage.ApertureArray[currentNet.Aperture].Parameters[0] / 2);
                                                                    dy = (float)(gerberImage.ApertureArray[currentNet.Aperture].Parameters[1] / 2);
                                                                    if (startX > stopX)
                                                                        dx = -dx;

                                                                    if (startY > stopY)
                                                                        dy = -dy;

                                                                    using (GraphicsPath path = new GraphicsPath())
                                                                    {
                                                                        path.AddLine(startX - dx, startY - dy, startX - dx, startY + dy);
                                                                        path.AddLine(startX - dx, startY + dy, stopX - dx, stopY + dy);
                                                                        path.AddLine(stopX - dx, stopY + dy, stopX + dx, stopY + dy);
                                                                        path.AddLine(stopX + dx, stopY + dy, stopX + dx, stopY - dy);
                                                                        path.AddLine(stopX + dx, stopY - dy, startX + dx, startY - dy);

                                                                    }
                                                                    break;
                                                                // For now, just render ovals or polygons like a circle.
                                                                case GerberApertureType.Oval:
                                                                case GerberApertureType.Polygon:
                                                                    startPoint = new PointF(startX, startY);
                                                                    endPoint = new PointF(stopX, stopY);

                                                                    break;
                                                                // Macros can only be flashed, so ignore any that might be here.
                                                                default:
                                                                    break;
                                                            }
                                                            break;
                                                        case GerberInterpolation.ClockwiseCircular:
                                                        case GerberInterpolation.CounterClockwiseCircular:
                                                            float centerX = (float)currentNet.CircleSegment.CenterX;
                                                            float centerY = (float)currentNet.CircleSegment.CenterY;
                                                            float width = (float)currentNet.CircleSegment.Width;
                                                            float height = (float)currentNet.CircleSegment.Height;
                                                            float startAngle = (float)currentNet.CircleSegment.StartAngle;
                                                            float endAngle = (float)currentNet.CircleSegment.EndAngle;

                                                            RectangleF arcRectangle = new RectangleF(centerX - (width / 2), centerY - (height / 2), width, height);
                                                            if (arcRectangle != RectangleF.Empty)
                                                            {
                                                                xmlFilerImpl.NewSubNodeAndInsert("Arc");
                                                                {
                                                                    xmlFilerImpl.Write("id", _idMgr.NextId);
                                                                    xmlFilerImpl.Write("color", "ByLayer:255,255,255");
                                                                    xmlFilerImpl.Write("lineWeight", LineWeight.ByLineWeightDefault);
                                                                    xmlFilerImpl.Write("layer", layerId);
                                                                    xmlFilerImpl.Write("center", new MsMath.Vector2(centerX, centerY));
                                                                    xmlFilerImpl.Write("radius", width / 2);

                                                                    xmlFilerImpl.Write("startAngle", startAngle / 180 * Math.PI);
                                                                    xmlFilerImpl.Write("endAngle", endAngle / 180 * Math.PI);
                                                                }
                                                                xmlFilerImpl.Pop();//结束 
                                                            }
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                    break;
                                                case GerberApertureState.Flash:
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                            catch (Exception ex)
                            {
                                string errorMessage = ex.Message;
                                if (ex.InnerException != null)
                                    errorMessage += ex.InnerException.Message;
                                return false;
                            }

                            //////////////////////////////////////////////////////////////
                        }
                        xmlFilerImpl.Pop();//结束entities
                    }
                    xmlFilerImpl.Pop();//结束Block
                }
                xmlFilerImpl.Pop();//结束BlockTable
                                   // 添加图层表数据
                xmlFilerImpl.NewSubNodeAndInsert("LayerTable");
                {
                    xmlFilerImpl.Write("id", 2);//添加
                    xmlFilerImpl.NewSubNodeAndInsert("Layer");
                    {
                        xmlFilerImpl.Write("id", layerId);
                        xmlFilerImpl.Write("name", 0);
                        xmlFilerImpl.Write("color", Colors.Color.FromRGB(255, 255, 255));
                        xmlFilerImpl.Write("lineWeight", LineWeight.ByLineWeightDefault);
                    }
                    xmlFilerImpl.Pop();//结束Layer
                }
                xmlFilerImpl.Pop();//结束LayerTable
            }
            xmlFilerImpl.Pop();//结束Database

            XmlDocument xmldoc = xmlFilerImpl.xmldoc;
            XmlNode dbNode = xmldoc.SelectSingleNode("Database");
            if (dbNode == null)
            {
                return false;
            }
            xmlFilerImpl.curXmlNode = dbNode;

            //
            ClearLayerTable();
            ClearBlockTable();

            // layer table
            XmlNode layerTblNode = dbNode.SelectSingleNode(_layerTable.className);
            if (layerTblNode == null)
            {
                return false;
            }
            xmlFilerImpl.curXmlNode = layerTblNode;
            _layerTable.XmlIn(xmlFilerImpl);

            // block table
            XmlNode blockTblNode = dbNode.SelectSingleNode(_blockTable.className);
            if (blockTblNode == null)
            {
                return false;
            }
            xmlFilerImpl.curXmlNode = blockTblNode;
            _blockTable.XmlIn(xmlFilerImpl);

            //
            _fileName = dxfFileFullPath;
            _idMgr.reset();
            return true;
        }
        // Finds the next renderable object in the net list.
        private static void GetNextRenderObject(Collection<GerberNet> gerberNetList, ref int currentIndex)
        {
            GerberNet currentNet = gerberNetList[currentIndex];
            if (currentNet.Interpolation == GerberInterpolation.PolygonAreaStart)
            {
                // If it's a polygon, step to the next non-polygon net.
                for (; currentIndex < gerberNetList.Count; currentIndex++)
                {
                    currentNet = gerberNetList[currentIndex];
                    if (currentNet.Interpolation == GerberInterpolation.PolygonAreaEnd)
                        break;
                }
                currentIndex++;
                return;
            }
            currentIndex++;
        }
        #endregion
        /// <summary>
        /// 清空图层表
        /// </summary>
        private void ClearLayerTable()
        {
            List<Layer> allLayers = new List<Layer>();
            foreach (Layer layer in _layerTable)
            {
                allLayers.Add(layer);
            }
            _layerTable.Clear();

            foreach (Layer layer in allLayers)
            {
                layer.Erase();
            }
        }

        /// <summary>
        /// 清空块表
        /// </summary>
        private void ClearBlockTable()
        {
            Dictionary<Entity, Entity> allEnts = new Dictionary<Entity, Entity>();
            List<Block> allBlocks = new List<Block>();

            foreach (Block block in _blockTable)
            {
                foreach (Entity entity in block)
                {
                    allEnts[entity] = entity;
                }
                block.Clear();
                allBlocks.Add(block);
            }
            _blockTable.Clear();

            foreach (KeyValuePair<Entity, Entity> kvp in allEnts)
            {
                kvp.Key.Erase();
            }

            foreach (Block block in allBlocks)
            {
                block.Erase();
            }
        }

        #region IdentityObject
        private void IdentifyDBTable(DBTable table)
        {
            MapSingleObject(table);
        }

        internal void IdentifyObject(DBObject obj)
        {
            IdentifyObjectSingle(obj);
            if (obj is Block)
            {
                Block block = obj as Block;
                foreach (Entity entity in block)
                {
                    IdentifyObjectSingle(entity);
                }
            }
        }

        private void IdentifyObjectSingle(DBObject obj)
        {
            if (obj.id.isNull)
            {
                obj.SetId(_idMgr.NextId);
            }
            MapSingleObject(obj);
        }
        #endregion

        #region MapObject
        private void MapObject(DBObject obj)
        {
            MapSingleObject(obj);
            if (obj is Block)
            {
                Block block = obj as Block;
                foreach (Entity entity in block)
                {
                    MapSingleObject(entity);
                }
            }
        }

        internal void UnmapObject(DBObject obj)
        {
            UnmapSingleObject(obj);
            if (obj is Block)
            {
                Block block = obj as Block;
                foreach (Entity entity in block)
                {
                    UnmapSingleObject(entity);
                }
            }
        }

        private void MapSingleObject(DBObject obj)
        {
            _dictId2Object[obj.id] = obj;
        }

        private void UnmapSingleObject(DBObject obj)
        {
            _dictId2Object.Remove(obj.id);
        }
        #endregion
        private MsMath.Vector2 _screenPanHawkEye = new MsMath.Vector2();//记录鹰眼对应当前视野的坐标
        private double _zoomCanvasHawkEye = -1;//记录当前鹰眼的缩放系数
        public MsMath.Vector2 screenPanHawkEye
        {
            get { return _screenPanHawkEye; }
            set { _screenPanHawkEye = value; }
        }
        public double zoomCanvasHawkEye
        {
            get { return _zoomCanvasHawkEye; }
            set { _zoomCanvasHawkEye = value; }
        }


        /// <summary>
        /// 测距标注，标注ID,起点，终点
        /// </summary>
        public void CreateLinearDimension(string DimID, MsMath.Vector2 start, MsMath.Vector2 end)
        {
            Vector2 firstPoint = new Vector2(start.x, start.y);
            Vector2 secondPoint = new Vector2(end.x, end.y);
            double offset = 2;
            double rotation = 0;
            LinearDimension dim0 = new LinearDimension(firstPoint, secondPoint, offset, rotation);
            dim0.UserText = DimID;
            dim0.Block = new netDxf.Blocks.Block(DimID);
            G_DimList.Add(dim0);
        }

        /// <summary>
        /// 半径标注，标注ID,圆心，圆边点
        /// </summary>
        public void CreateRadialDimensionn(string DimID, MsMath.Vector2 center, MsMath.Vector2 reference)
        {
            Vector2 centerPoint = new Vector2(center.x, center.y);
            Vector2 referencePoint = new Vector2(reference.x, reference.y);

            RadialDimension dim1 = new RadialDimension(centerPoint, referencePoint);
            dim1.UserText = DimID;
            dim1.Block = new netDxf.Blocks.Block(DimID);
            G_DimList.Add(dim1);

        }

        /// <summary>
        /// 圆弧角度，标注ID,圆心，圆边点，标注圆弧的半径
        /// </summary>
        public void CreateAngular3PointDimension(string DimID, MsMath.Vector2 center,MsMath.Vector2 start, MsMath.Vector2 end, double offset)
        {
            Vector2 centerPoint = new Vector2(center.x, center.y);
            Vector2 startPoint = new Vector2(start.x, start.y);
            Vector2 endPoint = new Vector2(end.x, end.y);

            Angular3PointDimension dim1 = new Angular3PointDimension(centerPoint, startPoint, endPoint, offset);

            dim1.UserText = DimID;
            dim1.Block = new netDxf.Blocks.Block(DimID);
            G_DimList.Add(dim1);

        }

        /// <summary>
        /// 直线夹角，标注ID,圆心，圆边点，标注圆弧的半径
        /// </summary>
        public void CreateAngular2LineDimension(string DimID, Line First, Line Second, double offset)
        {
            Vector2 startFirstLine = new Vector2(First.startPoint.x, First.startPoint.y);
            Vector2 endFirstLine = new Vector2(First.endPoint.x, First.endPoint.y);
            Vector2 startSecondLine = new Vector2(Second.startPoint.x, Second.startPoint.y);
            Vector2 endSecondLine = new Vector2(Second.endPoint.x, Second.endPoint.y);


            Angular2LineDimension dim1 = new Angular2LineDimension(startFirstLine, endFirstLine, startSecondLine, endSecondLine, offset);
            dim1.UserText = DimID;
            dim1.Block = new netDxf.Blocks.Block(DimID);
            G_DimList.Add(dim1);

        }
    }
}
