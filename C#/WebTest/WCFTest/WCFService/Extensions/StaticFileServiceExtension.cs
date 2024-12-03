using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Description;
using System.Text;

namespace ConsoleApp3.WCFService.Extensions
{
    public static class StaticFileServiceExtension
    {
        //public static ServiceHost AddCrossOriginBehavior(this ServiceHost host) 
        //{
        //    if (host == null)
        //    {
        //        throw new InvalidOperationException(nameof(host));
        //    }

        //    //拦截器
        //    CustomServiceBehavior crossOriginBehavior = new CustomServiceBehavior();
        //    host.Description.Behaviors.Add(crossOriginBehavior);

        //    return host;
        //}

        //public static ServiceHost AddTokenValidationBehavior(this ServiceHost host) 
        //{
        //    return host;
        //}

        public static ServiceHost AddStaticFileServece(this ServiceHost host) 
        {
            if (host == null) 
            {
                throw new InvalidOperationException(nameof(host));
            }

            WebHttpBinding staticFileBinding = new WebHttpBinding
            {
                MaxBufferSize = int.MaxValue,
                MaxReceivedMessageSize = int.MaxValue,
                TransferMode = TransferMode.Streamed
            };

            // 添加文件服务终结点
            ServiceEndpoint fileEndpoint = host.AddServiceEndpoint(typeof(IStaticFileService), staticFileBinding, "");
            fileEndpoint.Behaviors.Add(new WebHttpBehavior());

            return AddStaticFileService(host, "");
        }

        public static ServiceHost AddStaticFileService(this ServiceHost host, string requestBasePath) 
        {
            

            return AddStaticFileServece(host, requestBasePath, "wwwroot");
        }

        public static ServiceHost AddStaticFileServece(this ServiceHost host, string requestBasePath, string filePath) 
        {
            WebHttpBinding staticFileBinding = new WebHttpBinding
            {
                MaxBufferSize = int.MaxValue,
                MaxReceivedMessageSize = int.MaxValue,
                TransferMode = TransferMode.Streamed
            };

            return AddStaticFileServece(host, requestBasePath, filePath, staticFileBinding);
        }

        public static ServiceHost AddStaticFileServece(this ServiceHost host, string requestBasePath, string filePath, WebHttpBinding staticFileBinding)
        {
            if (host == null)
            {
                throw new InvalidOperationException(nameof(host));
            }

            // 添加文件服务终结点
            ServiceEndpoint fileEndpoint = host.AddServiceEndpoint(typeof(IStaticFileService), staticFileBinding, requestBasePath);
            fileEndpoint.Behaviors.Add(new WebHttpBehavior());

            return host;
        }
    }
}
