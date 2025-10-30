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

        public T GetWithLock<T>(string key, Func<T> func)
        {
            TimeSpan timeout = TimeSpan.FromSeconds(30);
            SemaphoreSlim semaphore = this.locks.GetOrAdd(key, _ => new SemaphoreSlim(1, 1));

            bool isAcquired = false;
            try
            {
                isAcquired = semaphore.Wait(timeout);
                if (isAcquired)
                {
                    if(Accessor.IsDevelopment)
                        logger.Info($"线程 {Environment.CurrentManagedThreadId} 获取 Semaphore 同步锁 {key} 成功");
                    return func();
                }
                else
                {
                    throw new ApplicationException(
                        $"线程 {Environment.CurrentManagedThreadId} 获取 Semaphore 同步锁 {key} 失败"
                        );
                }
            }
            finally
            {
                if (isAcquired)
                {
                    semaphore.Release();
                    if (Accessor.IsDevelopment)
                        logger.Info($"线程 {Environment.CurrentManagedThreadId} 释放 Semaphore 同步锁 {key} 成功");
                }
            }
        }

        public void RunWithLock(string key, Action action)
        {
            GetWithLock<object?>(key, () =>
            {
                action();
                return null;
            });
        }

        public async Task<T> GetWithLockAsync<T>(string key, Func<Task<T>> func)
        {
            TimeSpan timeout = TimeSpan.FromSeconds(30);
            SemaphoreSlim semaphore = this.locks.GetOrAdd(key, _ => new SemaphoreSlim(1, 1));

            bool isAcquired = false;
            try
            {
                isAcquired = await semaphore.WaitAsync(timeout);
                if (isAcquired)
                {
                    if (Accessor.IsDevelopment)
                        logger.Info($"线程 {Environment.CurrentManagedThreadId} 获取 Semaphore 异步锁 {key} 成功");
                    return await func();
                }
                else
                {
                    throw new ApplicationException(
                        $"线程 {Environment.CurrentManagedThreadId} 获取 Semaphore 异步锁 {key} 失败"
                        );
                }
            }
            finally
            {
                if (isAcquired)
                {
                    if (Accessor.IsDevelopment)
                        logger.Info($"线程 {Environment.CurrentManagedThreadId} 释放 Semaphore 异步锁 {key} 成功");
                    semaphore.Release();
                }
            }
        }

        public async Task RunWithLockAsync(string key, Func<Task> func)
        {
            await GetWithLockAsync<object?>(key, async () =>
            {
                await func();
                return null;
            });
        }
    }
}

