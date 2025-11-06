using System.Dynamic;
using System.Text.Json;
using System.Text.Json.Nodes;

namespace BackendExample.Utility
{
    /// <summary>
    /// Json Utility
    /// </summary>
    public class JsonUtility
    {
        ///// <summary>
        ///// 反序列化 JSON 字符串
        ///// </summary>
        //public static dynamic Deserialize(string data)
        //{
        //    object? result = JsonConvert.DeserializeObject(data);
        //    if (result == null)
        //        throw new UtilityException("反序列化JSON异常");
        //    return result;
        //}

        ///// <summary>
        ///// 序列化为 JSON 字符串
        ///// </summary>
        //public static string Serialize(object data)
        //{
        //    return JsonConvert.SerializeObject(data);
        //}


        ///// <summary>
        ///// 反序列化 JSON 字符串
        ///// </summary>
        //public static T? Deserialize<T>(string data)
        //{
        //    if (string.IsNullOrWhiteSpace(data))
        //        throw new UtilityException("JSON字符串不能为空", nameof(data));
        //    return JsonSerializer.Deserialize<T>(data);
        //}

        /// <summary>
        /// 反序列化 JSON 字符串
        /// </summary>
        public static JsonNode Deserialize(string data)
        {
            //if (string.IsNullOrWhiteSpace(data))
            //    throw new UtilityException("JSON字符串不能为空", nameof(data));

            JsonNode? result = JsonNode.Parse(data);
            if (result == null)
                throw new UtilityException("反序列化JSON异常");

            return result;
        }

        /// <summary>
        /// 反序列化 JSON 字符串
        /// </summary>
        public static dynamic? DeserializeDynamic(string data)
        {
            //if (string.IsNullOrWhiteSpace(data))
            //    throw new UtilityException("JSON字符串不能为空", nameof(data));

            dynamic? result = JsonSerializer.Deserialize<ExpandoObject>(data);
            if (result == null)
                throw new UtilityException("反序列化JSON异常");

            return result;
        }

        /// <summary>
        /// 序列化为 JSON 字符串
        /// </summary>
        public static string Serialize(object data)
        {
            //if (data == null)
            //    throw new UtilityException(nameof(data));

            return JsonSerializer.Serialize(data);
        }

        /// <summary>
        /// JsonNode 转为 IDictionary<string, object>
        /// </summary>
        public static IDictionary<string, object> JsonNodeToDictionary(JsonNode? node)
        {
            IDictionary<string, object> dict = new Dictionary<string, object>();
            if (node != null && node is JsonObject obj)
            {
                foreach (var kvp in obj)
                {
                    if (kvp.Value is JsonValue value)
                    {
                        dict[kvp.Key] = value.GetValue<object>();
                    }
                    else if (kvp.Value is JsonObject childObj)
                    {
                        dict[kvp.Key] = JsonNodeToDictionary(childObj);
                    }
                    else if (kvp.Value is JsonArray arr)
                    {
                        dict[kvp.Key] = arr.Select(
                            item => item is JsonObject jo ? JsonNodeToDictionary(jo) : (item as JsonValue)?.GetValue<object>()
                            ).ToList();
                    }
                    else
                    {
                        dict[kvp.Key] = kvp.Value?.ToJsonString() ?? string.Empty;
                    }
                }
            }
            return dict;
        }
    }
}
