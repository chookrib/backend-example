using BackendExample.Utility;

using Microsoft.Data.Sqlite;

namespace BackendExample.Adapter.Driven
{
    /// <summary>
    /// SQLite 数据库 Adapter
    /// </summary>
    public class SQLiteAdapter
    {
        private readonly string connectionString;

        public SQLiteAdapter(IConfiguration configuration)
        {
            this.connectionString = configuration.GetConnectionString("SQLite") ?? string.Empty;
            if (ValueUtility.IsEmptyString(this.connectionString))
                throw new PersistenceException("ConnectionString:SQLite 配置错误");
        }

        /// <summary>
        /// 获取数据库连接
        /// </summary>
        public SqliteConnection GetConnection()
        {
            return new SqliteConnection(this.connectionString);
        }

        /// <summary>
        /// 将 % 和 _ 转义为 \% 和 \_
        /// LIKE 查询指定 ESCAPE '\'  \ 本身也需要转义
        /// </summary>
        /// <param name="pattern"></param>
        /// <returns></returns>
        public string EscapeLikePattern(string pattern)
        {
            pattern = pattern.Replace("\\", "\\\\");
            pattern = pattern.Replace("%", "\\%");
            pattern = pattern.Replace("_", "\\_");
            return $"%{pattern}%";
        }
    }
}
