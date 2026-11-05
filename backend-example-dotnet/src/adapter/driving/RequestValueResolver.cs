using src.utility;
using System.Text;
using System.Text.Json.Nodes;

namespace src.adapter.driving
{
    /// <summary>
    /// 请求 Value Extractor
    /// </summary>
    public sealed class RequestValueResolver
    {
        /// <summary>
        /// 构造 JSON Extractor
        /// </summary>
        public static async Task<JsonExtractor> BuildJsonExtractor(HttpRequest request)
        {
            try
            {
                using StreamReader reader = new StreamReader(request.Body, Encoding.UTF8);
                string body = await reader.ReadToEndAsync();
                return new JsonExtractor(body);
            }
            catch (Exception ex)
            {
                throw new ControllerException("请求体不是合法的 JSON", ex);
            }
        }


        //==================================================================================================================

        public static readonly int DEFAULT_PAGE_NUM = 1;
        public static readonly int DEFAULT_PAGE_SIZE = 10;

        /// <summary>
        /// 规范化页码
        /// </summary>
        public static int NormalizePageNum(string? pageNum)
        {
            int pn = ValueUtility.ToIntOrDefault(pageNum, DEFAULT_PAGE_NUM);
            return pn >= 1 ? pn : DEFAULT_PAGE_NUM;
        }

        /// <summary>
        /// 规范化页大小
        /// </summary>
        public static int NormalizePageSize(string? pageSize)
        {
            int ps = ValueUtility.ToIntOrDefault(pageSize, DEFAULT_PAGE_SIZE);
            return ps >= 1 && ps <= 100 ? ps : DEFAULT_PAGE_SIZE;
        }

        /// <summary>
        /// 验证并修正分页参数
        /// </summary>
        public static (int pageNum, int pageSize, int totalCount) NormalizePaging(int pageNum, int pageSize, int totalCount)
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

        //==================================================================================================================

        /// <summary>
        /// 取排序参数
        /// </summary>
        public static OrderedDictionary<string, string> GetSortOptions(JsonNode? jsonNode)
        {
            OrderedDictionary<string, string> dict = new OrderedDictionary<string, string>();
            if (jsonNode != null && jsonNode is JsonArray a)
            {
                foreach (JsonNode? n in a)
                {
                    if (n != null && n is JsonObject o)
                    {
                        string field = o["field"]?.GetValue<string>() ?? string.Empty;
                        string order = o["order"]?.GetValue<string>() ?? string.Empty;

                        if (ValueUtility.IsBlank(field))
                            throw new ControllerException("排序字段不能为空");

                        if (!string.Equals(order, "asc", StringComparison.OrdinalIgnoreCase) &&
                            !string.Equals(order, "desc", StringComparison.OrdinalIgnoreCase))
                            throw new ControllerException("排序方向只能为 asc 或 desc");

                        dict.Add(field, order);
                    }
                }
            }
            return dict;
        }

    }
}
