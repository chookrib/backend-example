namespace src.application.user
{
    /// <summary>
    /// 用户排序字段
    /// </summary>
    public class UserQuerySortFields
    {
        public static readonly string CREATED_AT = "createdAt";     // 创建时间
        public static readonly string USERNAME = "username";       // 用户名

        public static readonly IList<string> ALL = new List<string> { CREATED_AT, USERNAME };
    }
}
