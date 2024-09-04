using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.Text;

namespace WCFTest.WCFService
{
    [ServiceContract]
    public interface IMyService
    {
        [OperationContract]
        string Echo(string message);
    }
}
