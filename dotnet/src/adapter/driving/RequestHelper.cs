using System.Text;
using DddExample.Utility;
using Newtonsoft.Json.Linq;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 请求 Helper
    /// </summary>
    public class RequestHelper
    {
        /// <summary>
        /// 获取请求体
        /// </summary>
        public static string GetBody(HttpRequest request)
        {
            using StreamReader reader = new StreamReader(request.Body, Encoding.UTF8);
            string body = reader.ReadToEnd();
            return body;
        }

        /// <summary>
        /// 获取请求体，异步
        /// </summary>
        public static async Task<string> GetBodyAsync(HttpRequest request)
        {
            using StreamReader reader = new StreamReader(request.Body, Encoding.UTF8);
            string body = await reader.ReadToEndAsync();
            return body;
        }

        /// <summary>
        /// 获取请求体JSON内容
        /// </summary>
        public static JObject GetJson(HttpRequest request)
        {
            try
            {
                return JsonUtility.Deserialize(GetBody(request));
            }
            catch
            {
                throw new ControllerException("请求体不是合法的JSON格式");
            }
        }

        /// <summary>
        /// 获取请求体JSON内容，异步
        /// </summary>
        public static async Task<JObject> GetJsonAsync(HttpRequest request)
        {
            try
            {
                return JsonUtility.Deserialize(await GetBodyAsync(request));
            }
            catch
            {
                throw new ControllerException("请求体不是合法的JSON格式");
            }
        }
    }
}
