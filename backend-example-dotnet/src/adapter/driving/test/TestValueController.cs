using System.Dynamic;

using BackendExample.Utility;

using Microsoft.AspNetCore.Mvc;

namespace BackendExample.Adapter.Driving.Test
{
    /// <summary>
    /// Value 测试 Controller
    /// </summary>
    public class TestValueController : ControllerBase
    {
        /// <summary>
        /// 测试日期时间转换
        /// </summary>
        [HttpGet("/api/test/value/to-datetime")]
        public Result TestValueToDateTime()
        {
            Dictionary<string, object> dateTimeDict = new Dictionary<string, object>();
            dateTimeDict.Add("yyyy", ValueUtility.ToDateTimeOrNull("2026")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM", ValueUtility.ToDateTimeOrNull("2026-01")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd", ValueUtility.ToDateTimeOrNull("2026-01-02")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH", ValueUtility.ToDateTimeOrNull("2026-01-02 03")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm:ss", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04:05")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm:ss.f", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04:05.1")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm:ss.ff", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04:05.12")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm:ss.fff", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04:05.123")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm:ss.ffff", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04:05.1234")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm:ss.fffff", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04:05.12345")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm:ss.ffffff", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04:05.123456")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm:ss.fffffff", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04:05.1234567")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm:ss.ffffffff", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04:05.12345678")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm:ss.fffffffff", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04:05.123456789")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("yyyy-MM-dd HH:mm:ss.ffffffffff", ValueUtility.ToDateTimeOrNull("2026-01-02 03:04:05.0123456789")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH", ValueUtility.ToDateTimeOrNull("03")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm", ValueUtility.ToDateTimeOrNull("03:04")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm:ss", ValueUtility.ToDateTimeOrNull("03:04:05")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm:ss.f", ValueUtility.ToDateTimeOrNull("03:04:05.1")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm:ss.ff", ValueUtility.ToDateTimeOrNull("03:04:05.12")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm:ss.fff", ValueUtility.ToDateTimeOrNull("03:04:05.123")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm:ss.ffff", ValueUtility.ToDateTimeOrNull("03:04:05.1234")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm:ss.fffff", ValueUtility.ToDateTimeOrNull("03:04:05.12345")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm:ss.ffffff", ValueUtility.ToDateTimeOrNull("03:04:05.123456")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm:ss.fffffff", ValueUtility.ToDateTimeOrNull("03:04:05.1234567")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm:ss.ffffffff", ValueUtility.ToDateTimeOrNull("03:04:05.12345678")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm:ss.fffffffff", ValueUtility.ToDateTimeOrNull("03:04:05.123456789")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));
            dateTimeDict.Add("HH:mm:ss.ffffffffff", ValueUtility.ToDateTimeOrNull("03:04:05.0123456789")?.ToString("yyyy-MM-dd HH:mm:ss.fffffff"));

            Dictionary<string, object> dateDict = new Dictionary<string, object>();
            dateDict.Add("yyyy", ValueUtility.ToDateOrNull("2026"));
            dateDict.Add("yyyy-MM", ValueUtility.ToDateOrNull("2026-01"));
            dateDict.Add("yyyy-MM-dd", ValueUtility.ToDateOrNull("2026-01-02"));
            dateDict.Add("yyyy-MM-dd HH", ValueUtility.ToDateOrNull("2026-01-02 03"));
            dateDict.Add("yyyy-MM-dd HH:mm", ValueUtility.ToDateOrNull("2026-01-02 03:04"));
            dateDict.Add("yyyy-MM-dd HH:mm:ss", ValueUtility.ToDateOrNull("2026-01-02 03:04:05"));
            dateDict.Add("yyyy-MM-dd HH:mm:ss.f", ValueUtility.ToDateOrNull("2026-01-02 03:04:05.1"));
            dateDict.Add("yyyy-MM-dd HH:mm:ss.ff", ValueUtility.ToDateOrNull("2026-01-02 03:04:05.12"));
            dateDict.Add("yyyy-MM-dd HH:mm:ss.fff", ValueUtility.ToDateOrNull("2026-01-02 03:04:05.123"));
            dateDict.Add("yyyy-MM-dd HH:mm:ss.ffff", ValueUtility.ToDateOrNull("2026-01-02 03:04:05.1234"));
            dateDict.Add("yyyy-MM-dd HH:mm:ss.fffff", ValueUtility.ToDateOrNull("2026-01-02 03:04:05.12345"));
            dateDict.Add("yyyy-MM-dd HH:mm:ss.ffffff", ValueUtility.ToDateOrNull("2026-01-02 03:04:05.123456"));
            dateDict.Add("yyyy-MM-dd HH:mm:ss.fffffff", ValueUtility.ToDateOrNull("2026-01-02 03:04:05.1234567"));
            dateDict.Add("yyyy-MM-dd HH:mm:ss.ffffffff", ValueUtility.ToDateOrNull("2026-01-02 03:04:05.12345678"));
            dateDict.Add("yyyy-MM-dd HH:mm:ss.fffffffff", ValueUtility.ToDateOrNull("2026-01-02 03:04:05.123456789"));
            dateDict.Add("yyyy-MM-dd HH:mm:ss.ffffffffff", ValueUtility.ToDateOrNull("2026-01-02 03:04:05.0123456789"));
            dateDict.Add("HH", ValueUtility.ToDateOrNull("03"));
            dateDict.Add("HH:mm", ValueUtility.ToDateOrNull("03:04"));
            dateDict.Add("HH:mm:ss", ValueUtility.ToDateOrNull("03:04:05"));
            dateDict.Add("HH:mm:ss.f", ValueUtility.ToDateOrNull("03:04:05.1"));
            dateDict.Add("HH:mm:ss.ff", ValueUtility.ToDateOrNull("03:04:05.12"));
            dateDict.Add("HH:mm:ss.fff", ValueUtility.ToDateOrNull("03:04:05.123"));
            dateDict.Add("HH:mm:ss.ffff", ValueUtility.ToDateOrNull("03:04:05.1234"));
            dateDict.Add("HH:mm:ss.fffff", ValueUtility.ToDateOrNull("03:04:05.12345"));
            dateDict.Add("HH:mm:ss.ffffff", ValueUtility.ToDateOrNull("03:04:05.123456"));
            dateDict.Add("HH:mm:ss.fffffff", ValueUtility.ToDateOrNull("03:04:05.1234567"));
            dateDict.Add("HH:mm:ss.ffffffff", ValueUtility.ToDateOrNull("03:04:05.12345678"));
            dateDict.Add("HH:mm:ss.fffffffff", ValueUtility.ToDateOrNull("03:04:05.123456789"));
            dateDict.Add("HH:mm:ss.ffffffffff", ValueUtility.ToDateOrNull("03:04:05.0123456789"));

            Dictionary<string, object> timeDict = new Dictionary<string, object>();
            timeDict.Add("yyyy", ValueUtility.ToTimeOrNull("2026")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM", ValueUtility.ToTimeOrNull("2026-01")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd", ValueUtility.ToTimeOrNull("2026-01-02")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH", ValueUtility.ToTimeOrNull("2026-01-02 03")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm", ValueUtility.ToTimeOrNull("2026-01-02 03:04")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm:ss", ValueUtility.ToTimeOrNull("2026-01-02 03:04:05")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm:ss.f", ValueUtility.ToTimeOrNull("2026-01-02 03:04:05.1")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm:ss.ff", ValueUtility.ToTimeOrNull("2026-01-02 03:04:05.12")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm:ss.fff", ValueUtility.ToTimeOrNull("2026-01-02 03:04:05.123")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm:ss.ffff", ValueUtility.ToTimeOrNull("2026-01-02 03:04:05.1234")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm:ss.fffff", ValueUtility.ToTimeOrNull("2026-01-02 03:04:05.12345")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm:ss.ffffff", ValueUtility.ToTimeOrNull("2026-01-02 03:04:05.123456")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm:ss.fffffff", ValueUtility.ToTimeOrNull("2026-01-02 03:04:05.1234567")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm:ss.ffffffff", ValueUtility.ToTimeOrNull("2026-01-02 03:04:05.12345678")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm:ss.fffffffff", ValueUtility.ToTimeOrNull("2026-01-02 03:04:05.123456789")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("yyyy-MM-dd HH:mm:ss.ffffffffff", ValueUtility.ToTimeOrNull("2026-01-02 03:04:05.0123456789")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH", ValueUtility.ToTimeOrNull("03")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm", ValueUtility.ToTimeOrNull("03:04")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm:ss", ValueUtility.ToTimeOrNull("03:04:05")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm:ss.f", ValueUtility.ToTimeOrNull("03:04:05.1")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm:ss.ff", ValueUtility.ToTimeOrNull("03:04:05.12")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm:ss.fff", ValueUtility.ToTimeOrNull("03:04:05.123")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm:ss.ffff", ValueUtility.ToTimeOrNull("03:04:05.1234")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm:ss.fffff", ValueUtility.ToTimeOrNull("03:04:05.12345")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm:ss.ffffff", ValueUtility.ToTimeOrNull("03:04:05.123456")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm:ss.fffffff", ValueUtility.ToTimeOrNull("03:04:05.1234567")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm:ss.ffffffff", ValueUtility.ToTimeOrNull("03:04:05.12345678")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm:ss.fffffffff", ValueUtility.ToTimeOrNull("03:04:05.123456789")?.ToString("HH:mm:ss.fffffff"));
            timeDict.Add("HH:mm:ss.ffffffffff", ValueUtility.ToTimeOrNull("03:04:05.0123456789")?.ToString("HH:mm:ss.fffffff"));

            Dictionary<string, object> dict = new Dictionary<string, object>();
            dict.Add("toDateTime", dateTimeDict);
            dict.Add("toDate", dateDict);
            dict.Add("toTime", timeDict);

            return Result.OkData(dict);
        }
    }
}
