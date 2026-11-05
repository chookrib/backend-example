using System.Globalization;

namespace src.utility
{
    /// <summary>
    /// 值 Utility
    /// </summary>
    public sealed class ValueUtility
    {
        /// <summary>
        /// 当值为 null 时抛出指定消息异常，否则返回值
        /// </summary>
        public static T RequireNonNull<T>(T value, string message = "值不能为 null")
        {
            if (value == null)
                throw new UtilityException(message);
            return value;
        }

        /// <summary>
        /// 当值为 null 时返回默认值，否则返回值，此方法不判断提供的默认值是否为 null
        /// </summary>
        public static T DefaultIfNull<T>(T value, T defaultValue)
        {
            return (value != null) ? value : defaultValue;
        }

        //==================================================================================================================

        /// <summary>
        /// 判断字符串是否为 null 或空字符串（包括仅含空格的字符串）
        /// </summary>
        public static bool IsBlank(string? value)
        {
            return string.IsNullOrWhiteSpace(value);
        }

        /// <summary>
        /// 当值为 null 或空字符串（包括仅含空格的字符串）时抛出指定消息异常，否则返回值
        /// </summary>
        public static string RequireNotBlank(string? value, string message = "值不能为空字符串")
        {
            if (IsBlank(value))
                throw new UtilityException(message);
            return value!;
        }

        /// <summary>
        /// 当值为 null 时返回空字符串 ""，否则返回值
        /// </summary>
        public static string EmptyIfNull(string? value)
        {
            return value != null ? value : "";
        }

        //==============================================================================================================

        // 定义 bool 值范围
        private static readonly string[] BOOL_TRUE = { "true", "1", "t", "y", "yes", "on" };
        private static readonly string[] BOOL_FALSE = { "false", "0", "f", "n", "no", "off" };

        /// <summary>
        /// 转 bool，失败返回 null
        /// </summary>
        public static bool? ToBoolOrNull(string? value)
        {
            if (IsBlank(value))
                return null;
            string b = value!.Trim().ToLower();
            if (BOOL_TRUE.Contains(b))
                return true;
            else if (BOOL_FALSE.Contains(b))
                return false;
            return null;
        }

        /// <summary>
        /// 转 bool，失败返回默认值
        /// </summary>
        public static bool ToBoolOrDefault(string? value, bool defaultValue)
        {
            bool? b = ToBoolOrNull(value);
            if (b.HasValue)
                return b.Value;
            return defaultValue;
        }

        /// <summary>
        /// 转 bool，失败抛出指定消息异常
        /// </summary>
        public static bool ToBoolOrThrow(string? value, string message = "无效的 bool 值")
        {
            bool? b = ToBoolOrNull(value);
            if (!b.HasValue)
                throw new UtilityException(message);
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
            if (int.TryParse(value!.Trim(), out int result))
                return result;
            return null;
        }

        /// <summary>
        /// 转 int，失败返回默认值
        /// </summary>
        public static int ToIntOrDefault(string? value, int defaultValue)
        {
            int? i = ToIntOrNull(value);
            if (i.HasValue)
                return i.Value;
            return defaultValue;
        }

        /// <summary>
        /// 转 int，失败抛出指定消息异常
        /// </summary>
        public static int ToIntOrThrow(string? value, string message = "无效的 int 值")
        {
            int? i = ToIntOrNull(value);
            if (!i.HasValue)
                throw new UtilityException(message);
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
            if (long.TryParse(value!.Trim(), out long result))
                return result;
            return null;
        }

        /// <summary>
        /// 转 long，失败返回默认值
        /// </summary>
        public static long ToLongOrDefault(string? value, long defaultValue)
        {
            long? l = ToLongOrNull(value);
            if (l.HasValue)
                return l.Value;
            return defaultValue;
        }

        /// <summary>
        /// 转 long，失败抛出指定消息异常
        /// </summary>
        public static long ToLongOrThrow(string? value, string message = "无效的 long 值")
        {
            long? l = ToLongOrNull(value);
            if (!l.HasValue)
                throw new UtilityException(message);
            return l.Value;
        }

        //==============================================================================================================

        // 定义 decimal 边界值 DECIMAL(28, 8)，满足绝大部分场景
        private const int DECIMAL_SCALE = 8;
        private static readonly decimal DECIMAL_MIN_VALUE = -99999999999999999999.99999999m;
        private static readonly decimal DECIMAL_MAX_VALUE = 99999999999999999999.99999999m;

