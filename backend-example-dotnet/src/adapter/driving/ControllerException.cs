using src.utility;

namespace src.adapter.driving
{
    /// <summary>
    /// Controller Exception
    /// </summary>
    public class ControllerException : AppException
    {
        public ControllerException() { }

        public ControllerException(string message) : base(message) { }

        public ControllerException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
