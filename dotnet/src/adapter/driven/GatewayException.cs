namespace DddExample.Adapter.Driven
{
    /// <summary>
    /// Gateway Exception
    /// </summary>
    public class ControllerException : Exception
    {
        public ControllerException() { }

        public ControllerException(string message) : base(message) { }

        public ControllerException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
