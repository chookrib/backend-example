namespace BackendExample.Domain
{
    /// <summary>
    /// 用户 Repository 接口
    /// </summary>
    public interface UserRepository
    {
        /// <summary>
        /// 插入
        /// </summary>
        Task Insert(User entity);

        /// <summary>
        /// 更新
        /// </summary>
        Task Update(User entity);

        /// <summary>
        /// 根据 id 删除
        /// </summary>
        Task DeleteById(string id);

        /// <summary>
        /// 根据 id 查询，找不到返回 null
        /// </summary>
        Task<User?> SelectById(string id);

        /// <summary>
        /// 根据 id 查询，找不到抛出异常
        /// </summary>
        Task<User> SelectByIdReq(string id);

        /// <summary>
        /// 根据 id 集合查询
        /// </summary>
        Task<IList<User>> SelectByIds(List<string> ids);

        /// <summary>
        /// 根据用户名查询，找不到返回 null
        /// </summary>
        Task<User?> SelectByUsername(string username);
    }
}
