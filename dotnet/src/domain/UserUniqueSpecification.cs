namespace DddExample.Domain
{
    /// <summary>
    /// 用户唯一性 Specification 接口
    /// </summary>
    public interface UserUniqueSpecification
    {
        /// <summary>
        /// 用户名是否唯一
        /// </summary>
        bool IsUsernameUnique(string username);

        /// <summary>
        /// 昵称是否唯一
        /// </summary>
        bool IsNicknameUnique(string nickname);

        /// <summary>
        /// 手机号是否唯一
        /// </summary>
        bool IsMobileUnique(string mobile);
    }
}
