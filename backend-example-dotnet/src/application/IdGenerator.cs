namespace src.application
{
    /// <summary>
    /// Id 生成器
    /// </summary>
    public class IdGenerator
    {
        /// <summary>
        /// Id 分隔符，指定一个 Id 中不存在的字符作为多个 Id 拼接分隔符
        /// </summary>
        public static readonly string SEPARATOR = ",";

        /// <summary>
        /// 生成唯一Id
        /// </summary>
        public static string GenerateId()
        {
            return Guid.CreateVersion7().ToString();    // 生成 UUID v7
        }
    }
}
