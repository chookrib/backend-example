using Newtonsoft.Json;

namespace DddExample.Utility
{
    /// <summary>
    /// Json Utility
    /// </summary>
    public class JsonUtility
    {
        /// <summary>
        /// 反序列化 JSON 字符串
        /// </summary>
        public static dynamic Deserialize(string data)
        {
            object? result = JsonConvert.DeserializeObject(data);
            if (result == null)
                throw new Exception("反序列化 JSON 字符串异常");
            return result;
        }

        /// <summary>
        /// 序列化为 JSON 字符串
        /// </summary>
        public static string Serialize(object data)
        {
            return JsonConvert.SerializeObject(data);
        }
    }
}
