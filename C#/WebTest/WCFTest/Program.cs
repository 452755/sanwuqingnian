using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.Text;
using WCFTest.WCFService;
using WCFTest.WCFService.WebSocket;

namespace WCFTest
{
    internal class Program
    {
        static void Main(string[] args)
        {
            testWebSocket();

            Console.WriteLine("按任意键继续...");
            Console.ReadKey();
        }

        static void testWebSocket() 
        {
            Uri baseAddress = new Uri("http://localhost:8080/MyService");
            ServiceHost host = new ServiceHost(typeof(MyService), baseAddress);

            WebSocketBinding binding = new WebSocketBinding();
            host.AddServiceEndpoint(typeof(IMyService), binding, "");

            host.Open();
            Console.WriteLine("Service is running...");
            Console.ReadLine();

            host.Close();
        }
    }
}
