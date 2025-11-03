namespace BackendExample.Application
{
    /// <summary>
    /// 锁 Service 接口
    /// </summary>
    public interface LockService
    {
        ///// <summary>
        ///// 使用锁执行一个带返回值的操作
        ///// 参数 func 可以传入 async 且能正常运行，但不要这样使用，异步方法请使用 ExecuteWithLockAsync 方法
        ///// </summary>
        //T GetWithLock<T>(string key, Func<T> func, int timeout = 30);

        ///// <summary>
        ///// 使用锁执行一个不带返回值的操作
        ///// </summary>
        //void RunWithLock(string key, Action action, int timeout = 30);

        ///// <summary>
        ///// 使用锁执行一个带返回值的操作，异步
        ///// </summary>
        //Task<T> GetWithLockAsync<T>(string key, Func<Task<T>> func, int timeout = 30);

        ///// <summary>
        ///// 使用锁执行一个不带返回值的操作，异步
        ///// </summary>
        //Task RunWithLockAsync(string key, Func<Task> func, int timeout = 30);


        IDisposable Lock(string key, int timeout = 10);


        ValueTask<IAsyncDisposable> LockAsync(string key, int timeout = 10);
    }
}
