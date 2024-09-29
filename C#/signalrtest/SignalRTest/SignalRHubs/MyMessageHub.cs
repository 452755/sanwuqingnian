using Microsoft.AspNetCore.SignalR;

namespace SignalRTest.SignalRHubs
{
    public class MyMessageHub : Hub<IMyMessage>
    {
        private ILogger<ChatHub> _logger;

        public MyMessageHub(ILogger<ChatHub> logger)
        {
            _logger = logger;
        }

        public async Task SendMessage(string user, string message)
        {
            _logger.LogInformation("SendMessage，user：{0}，message：{1}", user, message);
            await Clients.All.SendMyMessage(user, message);
        }
    }
}
