using System.Text;
using System.Text.Json.Nodes;

using BackendExample.Utility;

namespace BackendExample.Adapter.Driving
{
    /// <summary>
    /// 请求 Value Helper
    /// </summary>
    public class RequestValueHelper
    {
        ///// <summary>
        ///// 获取请求体
        ///// </summary>
        //public static string GetRequestBody(HttpRequest request)
        //{
        //    using StreamReader reader = new StreamReader(request.Body, Encoding.UTF8);
        //    string body = reader.ReadToEnd();
        //    return body;
        //}

        /// <summary>
        /// 获取请求体，异步
        /// </summary>
        public static async Task<string> GetRequestBodyAsync(HttpRequest request)
        {
            using StreamReader reader = new StreamReader(request.Body, Encoding.UTF8);
            string body = await reader.ReadToEndAsync();
            return body;
        }

        ///// <summary>
        ///// 获取请求体 json 数据
        ///// </summary>
        //public static JsonNode GetRequestJson(HttpRequest request)
        //{
        //    try
        //    {
        //        return JsonUtility.Deserialize(GetRequestBody(request));
        //    }
        //    catch (Exception ex)
        //    {
        //        throw new ControllerException("请求体不是合法的JSON格式", ex);
        //    }
        //}

        /// <summary>
        /// 获取请求体 json 数据，异步
        /// </summary>
        public static async Task<JsonNode> GetRequestJsonAsync(HttpRequest request)
        {
            try
            {
                return JsonUtility.Deserialize(await GetRequestBodyAsync(request));
            }
            catch(Exception ex)
            {
                throw new ControllerException("请求体不是合法的JSON格式", ex);
            }
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求 json 数据中的值，失败返回 null
        /// </summary>
        public static JsonValue? GetRequestJsonValue(JsonNode json, params string[] keys)
        {
            if (keys == null || keys.Length == 0)
                return null;
            JsonNode? node = json;
            foreach (string key in keys)
            {
                node = json[key];
                if (node == null)
                    return null;
            }
            if (node is JsonValue value)
                return value;
            return null;
        }

        /// <summary>
        /// 获取请求 json 数据中的值，失败抛出异常
        /// </summary>
        public static JsonValue GetRequestJsonValueReq(JsonNode json, params string[] keys)
        {
            JsonValue? value = GetRequestJsonValue(json, keys);
            if (value == null)
                throw new ControllerException($"请求体缺少 {string.Join('.', keys)}");
            return value;
        }

        /// <summary>
        /// 获取请求 json 数据中的节点，失败返回 null
        /// </summary>
        public static JsonNode? GetRequestJsonNode(JsonNode json, params string[] keys)
        {
            if (keys == null || keys.Length == 0)
                return null;
            JsonNode? node = json;
            foreach (string key in keys)
            {
                node = json[key];
                if (node == null)
                    return null;
            }
            return node;
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求 json 数据中 string 值，失败返回默认值
        /// </summary>
        public static string GetRequestJsonStringTrim(JsonNode json, string defaultValue, params string[] keys)
        {
            JsonValue? value = GetRequestJsonValue(json, keys);
            if (value == null)
                return defaultValue;
            if (value.TryGetValue<string>(out string? result) && result != null)
                return result.Trim();
            return defaultValue;
        }

        /// <summary>
        /// 获取请求 json 数据中 string 值，失败返回空字符串
        /// </summary>
        public static string GetRequestJsonStringTrimOrEmpty(JsonNode json, params string[] keys)
        {
            return GetRequestJsonStringTrim(json, string.Empty, keys);
        }

        /// <summary>
        /// 获取请求 json 数据中 string 值，失败抛出异常
        /// </summary>
        public static string GetRequestJsonStringTrimReq(JsonNode json, params string[] keys)
        {
            JsonValue value = GetRequestJsonValueReq(json, keys);
            if (value.TryGetValue<string>(out string? result) && result != null)
                return result;
            throw new ControllerException($"请求体 {string.Join('.', keys)} 值不是合法的 string");
        }

        /// <summary>
        /// 获取请求 json 数据中 string 数组值
        /// </summary>
        public static IList<string> GetRequestJsonStringTrimList(JsonNode json, params string[] keys)
        {
            IList<string> ss = new List<string>();
            JsonNode? node = GetRequestJsonNode(json, keys);
            if (node != null && node is JsonArray a)
            {
                foreach (JsonNode? n in a)
                {
                    if (n != null && n is JsonValue v && v.TryGetValue<string>(out string? s))
                        ss.Add(s?.Trim() ?? string.Empty);
                }
            }
            return ss;
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求 json 数据中 bool 值，失败返回默认值
        /// </summary>
        public static bool GetRequestJsonBool(JsonNode json, bool defaultValue, params string[] keys)
        {
            JsonValue? value = GetRequestJsonValue(json, keys);
            if (value == null)
                return defaultValue;
            if (value.TryGetValue<bool>(out bool boolResult))
                return boolResult;
            bool? b = null;
            if (value.TryGetValue<string>(out string? stringResult))
                b = ValueUtility.ToBoolOrNull(stringResult);
            if (b.HasValue)
                return b.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求 json 数据中 bool 值，失败抛出异常
        /// </summary>
        public static bool GetRequestJsonBoolReq(JsonNode json, params string[] keys)
        {
            JsonValue value = GetRequestJsonValueReq(json, keys);
            if (value.TryGetValue<bool>(out bool boolResult))
                return boolResult;
            bool? b = null;
            if (value.TryGetValue<string>(out string? stringResult))
                b = ValueUtility.ToBoolOrNull(stringResult);
            if (b.HasValue)
                return b.Value;
            throw new ControllerException($"请求体 {string.Join('.', keys)} 值不是合法的 bool");
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求 json 数据中 int 值，失败返回默认值
        /// </summary>
        public static int GetRequestJsonInt(JsonNode json, int defaultValue, params string[] keys)
        {
            JsonValue? value = GetRequestJsonValue(json, keys);
            if (value == null)
                return defaultValue;
            if (value.TryGetValue<int>(out int intResult))
                return intResult;
            int? i = null;
            if (value.TryGetValue<string>(out string? stringResult))
                i = ValueUtility.ToIntOrNull(stringResult);
            if (i.HasValue)
                return i.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求 json 数据中 int 值，失败抛出异常
        /// </summary>
        public static int GetRequestJsonIntReq(JsonNode json, params string[] keys)
        {
            JsonValue value = GetRequestJsonValueReq(json, keys);
            if (value.TryGetValue<int>(out int intResult))
                return intResult;
            int? i = null;
            if (value.TryGetValue<string>(out string? stringResult))
                i = ValueUtility.ToIntOrNull(stringResult);
            if (i.HasValue)
                return i.Value;
            throw new ControllerException($"请求体 {string.Join('.', keys)} 值不是合法的 int");
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求 json 数据中 long 值，失败返回默认值
        /// </summary>
        public static long GetRequestJsonLong(JsonNode json, long defaultValue, params string[] keys)
        {
            JsonValue? value = GetRequestJsonValue(json, keys);
            if (value == null)
                return defaultValue;
            if (value.TryGetValue<long>(out long longResult))
                return longResult;
            long? l = null;
            if (value.TryGetValue<string>(out string? stringResult))
                l = ValueUtility.ToLongOrNull(stringResult);
            if (l.HasValue)
                return l.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求 json 数据中 long 值，失败抛出异常
        /// </summary>
        public static long GetRequestJsonLongReq(JsonNode json, params string[] keys)
        {
            JsonValue value = GetRequestJsonValueReq(json, keys);
            if (value.TryGetValue<long>(out long longResult))
                return longResult;
            long? l = null;
            if (value.TryGetValue<string>(out string? stringResult))
                l = ValueUtility.ToLongOrNull(stringResult);
            if (l.HasValue)
                return l.Value;
            throw new ControllerException($"请求体 {string.Join('.', keys)} 值不是合法的 long");
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求 json 数据中 decimal 值，失败返回默认值
        /// </summary>
        public static decimal GetRequestJsonDecimal(JsonNode json, decimal defaultValue, params string[] keys)
        {
            JsonValue? value = GetRequestJsonValue(json, keys);
            if (value == null)
                return defaultValue;
            if (value.TryGetValue<decimal>(out decimal decimalResult))
                return decimalResult;
            decimal? d = null;
            if (value.TryGetValue<string>(out string? stringResult))
                d = ValueUtility.ToDecimalOrNull(stringResult);
            if (d.HasValue)
                return d.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求 json 数据中 decimal 值，失败抛出异常
        /// </summary>
        public static decimal GetRequestJsonDecimalReq(JsonNode json, params string[] keys)
        {
            JsonValue value = GetRequestJsonValueReq(json, keys);
            if (value.TryGetValue<decimal>(out decimal decimalResult))
                return decimalResult;
            decimal? d = null;
            if (value.TryGetValue<string>(out string? stringResult))
                d = ValueUtility.ToDecimalOrNull(stringResult);
            if (d.HasValue)
                return d.Value;
            throw new ControllerException($"请求体 {string.Join('.', keys)} 值不是合法的 decimal");
        }


        //==============================================================================================================

        /// <summary>
        /// 获取请求 json 数据中 datetime 值，失败返回默认值
        /// </summary>
        public static DateTime GetRequestJsonDateTime(JsonNode json, DateTime defaultValue, params string[] keys)
        {
            JsonValue? value = GetRequestJsonValue(json, keys);
            if (value == null)
                return defaultValue;
            //if (value.TryGetValue<DateTime>(out DateTime dateTimeResult))
            //    return dateTimeResult;
            DateTime? dt = null;
            if (value.TryGetValue<string>(out string? stringResult))
                dt = ValueUtility.ToDateTimeOrNull(stringResult);
            if (dt.HasValue)
                return dt.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求 json 数据中 datetime 值，失败抛出异常
        /// </summary>
        public static DateTime GetRequestJsonDateTimeReq(JsonNode json, params string[] keys)
        {
            JsonValue value = GetRequestJsonValueReq(json, keys);
            //if (value.TryGetValue<DateTime>(out DateTime dateTimeResult))
            //    return dateTimeResult;
            DateTime? dt = null;
            if (value.TryGetValue<string>(out string? stringResult))
                dt = ValueUtility.ToDateTimeOrNull(stringResult);
            if (dt.HasValue)
                return dt.Value;
            throw new ControllerException($"请求体 {string.Join('.', keys)} 值不是合法的 datetime");
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求 json 数据中 date 值，失败返回默认值
        /// </summary>
        public static DateOnly GetRequestJsonDate(JsonNode json, DateOnly defaultValue, params string[] keys)
        {
            JsonValue? value = GetRequestJsonValue(json, keys);
            if (value == null)
                return defaultValue;
            //if (value.TryGetValue<DateOnly>(out DateOnly dateResult))
            //    return dateResult;
            DateOnly? d = null;
            if (value.TryGetValue<string>(out string? stringResult))
                d = ValueUtility.ToDateOrNull(stringResult);
            if (d.HasValue)
                return d.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求 json 数据中 date 值，失败抛出异常
        /// </summary>
        public static DateOnly GetRequestJsonDateReq(JsonNode json, params string[] keys)
        {
            JsonValue value = GetRequestJsonValueReq(json, keys);
            //if (value.TryGetValue<DateOnly>(out DateOnly dateResult))
            //    return dateResult;
            DateOnly? d = null;
            if (value.TryGetValue<string>(out string? stringResult))
                d = ValueUtility.ToDateOrNull(stringResult);
            if (d.HasValue)
                return d.Value;
            throw new ControllerException($"请求体 {string.Join('.', keys)} 值不是合法的 date");
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求 json 数据中 time 值，失败返回默认值
        /// </summary>
        public static TimeOnly GetRequestJsonTime(JsonNode json, TimeOnly defaultValue, params string[] keys)
        {
            JsonValue? value = GetRequestJsonValue(json, keys);
            if (value == null)
                return defaultValue;
            //if (value.TryGetValue<TimeOnly>(out TimeOnly timeResult))
            //    return timeResult;
            TimeOnly? t = null;
            if (value.TryGetValue<string>(out string? stringResult))
                t = ValueUtility.ToTimeOrNull(stringResult);
            if (t.HasValue)
                return t.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求 json 数据中 time 值，失败抛出异常
        /// </summary>
        public static TimeOnly GetRequestJsonTimeReq(JsonNode json, params string[] keys)
        {
            JsonValue value = GetRequestJsonValueReq(json, keys);
            //if (value.TryGetValue<TimeOnly>(out TimeOnly timeResult))
            //    return timeResult;
            TimeOnly? t = null;
            if (value.TryGetValue<string>(out string? stringResult))
                t = ValueUtility.ToTimeOrNull(stringResult);
            if (t.HasValue)
                return t.Value;
            throw new ControllerException($"请求体 {string.Join('.', keys)} 值不是合法的 time");
        }


        //==============================================================================================================

        /// <summary>
        /// 获取请求参数中 string 值，失败返回默认值
        /// </summary>
        public static string GetRequestParamStringTrim(HttpRequest request, string defaultValue, string key)
        {
            string? value = request.Query[key];
            if (value == null)
                return defaultValue;
            return value;
        }

        /// <summary>
        /// 获取请求参数中 string 值，失败返回空字符串
        /// </summary>
        public static string GetRequestParamStringTrimOrEmpty(HttpRequest request, string key)
        {
            return GetRequestParamStringTrim(request, string.Empty, key);
        }

        /// <summary>
        /// 获取请求参数中 string 值，失败抛出异常
        /// </summary>
        public static string GetRequestParamStringTrimReq(HttpRequest request, string key)
        {
            string? value = request.Query[key];
            if (value == null)
                throw new ControllerException($"请求参数 {key} 值缺失");
            return value;
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求参数中 bool 值，失败返回默认值
        /// </summary>
        public static bool GetRequestParamBool(HttpRequest request, bool defaultValue, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            bool? b = ValueUtility.ToBoolOrNull(value);
            if (b.HasValue)
                return b.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求参数中 bool 值，失败抛出异常
        /// </summary>
        public static bool GetRequestParamBoolReq(HttpRequest request, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            bool? b = ValueUtility.ToBoolOrNull(value);
            if (b.HasValue)
                return b.Value;
            throw new ControllerException($"请求参数 {key} 值不是合法的 bool");
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求参数中 int 值，失败返回默认值
        /// </summary>
        public static int GetRequestParamInt(HttpRequest request, int defaultValue, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            int? i = ValueUtility.ToIntOrNull(value);
            if (i.HasValue)
                return i.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求参数中 int 值，失败抛出异常
        /// </summary>
        public static int GetRequestParamIntReq(HttpRequest request, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            int? i = ValueUtility.ToIntOrNull(value);
            if (i.HasValue)
                return i.Value;
            throw new ControllerException($"请求参数 {key} 值不是合法的 int");
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求参数中 long 值，失败返回默认值
        /// </summary>
        public static long GetRequestParamLong(HttpRequest request, long defaultValue, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            long? l = ValueUtility.ToLongOrNull(value);
            if (l.HasValue)
                return l.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求参数中 long 值，失败抛出异常
        /// </summary>
        public static long GetRequestParamLongReq(HttpRequest request, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            long? l = ValueUtility.ToLongOrNull(value);
            if (l.HasValue)
                return l.Value;
            throw new ControllerException($"请求参数 {key} 值不是合法的 long");
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求参数中 decimal 值，失败返回默认值
        /// </summary>
        public static decimal GetRequestParamDecimal(HttpRequest request, decimal defaultValue, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            decimal? d = ValueUtility.ToDecimalOrNull(value);
            if (d.HasValue)
                return d.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求参数中 decimal 值，失败抛出异常
        /// </summary>
        public static decimal GetRequestParamDecimalReq(HttpRequest request, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            decimal? d = ValueUtility.ToDecimalOrNull(value);
            if (d.HasValue)
                return d.Value;
            throw new ControllerException($"请求参数 {key} 值不是合法的 decimal");
        }


        //==============================================================================================================

        /// <summary>
        /// 获取请求参数中 datetime 值，失败返回默认值
        /// </summary>
        public static DateTime GetRequestParamDateTime(HttpRequest request, DateTime defaultValue, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            DateTime? dt = ValueUtility.ToDateTimeOrNull(value);
            if (dt.HasValue)
                return dt.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求参数中 datetime 值，失败抛出异常
        /// </summary>
        public static DateTime GetRequestParamDateTimeReq(HttpRequest request, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            DateTime? dt = ValueUtility.ToDateTimeOrNull(value);
            if (dt.HasValue)
                return dt.Value;
            throw new ControllerException($"请求参数 {key} 值不是合法的 datetime");
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求参数中 date 值，失败返回默认值
        /// </summary>
        public static DateOnly GetRequestParamDate(HttpRequest request, DateOnly defaultValue, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            DateOnly? d = ValueUtility.ToDateOrNull(value);
            if (d.HasValue)
                return d.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求参数中 date 值，失败抛出异常
        /// </summary>
        public static DateOnly GetRequestParamDateReq(HttpRequest request, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            DateOnly? d = ValueUtility.ToDateOrNull(value);
            if (d.HasValue)
                return d.Value;
            throw new ControllerException($"请求参数 {key} 值不是合法的 date");
        }

        //==============================================================================================================

        /// <summary>
        /// 获取请求参数中 time 值，失败返回默认值
        /// </summary>
        public static TimeOnly GetRequestParamTime(HttpRequest request, TimeOnly defaultValue, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            TimeOnly? t = ValueUtility.ToTimeOrNull(value);
            if (t.HasValue)
                return t.Value;
            return defaultValue;
        }

        /// <summary>
        /// 获取请求参数中 time 值，失败抛出异常
        /// </summary>
        public static TimeOnly GetRequestParamTimeReq(HttpRequest request, string key)
        {
            string value = GetRequestParamStringTrimOrEmpty(request, key);
            TimeOnly? t = ValueUtility.ToTimeOrNull(value);
            if (t.HasValue)
                return t.Value;
            throw new ControllerException($"请求参数 {key} 值不是合法的 time");
        }

        //==============================================================================================================

        /// <summary>
        /// 验证并修正分页参数
        /// </summary>
        public static (int pageNum, int pageSize, int totalCount) FixPaging(int pageNum, int pageSize, int totalCount)
        {
            if (pageSize < 1)
                pageSize = 1;
            if (totalCount < 0)
                totalCount = 0;

            int maxPageNum = totalCount / pageSize;
            if (totalCount % pageSize > 0)
                maxPageNum++;

            if (pageNum > maxPageNum)
                pageNum = maxPageNum;
            if (pageNum < 1)
                pageNum = 1;

            return (pageNum, pageSize, totalCount);
        }

    }
}
