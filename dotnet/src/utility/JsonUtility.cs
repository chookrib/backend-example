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
        //        throw new Exception("反序列化JSON异常");
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
        //        throw new ArgumentException("JSON字符串不能为空", nameof(data));
        //    return JsonSerializer.Deserialize<T>(data);
        //}

        /// <summary>
        /// 反序列化 JSON 字符串
        /// </summary>
        public static JsonNode Deserialize(string data)
        {
            //if (string.IsNullOrWhiteSpace(data))
            //    throw new ArgumentException("JSON字符串不能为空", nameof(data));

            JsonNode? result = JsonNode.Parse(data);
            if (result == null)
                throw new Exception("反序列化JSON异常");

            return result;
        }

        /// <summary>
        /// 反序列化 JSON 字符串
        /// </summary>
        public static dynamic? DeserializeDynamic(string data)
        {
            //if (string.IsNullOrWhiteSpace(data))
            //    throw new ArgumentException("JSON字符串不能为空", nameof(data));

            dynamic? result = JsonSerializer.Deserialize<ExpandoObject>(data);
            if (result == null)
                throw new Exception("反序列化JSON异常");

            return result;
        }

        /// <summary>
        /// 序列化为 JSON 字符串
        /// </summary>
        public static string Serialize(object data)
        {
            //if (data == null)
            //    throw new ArgumentNullException(nameof(data));

            return JsonSerializer.Serialize(data);
        }
    }
}
