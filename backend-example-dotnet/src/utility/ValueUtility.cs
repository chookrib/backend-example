using System.Globalization;

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
        public static bool IsEmptyString(string? value)
        {
            return string.IsNullOrWhiteSpace(value);
        }

        //==============================================================================================================

        /// <summary>
        /// 转 bool，失败返回 null
        /// </summary>
        public static bool? ToBoolOrNull(string? value)
        {
            if (IsEmptyString(value))
                return null;
            if (new List<string>() { "true", "1", "t", "y", "yes", "on" }.Contains(value!.Trim().ToLower()))
                return true;
            else if (new List<string>() { "false", "0", "f", "n", "no", "off" }.Contains(value!.Trim().ToLower()))
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

        //==============================================================================================================

        /// <summary>
        /// 转 int，失败返回 null
        /// </summary>
        public static int? ToIntOrNull(string? value)
        {
            if (IsEmptyString(value))
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

        //==============================================================================================================

        /// <summary>
        /// 转 long，失败返回 null
        /// </summary>
        public static long? ToLongOrNull(string? value)
        {
            if (IsEmptyString(value))
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
            if (IsEmptyString(value))
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

        //==============================================================================================================
        // datetime 精度统一为毫秒

        /**
         * 获取当前时间，精度到毫秒
         */
        public static DateTime GetDateTimeNow()
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
            if (IsEmptyString(value))
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
            if (IsEmptyString(value))
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
            if (IsEmptyString(value))
                return null;

            //if (TimeOnly.TryParse(value!.Trim(), out TimeOnly time))
            //    return time;
            //if (DateTime.TryParse(value!.Trim(), out DateTime datetime))
            //    return TimeOnly.FromDateTime(datetime);
            //return null;

            string t = value.Trim();

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
    }
}
