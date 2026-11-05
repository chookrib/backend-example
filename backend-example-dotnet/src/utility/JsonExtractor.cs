using System.Text.Json.Nodes;

namespace src.utility
{
    /// <summary>
    /// JSON Extractor
    /// </summary>
    public class JsonExtractor
    {
        private readonly JsonNode rootNode;

        public JsonExtractor(string jsonString)
        {
            this.rootNode = JsonUtility.Deserialize(jsonString);
        }

        /// <summary>
        /// 获取节点，失败返回 null，多级路径以 . 拼接
        /// </summary>
        public JsonNode? GetNode(string path)
        {
            if (ValueUtility.IsBlank(path))
                return null;

            string[] paths = path.Split(".");
            if (paths.Length == 0)
                return null;

            JsonNode currentNode = rootNode;
            foreach (string p in paths)
            {
                if (currentNode is not JsonObject obj)
                    return null;

                if (!obj.ContainsKey(p))
                    return null;

                JsonNode? n = currentNode[p];
                if (n == null)
                    return null;
                currentNode = n;
            }
            return currentNode;
        }

        /// <summary>
        /// 获取节点，失败抛出指定消息异常，多级路径以 . 拼接
        /// </summary>

        public JsonNode GetNodeOrThrow(string path, string? message = null)
        {
            JsonNode? node = GetNode(path);
            if(node == null)
            {
                if (message == null)
                    message = $"JSON 中 {path} 不存在";
                throw new UtilityException(message);

            }
            return node;
        }

        /// <summary>
        /// 获取字符串值，失败返回 null，多级路径以 . 拼接
        /// </summary>
        public string? GetString(string path)
        {
            JsonNode? node = GetNode(path);
            if (node == null)
                return null;
            if (node is JsonValue value)
            {
                return value.ToString();
                //if (value.TryGetValue<string>(out string? result) && result != null)
                //    return result;
                //return null;
            }
            return node.ToJsonString();
        }

        /// <summary>
        /// 获取字符串值，如果为空字符串抛出指定消息异常，多级路径以 . 拼接
        /// </summary>
        public string GetNotBlankStringOrThrow(string path, string? message = null)
        {
            string? value = GetString(path);
            if (ValueUtility.IsBlank(value))
            {
                if (message == null)
                    message = $"JSON 中 {path} 为空字符串";
                throw new UtilityException(message);
            }
            return value!;
        }

        /// <summary>
        /// 获取字符串值，失败返回空字符串，多级路径以 . 拼接
        /// </summary>
        public string GetStringOrEmpty(string path)
        {
            string? value = GetString(path);
            if (value == null)
                return "";
            return value;
        }

    }
}
