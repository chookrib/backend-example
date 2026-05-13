using System.Dynamic;

using BackendExample.Application;
using BackendExample.Domain;
using BackendExample.Utility;

using Dapper;

using Microsoft.Data.Sqlite;

namespace BackendExample.Adapter.Driven
{
    /// <summary>
    /// 用户持久化 Adapter
    /// </summary>
    public class UserPersistenceAdapter : UserRepository, UserUniqueSpecification, UserQueryHandler
    {
        private readonly SQLiteAdapter sqliteAdapter;
        private readonly string tableName;

        public UserPersistenceAdapter(SQLiteAdapter sqliteAdapter)
        {
            this.sqliteAdapter = sqliteAdapter;
            this.tableName = "t_user" + DateTime.Now.ToString("yyyyMMddHHmmss");
            using var conn = this.sqliteAdapter.GetConnection();
            conn.Execute($"""
                CREATE TABLE IF NOT EXISTS {this.tableName}
                (
                    u_id TEXT PRIMARY KEY,
                    u_username TEXT,
                    u_password TEXT,
                    u_nickname TEXT,
                    u_mobile TEXT,
                    u_is_admin INTEGER,
                    u_created_at TEXT,
                    u_updated_at TEXT
                )
                """);
            conn.Execute($"DELETE FROM {this.tableName} WHERE LOWER(u_username) = 'admin'");
            conn.Execute($"""
                INSERT INTO {this.tableName} (
                    u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at, u_updated_at
                ) VALUES (
                    '0', 'admin', '{CryptoUtility.Md5Encode("password")}', '管理员', '', 1, DATETIME('now', 'localtime'), DATETIME('now', 'localtime')
                )
                """);
        }

        private User ToEntity(dynamic row)
        {
            return User.Restore(
                row.u_id,
                row.u_username,
                row.u_password,
                row.u_nickname,
                row.u_mobile,
                ValueUtility.ToBoolOrDefault(row.u_is_admin.ToString(), false),
                ValueUtility.ToDateTimeOrDefault(row.u_created_at.ToString(), DateTime.MinValue),
                ValueUtility.ToDateTimeOrDefault(row.u_updated_at.ToString(), DateTime.MinValue)
                );
        }

        public async Task Insert(User entity)
        {
            using var conn = this.sqliteAdapter.GetConnection();
            await conn.ExecuteAsync($"""
                INSERT INTO {this.tableName} (
                    u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at, u_updated_at
                ) VALUES (
                    @id, @username, @password, @nickname, @mobile, @isAdmin, @createdAt, @updatedAt
                )
                """, new
            {
                id = entity.Id,
                username = entity.Username,
                password = entity.Password,
                nickname = entity.Nickname,
                mobile = entity.Mobile,
                isAdmin = entity.IsAdmin ? 1 : 0,
                createdAt = entity.CreatedAt.ToString("yyyy-MM-dd HH:mm:ss"),
                updatedAt = entity.UpdatedAt?.ToString("yyyy-MM-dd HH:mm:ss")
            });
        }

        public async Task Update(User entity)
        {
            using var conn = this.sqliteAdapter.GetConnection();
            await conn.ExecuteAsync($"""
                UPDATE {this.tableName}
                    SET
                        u_username = @username,
                        u_password = @password,
                        u_nickname = @nickname,
                        u_mobile = @mobile,
                        u_is_admin = @isAdmin,
                        u_created_at = @createdAt,
                        u_updated_at = @updatedAt
                WHERE
                    u_id = @id
                """, new
            {
                id = entity.Id,
                username = entity.Username,
                password = entity.Password,
                nickname = entity.Nickname,
                mobile = entity.Mobile,
                isAdmin = entity.IsAdmin ? 1 : 0,
                createdAt = entity.CreatedAt.ToString("yyyy-MM-dd HH:mm:ss"),
                updatedAt = entity.UpdatedAt?.ToString("yyyy-MM-dd HH:mm:ss")
            });
        }

        public async Task DeleteById(string id)
        {
            if (ValueUtility.IsEmptyString(id))
                return;
            using var conn = this.sqliteAdapter.GetConnection();
            await conn.ExecuteAsync($"DELETE FROM {this.tableName} WHERE u_id = @id", new { id });
        }

        public async Task<User?> SelectById(string id)
        {
            if (ValueUtility.IsEmptyString(id))
                return null;
            using var conn = this.sqliteAdapter.GetConnection();
            dynamic? row = await conn.QuerySingleOrDefaultAsync(
                $"SELECT * FROM {this.tableName} WHERE u_id = @id", new { id }
                );
            if (row == null)
                return null;
            return ToEntity(row);
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

            using var conn = this.sqliteAdapter.GetConnection();
            IEnumerable<dynamic> rows = await conn.QueryAsync(
                $"SELECT * FROM {this.tableName} WHERE u_id IN @ids", new { ids }
                );
            foreach (dynamic row in rows)
            {
                User entity = ToEntity(row);
                list.Add(entity);
            }
            return list;
        }

        public async Task<User?> SelectByUsername(string username)
        {
            if (ValueUtility.IsEmptyString(username))
                return null;
            using var conn = this.sqliteAdapter.GetConnection();
            dynamic? row = await conn.QuerySingleOrDefaultAsync(
                $"SELECT * FROM {this.tableName} WHERE u_username = @username", new { username }
                );
            if (row == null)
                return null;
            return ToEntity(row);
        }

