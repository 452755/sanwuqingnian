using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace ConsoleApp3.WCFService
{
    public class StaticFileService : IStaticFileService
    {
        Stream IStaticFileService.GetFile()
        {
            throw new NotImplementedException();
        }
    }
}
