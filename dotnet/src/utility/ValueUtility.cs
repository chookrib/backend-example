namespace DddExample.Utility
{
    /// <summary>
    /// 值 Utility
    /// </summary>
    public class ValueUtility
    {
        /// <summary>
        /// 判断字符串是否为 null 或空字符串
        /// </summary>
        public static bool IsBlank(string value)
        {
            return string.IsNullOrWhiteSpace(value);
        }
    }
}