        /// <summary>
        /// 转 decimal，失败返回 null
        /// </summary>
        public static decimal? ToDecimalOrNull(string? value)
        {
            if (IsBlank(value))
                return null;

            //if (decimal.TryParse(value!.Trim(), out decimal result))
            //    return result;

            // 使用不变区域性解析，保证小数点"."的绝对识别
            if (decimal.TryParse(value!.Trim(), NumberStyles.Number, CultureInfo.InvariantCulture, out decimal d))
            {
                // 规范化小数位（四舍五入：AwayFromZero 相当于 Java 的 Half-Up）
                d = Math.Round(d, DECIMAL_SCALE, MidpointRounding.AwayFromZero);
                // 检查是否超出边界
                if (d < DECIMAL_MIN_VALUE || d > DECIMAL_MAX_VALUE)
                    return null;
                return d;
            }

            return null;
        }

        /// <summary>
        /// 转 decimal，失败返回默认值
        /// </summary>
        public static decimal ToDecimalOrDefault(string? value, decimal defaultValue)
        {
            decimal? d = ToDecimalOrNull(value);
            if (d.HasValue)
                return d.Value;
            if (defaultValue < DECIMAL_MIN_VALUE || defaultValue > DECIMAL_MAX_VALUE)
                throw new UtilityException($"默认值超出 decimal 范围");
            return defaultValue;
        }

        /// <summary>
        /// 转 decimal，失败抛出指定消息异常
        /// </summary>
        public static decimal ToDecimalOrThrow(string? value, string message = "无效的 decimal 值")
        {
            decimal? d = ToDecimalOrNull(value);
            if (!d.HasValue)
                throw new UtilityException(message);
            return d.Value;
        }

        //==============================================================================================================
        // datetime 精度统一为毫秒

        /**
         * 获取当前时间，精度到毫秒
         */
        public static DateTime GetCurrentDateTime()
        {
            DateTime now = DateTime.Now;
            return new DateTime(now.Year, now.Month, now.Day, now.Hour, now.Minute, now.Second, now.Millisecond);
        }

        /// <summary>
        /// 格式化 datetime，精度到秒
        /// </summary>
        public static string FormatDateTime(DateTime value)
        {
            return value.ToString("yyyy-MM-dd HH:mm:ss");
        }

        /// <summary>
        /// 格式化 datetime，精度到毫秒
        /// </summary>
        public static string FormatDateTimeMillisecond(DateTime value)
        {
            return value.ToString("yyyy-MM-dd HH:mm:ss.fff");
        }

        private static readonly string[] DATETIME_FORMATS = {
            "yyyy-MM-dd",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.FFFFFFF" // 最多匹配7位小数秒，且去尾零
        };

        /// <summary>
        /// 转 datetime，失败返回 null
        /// </summary>
        public static DateTime? ToDateTimeOrNull(string? value)
        {
            if (IsBlank(value))
                return null;

            //if (DateTime.TryParse(value!.Trim(), out DateTime result))
            //    return result;

            string dt = value!.Trim();

            // 防御性处理：C# 最大支持7位，截断超出的部分
            int dotIndex = dt.IndexOf('.');
            if (dotIndex > 0)
            {
                int fractionLength = dt.Length - dotIndex - 1;
                if (fractionLength > 7)
                    dt = dt.Substring(0, dotIndex + 8);
            }

            if (DateTime.TryParseExact(dt, DATETIME_FORMATS, CultureInfo.InvariantCulture, DateTimeStyles.None,
                out DateTime validDateTime))
            {
                return new DateTime(validDateTime.Year, validDateTime.Month, validDateTime.Day,
                validDateTime.Hour, validDateTime.Minute, validDateTime.Second,
                validDateTime.Millisecond);
            }

            return null;
        }

        /// <summary>
        /// 转 datetime，失败返回默认值
        /// </summary>
        public static DateTime ToDateTimeOrDefault(string? value, DateTime defaultValue)
        {
            DateTime? dt = ToDateTimeOrNull(value);
            if (dt.HasValue)
                return dt.Value;
            // return defaultValue;
            return new DateTime(defaultValue.Year, defaultValue.Month, defaultValue.Day,
                defaultValue.Hour, defaultValue.Minute, defaultValue.Second,
                defaultValue.Millisecond);
        }

