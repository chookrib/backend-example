using BackendExample.Application;
using log4net;
using Microsoft.AspNetCore.Mvc;

namespace BackendExample.Adapter.Driving
{
    /// <summary>
    /// 测试锁 Controller
    /// </summary>
    public class TestLockController : ControllerBase
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(TestLockController));

        private readonly TestLockService testLockService;

        public TestLockController(TestLockService testLockService)
        {
            this.testLockService = testLockService;
        }

        /// <summary>
        /// 设置 count
        /// </summary>
        [HttpGet("/api/test/lock/set-count")]
        public Result TestLockSetCount()
        {
            int value = RequestValueHelper.GetRequestParamIntOrDefault(Request, 1, "value");
            this.testLockService.SetCount(value);
            return Result.OkData(new { count = value });
        }

        /// <summary>
        /// 同步减少 count，不加锁，会超减，需多执行几次
        /// </summary>
        [HttpGet("/api/test/lock/sync-decrease-count")]
        public Result TestLockSyncDecreaseCount()
        {
            this.testLockService.SyncDecreaseCount();
            return Result.Ok();
        }

        /// <summary>
        /// 异步减少 count，不加锁，会超减，需多执行几次
        /// </summary>
        [HttpGet("/api/test/lock/async-decrease-count")]
        public async Task<Result> TestLockAsyncDecreaseCount()
        {
            await this.testLockService.AsyncDecreaseCount();
            return Result.Ok();
        }

        /// <summary>
        /// 同步减少 count，加同步锁，不会超减
        /// </summary>
        [HttpGet("/api/test/lock/sync-decrease-count-with-sync-lock")]
        public Result TestLockSyncDecreaseCountWithSyncLock()
        {
            this.testLockService.SyncDecreaseCountWithSyncLock();
            return Result.Ok();
        }

        /// <summary>
        /// 同步减少 count，加异步锁，不会超减
        /// </summary>
        [HttpGet("/api/test/lock/sync-decrease-count-with-async-lock")]
        public async Task<Result> TestLockSyncDecreaseCountWithAsyncLock()
        {
            await this.testLockService.SyncDecreaseCountWithAsyncLock();
            return Result.Ok();
        }

        /// <summary>
        /// 异步减少 count，加同步锁，不会超减
        /// </summary>
        [HttpGet("/api/test/lock/async-decrease-count-with-sync-lock")]
        public async Task<Result> TestLockAsyncDecreaseCountWithSyncLock()
        {
            await this.testLockService.AsyncDecreaseCountWithSyncLock();
            return Result.Ok();
        }

        /// <summary>
        /// 异步减少 count，加异步锁，不会超减
        /// </summary>
        [HttpGet("/api/test/lock/async-decrease-count-with-async-lock")]
        public async Task<Result> TestLockAsyncDecreaseCountWithAsyncLock()
        {
            await this.testLockService.AsyncDecreaseCountWithAsyncLock();
            return Result.Ok();
        }

        //==============================================================================================================

        /// <summary>
        /// 同步 Tread.Sleep
        /// </summary>
        [HttpGet("/api/test/lock/thread-sleep")]
        public Result TestLockThreadSleep()
        {
            this.testLockService.ThreadSleep();
            return Result.Ok();
        }

        /// <summary>
        /// 异步 Task.Delay
        /// </summary>
        [HttpGet("/api/test/lock/async-task-delay")]
        public async Task<Result> TestLockAsyncTaskDelay()
        {
            await this.testLockService.AsyncTaskDelay();
            return Result.Ok();
        }

        /// <summary>
        /// 异步 Task.Delay，加异步锁
        /// </summary>
        [HttpGet("/api/test/lock/task-delay-with-lock-async")]
        public async Task<Result> TestLockTaskDelayWithLockAsync()
        {
            await this.testLockService.AsyncTaskDelayWithAsyncLock();
            return Result.Ok();
        }

        //==============================================================================================================

        /// <summary>
        /// 测试 LockException 异常
        /// </summary>
        [HttpGet("/api/test/lock/exception")]
        public Result TestLockException()
        {
            throw new LockException("测试 LockException 异常");
        }
    }
}
