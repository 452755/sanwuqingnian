using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel.Channels;
using System.ServiceModel;
using System.Text;
using System.Threading.Tasks;

namespace WCFTest.WCFService.WebSocket
{
    public class WebSocketReplyChannel : ChannelBase, IReplyChannel
    {
        private readonly WebSocketReplyChannelListener _listener;

        public WebSocketReplyChannel(WebSocketReplyChannelListener listener) : base(listener)
        {
            _listener = listener;
        }

        public EndpointAddress LocalAddress => new EndpointAddress(_listener.Uri);

        public RequestContext ReceiveRequest()
        {
            throw new NotImplementedException();
        }

        public RequestContext ReceiveRequest(TimeSpan timeout)
        {
            throw new NotImplementedException();
        }

        public IAsyncResult BeginReceiveRequest(AsyncCallback callback, object state)
        {
            throw new NotImplementedException();
        }

        public IAsyncResult BeginReceiveRequest(TimeSpan timeout, AsyncCallback callback, object state)
        {
            throw new NotImplementedException();
        }

        public RequestContext EndReceiveRequest(IAsyncResult result)
        {
            throw new NotImplementedException();
        }

        public bool TryReceiveRequest(TimeSpan timeout, out RequestContext context)
        {
            context = null;
            return false;
        }

        public IAsyncResult BeginTryReceiveRequest(TimeSpan timeout, AsyncCallback callback, object state)
        {
            throw new NotImplementedException();
        }

        public bool EndTryReceiveRequest(IAsyncResult result, out RequestContext context)
        {
            context = null;
            return false;
        }

        public bool WaitForRequest(TimeSpan timeout)
        {
            return true;
        }

        public IAsyncResult BeginWaitForRequest(TimeSpan timeout, AsyncCallback callback, object state)
        {
            // return Task.CompletedTask;
            throw new NotImplementedException();
        }

        public bool EndWaitForRequest(IAsyncResult result)
        {
            return true;
        }

        protected override void OnAbort()
        {
            throw new NotImplementedException();
        }

        protected override void OnClose(TimeSpan timeout)
        {
            throw new NotImplementedException();
        }

        protected override void OnEndClose(IAsyncResult result)
        {
            throw new NotImplementedException();
        }

        protected override IAsyncResult OnBeginClose(TimeSpan timeout, AsyncCallback callback, object state)
        {
            throw new NotImplementedException();
        }

        protected override void OnOpen(TimeSpan timeout)
        {
            throw new NotImplementedException();
        }

        protected override IAsyncResult OnBeginOpen(TimeSpan timeout, AsyncCallback callback, object state)
        {
            throw new NotImplementedException();
        }

        protected override void OnEndOpen(IAsyncResult result)
        {
            throw new NotImplementedException();
        }
    }
}
