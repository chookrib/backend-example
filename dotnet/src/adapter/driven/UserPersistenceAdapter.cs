using System.Dynamic;

using Dapper;

using BackendExample.Application;
using BackendExample.Domain;
using BackendExample.Utility;

using Microsoft.Data.Sqlite;

namespace BackendExample.Adapter.Driven
{
    /// <summary>
    /// 用户持久化 Adapter
    /// </summary>
    public class UserPersistenceAdapter : UserRepository, UserUniqueSpecification, UserQueryHandler
    {
        private readonly string connectionString;

        public UserPersistenceAdapter(IConfiguration configuration)
        {
            this.connectionString = configuration.GetConnectionString("SQLite") ?? string.Empty;
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            conn.Execute("""
                create table if not exists t_user
                (
                    u_id text primary key,
                    u_username text,
                    u_password text,
                    u_nickname text,
                    u_mobile text,
                    u_is_admin integer,
                    u_created_at text
                )
                """);
            conn.Execute("delete from t_user where lower(u_username) = 'admin'");
            conn.Execute($"""
                insert into t_user
                (u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at)
                values
                ('0', 'admin', '{CryptoUtility.EncodeMd5("password")}', '管理员', '', 1, datetime('now', 'localtime'))
                """);
        }

        private User ToUser(dynamic result)
        {
            return User.RestoreUser(
                result.u_id,
                result.u_username,
                result.u_password,
                result.u_nickname,
                result.u_mobile,
                ValueUtility.ToBoolOrDefault(result.u_is_admin.ToString(), false),
                ValueUtility.ToDateTimeOrDefault(result.u_created_at.ToString(), DateTime.MinValue)
                );
        }

        public async Task Insert(User entity)
        {
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            await conn.ExecuteAsync("""
                insert into t_user
                (u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at)
                values
                (@id, @username, @password, @nickname, @mobile, @isAdmin, @createdAt)
                """, new
            {
                id = entity.Id,
                username = entity.Username,
                password = entity.Password,
                nickname = entity.Nickname,
                mobile = entity.Mobile,
                isAdmin = entity.IsAdmin ? 1 : 0,
                createdAt = entity.CreatedAt.ToString("yyyy-MM-dd HH:mm:ss")
            });
        }

        public async Task Update(User entity)
        {
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            await conn.ExecuteAsync("""
                update t_user
                set
                    u_username = @username,
                    u_password = @password,
                    u_nickname = @nickname,
                    u_mobile = @mobile,
                    u_is_admin = @isAdmin,
                    u_created_at = @createdAt
                where
                    u_id = @id
                """, new
            {
                id = entity.Id,
                username = entity.Username,
                password = entity.Password,
                nickname = entity.Nickname,
                mobile = entity.Mobile,
                isAdmin = entity.IsAdmin ? 1 : 0,
                createdAt = entity.CreatedAt.ToString("yyyy-MM-dd HH:mm:ss")
            });
        }


        public async Task DeleteById(string id)
        {
            if (ValueUtility.IsBlank(id))
                return;
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            await conn.ExecuteAsync("delete from t_user where u_id = @id", new { id });
        }

        public async Task<User?> SelectById(string id)
        {
            if (ValueUtility.IsBlank(id))
                return null;
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            dynamic? result = await conn.QuerySingleOrDefaultAsync("select * from t_user where u_id = @id", new { id });
            if (result == null)
                return null;
            return ToUser(result);
        }

        public async Task<User> SelectByIdReq(string id)
        {
            User? entity = await SelectById(id);
            if (entity == null)
                throw new PersistenceException($"用户 {id} 不存在");
            return entity;
        }

        public async Task<IList<User>> SelectByIds(List<string> ids)
        {
            IList<User> list = new List<User>();
            if (ids == null || ids.Count == 0)
                return list;

            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            IEnumerable<dynamic> result = await conn.QueryAsync("select * from t_user where u_id in @ids", new { ids });
            foreach (dynamic item in result)
            {
                User entity = ToUser(item);
                list.Add(entity);
            }
            return list;
        }

        public async Task<User?> SelectByUsername(string username)
        {
            if (ValueUtility.IsBlank(username))
                return null;
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            dynamic? result = await conn.QuerySingleOrDefaultAsync("select * from t_user where u_username = @username",
                new { username });
            if (result == null)
                return null;
            return ToUser(result);
        }

        //==============================================================================================================
        // UserUniqueSpecification

        public async Task<bool> IsUsernameUnique(string username)
        {
            if (ValueUtility.IsBlank(username))
                throw new PersistenceException("参数 username 不能为空");
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            return await conn.ExecuteScalarAsync<int>(
                "select count(1) from t_user where lower(u_username) = lower(@username)",
                new { username }
                ) == 0;
        }

