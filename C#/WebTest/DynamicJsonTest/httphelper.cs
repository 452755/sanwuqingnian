using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net;

namespace Mesnac.WebChat.Util
{
    /// <summary>
    /// Http请求封装类，封装常用的Get，Post请求方法
    /// </summary>
    public class HttpRequest
    {
        /// <summary>
        /// 向指定URL发送GET方法的请求
        /// </summary>
        /// <param name="url">发送请求的URL</param>
        /// <param name="param">请求参数，请求参数应该是 name1=value1&name2=value2 的形式。</param>
        /// <returns>所代表远程资源的响应结果</returns>
        public static string SendGet(string url, string param)
        {
            string result = String.Empty;
            StreamReader reader = null;
            try
            {
                string urlNameString = url + "?" + param;
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(urlNameString);
                request.Method = "GET";
                request.ContentType = "text/html;charset=UTF-8";
                request.Accept = "*/*";
                request.UserAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)";

                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                Stream responseStream = response.GetResponseStream();
                reader = new StreamReader(responseStream, Encoding.GetEncoding("utf-8"));
                result = reader.ReadToEnd();

                reader.Close();
                responseStream.Close();
                response.Close();
                reader = null;
                responseStream = null;
                response = null;
            }
            catch (Exception ex)
            {
                Console.WriteLine("发送GET请求出现异常：" + ex.Message);
            }
            finally
            {
                if (reader != null)
                {
                    reader.Close();
                }
            }
            return result;
        }

