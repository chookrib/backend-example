namespace BackendExample.Utility
{
    /// <summary>
    /// Utility Exception
    /// </summary>
    public class UtilityException : Exception
    {
        public UtilityException() { }

        public UtilityException(string message) : base(message) { }

        public UtilityException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
