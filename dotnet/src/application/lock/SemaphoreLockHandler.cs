
using log4net;

namespace BackendExample.Application
{
    public sealed class SemaphoreLockHandler : IDisposable
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(SemaphoreLockHandler));

        private readonly string key;
        private readonly SemaphoreSlim semaphore;

        public SemaphoreLockHandler(string key, SemaphoreSlim semaphore)
        {
            this.key = key;
            this.semaphore = semaphore;
        }

        public void Dispose()
        {
            this.semaphore.Release();
            if (Accessor.AppIsDev)
                logger.Info($"线程 {Environment.CurrentManagedThreadId} 释放 Semaphore 同步锁 {key} 成功");
        }
    }

    public sealed class AsyncSemaphoreLockHandler : IAsyncDisposable
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(AsyncSemaphoreLockHandler));

        private readonly string key;
        private readonly SemaphoreSlim semaphore;

        public AsyncSemaphoreLockHandler(string key, SemaphoreSlim semaphore)
        {
            this.key = key;
            this.semaphore = semaphore;
        }

        public ValueTask DisposeAsync()
        {
            this.semaphore.Release();
            if (Accessor.AppIsDev)
                logger.Info($"线程 {Environment.CurrentManagedThreadId} 释放 Semaphore 异步锁 {key} 成功");
            return default;
        }
    }
}
