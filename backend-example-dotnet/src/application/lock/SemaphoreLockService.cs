using System.Collections.Concurrent;

using log4net;

namespace BackendExample.Application.Lock
{
    /// <summary>
    /// 基于 SemaphoreSlim 实现的锁 Service
    /// </summary>
    public class SemaphoreLockService : LockService
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(SemaphoreLockService));

        private readonly bool enableLog;
        private readonly ConcurrentDictionary<string, SemaphoreSlim> locks = new();

        public SemaphoreLockService(ApplicationConfig applicationConfig)
        {
            this.enableLog = applicationConfig.IsAppEnvDev();
        }

        public IDisposable Lock(string key, int timeout = 30)
        {
            SemaphoreSlim semaphore = this.locks.GetOrAdd(key, _ => new SemaphoreSlim(1, 1));
            bool acquired = semaphore.Wait(TimeSpan.FromSeconds(timeout));
            if (!acquired)
            {
                throw new LockException(
                        $"线程 {Environment.CurrentManagedThreadId} 获取 Semaphore 同步锁 {key} 失败"
                        );
            }
            if (this.enableLog)
            {
                logger.Info($"线程 {Environment.CurrentManagedThreadId} 获取 Semaphore 同步锁 {key} 成功");
            }
            return new SemaphoreLockHandler(this.enableLog, semaphore, key);
        }

        public async ValueTask<IAsyncDisposable> LockAsync(string key, int timeout = 30)
        {
            SemaphoreSlim semaphore = this.locks.GetOrAdd(key, _ => new SemaphoreSlim(1, 1));
            bool acquired = await semaphore.WaitAsync(TimeSpan.FromSeconds(timeout));
            if (!acquired)
            {
                throw new LockException(
                        $"线程 {Environment.CurrentManagedThreadId} 获取 Semaphore 同步锁 {key} 失败"
                        );
            }
            if (this.enableLog)
            {
                logger.Info($"线程 {Environment.CurrentManagedThreadId} 获取 Semaphore 异步锁 {key} 成功");
            }
            return new AsyncSemaphoreLockHandler(this.enableLog, semaphore, key);
        }

        /// <summary>
        /// Semaphore 同步锁处理器
        /// </summary>
        public sealed class SemaphoreLockHandler : IDisposable
        {
            private readonly bool enableLog;
            private readonly SemaphoreSlim semaphore;
            private readonly string key;
            bool isDisposed = false;

            public SemaphoreLockHandler(bool enableLog, SemaphoreSlim semaphore, string key)
            {
                this.enableLog = enableLog;
                this.semaphore = semaphore;
                this.key = key;
            }

            public void Dispose()
            {
                if (!this.isDisposed)
                {
                    this.semaphore.Release();
                    this.isDisposed = true;
                    if (this.enableLog)
                    {
                        logger.Info($"线程 {Environment.CurrentManagedThreadId} 释放 Semaphore 同步锁 {key} 成功");
                    }
                }
            }
        }

        /// <summary>
        /// Semaphore 异步锁处理器
        /// </summary>
        public sealed class AsyncSemaphoreLockHandler : IAsyncDisposable
        {
            private readonly bool enableLog;
            private readonly SemaphoreSlim semaphore;
            private readonly string key;
            bool isDisposed = false;

            public AsyncSemaphoreLockHandler(bool enableLog, SemaphoreSlim semaphore, string key)
            {
                this.enableLog = enableLog;
                this.semaphore = semaphore;
                this.key = key;
            }

            public ValueTask DisposeAsync()
            {
                if (!this.isDisposed)
                {
                    this.semaphore.Release();
                    this.isDisposed = true;
                    if (this.enableLog)
                    {
                        logger.Info($"线程 {Environment.CurrentManagedThreadId} 释放 Semaphore 异步锁 {key} 成功");
                    }
                }
                return default;
            }
        }
    }
}