        //==============================================================================================================
        // UserUniqueSpecification

        public async Task<bool> IsUsernameUnique(string username)
        {
            if (ValueUtility.IsEmptyString(username))
                throw new PersistenceException("参数 username 不能为空");
            using var conn = this.sqliteAdapter.GetConnection();
            return await conn.ExecuteScalarAsync<int>(
                $"SELECT COUNT(*) FROM {this.tableName} WHERE LOWER(u_username) = LOWER(@username)", new { username }
            ) == 0;
        }

        public async Task<bool> IsNicknameUnique(string nickname)
        {
            if (ValueUtility.IsEmptyString(nickname))
                throw new PersistenceException("参数 nickname 不能为空");
            using var conn = this.sqliteAdapter.GetConnection();
            return await conn.ExecuteScalarAsync<int>(
                $"SELECT COUNT(*) FROM {this.tableName} WHERE LOWER(u_nickname) = LOWER(@nickname)", new { nickname }
                ) == 0;
        }

        public async Task<bool> IsMobileUnique(string mobile)
        {
            if (ValueUtility.IsEmptyString(mobile))
                throw new PersistenceException("参数 mobile 不能为空");
            using var conn = this.sqliteAdapter.GetConnection();
            return await conn.ExecuteScalarAsync<int>(
                $"SELECT COUNT(*) FROM {this.tableName} WHERE LOWER(u_mobile) = LOWER(@mobile)", new { mobile }
            ) == 0;
        }

        //==============================================================================================================
        // UserQueryHandler

        private UserDto ToDto(dynamic row)
        {
            return new UserDto(
                row.u_id,
                row.u_username,
                //row.u_password,
                row.u_nickname,
                row.u_mobile,
                ValueUtility.ToBoolOrDefault(row.u_is_admin.ToString(), false),
                ValueUtility.ToDateTimeOrDefault(row.u_created_at.ToString(), DateTime.MinValue),
                ValueUtility.ToDateTimeOrDefault(row.u_updated_at.ToString(), DateTime.MinValue)
                );
        }

        private string BuildQueryCriteria(UserQueryCriteria? criteria, out dynamic @params)
        {
            @params = new ExpandoObject();

            if (criteria == null)
                return string.Empty;

            IList<string> sqls = new List<string>();
            if (!ValueUtility.IsEmptyString(criteria.Keyword))
            {
                sqls.Add("u_username LIKE @Keyword ESCAPE '\\' OR u_nickname LIKE @keyword ESCAPE '\\'");
                @params.keyword = this.sqliteAdapter.EscapeLikePattern(criteria.Keyword);
            }

            if (sqls.Count > 0)
                return " WHERE " + string.Join(" AND ", sqls);

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
                        sqls.Add("u_created_at ASC");
                        break;
                    case UserQuerySort.CREATED_AT_DESC:
                        sqls.Add("u_created_at DESC");
                        break;
                    case UserQuerySort.USERNAME_ASC:
                        sqls.Add("u_username ASC");
                        break;
                    case UserQuerySort.USERNAME_DESC:
                        sqls.Add("u_username DESC");
                        break;
                }
            }

            if (sqls.Count == 0)
                sqls.Add("u_created_at DESC");

            return " ORDER BY " + string.Join(", ", sqls);
        }

        public async Task<UserDto?> QueryById(string id)
        {
            if (ValueUtility.IsEmptyString(id))
                return null;
            using var conn = this.sqliteAdapter.GetConnection();
            dynamic? row = await conn.QuerySingleOrDefaultAsync(
                $"SELECT * FROM {this.tableName} WHERE u_id = @id", new { id }
                );
            if (row == null)
                return null;
            return ToDto(row);
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
            using var conn = this.sqliteAdapter.GetConnection();
            return await conn.ExecuteScalarAsync<int>(
                $"SELECT COUNT(*) FROM {this.tableName} {crieriaSql}", (object)@params
                );
        }

        public async Task<IList<UserDto>> Query(UserQueryCriteria? criteria, params UserQuerySort[] sorts)
        {
            dynamic @params;
            string crieriaSql = BuildQueryCriteria(criteria, out @params);
            string sortSql = BuildQuerySort(sorts);
            using var conn = this.sqliteAdapter.GetConnection();
            IEnumerable<dynamic> rows = await conn.QueryAsync(
                $"SELECT * FROM {this.tableName} {crieriaSql} {sortSql}", (object)@params
                );
            IList<UserDto> list = new List<UserDto>();
            foreach (dynamic row in rows)
            {
                UserDto dto = ToDto(row);
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

            using var conn = this.sqliteAdapter.GetConnection();
            IEnumerable<dynamic> rows = await conn.QueryAsync(
                $"SELECT * FROM {this.tableName}{crieriaSql}{sortSql} LIMIT @limitCount OFFSET @limitOffset", (object)@params
                );
            IList<UserDto> list = new List<UserDto>();
            foreach (dynamic row in rows)
            {
                UserDto dto = ToDto(row);
                list.Add(dto);
            }
            return list;
        }
    }
}
