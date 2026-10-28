using src.utility;

namespace src.application
{
    /// <summary>
    /// Application Exception
    /// </summary>
    public class ApplicationException : AppException
    {
        public ApplicationException() { }

        public ApplicationException(string message) : base(message) { }

        public ApplicationException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
