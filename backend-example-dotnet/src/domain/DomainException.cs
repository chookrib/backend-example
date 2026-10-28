using src.utility;

namespace src.domain
{
    /// <summary>
    /// Domain Exception
    /// </summary>
    public class DomainException : AppException
    {
        public DomainException() { }

        public DomainException(string message) : base(message) { }

        public DomainException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
