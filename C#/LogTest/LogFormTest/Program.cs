using RestaurantLocalService.Common;
using System.Diagnostics;
using System.Text;

namespace RestaurantLocalService
{
    internal static class Program
    {
        /// <summary>
        ///  The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            ApplicationConfiguration.Initialize();

            LogForm logForm = new LogForm();

            // Console.SetOut(new ConsoleRedirect(logForm.WriteLineLog, logForm.WriteLog));

            Thread thread = new Thread(() => {
                string chars = "ABCDEFGHIJKLMNOPQRSTUWVXYZ0123456789abcdefghijklmnopqrstuvwxyz ";
                Random random = new Random();

                while (true) 
                {
                    StringBuilder stringBuilder = new StringBuilder();
                    int index = random.Next(300);

                    for (int i = 0; i < index; i++)
                    {
                        int charIndex = random.Next(chars.Length);
                        stringBuilder.Append(chars[charIndex]);
                    }

                    Debug.WriteLine(stringBuilder.ToString());

                    int sleep = random.Next(1000);
                    Thread.Sleep(sleep);
                }
            });

            thread.Start();

            // To customize application configuration such as set high DPI settings or default font,
            // see https://aka.ms/applicationconfiguration.
            Application.Run(logForm);
        }
    }
}