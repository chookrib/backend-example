using System.Diagnostics;

using src.utility;

using log4net;

using StackExchange.Redis;

namespace src.application.@lock
{
    /// <summary>
    /// 基于 Redis 实现的锁 Service
    /// </summary>
    public class RedisLockService : LockService
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(RedisLockService));

        private readonly IDatabase redisDatabase;
        private readonly string lockKeyPrefix;
        private readonly bool enableLog;

        public RedisLockService(ApplicationConfig applicationConfig, IConfiguration configuration)
        {
            string redisUrl = configuration.GetValue<string>("App:LockRedisUrl") ?? string.Empty;
            if (ValueUtility.IsEmptyString(redisUrl))
                throw new ApplicationException("App:LockRedisUrl 配置错误");

            IConnectionMultiplexer redis = ConnectionMultiplexer.Connect(redisUrl);
            this.redisDatabase = redis.GetDatabase();     // 根据 redisUrl 中指定的数据库取数据库

            this.lockKeyPrefix = applicationConfig.GetAppName();
            this.enableLog = applicationConfig.IsAppEnvDev();
        }

        public IDisposable Lock(string key, int timeout = 10)
        {
            string lockKey = lockKeyPrefix + ":lock:" + key;
            string token = Guid.NewGuid().ToString();
            //TimeSpan? expiry = TimeSpan.FromSeconds(timeout);
            TimeSpan? expiry = null;

            var sw = Stopwatch.StartNew();
            while (sw.Elapsed.TotalSeconds < timeout)
            {
                if (redisDatabase.StringSet(lockKey, token, expiry, When.NotExists))
                {
                    if (enableLog)
                    {
                        logger.Info($"线程 {Environment.CurrentManagedThreadId} 获取 Redis 同步锁 {key} 成功");
                    }
                    return new RedisLockHandle(enableLog, redisDatabase, lockKey, token);
                }
                Thread.Sleep(200);
            }

            throw new LockException($"线程 {Environment.CurrentManagedThreadId} 获取 Redis 同步锁 {key} 失败");
        }

        public async ValueTask<IAsyncDisposable> LockAsync(string key, int timeout = 10)
        {
            string lockKey = lockKeyPrefix + ":lock:" + key;
            string token = Guid.NewGuid().ToString();
            //TimeSpan? expiry = TimeSpan.FromSeconds(timeout);
            TimeSpan? expiry = null;

            var sw = Stopwatch.StartNew();
            while (sw.Elapsed.TotalSeconds < timeout)
            {
                if (await redisDatabase.StringSetAsync(lockKey, token, expiry, When.NotExists))
                {
                    if (enableLog)
                    {
                        logger.Info($"线程 {Environment.CurrentManagedThreadId} 获取 Redis 异步锁 {key} 成功");
                    }
                    return new AsyncRedisLockHandle(enableLog, redisDatabase, lockKey, token);
                }
                await Task.Delay(200);
            }
            
            throw new LockException(
                        $"线程 {Environment.CurrentManagedThreadId} 获取 Redis 异步锁 {key} 失败"
                        );
        }

        /// <summary>
        /// Redis 同步锁处理器
        /// </summary>
        private sealed class RedisLockHandle : IDisposable
        {
            private readonly bool enableLog;
            private readonly IDatabase redisDatabase;
            private readonly string lockKey;
            private readonly string token;
            private bool isDisposed;

            public RedisLockHandle(bool enableLog, IDatabase redisDatabase, string lockKey, string token)
            {
                this.enableLog = enableLog;
                this.redisDatabase = redisDatabase;
                this.lockKey = lockKey;
                this.token = token;
            }

            public void Dispose()
            {
                if (!isDisposed)
                {
                    redisDatabase.ScriptEvaluate(@"
if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
else
    return 0
end", new RedisKey[] { lockKey }, new RedisValue[] { token });
                    isDisposed = true;
                    if (enableLog)
                    {
                        logger.Info($"线程 {Environment.CurrentManagedThreadId} 释放 Redis 同步锁 {lockKey} 成功");
                    }
                }
            }
        }

        /// <summary>
        /// Redis 异步锁处理器
        /// </summary>
        private sealed class AsyncRedisLockHandle : IAsyncDisposable
        {
            private readonly bool enableLog;
            private readonly IDatabase redisDatabase;
            private readonly string lockKey;
            private readonly string token;
            private bool isDisposed;

            public AsyncRedisLockHandle(bool enableLog, IDatabase redisDatabase, string lockKey, string token)
            {
                this.enableLog = enableLog;
                this.redisDatabase = redisDatabase;
                this.lockKey = lockKey;
                this.token = token;
            }

            public async ValueTask DisposeAsync()
            {
                if (!isDisposed)
                {
                    await redisDatabase.ScriptEvaluateAsync(@"
if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
else
    return 0
end", new RedisKey[] { lockKey }, new RedisValue[] { token });
                    isDisposed = true;
                    if (enableLog)
                    {
                        logger.Info($"线程 {Environment.CurrentManagedThreadId} 释放 Redis 异步锁 {lockKey} 成功");
                    }
                }
            }
        }
    }
}
