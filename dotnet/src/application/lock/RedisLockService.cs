using StackExchange.Redis;
using System;
using System.Threading;
using System.Threading.Tasks;

namespace BackendExample.Application
{
    public class RedisLockService : LockService
    {
        private readonly IDatabase _redisDatabase;

        public RedisLockService(IConnectionMultiplexer redisConnection)
        {
            _redisDatabase = redisConnection.GetDatabase();
        }

        public override T GetWithLock<T>(string key, Func<T> func, int timeout = 30)
        {
            if (TryAcquireLock(key, timeout))
            {
                try
                {
                    return func();
                }
                finally
                {
                    ReleaseLock(key);
                }
            }
            throw new TimeoutException("Failed to acquire lock within the specified timeout.");
        }

        public override async Task<T> GetWithLockAsync<T>(string key, Func<Task<T>> func, int timeout = 30)
        {
            if (await TryAcquireLockAsync(key, timeout))
            {
                try
                {
                    return await func();
                }
                finally
                {
                    await ReleaseLockAsync(key);
                }
            }
            throw new TimeoutException("Failed to acquire lock within the specified timeout.");
        }

        public override void RunWithLock(string key, Action action, int timeout = 30)
        {
            if (TryAcquireLock(key, timeout))
            {
                try
                {
                    action();
                }
                finally
                {
                    ReleaseLock(key);
                }
            }
            else
            {
                throw new TimeoutException("Failed to acquire lock within the specified timeout.");
            }
        }

        public override async Task RunWithLockAsync(string key, Func<Task> func, int timeout = 30)
        {
            if (await TryAcquireLockAsync(key, timeout))
            {
                try
                {
                    await func();
                }
                finally
                {
                    await ReleaseLockAsync(key);
                }
            }
            else
            {
                throw new TimeoutException("Failed to acquire lock within the specified timeout.");
            }
        }

        private bool TryAcquireLock(string key, int timeout)
        {
            var lockKey = GetLockKey(key);
            var lockValue = Guid.NewGuid().ToString();
            var expiry = TimeSpan.FromSeconds(timeout);

            return _redisDatabase.StringSet(lockKey, lockValue, expiry, When.NotExists);
        }

        private async Task<bool> TryAcquireLockAsync(string key, int timeout)
        {
            var lockKey = GetLockKey(key);
            var lockValue = Guid.NewGuid().ToString();
            var expiry = TimeSpan.FromSeconds(timeout);

            return await _redisDatabase.StringSetAsync(lockKey, lockValue, expiry, When.NotExists);
        }

        private void ReleaseLock(string key)
        {
            var lockKey = GetLockKey(key);
            _redisDatabase.KeyDelete(lockKey);
        }

        private async Task ReleaseLockAsync(string key)
        {
            var lockKey = GetLockKey(key);
            await _redisDatabase.KeyDeleteAsync(lockKey);
        }

        private string GetLockKey(string key)
        {
            return $"lock:{key}";
        }
    }
}
