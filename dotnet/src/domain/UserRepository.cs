namespace DddExample.Domain
{
    /// <summary>
    /// 用户 Repository 接口
    /// </summary>
    public interface UserRepository
    {
        /// <summary>
        /// 插入
        /// </summary>
        void Insert(User entity);

        /// <summary>
        /// 更新
        /// </summary>
        void Update(User entity);

        /// <summary>
        /// 根据 id 删除
        /// </summary>
        void DeleteById(String id);

        /// <summary>
        /// 根据 id 查询，找不到返回 null
        /// </summary>
        User? SelectById(String id);

        /// <summary>
        /// 根据 id 查询，找不到抛出异常
        /// </summary>
        User SelectByIdReq(String id);

        /// <summary>
        /// 根据 id 集合查询
        /// </summary>
        IList<User> SelectByIds(List<String> ids);

        /// <summary>
        /// 根据用户名查询，找不到返回 null
        /// </summary>
        User? SelectByUsername(String username);
    }
}
