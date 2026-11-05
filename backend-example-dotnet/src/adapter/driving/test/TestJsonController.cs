using Microsoft.AspNetCore.Mvc;
using src.utility;

namespace src.adapter.driving.test
{
    /// <summary>
    /// JSON 测试 Controller
    /// </summary>
    public class TestJsonController : ControllerBase
    {
        [HttpGet("/api/test/json")]
        public Result TestJson()
        {
            string jsonString = """
                {
                    "1": {
                        "1_1": "1-1",
                        "1_2": "1-2"
                    },
                    "2":{
                        "2_1": 21,
                        "2_2": 22
                    },
                    "3": ["31", 32]
                }
                """;
            JsonExtractor jsonExtractor = new JsonExtractor(jsonString);

            return Result.OkData(new
            {
                test1 = jsonExtractor.GetString("1"),
                test2 = jsonExtractor.GetString("1.1_1"),
                test3 = jsonExtractor.GetStringOrEmpty("1.1_1.1_1_1"),
                test4 = jsonExtractor.GetString("2.2_1"),
                test5 = jsonExtractor.GetStringList("3")
            });
        }
    }
}
