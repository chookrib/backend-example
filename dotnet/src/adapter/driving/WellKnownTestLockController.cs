using DddExample.Application;
using log4net;
using Microsoft.AspNetCore.Mvc;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 测试锁 Well Known Controller
    /// </summary>
    public class WellKnownTestLockController : ControllerBase
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(WellKnownTestLockController));

        private readonly TestLockService testLockService;

        public WellKnownTestLockController(TestLockService testLockService)
        {
            this.testLockService = testLockService;
        }

        /// <summary>
        /// 扣减测试，不加锁
        /// </summary>
        [HttpGet("/.well-known/test/lock/reduce-unsafe")]
        public Result TestLockReduceUnsafe()
        {
            this.testLockService.ReduceUnsafe();
            return Result.Ok();
        }

        /// <summary>
        /// 扣减测试，加锁
        /// </summary>
        [HttpGet("/.well-known/test/lock/reduce-safe")]
        public Result TestLockReduceSafe()
        {
            this.testLockService.ReduceSafe();
            return Result.Ok();
        }

        /// <summary>
        /// 加锁等待测试
        /// </summary>
        [HttpGet("/.well-known/test/lock/sleep")]
        public Result TestLockSleep()
        {
            this.testLockService.LockSleep();
            return Result.Ok();
        }
    }
}