        public async Task<bool> IsNicknameUnique(string nickname)
        {
            if (ValueUtility.IsBlank(nickname))
                throw new PersistenceException("参数 nickname 不能为空");
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            return await conn.ExecuteScalarAsync<int>(
                "select count(1) from t_user where lower(u_nickname) = lower(@nickname)",
                new { nickname }
                ) == 0;
        }

        public async Task<bool> IsMobileUnique(string mobile)
        {
            if (ValueUtility.IsBlank(mobile))
                throw new PersistenceException("参数 mobile 不能为空");
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            return await conn.ExecuteScalarAsync<int>(
                "select count(1) from t_user where lower(u_mobile) = lower(@mobile)",
                new { mobile }
                ) == 0;
        }

        //==============================================================================================================
        // UserQueryHandler

        private UserDto ToUserDto(dynamic result)
        {
            return new UserDto(
                result.u_id,
                result.u_username,
                //result.u_password,
                result.u_nickname,
                result.u_mobile,
                ValueUtility.ToBoolOrDefault(result.u_is_admin.ToString(), false),
                ValueUtility.ToDateTimeOrDefault(result.u_created_at.ToString(), DateTime.MinValue)
                );
        }

        private string BuildQueryCriteria(UserQueryCriteria? criteria, out dynamic @params)
        {
            @params = new ExpandoObject();

            if (criteria == null)
                return string.Empty;

            IList<string> sqls = new List<string>();
            if (!ValueUtility.IsBlank(criteria.Keyword))
            {
                sqls.Add("u_username like @Keyword or u_nickname like @keyword");
                @params.keyword = criteria.Keyword;
            }

            if (sqls.Count > 0)
                return " where " + string.Join(" and ", sqls);

            return string.Empty;
        }

        private string BuildQuerySort(params UserQuerySort[] sorts)
        {
            IList<string> sqls = new List<string>();
            foreach (UserQuerySort sort in sorts)
            {
                switch (sort)
                {
                    case UserQuerySort.CREATED_AT_ASC:
                        sqls.Add("u_created_at asc");
                        break;
                    case UserQuerySort.CREATED_AT_DESC:
                        sqls.Add("u_created_at desc");
                        break;
                    case UserQuerySort.USERNAME_ASC:
                        sqls.Add("u_username asc");
                        break;
                    case UserQuerySort.USERNAME_DESC:
                        sqls.Add("u_username desc");
                        break;
                }
            }

            if (sqls.Count == 0)
                sqls.Add("u_created_at desc");

            return " order by " + string.Join(", ", sqls);
        }

        public async Task<UserDto?> QueryById(string id)
        {
            if (ValueUtility.IsBlank(id))
                return null;
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            dynamic? result = await conn.QuerySingleOrDefaultAsync("select * from t_user where u_id = @id", new { id });
            if (result == null)
                return null;
            return ToUserDto(result);
        }

        public async Task<UserDto> QueryByIdReq(string id)
        {
            UserDto? dto = await QueryById(id);
            if (dto == null)
                throw new PersistenceException($"用户 {id} 不存在");
            return dto;
        }

        public async Task<int> QueryCount(UserQueryCriteria? criteria)
        {
            dynamic @params;
            string crieriaSql = BuildQueryCriteria(criteria, out @params);
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            return await conn.ExecuteScalarAsync<int>("select count(*) from t_user" + crieriaSql, (object)@params);
        }

        public async Task<IList<UserDto>> Query(UserQueryCriteria? criteria, params UserQuerySort[] sorts)
        {
            dynamic @params;
            string crieriaSql = BuildQueryCriteria(criteria, out @params);
            string sortSql = BuildQuerySort(sorts);
            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            IEnumerable<dynamic> result = await conn.QueryAsync("select * from t_user" + crieriaSql + sortSql, (object)@params);
            IList<UserDto> list = new List<UserDto>();
            foreach (dynamic item in result)
            {
                UserDto dto = ToUserDto(item);
                list.Add(dto);
            }
            return list;
        }

        public async Task<IList<UserDto>> QueryByPage(int pageNum, int pageSize, UserQueryCriteria? criteria,
            params UserQuerySort[] sorts)
        {
            dynamic @params;
            string crieriaSql = BuildQueryCriteria(criteria, out @params);
            string sortSql = BuildQuerySort(sorts);
            @params.limitCount = pageSize;
            @params.limitOffset = (pageNum - 1) * pageSize;

            using var conn = new SqliteConnection(this.connectionString);
            conn.Open();
            IEnumerable<dynamic> result = await conn.QueryAsync(
                "select * from t_user" + crieriaSql + sortSql + "limit @limitCount offset @limitOffset",
                (object)@params
                );
            IList<UserDto> list = new List<UserDto>();
            foreach (dynamic item in result)
            {
                UserDto dto = ToUserDto(item);
                list.Add(dto);
            }
            return list;
        }
    }
}
