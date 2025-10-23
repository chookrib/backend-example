namespace DddExample.Application
{
    /// <summary>
    /// 锁 Service 接口
    /// </summary>
    public interface LockService
    {
        /// <summary>
        /// 使用锁执行一个带返回值的操作
        /// </summary>
        T ExecuteWithLock<T>(string key, Func<T> func);

        /// <summary>
        /// 使用锁执行一个不带返回值的操作
        /// </summary>
        void ExecuteWithLock(string key, Action action);
    }
}