        /// <summary>
        /// 向指定URL发送GET方法的请求
        /// </summary>
        /// <param name="url">发送请求的URL</param>
        /// <param name="param">请求参数，请求参数应该是 name1=value1&name2=value2 的形式。</param>
        /// <param name="encoding">设置响应信息的编码格式，如utf-8</param>
        /// <returns>所代表远程资源的响应结果</returns>
        public static string SendGet(string url, string param, string encoding)
        {
            string result = String.Empty;
            StreamReader reader = null;
            try
            {
                string urlNameString = url + "?" + param;
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(urlNameString);
                request.Method = "GET";
                request.ContentType = "text/html;charset=" + encoding;
                request.Accept = "*/*";
                request.UserAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)";

                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                Stream responseStream = response.GetResponseStream();
                reader = new StreamReader(responseStream, Encoding.GetEncoding(encoding));
                result = reader.ReadToEnd();

                reader.Close();
                responseStream.Close();
                response.Close();
                reader = null;
                responseStream = null;
                response = null;
            }
            catch (Exception ex)
            {
                Console.WriteLine("发送GET请求出现异常：" + ex.Message);
            }
            finally
            {
                if (reader != null)
                {
                    reader.Close();
                }
            }
            return result;
        }

        /// <summary>
        /// 向指定 URL 发送POST方法的请求
        /// </summary>
        /// <param name="url">发送请求的 URL</param>
        /// <param name="jsonData">请求参数，请求参数应该是Json格式字符串的形式。</param>
        /// <returns>所代表远程资源的响应结果</returns>
        public static string SendPost(string url, string jsonData)
        {
            string result = String.Empty;
            try
            {
                CookieContainer cookie = new CookieContainer();

                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                request.Method = "POST";
                //request.Headers.Add("x-requested-with", "XMLHttpRequest");
                //request.ServicePoint.Expect100Continue = false;
                request.ContentType = "application/json;charset=UTF-8";
                request.Accept = "application/json, text/plain, */*";
                request.UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36"; 
                request.ContentLength = Encoding.UTF8.GetByteCount(jsonData);
                //request.CookieContainer = cookie;
                using (StreamWriter writer = new StreamWriter(request.GetRequestStream()))
                {
                    writer.Write(jsonData);
                }

                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                response.Cookies = cookie.GetCookies(response.ResponseUri);
                using (Stream responseStream = response.GetResponseStream())
                {
                    using (StreamReader reader = new StreamReader(responseStream))
                    {
                        result = reader.ReadToEnd();

                        reader.Close();
                    }
                    responseStream.Close();
                }
                response.Close();
                response = null;
                request = null;
            }
            catch (Exception ex)
            {
                Console.WriteLine("发送POST请求出现异常：" + ex.Message);
            }
            return result;
        }

        /// <summary>
        /// 向指定 URL 发送POST方法的请求
        /// </summary>
        /// <param name="url">发送请求的 URL</param>
        /// <param name="jsonData">请求参数，请求参数应该是Json格式字符串的形式。</param>
        /// <param name="token"></param>
        /// <returns>所代表远程资源的响应结果</returns>
        public static string SendPost(string url, string jsonData, string token)
        {
            string result = String.Empty;
            try
            {
                CookieContainer cookie = new CookieContainer();
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                request.Method = "POST";
                if (token != null || token != "") {
                    request.Headers.Add("Authorization", token);
                }
                //request.ServicePoint.Expect100Continue = false;
                request.ContentType = "application/json;charset=UTF-8";
                request.Accept = "application/json, text/plain, */*";
                request.UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36";
                request.ContentLength = Encoding.UTF8.GetByteCount(jsonData);
                //request.CookieContainer = cookie;
                using (StreamWriter writer = new StreamWriter(request.GetRequestStream()))
                {
                    writer.Write(jsonData);
                }

                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                response.Cookies = cookie.GetCookies(response.ResponseUri);
                using (Stream responseStream = response.GetResponseStream())
                {
                    using (StreamReader reader = new StreamReader(responseStream))
                    {
                        result = reader.ReadToEnd();

                        reader.Close();
                    }
                    responseStream.Close();
                }
                response.Close();
                response = null;
                request = null;
            }
            catch (Exception ex)
            {
                Console.WriteLine("发送GET请求出现异常：" + ex.Message);
            }
            return result;
        }

        /// <summary>
        /// 上传媒体文件
        /// </summary>
        /// <param name="url">上传媒体文件的微信公众平台接口地址</param>
        /// <param name="file">要上传的媒体文件对象</param>
        /// <returns>返回上传的响应结果，详情参看微信公众平台开发者接口文档</returns>
        public static string UploadPost(string url, string file)
        {
            string result = String.Empty;
            try
            {
                WebClient client = new WebClient();
                client.Credentials = CredentialCache.DefaultCredentials;
                byte[] responseArray = client.UploadFile(url, "POST", file);
                result = System.Text.Encoding.Default.GetString(responseArray, 0, responseArray.Length);
                Console.WriteLine("上传result:" + result);
            }
            catch (Exception ex)
            {
                result = "Error:" + ex.Message;
                Console.WriteLine("发送GET请求出现异常：" + ex.Message);
            }
            finally
            {
                Console.WriteLine("上传MediaId:" + result);
            }
            return result;
        }

        public static bool DownloadFile(string url, string param, string outFileName)
        {
            bool result = false;
            FileStream fs = null;
            try
            {
                string urlNameString = url + "?" + param;
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(urlNameString);
                request.Method = "GET";
                request.ContentType = "text/html;charset=UTF-8";
                request.Accept = "*/*";
                request.UserAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)";

                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                Stream responseStream = response.GetResponseStream();
                fs = new FileStream(outFileName, FileMode.Create);
                int bufferSize = 2048;
                byte[] data = new byte[bufferSize];
                int length = 0;
                while ((length = responseStream.Read(data, 0, bufferSize)) > 0)
                {
                    fs.Write(data, 0, length);
                }
                fs.Close();
                responseStream.Close();
                response.Close();
                fs = null;
                responseStream = null;
                response = null;

                result = true;
            }
            catch (Exception ex)
            {
                Console.WriteLine("下载媒体文件时出现异常：" + ex.Message);
                //Mesnac.Log.LogService.Instance.Error("下载媒体文件时出现异常：" + ex.Message);
            }
            finally
            {
                if (fs != null)
                {
                    fs.Close();
                }
            }
            return result;
        }
    }
}
