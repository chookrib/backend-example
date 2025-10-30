using BackendExample.Application;
using log4net;
using Microsoft.AspNetCore.Mvc;

namespace BackendExample.Adapter.Driving
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
        /// 设置 count
        /// </summary>
        [HttpGet("/.well-known/test/lock/set-count")]
        public Result TestLockSetCount()
        {
            int value = RequestValueHelper.GetRequestParamInt(Request, 1, "value");
            this.testLockService.SetCount(value);
            return Result.OkData(new { count = value });
        }

        /// <summary>
        /// 减少 count，不加锁
        /// </summary>
        [HttpGet("/.well-known/test/lock/decrease-count")]
        public Result TestLockDecreaseCount()
        {
            this.testLockService.DecreaseCount();
            return Result.Ok();
        }

        /// <summary>
        /// 减少 count，加锁
        /// </summary>
        [HttpGet("/.well-known/test/lock/decrease-count-with-lock")]
        public Result TestLockDecreaseCountWithLock()
        {
            this.testLockService.DecreaseCountWithLock();
            return Result.Ok();
        }

        /// <summary>
        /// 减少 count，加锁，异步
        /// </summary>
        [HttpGet("/.well-known/test/lock/decrease-count-with-lock-async")]
        public async Task<Result> TestLockDecreaseCountWithLockAsync()
        {
            await this.testLockService.DecreaseCountWithLockAsync();
            return Result.Ok();
        }

        //==============================================================================================================

        /// <summary>
        /// Tread.Sleep
        /// </summary>
        [HttpGet("/.well-known/test/lock/thread-sleep")]
        public Result TestLockThreadSleep()
        {
            this.testLockService.ThreadSleep();
            return Result.Ok();
        }

        /// <summary>
        /// Task.Delay，异步
        /// </summary>
        [HttpGet("/.well-known/test/lock/task-delay-async")]
        public async Task<Result> TestLockTaskDelayAsync()
        {
            await this.testLockService.TaskDelayAsync();
            return Result.Ok();
        }

        /// <summary>
        /// Task.Delay，加锁，异步
        /// </summary>
        [HttpGet("/.well-known/test/lock/task-delay-with-lock-async")]
        public async Task<Result> TestLockTaskDelayWithLockAsync()
        {
            await this.testLockService.TaskDelayWithLockAsync();
            return Result.Ok();
        }
    }
}
