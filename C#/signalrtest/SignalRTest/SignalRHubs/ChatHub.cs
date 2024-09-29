using Microsoft.AspNetCore.SignalR;

namespace SignalRTest.SignalRHubs
{
    public class ChatHub : Hub
    {
        private ILogger<ChatHub> _logger;

        public ChatHub(ILogger<ChatHub> logger) 
        {
            _logger = logger;
        }

        public override Task OnConnectedAsync()
        {
            _logger.LogInformation("连接成功：客户端id: {0}", Context.ConnectionId);
            Clients.Client(Context.ConnectionId).SendAsync("Connected");
            return base.OnConnectedAsync();
        }

        public override Task OnDisconnectedAsync(Exception? exception)
        {
            _logger.LogInformation(exception?.Message);
            return base.OnDisconnectedAsync(exception);
        }

        public async Task SendMessage(string user, string message)
        {
            _logger.LogInformation("SendMessage，user：{0}，message：{1}", user, message);
            await Clients.All.SendAsync("ReceiveMessage", user, message);
        }

        public async Task SendNiMaMessage(string user, string message) 
        {
            _logger.LogInformation("SendNiMaMessage，user：{0}，message：{1}", user, message);
            await Clients.All.SendAsync("ReceiveMessage", user, message);
        }
    }
}
