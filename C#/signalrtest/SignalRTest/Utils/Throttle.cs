namespace SignalRTest.Utils
{
    public class Throttle
    {
        private int delayMillisecond = 0;

        private DateTime lastInvokeTime = DateTime.Now;

        private Delegate _delegate = null;

        public Throttle(Delegate _delegate, int delayMillisecond) 
        {
            this._delegate = _delegate;
            this.delayMillisecond = delayMillisecond;
            this.lastInvokeTime = DateTime.Now.AddMilliseconds(-delayMillisecond);
        }

        public void Invoke(params object[] args) 
        {
            if (DateTime.Now < this.lastInvokeTime.AddMilliseconds(this.delayMillisecond))
            {
                return;
            }
            else 
            {
                this._delegate.DynamicInvoke(args);
            }
        }
    }
}
