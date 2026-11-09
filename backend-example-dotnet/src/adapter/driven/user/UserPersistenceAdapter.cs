using Dapper;
using src.application.user;
using src.domain.user;
using src.utility;
using System.Dynamic;

namespace src.adapter.driven.user
{
    /// <summary>
    /// 用户持久化 Adapter
    /// </summary>
    public class UserPersistenceAdapter : UserRepository, UserUniquenessValidator, UserQueryHandler
    {
        private readonly DatabaseAdapter databaseAdapter;
        private readonly string tableName;

        public UserPersistenceAdapter(DatabaseAdapter databaseAdapter)
        {
            this.databaseAdapter = databaseAdapter;
            this.tableName = "t_user" + DateTime.Now.ToString("yyyyMMddHHmmss");
            using var conn = this.databaseAdapter.GetConnection();
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
            try
            {
                return User.Reconstitute(
                    row.u_id,
                    row.u_username,
                    row.u_password,
                    row.u_nickname,
                    row.u_mobile,
                    ValueUtility.ToBoolOrThrow(row.u_is_admin.ToString()),
                    ValueUtility.ToDateTimeOrThrow(row.u_created_at.ToString()),
                    ValueUtility.ToDateTimeOrNull((string?)row.u_updated_at)
                    );
            }
            catch (Exception ex)
            {
                throw new PersistenceException("还原 User 失败", ex);
            }
        }

        public async Task Insert(User entity)
        {
            using var conn = databaseAdapter.GetConnection();
            await conn.ExecuteAsync($"""
                INSERT INTO {tableName} (
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
                updatedAt = entity.UpdatedAt?.ToString("yyyy-MM-dd HH:mm:ss") ?? string.Empty
            });
        }

        public async Task Update(User entity)
        {
            using var conn = databaseAdapter.GetConnection();
            await conn.ExecuteAsync($"""
                UPDATE {tableName}
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
                updatedAt = entity.UpdatedAt?.ToString("yyyy-MM-dd HH:mm:ss") ?? string.Empty
            });
        }

        public async Task DeleteById(string id)
        {
            if (ValueUtility.IsBlank(id))
                return;
            using var conn = databaseAdapter.GetConnection();
            await conn.ExecuteAsync($"DELETE FROM {this.tableName} WHERE u_id = @id", new { id });
        }

        public async Task<User?> FindById(string id)
        {
            if (ValueUtility.IsBlank(id))
                return null;
            using var conn = databaseAdapter.GetConnection();
            dynamic? row = await conn.QuerySingleOrDefaultAsync(
                $"SELECT * FROM {tableName} WHERE u_id = @id", new { id }
                );
            if (row == null)
                return null;
            return ToEntity(row);
        }

        public async Task<User> FindByIdOrThrow(string id)
        {
            User? entity = await FindById(id);
            if (entity == null)
                throw new PersistenceException($"用户 {id} 不存在");
            return entity;
        }

        public async Task<IList<User>> FindByIds(List<string> ids)
        {
            IList<User> list = new List<User>();
            if (ids == null || ids.Count == 0)
                return list;

            using var conn = databaseAdapter.GetConnection();
            IEnumerable<dynamic> rows = await conn.QueryAsync(
                $"SELECT * FROM {tableName} WHERE u_id IN @ids", new { ids }
                );
            foreach (dynamic row in rows)
            {
                User entity = ToEntity(row);
                list.Add(entity);
            }
            return list;
        }

        public async Task<User?> FindByUsername(string username)
        {
            if (ValueUtility.IsBlank(username))
                return null;
            using var conn = databaseAdapter.GetConnection();
            dynamic? row = await conn.QuerySingleOrDefaultAsync(
                $"SELECT * FROM {tableName} WHERE LOWER(u_username) = LOWER(@username)", new { username }
                );
            if (row == null)
                return null;
            return ToEntity(row);
        }

        //==============================================================================================================
        // UserUniqueSpecification

        public async Task<bool> IsUsernameAvailable(string username)
        {
            if (ValueUtility.IsBlank(username))
                throw new PersistenceException("无法检查 username 为空字符串时是否可用");
            using var conn = databaseAdapter.GetConnection();
            return await conn.ExecuteScalarAsync<int>(
                $"SELECT COUNT(*) FROM {tableName} WHERE LOWER(u_username) = LOWER(@username)", new { username }
            ) == 0;
        }

        public async Task<bool> IsNicknameAvailable(string nickname)
        {
            if (ValueUtility.IsBlank(nickname))
                throw new PersistenceException("无法检查 nickname 为空字符串时是否可用");
            using var conn = databaseAdapter.GetConnection();
            return await conn.ExecuteScalarAsync<int>(
                $"SELECT COUNT(*) FROM {tableName} WHERE LOWER(u_nickname) = LOWER(@nickname)", new { nickname }
                ) == 0;
        }

