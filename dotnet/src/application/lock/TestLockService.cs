using log4net;

namespace DddExample.Application
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
        /// 减少 count，不加锁
        /// </summary>
        public bool DecreaseCount()
        {
            int c = count;
            if(c > 0)
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
        /// 减少 count，加锁
        /// </summary>
        public void DecreaseCountWithLock()
        {
           this.lockService.RunWithLock(LockKeys.TEST, () =>
           {
               DecreaseCount();
           });
        }

        /// <summary>
        /// 减少 count，加锁，异步
        /// </summary>
        public async Task DecreaseCountWithLockAsync()
        {
            await this.lockService.GetWithLockAsync(LockKeys.TEST, async () =>
            {
                //await Task.Delay(new Random().Next(1000, 5000));
                await Task.Delay(0);
                return DecreaseCount();
            });
        }

        //==============================================================================================================

        /// <summary>
        /// Thread.Sleep
        /// </summary>
        public void ThreadSleep()
        {
            Thread.Sleep(10 * 1000);
        }

        /// <summary>
        /// Task.Delay
        /// </summary>
        public async Task TaskDelayAsync()
        {
            await Task.Delay(10 * 1000);
        }

        /// <summary>
        /// Task.Delay，加锁，异步
        /// </summary>
        public async Task TaskDelayWithLockAsync()
        {
            await this.lockService.RunWithLockAsync(LockKeys.TEST, async () =>
            {
                await Task.Delay(10* 1000);
            });
        }

    }
}
