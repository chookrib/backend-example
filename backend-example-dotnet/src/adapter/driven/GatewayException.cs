using src.utility;

namespace src.adapter.driven
{
    /// <summary>
    /// Gateway Exception
    /// </summary>
    public class GatewayException : AppException
    {
        public GatewayException() { }

        public GatewayException(string message) : base(message) { }

        public GatewayException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