        public async Task<bool> IsMobileAvailable(string mobile)
        {
            if (ValueUtility.IsBlank(mobile))
                throw new PersistenceException("无法检查 mobile 为空字符串时是否可用");
            using var conn = databaseAdapter.GetConnection();
            return await conn.ExecuteScalarAsync<int>(
                $"SELECT COUNT(*) FROM {tableName} WHERE LOWER(u_mobile) = LOWER(@mobile)", new { mobile }
            ) == 0;
        }

        //==============================================================================================================
        // UserQueryHandler

        private UserDto ToDto(dynamic row)
        {
            try
            {
                return new UserDto(
                    row.u_id,
                    row.u_username,
                    //row.u_password,
                    row.u_nickname,
                    row.u_mobile,
                    ValueUtility.ToBoolOrThrow(row.u_is_admin.ToString()),
                    ValueUtility.ToDateTimeOrThrow(row.u_created_at.ToString()),
                    ValueUtility.ToDateTimeOrNull((string?)row.u_updated_at)
                    );
            }
            catch (Exception ex)
            {
                throw new PersistenceException("还原 UserDto 失败", ex);
            }
        }

        private string BuildQueryCriteriaSql(UserQueryCriteria? criteria, out dynamic @params)
        {
            @params = new ExpandoObject();

            if (criteria == null)
                return string.Empty;

            IList<string> sqls = new List<string>();
            if (!ValueUtility.IsBlank(criteria.Keyword))
            {
                sqls.Add("(u_username LIKE @keyword ESCAPE '\\' OR u_nickname LIKE @keyword ESCAPE '\\')");
                @params.keyword = databaseAdapter.EscapeLikePattern(criteria.Keyword);
            }

            if (sqls.Count > 0)
                return " WHERE " + string.Join(" AND ", sqls);

            return string.Empty;
        }

        private static readonly Dictionary<string, string> SORT_FIELD_MAP = new Dictionary<string, string>
        {
            { UserQuerySortFields.CREATED_AT, "u_created_at" },
            { UserQuerySortFields.USERNAME, "u_username" }
        };

        private string BuildQuerySortSql(OrderedDictionary<string, string>? sortOptions = null)
        {
            IList<string> sqls = new List<string>();
            if (sortOptions != null)
            {
                foreach (KeyValuePair<string, string> sortOption in sortOptions)
                {
                    if (SORT_FIELD_MAP.TryGetValue(sortOption.Key, out string? mappedField))
                    {
                        sqls.Add($"{mappedField} {sortOption.Value}");
                    }
                }
            }

            if (sqls.Count == 0)
                sqls.Add("u_created_at DESC");

            sqls.Add("u_id DESC");
            return " ORDER BY " + string.Join(", ", sqls);
        }

        public async Task<UserDto?> QueryById(string id)
        {
            if (ValueUtility.IsBlank(id))
                return null;
            using var conn = databaseAdapter.GetConnection();
            dynamic? row = await conn.QuerySingleOrDefaultAsync(
                $"SELECT * FROM {tableName} WHERE u_id = @id", new { id }
                );
            if (row == null)
                return null;
            return ToDto(row);
        }

        public async Task<UserDto> QueryByIdOrThrow(string id)
        {
            UserDto? dto = await QueryById(id);
            if (dto == null)
                throw new PersistenceException($"用户 {id} 不存在");
            return dto;
        }

        public async Task<int> QueryCount(UserQueryCriteria? criteria)
        {
            dynamic @params;
            string crieriaSql = BuildQueryCriteriaSql(criteria, out @params);
            using var conn = databaseAdapter.GetConnection();
            return await conn.ExecuteScalarAsync<int>(
                $"SELECT COUNT(*) FROM {tableName} {crieriaSql}", (object)@params
                );
        }

        public async Task<IList<UserDto>> Query(UserQueryCriteria? criteria, OrderedDictionary<string, string>? sortOptions = null)
        {
            dynamic @params;
            string crieriaSql = BuildQueryCriteriaSql(criteria, out @params);
            string sortSql = BuildQuerySortSql(sortOptions);
            using var conn = databaseAdapter.GetConnection();
            IEnumerable<dynamic> rows = await conn.QueryAsync(
                $"SELECT * FROM {tableName} {crieriaSql} {sortSql}", (object)@params
                );
            IList<UserDto> list = new List<UserDto>();
            foreach (dynamic row in rows)
            {
                UserDto dto = ToDto(row);
                list.Add(dto);
            }
            return list;
        }

        public async Task<IList<UserDto>> QueryPage(int pageNum, int pageSize, UserQueryCriteria? criteria,
            OrderedDictionary<string, string>? sortOptions = null)
        {
            dynamic @params;
            string crieriaSql = BuildQueryCriteriaSql(criteria, out @params);
            string sortSql = BuildQuerySortSql(sortOptions);
            @params.limitCount = pageSize;
            @params.limitOffset = (pageNum - 1) * pageSize;

            using var conn = databaseAdapter.GetConnection();
            IEnumerable<dynamic> rows = await conn.QueryAsync(
                $"SELECT * FROM {tableName}{crieriaSql}{sortSql} LIMIT @limitCount OFFSET @limitOffset", (object)@params
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
