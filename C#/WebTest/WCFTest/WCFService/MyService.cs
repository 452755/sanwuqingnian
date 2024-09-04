using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace WCFTest.WCFService
{
    public class MyService : IMyService
    {
        public string Echo(string message)
        {
            return $"Echo: {message}";
        }
    }
}