        /// <summary>
        /// 转 datetime，失败抛出指定消息异常
        /// </summary>
        public static DateTime ToDateTimeOrThrow(string? value, string message = "无效的 datetime 值")
        {
            DateTime? dt = ToDateTimeOrNull(value);
            if (!dt.HasValue)
                throw new UtilityException(message);
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

            //if (DateOnly.TryParse(value!.Trim(), out DateOnly date))
            //    return date;
            //if (DateTime.TryParse(value!.Trim(), out DateTime datetime))
            //    return DateOnly.FromDateTime(datetime);

            //string[] formats = {
            //    "yyyy-MM-dd",
            //    "yyyy-MM-dd HH:mm",
            //    "yyyy-MM-dd HH:mm:ss",
            //    "yyyy-MM-dd HH:mm:ss.FFFFFFF"
            //};

            //if (DateTime.TryParseExact(value!.Trim(), formats, CultureInfo.InvariantCulture, DateTimeStyles.None,
            //    out DateTime dt))
            //{
            //    return DateOnly.FromDateTime(dt);
            //}

            //return null;

            string d = value!.Trim();
            if (d.Length < 10)
                return null;
            if (DateOnly.TryParseExact(d.Substring(0, 10), "yyyy-MM-dd", out DateOnly validDate))
            {
                return validDate;
            }
            return null;
        }

        /// <summary>
        /// 转 date，失败返回默认值
        /// </summary>
        public static DateOnly ToDateOrDefault(string? value, DateOnly defaultValue)
        {
            DateOnly? d = ToDateOrNull(value);
            if (d.HasValue)
                return d.Value;
            return defaultValue;
        }

        /// <summary>
        /// 转 date，失败抛出指定消息异常
        /// </summary>
        public static DateOnly ToDateOrThrow(string? value, string message = "无效的 date 值")
        {
            DateOnly? d = ToDateOrNull(value);
            if (!d.HasValue)
                throw new UtilityException(message);
            return d.Value;
        }


        //==============================================================================================================
        // time 精度统一为秒

        /// <summary>
        /// 格式化 time，精度到秒
        /// </summary>
        public static string FormatTime(TimeOnly value)
        {
            return value.ToString("HH:mm:ss");
        }

        // 定义所有支持的格式模式
        private static readonly string[] TIME_FORMATS = new string[]
        {
            // 带日期的格式
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.FFFFFFF", // C# 支持7位小数（100纳秒精度）
            // 不带日期的格式
            "HH:mm",
            "HH:mm:ss",
            "HH:mm:ss.FFFFFFF" // C# 支持7位小数
        };

        /// <summary>
        /// 转 time，精度到秒，失败返回 null
        /// </summary>
        public static TimeOnly? ToTimeOrNull(string? value)
        {
            if (IsBlank(value))
                return null;

            //if (TimeOnly.TryParse(value!.Trim(), out TimeOnly time))
            //    return time;
            //if (DateTime.TryParse(value!.Trim(), out DateTime datetime))
            //    return TimeOnly.FromDateTime(datetime);
            //return null;

            string t = value!.Trim();

            // 防御性处理：C# 最大支持7位，截断超出的部分
            int dotIndex = t.IndexOf('.');
            if (dotIndex > 0)
            {
                int fractionLength = t.Length - dotIndex - 1;
                if (fractionLength > 7)
                    t = t.Substring(0, dotIndex + 8);
            }

            foreach (string format in TIME_FORMATS)
            {
                if (TimeOnly.TryParseExact(t, format, CultureInfo.InvariantCulture, DateTimeStyles.None,
                    out TimeOnly validTime))
                {
                    return new TimeOnly(validTime.Hour, validTime.Minute, validTime.Second);
                }
            }


            return null;
        }

        /// <summary>
        /// 转 time，精度到秒，失败返回默认值
        /// </summary>
        public static TimeOnly ToTimeOrDefault(string? value, TimeOnly defaultValue)
        {
            TimeOnly? t = ToTimeOrNull(value);
            if (t.HasValue)
                return t.Value;
            return new TimeOnly(defaultValue.Hour, defaultValue.Minute, defaultValue.Second);
        }

        /// <summary>
        /// 转 time，失败抛出指定消息异常
        /// </summary>
        public static TimeOnly ToTimeOrThrow(string? value, string message = "无效的 time 值")
        {
            TimeOnly? t = ToTimeOrNull(value);
            if (!t.HasValue)
                throw new UtilityException(message);
            return t.Value;
        }
    }
}
