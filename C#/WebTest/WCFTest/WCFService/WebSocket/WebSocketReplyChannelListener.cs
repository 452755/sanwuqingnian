using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel.Channels;
using System.Text;
using System.Threading.Tasks;
using System.Threading;
using System.Net.Sockets;

namespace WCFTest.WCFService.WebSocket
{
    public class WebSocketChannelListener : ChannelListenerBase<IReplyChannel>
    {
        private readonly Uri _uri;
        private TcpListener _listener;

        public WebSocketChannelListener(BindingContext context)
            : base(context.Binding)
        {
            _uri = context.ListenUriBaseAddress;
        }

        protected override void OnOpen(TimeSpan timeout)
        {
            _listener = new TcpListener(_uri.Host == "localhost" ? System.Net.IPAddress.Loopback : System.Net.IPAddress.Any, _uri.Port);
            _listener.Start();
            base.OnOpen(timeout);
        }

        protected override void OnClose(TimeSpan timeout)
        {
            _listener.Stop();
            base.OnClose(timeout);
        }

        protected override IReplyChannel OnAcceptChannel(TimeSpan timeout)
        {
            TcpClient client = _listener.AcceptTcpClient();
            return new WebSocketReplyChannel(client, this);
        }

        protected override bool OnWaitForChannel(TimeSpan timeout)
        {
            return _listener.Pending();
        }

        public override Uri Uri
        {
            get { return _uri; }
        }
    }
}
