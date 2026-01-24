namespace BackendExample.Application
{
    /// <summary>
    /// 锁 Service 接口
    /// </summary>
    public interface LockService
    {
        /// <summary>
        /// 同步加锁
        /// </summary>
        IDisposable Lock(string key, int timeout = 10);

        /// <summary>
        /// 异步加锁
        /// </summary>
        ValueTask<IAsyncDisposable> LockAsync(string key, int timeout = 10);
    }
}
