namespace DddExample.Application
{
    /// <summary>
    /// 测试锁 Service
    /// </summary>
    public class TestLockService
    {
        private readonly LockService lockService;

        public TestLockService(LockService lockService)
        {
            this.lockService = lockService;
        }

        private int count = 10;

        /// <summary>
        /// 扣减测试，不加锁
        /// </summary>
        public void ReduceUnsafe()
        {
            if(count > 0)
            {
                count--;
                Console.WriteLine($"扣减后 count = {count}");
            }
            else
            {
                Console.WriteLine($"无法扣减 count = {count}");
            }
        }

        /// <summary>
        /// 扣减测试，加锁
        /// </summary>
        public void ReduceSafe()
        {
           this.lockService.ExecuteWithLock(LockKeys.TEST, () =>
           {
               ReduceUnsafe();
           });
        }

        /// <summary>
        /// 加锁等待测试
        /// </summary>
        public void LockSleep()
        {
            Thread.Sleep(10 * 1000);
        }

    }
}
