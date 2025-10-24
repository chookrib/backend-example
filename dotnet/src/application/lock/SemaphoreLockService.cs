using System.Collections.Concurrent;

namespace DddExample.Application
{
    /// <summary>
    /// 基于 ReentrantLock 实现的锁 Service
    /// </summary>
    public class SemaphoreLockService : LockService
    {
        private readonly ConcurrentDictionary<string, SemaphoreSlim> locks = new();

        public T ExecuteWithLock<T>(string key, Func<T> func)
        {
            TimeSpan timeout = TimeSpan.FromSeconds(30);
            SemaphoreSlim semaphore = this.locks.GetOrAdd(key, _ => new SemaphoreSlim(1, 1));

            bool isAcquired = false;
            try
            {
                isAcquired = semaphore.Wait(timeout);   // await semaphore.WaitAsync(timeout);
                if (isAcquired)
                {
                    return func();
                }
                else
                {
                    throw new ApplicationException($"获取锁 {key} 失败");
                }
            }
            finally
            {
                if (isAcquired)
                {
                    semaphore.Release();
                }
            }
        }

        public void ExecuteWithLock(string key, Action action)
        {
            TimeSpan timeout = TimeSpan.FromSeconds(30);
            SemaphoreSlim semaphore = this.locks.GetOrAdd(key, _ => new SemaphoreSlim(1, 1));

            bool isAcquired = false;
            try
            {
                isAcquired = semaphore.Wait(timeout);   // await semaphore.WaitAsync(timeout);
                if (isAcquired)
                {
                    action();
                }
                else
                {
                    throw new ApplicationException($"获取锁 {key} 失败");
                }
            }
            finally
            {
                if (isAcquired)
                {
                    semaphore.Release();
                }
            }
        }
    }
}

