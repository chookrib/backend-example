namespace BackendExample.Application
{
    /// <summary>
    /// Lock Exception
    /// </summary>
    public class LockException : Exception
    {
        public LockException() { }

        public LockException(string message) : base(message) { }

        public LockException(string message, Exception innerException)
            : base(message, innerException) { }
    }
}
