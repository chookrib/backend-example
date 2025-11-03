using System.Collections.Concurrent;

using log4net;

namespace BackendExample.Application
{
    /// <summary>
    /// 基于 SemaphoreSlim 实现的锁 Service
    /// </summary>
    public class SemaphoreLockService : LockService
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(SemaphoreLockService));

        private readonly ConcurrentDictionary<string, SemaphoreSlim> locks = new();

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
            if (Accessor.AppIsDev)
                logger.Info($"线程 {Environment.CurrentManagedThreadId} 获取 Semaphore 同步锁 {key} 成功");
            return new SemaphoreLockHandler(key, semaphore);
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
            if (Accessor.AppIsDev)
                logger.Info($"线程 {Environment.CurrentManagedThreadId} 获取 Semaphore 异步锁 {key} 成功");
            return new AsyncSemaphoreLockHandler(key, semaphore);
        }
    }

    /// <summary>
    /// Semaphore 同步锁处理器
    /// </summary>
    public sealed class SemaphoreLockHandler : IDisposable
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(SemaphoreLockHandler));

        private readonly string key;
        private readonly SemaphoreSlim semaphore;
        bool isRelease = false;

        public SemaphoreLockHandler(string key, SemaphoreSlim semaphore)
        {
            this.key = key;
            this.semaphore = semaphore;
        }

        public void Dispose()
        {
            if (!this.isRelease)
            {
                this.semaphore.Release();
                this.isRelease = true;
                if (Accessor.AppIsDev)
                    logger.Info($"线程 {Environment.CurrentManagedThreadId} 释放 Semaphore 同步锁 {key} 成功");
            }
        }
    }

    /// <summary>
    /// Semaphore 异步锁处理器
    /// </summary>
    public sealed class AsyncSemaphoreLockHandler : IAsyncDisposable
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(AsyncSemaphoreLockHandler));

        private readonly string key;
        private readonly SemaphoreSlim semaphore;
        bool isRelease = false;

        public AsyncSemaphoreLockHandler(string key, SemaphoreSlim semaphore)
        {
            this.key = key;
            this.semaphore = semaphore;
        }

        public ValueTask DisposeAsync()
        {
            if (!this.isRelease)
            {
                this.semaphore.Release();
                this.isRelease = true;
                if (Accessor.AppIsDev)
                    logger.Info($"线程 {Environment.CurrentManagedThreadId} 释放 Semaphore 异步锁 {key} 成功");
            }
            return default;
        }
    }
}

