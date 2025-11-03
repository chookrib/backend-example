using log4net;

namespace BackendExample.Application
{
    /// <summary>
    /// 测试锁 Service
    /// </summary>
    public class TestLockService
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(TestLockService));

        private readonly LockService lockService;

        public TestLockService(LockService lockService)
        {
            this.lockService = lockService;
        }

        private int count = 0;

        /// <summary>
        /// 设置 count
        /// </summary>
        public void SetCount(int value)
        {
            this.count = value;
            logger.Info($"设置 count = {value}");
        }

        /// <summary>
        /// 同步减少 count，不加锁
        /// </summary>
        public bool SyncDecreaseCount()
        {
            int c = count;
            if (c > 0)
            {
                this.count--;
                logger.Info($"减少 count 成功: {c} - 1 = {this.count}");
                return true;
            }
            else
            {
                logger.Info($"减少 count 失败: {c}");
                return false;
            }
        }

        /// <summary>
        /// 异步减少 count，不加锁
        /// </summary>
        public async Task<bool> AsyncDecreaseCount()
        {
            int c = count;

            //await Task.Delay(new Random().Next(1000, 5000));
            //await Task.Delay(0);

            if (c > 0)
            {
                this.count--;
                logger.Info($"减少 count 成功: {c} - 1 = {this.count}");
                return true;
            }
            else
            {
                logger.Info($"减少 count 失败: {c}");
                return false;
            }

        }

        /// <summary>
        /// 同步减少 count，加同步锁
        /// </summary>
        public void SyncDecreaseCountWithSyncLock()
        {
            using (this.lockService.Lock(LockKeys.TEST))
            {
                this.SyncDecreaseCount();
            }
        }

        /// <summary>
        /// 同步减少 count，加异步锁
        /// </summary>
        public async Task SyncDecreaseCountWithAsyncLock()
        {
            await using (await this.lockService.LockAsync(LockKeys.TEST))
            {
                this.SyncDecreaseCount();
            }
        }

        /// <summary>
        /// 异步减少 count，加同步锁
        /// </summary>
        public async Task AsyncDecreaseCountWithSyncLock()
        {
            using (this.lockService.Lock(LockKeys.TEST))
            {
                await this.AsyncDecreaseCount();
            }
        }

        /// <summary>
        /// 异步减少 count，加异步锁
        /// </summary>
        public async Task AsyncDecreaseCountWithAsyncLock()
        {
            await using (await this.lockService.LockAsync(LockKeys.TEST))
            {
                //await Task.Delay(new Random().Next(1000, 5000));
                await Task.Delay(0);
                await this.AsyncDecreaseCount();
            }
        }

        //==============================================================================================================

        /// <summary>
        /// 同步 Thread.Sleep
        /// </summary>
        public void ThreadSleep()
        {
            Thread.Sleep(10 * 1000);
        }

        /// <summary>
        /// 异步 Task.Delay
        /// </summary>
        public async Task AsyncTaskDelay()
        {
            await Task.Delay(10 * 1000);
        }

        /// <summary>
        /// 异步 Task.Delay，加异步锁
        /// </summary>
        public async Task AsyncTaskDelayWithAsyncLock()
        {
            await using (await this.lockService.LockAsync(LockKeys.TEST))
            {
                await Task.Delay(10 * 1000);
            }
        }

    }
}
