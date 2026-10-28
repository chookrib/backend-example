using src.utility;

namespace src.adapter.driving
{
    /// <summary>
    /// 未登录 Exception
    /// </summary>
    public class NotLoginException : AppException
    {
        public NotLoginException() { }

        public NotLoginException(string message) : base(message) { }

        public NotLoginException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
