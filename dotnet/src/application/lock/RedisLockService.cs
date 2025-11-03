using StackExchange.Redis;

namespace BackendExample.Application
{
    public class RedisLockService : LockService
    {
        private readonly IDatabase _redisDb;
        private readonly string _lockPrefix = "lock:";
        private readonly TimeSpan _defaultExpiry = TimeSpan.FromSeconds(10);

        public RedisLockService(IConnectionMultiplexer redis)
        {
            _redisDb = redis.GetDatabase();
        }

        public IDisposable Lock(string key, int timeout = 10)
        {
            var token = Guid.NewGuid().ToString();
            var expiry = TimeSpan.FromSeconds(timeout);
            var lockKey = _lockPrefix + key;

            while (true)
            {
                if (_redisDb.StringSet(lockKey, token, expiry, When.NotExists))
                {
                    return new RedisLockHandle(_redisDb, lockKey, token);
                }
                Thread.Sleep(200);
            }

            throw new LockException(
                        $"线程 {Environment.CurrentManagedThreadId} 获取 Redis 同步锁 {key} 失败"
                        );
        }

        public async ValueTask<IAsyncDisposable> LockAsync(string key, int timeout = 10)
        {
            var token = Guid.NewGuid().ToString();
            var expiry = TimeSpan.FromSeconds(timeout);
            var lockKey = _lockPrefix + key;

            while (true)
            {
                if (await _redisDb.StringSetAsync(lockKey, token, expiry, When.NotExists))
                {
                    return new AsyncRedisLockHandle(_redisDb, lockKey, token);
                }
                await Task.Delay(200);
            }

            throw new LockException(
                        $"线程 {Environment.CurrentManagedThreadId} 获取 Redis 异步锁 {key} 失败"
                        );
        }

        private class RedisLockHandle : IDisposable
        {
            private readonly IDatabase _db;
            private readonly string _key;
            private readonly string _token;
            private bool _disposed;

            public RedisLockHandle(IDatabase db, string key, string token)
            {
                _db = db;
                _key = key;
                _token = token;
            }

            public void Dispose()
            {
                if (!_disposed)
                {
                    _db.ScriptEvaluate(@"
if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
else
    return 0
end", new RedisKey[] { _key }, new RedisValue[] { _token });
                    _disposed = true;
                }
            }
        }

        private class AsyncRedisLockHandle : IAsyncDisposable
        {
            private readonly IDatabase _db;
            private readonly string _key;
            private readonly string _token;
            private bool _disposed;

            public AsyncRedisLockHandle(IDatabase db, string key, string token)
            {
                _db = db;
                _key = key;
                _token = token;
            }

            public async ValueTask DisposeAsync()
            {
                if (!_disposed)
                {
                    await _db.ScriptEvaluateAsync(@"
if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
else
    return 0
end", new RedisKey[] { _key }, new RedisValue[] { _token });
                    _disposed = true;
                }
            }
        }
    }
}
