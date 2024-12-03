using System.ServiceModel.Dispatcher;
using System.ServiceModel.Channels;
using System.ServiceModel;
using System.Reflection;
using System.Linq;
using System;
using ConsoleApp3.WCFService.Utils;
using ConsoleApp3.WCFService.Exception;
using ConsoleApp3.WCFService.Attribute;

namespace ConsoleApp3.WCFService.CustomMessageInspector
{
    public class TokenValidation : IDispatchMessageInspector
    {
        public object AfterReceiveRequest(ref Message request, IClientChannel channel, InstanceContext instanceContext)
        {
            // 获取请求对象
            var httpRequest = request.Properties[HttpRequestMessageProperty.Name] as HttpRequestMessageProperty;
            // 如果请求对象为空，或者是预检请求，则不需要验证 Token
            if (httpRequest == null || httpRequest.Method == HttpMethods.Option)
            {
                return null;
            }

            // 获取token信息
            var token = httpRequest.Headers[TokenOperate.Name];

            // 判断接口是否需要验证 token
            if (IsValidationRequired(request) == false)
            {
                return null;
            }

            // 判断 token 验证是否通过
            if (TokenOperate.ValidateToken<object>(token) == false)
            {
                // wpf 拦截器的机制导致的只能通过在接口处理之前抛出异常的方式进行截断后续的方法执行，否则即便token判断无效还是会执行实际接口方法
                throw new TokenValidationException("Token 验证失败");
            }

            return null;
        }

        public void BeforeSendReply(ref Message reply, object correlationState)
        {

        }

        private bool IsValidationRequired(Message request)
        {
            var operationContext = OperationContext.Current;
            var requestUri = operationContext.IncomingMessageHeaders.To;

            if (requestUri == null)
            {
                return true; // 如果没有操作信息，默认进行 Token 验证
            }

            string invokePath = requestUri.AbsolutePath.Replace("/TzxRestFulServer/", "");

            if (string.IsNullOrEmpty(invokePath) == true)
            {
                return true;
            }

            ServiceMetadataAttribute serviceMetadata = ServiceMetadataManager.GetCustomWebInvoke(invokePath);
            if (serviceMetadata == null)
            {
                return true;
            }

            return serviceMetadata.CheckToken;
        }
    }
}
