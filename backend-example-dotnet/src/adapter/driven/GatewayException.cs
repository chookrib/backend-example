namespace BackendExample.Adapter.Driven
{
    /// <summary>
    /// Gateway Exception
    /// </summary>
    public class GatewayException : Exception
    {
        public GatewayException() { }

        public GatewayException(string message) : base(message) { }

        public GatewayException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
