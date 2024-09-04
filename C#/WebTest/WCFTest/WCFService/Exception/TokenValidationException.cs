using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ConsoleApp3.WCFService.Exception
{
    public class TokenValidationException : System.Exception
    {
        public TokenValidationException(string message) : base(message) { }
    }
}
