using System.Text;
using System.Text.Json;

using Microsoft.AspNetCore.Mvc;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 测试请求 Well Known Controller
    /// </summary>
    public class WellKnownTestRequestController: ControllerBase
    {
        [HttpPost("/.well-known/test/request/string")]
        async public Task<Result> TestRequestString()
        {
            using StreamReader reader = new StreamReader(Request.Body, Encoding.UTF8);
            string body = await reader.ReadToEndAsync();
            return Result.OkData(body);
        }

        [HttpPost("/.well-known/test/request/from-body-string")]
        public Result TestRequestFromBodyString([FromBody] string body)
        {
            return Result.OkData(body);
        }

        [HttpPost("/.well-known/test/request/from-body-json")]
        public Result TestRequestFromBodyJson([FromBody] JsonElement? body)
        {
            //[FromBody] JsonElement body 只处理 application/json、text/json 以及类似的 JSON 类型
            //string name = body.Value.GetProperty("name").GetString() ?? "";
            if (body == null)
                return Result.Ok();
            else
                return Result.OkData(body);
        }

        [HttpPost("/.well-known/test/request/from-body-dynamic")]
        public Result TestRequestFromBodyDynamic([FromBody] dynamic? body)
        {
            return Result.OkData(body);
        }
    }
}
