using ConsoleApp3.WCFService.Attribute;
using ConsoleApp3.WCFService.Utils;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace ConsoleApp3.WCFService
{
    /// <summary>
    /// 定义服务契约
    /// </summary>
    [ServiceContract]
    public interface IStaticFileService
    {
        /// <summary>
        /// 获取餐桌信息接口
        /// </summary>
        /// <param name="baseParam"></param>
        /// <returns></returns>
        [OperationContract]
        [WebInvoke(
            Method = HttpMethods.Get,
            UriTemplate = "*",
            BodyStyle = WebMessageBodyStyle.Bare,
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json)]
        [ServiceMetadata("获取静态文件", false, false)]
        Stream GetFile();
    }
}
