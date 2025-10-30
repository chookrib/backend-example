namespace BackendExample.Application
{
    /// <summary>
    /// Id 生成器
    /// </summary>
    public class IdGenerator
    {
        /// <summary>
        /// 生成唯一Id
        /// </summary>
        public static string GenerateId()
        {
            return Guid.CreateVersion7().ToString();    // 生成 UUID v7
        }
    }
}
