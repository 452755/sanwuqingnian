using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ConsoleApp3.WCFService.Attribute
{
    /// <summary>
    /// 自定义接口元信息特性
    /// </summary>
    [AttributeUsage(AttributeTargets.Method)]
    public class ServiceMetadataAttribute : System.Attribute
    {
        #region 特性设置

        private bool checkToken = true;

        private string description = "";

        private bool outputLog = false;

        /// <summary>
        /// 初始化自定义接口元信息特性
        /// </summary>
        public ServiceMetadataAttribute() : this("WCF服务", true, true)
        {

        }

        /// <summary>
        /// 初始化自定义接口元信息特性
        /// </summary>
        /// <param name="description">接口描述</param>
        /// <param name="checkToken">是否检查Token</param>
        /// <param name="outputLog">是否输出日志</param>
        public ServiceMetadataAttribute(string description, bool checkToken = true, bool outputLog = true)
        {
            this.checkToken = checkToken;
            this.description = description;
            this.outputLog = outputLog;
        }

        /// <summary>
        /// 是否检查Token
        /// </summary>
        public bool CheckToken
        {
            get { return checkToken; }
            set { checkToken = value; }
        }

        /// <summary>
        /// 接口描述
        /// </summary>
        public string Description
        {
            get { return description; }
            set { description = value; }
        }

        /// <summary>
        /// 是否输出日志
        /// </summary>
        public bool OutputLog
        {
            get { return outputLog; }
            set { outputLog = value; }
        }

        #endregion
    }
}
