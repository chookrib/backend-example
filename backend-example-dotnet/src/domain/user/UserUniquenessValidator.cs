namespace src.domain.user
{
    /// <summary>
    /// 用户唯一性 Validator 接口
    /// </summary>
    public interface UserUniquenessValidator
    {
        /// <summary>
        /// 用户名是否可用
        /// </summary>
        Task<bool> IsUsernameAvailable(string username);

        /// <summary>
        /// 昵称是否可用
        /// </summary>
        Task<bool> IsNicknameAvailable(string nickname);

        /// <summary>
        /// 手机号是否可用
        /// </summary>
        Task<bool> IsMobileAvailable(string mobile);
    }
}
