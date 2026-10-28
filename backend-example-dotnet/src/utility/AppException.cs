namespace src.utility
{
    /// <summary>
    /// 应用异常基类
    /// </summary>
    public abstract class AppException : Exception
    {
        public AppException() { }

        public AppException(string message) : base(message) { }

        public AppException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
