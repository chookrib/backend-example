namespace DddExample.Domain
{
    /// <summary>
    /// 用户唯一性检查接口
    /// </summary>
    public interface UserUniqueChecker
    {
        /// <summary>
        /// 检查用户名是否唯一
        /// </summary>
        bool IsUsernameUnique(string username);

        /// <summary>
        /// 检查昵称是否唯一
        /// </summary>
        bool IsNicknameUnique(string nickname);

        /// <summary>
        /// 检查手机号是否唯一
        /// </summary>
        bool IsMobileUnique(string mobile);
    }
}
