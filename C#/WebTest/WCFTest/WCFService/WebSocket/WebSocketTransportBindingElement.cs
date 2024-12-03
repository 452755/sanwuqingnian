using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel.Channels;
using System.Text;

namespace WCFTest.WCFService.WebSocket
{
    public class WebSocketTransportBindingElement : TransportBindingElement
    {
        public override BindingElement Clone()
        {
            return new WebSocketTransportBindingElement();
        }

        public override string Scheme => "http";

        public override IChannelFactory<TChannel> BuildChannelFactory<TChannel>(BindingContext context)
        {
            throw new NotImplementedException();
        }

        public override IChannelListener<TChannel> BuildChannelListener<TChannel>(BindingContext context)
        {
            if (typeof(TChannel) == typeof(IReplyChannel))
            {
                return (IChannelListener<TChannel>)new WebSocketReplyChannelListener(context);
            }

            throw new NotSupportedException();
        }

        public override bool CanBuildChannelListener<TChannel>(BindingContext context)
        {
            return typeof(TChannel) == typeof(IReplyChannel);
        }
    }
}
