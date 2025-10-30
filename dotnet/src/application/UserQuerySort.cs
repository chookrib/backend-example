namespace BackendExample.Application
{
    /// <summary>
    /// 用户 Query Sort
    /// </summary>
    public enum UserQuerySort
    {
        /// <summary>
        /// 创建时间升序
        /// </summary>
        CREATED_AT_ASC = 1,
        /// <summary>
        /// 创建时间降序
        /// </summary>
        CREATED_AT_DESC = -1,
        /// <summary>
        /// 用户名升序
        /// </summary>
        USERNAME_ASC = 2,
        /// <summary>
        /// 用户名降序
        /// </summary>
        USERNAME_DESC = -2
    }
}
