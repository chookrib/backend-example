namespace DddExample.Application
{
    /// <summary>
    /// 用户 Query Handler 接口
    /// </summary>
    public interface UserQueryHandler
    {
        /// <summary>
        /// 根据 id 查询，找不到返回 null
        /// </summary>
        UserDto? QueryById(string id);

        /// <summary>
        /// 根据 id 查询，找不到抛出异常
        /// </summary>
        UserDto QueryByIdReq(string id);

        /// <summary>
        /// 查询记录数
        /// </summary>
        int QueryCount(UserQueryCriteria? criteria);

        /// <summary>
        /// 查询
        /// </summary>
        IList<UserDto> Query(UserQueryCriteria? criteria, params UserQuerySort[] sorts);

        /// <summary>
        /// 分页查询
        /// </summary>
        IList<UserDto> QueryByPage(int pageNum, int pageSize, UserQueryCriteria? criteria, params UserQuerySort[] sorts);
    }
}
