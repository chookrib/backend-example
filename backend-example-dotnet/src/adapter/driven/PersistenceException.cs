using src.utility;

namespace src.adapter.driven
{
    /// <summary>
    /// Persistence Exception
    /// </summary>
    public class PersistenceException : AppException
    {
        public PersistenceException() { }

        public PersistenceException(string message) : base(message) { }

        public PersistenceException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
