using System.Text;

namespace src.utility
{
    /// <summary>
    /// Id Utility
    /// </summary>
    public class IdUtility
    {
        /// <summary>
        /// 生成 UUID v4
        /// </summary>
        public static string GenerateUUIDv4()
        {
            return Guid.NewGuid().ToString();
        }

        /// <summary>
        /// 生成 UUID v7
        /// </summary>
        public static string GenerateUUIDv7()
        {
            return Guid.CreateVersion7().ToString();    // 生成 UUID v7
        }

        /// <summary>
        /// Id 界定符，指定一个 Id 中不存在的字符作为多个 Id 拼接界定符
        /// </summary>
        public static readonly string DELIMITER = ",";

        /// <summary>
        /// 拼接 Id 字符串
        /// </summary>
        public static string WrapJoin(List<string> ids)
        {
            if (ids == null || !ids.Any())
                return string.Empty;

            StringBuilder sb = new StringBuilder();
            foreach (string id in ids)
            {
                if (!ValueUtility.IsEmptyString(id))
                    sb.Append(id).Append(DELIMITER);
            }

            if (sb.Length > 0)
                return DELIMITER + sb.ToString();
            else
                return string.Empty;
        }

        /// <summary>
        /// 包裹 Id 字符串
        /// </summary>
        public static string Wrap(string id)
        {
            if (ValueUtility.IsEmptyString(id))
                throw new UtilityException("不能包裹一个空的 Id");

            return DELIMITER + id + DELIMITER;
        }

        /// <summary>
        /// 拆分 Id 字符串
        /// </summary>
        public static List<string> Split(string ids)
        {
            if (ValueUtility.IsEmptyString(ids))
                return new List<string>();

            return ids.Split(DELIMITER)
                      // .Select(s => s.Trim())
                      .Where(s => !ValueUtility.IsEmptyString(s))
                      .ToList();
        }
    }
}
