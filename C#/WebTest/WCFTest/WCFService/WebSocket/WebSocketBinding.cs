using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel.Channels;
using System.Text;

namespace WCFTest.WCFService.WebSocket
{
    public class WebSocketBinding : Binding
    {
        public override BindingElementCollection CreateBindingElements()
        {
            var elements = new BindingElementCollection();
            elements.Add(new WebSocketTransportBindingElement());
            return elements.Clone();
        }

        public override string Scheme => "http";
    }
}
