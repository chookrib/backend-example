namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 未登录 Exception
    /// </summary>
    public class NotLoginException : Exception
    {
        public NotLoginException() { }

        public NotLoginException(string message) : base(message) { }

        public NotLoginException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
