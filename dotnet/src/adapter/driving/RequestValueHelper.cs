using System.Text;
using System.Text.Json;
using System.Text.Json.Nodes;
using System.Xml.Linq;
using DddExample.Utility;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 请求 Helper
    /// </summary>
    public class RequestValueHelper
    {
        /// <summary>
        /// 获取请求体
        /// </summary>
        public static string GetRequestBody(HttpRequest request)
        {
            using StreamReader reader = new StreamReader(request.Body, Encoding.UTF8);
            string body = reader.ReadToEnd();
            return body;
        }

        /// <summary>
        /// 获取请求体，异步
        /// </summary>
        public static async Task<string> GetRequestBodyAsync(HttpRequest request)
        {
            using StreamReader reader = new StreamReader(request.Body, Encoding.UTF8);
            string body = await reader.ReadToEndAsync();
            return body;
        }

        /// <summary>
        /// 获取请求体 json 数据
        /// </summary>
        public static JsonNode GetRequestJson(HttpRequest request)
        {
            try
            {
                return JsonUtility.Deserialize(GetRequestBody(request));
            }
            catch
            {
                throw new ControllerException("请求体不是合法的JSON格式");
            }
        }

        /// <summary>
        /// 获取请求体 json 数据，异步
        /// </summary>
        public static async Task<JsonNode> GetRequestJsonAsync(HttpRequest request)
        {
            try
            {
                return JsonUtility.Deserialize(await GetRequestBodyAsync(request));
            }
            catch
            {
                throw new ControllerException("请求体不是合法的JSON格式");
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

        //==============================================================================================================

        /// <summary>
        /// 获取请求 json 数据中 string 值，失败返回默认值
        /// </summary>
        public static string GetRequestJsonStringTrim(JsonNode json, string defaultValue, params string[] keys)
        {
            JsonValue? value = GetRequestJsonValue(json, keys);
            if (value == null)
                return defaultValue;
            if (value.TryGetValue<string>(out string? result))
                return result?.Trim() ?? defaultValue;
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
            string? s = null;
            if (value.TryGetValue<string>(out string? result))
                s = result;
            if (s == null)
                throw new ControllerException($"请求体 {string.Join('.', keys)} 值不是合法的 string");
            return s;
        }

        //==============================================================================================================


    }
}
