using System;
using System.IO;
using System.Windows.Forms;

namespace RestaurantLocalService.Common
{
	
	public class ConsoleRedirect : StringWriter
	{
		private Action<string> writeLineAction;

		private Action<string> writeAction;

        public ConsoleRedirect(Action<string> writeLine) : this(writeLine, null)
		{
			
		}

		public ConsoleRedirect(Action<string> writeLine, Action<string> write) 
		{
			this.writeLineAction = writeLine;
			this.writeAction = write;
		}

		public override void WriteLine(string value)
		{
            base.WriteLine(value);

            if (writeLineAction != null) 
			{
                writeLineAction(value);
            }
		}

		public override void Write(string value)
		{
            base.Write(value);

            if (writeAction != null)
            {
                writeAction(value);
            }
        }
	}
}
