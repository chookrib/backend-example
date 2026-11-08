using System.Dynamic;
using System.Text.Json;
using System.Text.Json.Nodes;

namespace src.utility
{
    /// <summary>
    /// Json Utility
    /// </summary>
    public sealed class JsonUtility
    {
        /// <summary>
        /// 序列化为 JSON 字符串
        /// </summary>
        public static string Serialize(object data, String message = "序列化 JSON 失败")
        {
            try
            {
                return JsonSerializer.Serialize(data);
            }
            catch(Exception ex)
            {
                throw new UtilityException(message, ex);
            }
        }

        /// <summary>
        /// 反序列化 JSON 字符串
        /// </summary>
        public static JsonNode Deserialize(string data, string message = "反序列化 JSON 失败")
        {
            //if (string.IsNullOrWhiteSpace(data))
            //    throw new UtilityException("JSON字符串不能为空", nameof(data));

            JsonNode? result = null;
            try
            {
                result = JsonNode.Parse(data);
            }
            catch(Exception ex)
            {
                throw new UtilityException(message, ex);
            }
            if (result == null)
            {
                throw new UtilityException(message);
            }

            return result;
        }

        /// <summary>
        /// 反序列化 JSON 字符串
        /// </summary>
        public static dynamic? DeserializeDynamic(string data, string message = "反序列化 JSON 失败")
        {
            //if (string.IsNullOrWhiteSpace(data))
            //    throw new UtilityException("JSON字符串不能为空", nameof(data));

            dynamic? result = null;
            try
            {
                result = JsonSerializer.Deserialize<ExpandoObject>(data);
            }
            catch(Exception ex)
            {
                throw new UtilityException(message, ex);
            }

            if (result == null)
            {
                throw new UtilityException("反序列化JSON异常");
            }

            return result;
        }


        /// <summary>
        /// JsonNode 转为 IDictionary<string, object>
        /// </summary>
        public static IDictionary<string, object> DeserializeDictionary(JsonNode? node)
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
                        dict[kvp.Key] = DeserializeDictionary(childObj);
                    }
                    else if (kvp.Value is JsonArray arr)
                    {
                        dict[kvp.Key] = arr.Select(
                            item => item is JsonObject jo ? DeserializeDictionary(jo) : (item as JsonValue)?.GetValue<object>()
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
