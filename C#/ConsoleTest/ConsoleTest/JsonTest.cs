//using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApp1
{
    public class JsonTest {
        public static void Test(string[] args) {
            new HttpHelper.Builder().onsuccess((str)=>{
                Console.WriteLine(str);
            });
            HttpHelper.doGet().onsuccess((str) => {
                Console.WriteLine(str);
            });
            object o = new object();
            Console.WriteLine(o);
            var p = new { aaa = new { aaa = 1 }, bbb = new { bbb = 1 } };
            dynamic dynamic = p;
            Console.WriteLine(dynamic.aaa.aaa.GetType());
            Console.WriteLine();
            Console.WriteLine(dynamic);
            //Console.WriteLine(getJsonByObject(dynamic));

            dynamic dynamic1 = new { };
            dynamic = getObjectByJson("{\"duixiang\":[{\"wang\":\"wang\"},{\"hei\":\"hei\"},{\"dai\":\"dai\"},{},{}]}",new { });
            Console.WriteLine(dynamic);
            Console.WriteLine(dynamic.duixiang);
        }

        public static string getJsonByObject(Object obj)
        {
            //实例化DataContractJsonSerializer对象，需要待序列化的对象类型
            DataContractJsonSerializer serializer = new DataContractJsonSerializer(obj.GetType());
            //实例化一个内存流，用于存放序列化后的数据
            MemoryStream stream = new MemoryStream();
            //使用WriteObject序列化对象
            serializer.WriteObject(stream, obj);
            //写入内存流中
            byte[] dataBytes = new byte[stream.Length];
            stream.Position = 0;
            stream.Read(dataBytes, 0, (int)stream.Length);
            //通过UTF8格式转换为字符串
            return Encoding.UTF8.GetString(dataBytes);
        }

        public static Object getObjectByJson(string jsonString, Object obj)
        {
            //实例化DataContractJsonSerializer对象，需要待序列化的对象类型
            DataContractJsonSerializer serializer = new DataContractJsonSerializer(obj.GetType());
            //把Json传入内存流中保存
            MemoryStream stream = new MemoryStream(Encoding.UTF8.GetBytes(jsonString));
            // 使用ReadObject方法反序列化成对象
            return serializer.ReadObject(stream);
        }
    }
    public class HttpHelper
    {
        public class Builder{
            public Builder onsuccess(Action<string> callback) {
                HttpHelper.doGet().onsuccess(callback);
                return this;
            }
        }
        public static HttpHelper doGet() {
            return new HttpHelper();
        }
        public static HttpHelper doPost() {
            return new HttpHelper();
        }
        public HttpHelper onsuccess(Action<string> callback) {
            callback("dfsf");
            return this;
        }
    }
}
