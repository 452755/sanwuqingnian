using System.Net;

Console.WriteLine("Begin!");

HttpListener listener = new HttpListener();
string host = "http://127.0.0.1:10003/";
listener.Prefixes.Add(host);
listener.Start();
Console.WriteLine(host);

var l = new Action<HttpListener>(async (listener) => {
    while (true) 
    {
        var context = await listener.GetContextAsync();
        Console.WriteLine(context.Request.Url);

        await using (var sw = new StreamWriter(context.Response.OutputStream))
        {
            await sw.WriteLineAsync("<h1>我是你爹<h1>");
        }
    }
});

l(listener);

bool isExit = false;

Console.CancelKeyPress += (sender, e) => 
{
    e.Cancel = true;
    isExit = true;
};

while (true) 
{
    if (isExit) 
    {
        break;
    }    
}

//listener.Stop();
listener.Close();

Console.WriteLine("End!");

