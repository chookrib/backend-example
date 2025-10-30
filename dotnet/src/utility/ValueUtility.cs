namespace BackendExample.Utility
{
    /// <summary>
    /// 值 Utility
    /// </summary>
    public class ValueUtility
    {
        /// <summary>
        /// 判断字符串是否为 null 或空字符串
        /// </summary>
        public static bool IsBlank(string? value)
        {
            return string.IsNullOrWhiteSpace(value);
        }

        //==============================================================================================================

        /// <summary>
        /// 转 bool，失败返回 null
        /// </summary>
        public static bool? ToBoolOrNull(string? value)
        {
            if (IsBlank(value))
                return null;
            if (new List<string>() { "true", "1", "t", "y", "yes", "on" }.Contains(value!.ToLower()))
                return true;
            else if (new List<string>() { "false", "0", "f", "n", "no", "off" }.Contains(value.ToLower()))
                return false;
            return null;
        }

        /// <summary>
        /// 转 bool，失败返回默认值
        /// </summary>
        public static bool ToBoolOrDefault(string? value, bool defaultValue)
        {
            bool? b = ToBoolOrNull(value);
            if (!b.HasValue)
                return defaultValue;
            return b.Value;
        }

        //==============================================================================================================

        /// <summary>
        /// 转 int，失败返回 null
        /// </summary>
        public static int? ToIntOrNull(string? value)
        {
            if (IsBlank(value))
                return null;
            if (int.TryParse(value, out int result))
                return result;
            return null;
        }

        /// <summary>
        /// 转 int，失败返回默认值
        /// </summary>
        public static int ToIntOrDefault(string? value, int defaultValue)
        {
            int? i = ToIntOrNull(value);
            if (!i.HasValue)
                return defaultValue;
            return i.Value;
        }

        //==============================================================================================================

        /// <summary>
        /// 转 long，失败返回 null
        /// </summary>
        public static long? ToLongOrNull(string? value)
        {
            if (IsBlank(value))
                return null;
            if (long.TryParse(value, out long result))
                return result;
            return null;
        }

        /// <summary>
        /// 转 long，失败返回默认值
        /// </summary>
        public static long ToLongOrDefault(string? value, long defaultValue)
        {
            long? l = ToLongOrNull(value);
            if (!l.HasValue)
                return defaultValue;
            return l.Value;
        }

        //==============================================================================================================

        /// <summary>
        /// 转 decimal，失败返回 null
        /// </summary>
        public static decimal? ToDecimalOrNull(string? value)
        {
            if (IsBlank(value))
                return null;
            if (decimal.TryParse(value, out decimal result))
                return result;
            return null;
        }

        /// <summary>
        /// 转 decimal，失败返回默认值
        /// </summary>
        public static decimal ToDecimalOrDefault(string? value, decimal defaultValue)
        {
            decimal? d = ToDecimalOrNull(value);
            if (!d.HasValue)
                return defaultValue;
            return d.Value;
        }

        //==============================================================================================================

        /// <summary>
        /// 格式化 datetime
        /// </summary>
        public static string FormatDateTime(DateTime value)
        {
            return value.ToString("yyyy-MM-dd HH:mm:ss");
        }

        /// <summary>
        /// 转 datetime，失败返回 null
        /// </summary>
        public static DateTime? ToDateTimeOrNull(string? value)
        {
            if (IsBlank(value))
                return null;
            if (DateTime.TryParse(value, out DateTime result))
                return result;
            return null;
        }

        /// <summary>
        /// 转 datetime，失败返回默认值
        /// </summary>
        public static DateTime ToDateTimeOrDefault(string? value, DateTime defaultValue)
        {
            DateTime? dt = ToDateTimeOrNull(value);
            if (!dt.HasValue)
                return defaultValue;
            return dt.Value;
        }

        //==============================================================================================================

        /// <summary>
        /// 格式化 date
        /// </summary>
        public static string FormatDate(DateOnly value)
        {
            return value.ToString("yyyy-MM-dd");
        }

        /// <summary>
        /// 转 date，失败返回 null
        /// </summary>
        public static DateOnly? ToDateOrNull(string? value)
        {
            if (IsBlank(value))
                return null;
            if (DateOnly.TryParse(value, out DateOnly result))
                return result;
            return null;
        }

        /// <summary>
        /// 转 date，失败返回默认值
        /// </summary>
        public static DateOnly ToDateOrDefault(string? value, DateOnly defaultValue)
        {
            DateOnly? d = ToDateOrNull(value);
            if (!d.HasValue)
                return defaultValue;
            return d.Value;
        }

        //==============================================================================================================

        /// <summary>
        /// 格式化 time
        /// </summary>
        public static string FormatTime(TimeOnly value)
        {
            return value.ToString("HH:mm:ss");
        }

        /// <summary>
        /// 转 time，失败返回 null
        /// </summary>
        public static TimeOnly? ToTimeOrNull(string? value)
        {
            if (IsBlank(value))
                return null;
            if (TimeOnly.TryParse(value, out TimeOnly result))
                return result;
            return null;
        }

        /// <summary>
        /// 转 time，失败返回默认值
        /// </summary>
        public static TimeOnly ToTimeOrDefault(string? value, TimeOnly defaultValue)
        {
            TimeOnly? t = ToTimeOrNull(value);
            if (!t.HasValue)
                return defaultValue;
            return t.Value;
        }
    }
}
