using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel.Channels;
using System.ServiceModel;
using System.Text;

namespace ConsoleApp3.WCFService.Utils
{
    /// <summary>
    /// 员工助理 AppToken 操作类
    /// </summary>
    public static class TokenOperate
    {
        public const string Name = "token";

        private readonly static Dictionary<string, object> tokenDic = new Dictionary<string, object>();

        /// <summary>
        /// 验证Token
        /// </summary>
        /// <param name="token"></param>
        /// <returns></returns>
        public static bool ValidateToken<T>(string token)
        {
            if (string.IsNullOrEmpty(token) == true)
            {
                return false;
            }

            T tokenInfo = getTokenInfo<T>(token);
            if (tokenInfo == null)
            {
                return false;
            }

            return true;
        }

        /// <summary>
        /// 生成Token
        /// </summary>
        /// <returns></returns>
        public static string generateToken()
        {
            return "token_valid";
        }

        /// <summary>
        /// 获取token 对应的对象
        /// </summary>
        /// <param name="token"></param>
        /// <returns></returns>
        public static T getTokenInfo<T>(string token)
        {
            if (tokenDic.ContainsKey(token) == false)
            {
                return default(T);
            }

            return tokenDic[token] is T ? (T)tokenDic[token] : default(T);
        }

        /// <summary>
        /// 添加Token
        /// </summary>
        /// <param name="token"></param>
        /// <param name="tokenInfo"></param>
        public static void addTokenInfo<T>(string token, T tokenInfo)
        {
            if (tokenDic.ContainsKey(token))
            {
                tokenDic[token] = tokenInfo;
            }
            else
            {
                tokenDic.Add(token, tokenInfo);
            }
        }

        /// <summary>
        /// 移除token
        /// </summary>
        /// <param name="token">需要移除的token</param>
        public static void removeToken(string token)
        {
            if (string.IsNullOrEmpty(token) == true)
            {
                return;
            }

            if (tokenDic.ContainsKey(token) == false)
            {
                return;
            }

            tokenDic.Remove(token);
        }

        public static string getCurrentRequestToken()
        {
            var httpRequest = OperationContext.Current.IncomingMessageProperties[HttpRequestMessageProperty.Name] as HttpRequestMessageProperty;
            if (httpRequest == null)
            {
                return null;
            }

            return httpRequest.Headers[TokenOperate.Name];
        }
    }
}
