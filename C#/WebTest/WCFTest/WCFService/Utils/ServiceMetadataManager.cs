using ConsoleApp3.WCFService.Attribute;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.ServiceModel.Web;
using System.Text;

namespace ConsoleApp3.WCFService.Utils
{
    public static class ServiceMetadataManager
    {
        #region 特性操作(在启动服务时，直接将接口的特性信息存储起来，减少真正使用特性时的资源消耗)

        private static Dictionary<string, ServiceMetadataAttribute> ServiceMetadataDictionary = null;

        private static Dictionary<string, string> UriTemplateMappingDictionary = null;

        /// <summary>
        /// 加载服务元信息特性
        /// </summary>
        /// <param name="serviceType"></param>
        /// <returns></returns>
        public static void LoadServiceMetadata(Type serviceType)
        {
            if (serviceType == null)
            {
                ServiceMetadataDictionary = null;
                UriTemplateMappingDictionary = null;
                return;
            }

            ServiceMetadataDictionary = new Dictionary<string, ServiceMetadataAttribute>();
            UriTemplateMappingDictionary = new Dictionary<string, string>();

            foreach (MethodInfo method in serviceType.GetMethods())
            {
                object[] webInvokeAttrs = method.GetCustomAttributes(typeof(WebInvokeAttribute), true);
                if (webInvokeAttrs == null || webInvokeAttrs.Length == 0)
                {
                    continue;
                }

                WebInvokeAttribute webInvoke = webInvokeAttrs[0] as WebInvokeAttribute;
                if (webInvoke == null)
                {
                    continue;
                }

                UriTemplateMappingDictionary.Add(webInvoke.UriTemplate, method.Name);

                object[] serviceMetadataAttrs = method.GetCustomAttributes(typeof(ServiceMetadataAttribute), true);
                if (serviceMetadataAttrs == null || serviceMetadataAttrs.Length == 0)
                {
                    continue;
                }

                ServiceMetadataAttribute serviceMetadata = serviceMetadataAttrs[0] as ServiceMetadataAttribute;
                if (serviceMetadata == null)
                {
                    continue;
                }

                ServiceMetadataDictionary.Add(method.Name, serviceMetadata);
            }
        }

        /// <summary>
        /// 获取自定义WebInvoke特性
        /// </summary>
        /// <param name="invoke">接口</param>
        /// <returns></returns>
        public static ServiceMetadataAttribute GetCustomWebInvoke(string invoke)
        {
            // 判断urlTemplate
            if (UriTemplateMappingDictionary == null || UriTemplateMappingDictionary.Count == 0)
            {
                return null;
            }

            // 判断serviceMetadata
            if (ServiceMetadataDictionary == null || ServiceMetadataDictionary.Count == 0)
            {
                return null;
            }

            // 如果两个都不存在invoke
            if (UriTemplateMappingDictionary.ContainsKey(invoke) == false && ServiceMetadataDictionary.ContainsKey(invoke) == false)
            {
                return null;
            }

            if (ServiceMetadataDictionary.ContainsKey(invoke) == true)
            {
                return ServiceMetadataDictionary[invoke];
            }

            if (UriTemplateMappingDictionary.ContainsKey(invoke) == true)
            {
                string method = UriTemplateMappingDictionary[invoke];

                if (ServiceMetadataDictionary.ContainsKey(method) == false)
                {
                    return null;
                }

                return ServiceMetadataDictionary[method];
            }

            return null;
        }

        #endregion
    }
}
