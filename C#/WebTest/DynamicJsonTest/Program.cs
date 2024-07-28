using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Net.Mime;
using System.Reflection.PortableExecutable;
using System.Text;
using Codeplex.Data;
using Mesnac.WebChat.Util;

namespace ConsoleApp1
{
    class Program
    {
        private static void Main() 
        {
            //List<HttpRequest> a = new();
            //a.Add(new HttpRequest());
            //DateTime a = new DateTime();
            //a.AddRange(new DateTime[2] { DateTime.Now,DateTime.MinValue});
            //string a = "{\"wang\":0}";
            //string json = DynamicJson.Serialize(a);
            //dynamic json = DynamicJson.Parse(a);
            //Console.WriteLine(json.wang);
            //Console.WriteLine(json);
            DynamicJson dynamicJson = new DynamicJson();
            //dynamicJson.IsArray = true;
            
            Console.WriteLine(DynamicJson.Serialize(dynamicJson));
        }
        static int rep = 0;
        static string aaa = "ABCDEFGHIGKLMNOPQRSTUVWXYZ";
        void Main(string[] args)
        {
            dynamic dakalist = GetDaKaList();
            foreach (dynamic user in dakalist)
            {
                daka(user);
            }
            Console.ReadLine();
        }

        static dynamic GetDaKaList() 
        {
            string url = "http://106.13.97.93:9000/getuser";
            string resData = HttpRequest.SendGet(url,null);
            dynamic dynamic = DynamicJson.Parse(resData);
            return dynamic.dakalist;
        }

        static void daka(dynamic user)
        {
            string url = "https://jiankang.suoeryun.com/api/userManager/passageway/pclogin";
            string contenttype = "application/json;charset=UTF-8";
            Hashtable header = null;
            var loginData = new
            {
                f_number = user.name,
                f_password = user.password,
                f_schoolid = user.schoolid
            };
            string jsonLoginData = DynamicJson.Serialize(loginData);
            string result = HttpRequest.SendPost(url, jsonLoginData);
            var loginresultobj = DynamicJson.Parse(result);
            string token = loginresultobj.data.token;
            string dakaUrl = "https://jiankang.suoeryun.com/api/outbreakRegistered/createOutbreakRegistered";
            var dakaData = new
            {
                f_currentdetailsaddress = user.address,
                f_currentlocation = "44",
                f_daily_temperature = "36.5",
                f_is_chest_tightness = 0,
                f_is_confirm_contact = 0,
                f_is_confirmed = 0,
                f_is_cough = 0,
                f_is_fever = 0,
                f_is_high_risk_come = 0,
                f_is_nausea_emesis = 0,
                f_is_quarantine_home = 0,
                f_is_quarantine_medicine = 0,
                f_is_rhinitis = 0,
                f_is_suspected = 0,
                f_is_suspected_contact = 0
            };
            string dakaJsondata = DynamicJson.Serialize(dakaData);
            string dakaresult = HttpRequest.SendPost(dakaUrl, dakaJsondata, token);
            Console.WriteLine(dakaresult);
        }

        static string jiami(string oldstring) 
        {
            byte[] b = Encoding.UTF8.GetBytes(oldstring);
            string e = Convert.ToBase64String(b);
            StringBuilder sb = new StringBuilder();
            sb.Append(e.Substring(0, 2));
            sb.Append(suiji());
            sb.Append(e.Substring(2, e.Length - 2));
            sb.Append(suiji());
            return sb.ToString();
        }
        static string suiji() 
        {
            char[] zifus = aaa.ToCharArray();
            long num2 = DateTime.Now.Ticks + rep;
            rep++;
            string result = "";
            Random random = new Random(((int)(((ulong)num2) & 0xffffffffL)) | ((int)(num2 >> rep)));
            for (int i = 0; i < 5; i++)
            {
                result += zifus[random.Next(zifus.Length)];
            }
            return result;
        }

        
    }
}
