namespace src.utility
{
    /// <summary>
    /// Utility Exception
    /// </summary>
    public class UtilityException : AppException
    {
        public UtilityException() { }

        public UtilityException(string message) : base(message) { }

        public UtilityException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
