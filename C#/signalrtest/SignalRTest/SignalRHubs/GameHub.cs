using Microsoft.AspNetCore.SignalR;

namespace SignalRTest.SignalRHubs
{
    public class GameRoom 
    {
        /// <summary>
        /// 房间名称
        /// </summary>
        public string RoomName { get; set; } = "";

        /// <summary>
        /// 玩家一的连接Id
        /// </summary>
        public string UserOneConnectionId { get; set; } = "";

        /// <summary>
        /// 玩家二的连接Id
        /// </summary>
        public string UserTwoConnectionId { get; set; } = "";
    }

    public class GameHub : Hub
    {
        public static List<GameRoom> gameRooms = new List<GameRoom>();

        private ILogger<ChatHub> _logger;

        public GameHub(ILogger<ChatHub> logger) 
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
            _logger.LogError(exception, "客户端：{0} 断开连接", Context.ConnectionId);
            Clients.Client(Context.ConnectionId).SendAsync("Disconnected");
            return base.OnDisconnectedAsync(exception);
        }

        public async Task CreateGameRoom(string roomName) 
        {
            _logger.LogInformation("客户端: {0}，创建游戏房间：{1}", Context.ConnectionId, roomName);
            await Clients.All.SendAsync("NewRoomCreated", roomName);
        }

        public async Task ConnectGameRoom(string roomName) 
        {
            _logger.LogInformation("客户端: {0}，创建游戏房间：{1}", Context.ConnectionId, roomName);
            var room = gameRooms.Find((room) => room.RoomName == roomName);
            room.UserTwoConnectionId = Context.ConnectionId;
            await Clients.Client(room.UserOneConnectionId).SendAsync("", "");
        }
    }
}
