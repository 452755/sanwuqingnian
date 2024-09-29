namespace SignalRTest.SignalRHubs
{
    public interface IMyMessage
    {
        Task SendMyMessage(string name, string body);
    }
}
