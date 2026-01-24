using System.Text;
using System.Text.Json;

using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ModelBinding;

namespace BackendExample.Adapter.Driving
{
    /// <summary>
    /// 测试请求 Controller
    /// </summary>
    public class TestRequestController: ControllerBase
    {
        [HttpPost("/api/test/request/string")]
        async public Task<Result> TestRequestString()
        {
            using StreamReader reader = new StreamReader(Request.Body, Encoding.UTF8);
            string body = await reader.ReadToEndAsync();
            return Result.OkData(body);
        }

        [HttpPost("/api/test/request/from-body-string")]
        public Result TestRequestFromBodyString([FromBody] string body)
        {
            return Result.OkData(body);
        }

        [HttpPost("/api/test/request/from-body-json")]
        public Result TestRequestFromBodyJson([FromBody] JsonElement? body)
        {
            //[FromBody] JsonElement body 只处理 application/json、text/json 以及类似的 JSON 类型
            //string name = body.Value.GetProperty("name").GetString() ?? "";
            if (body == null)
                return Result.Ok();
            else
                return Result.OkData(body);
        }

        [HttpPost("/api/test/request/from-body-dynamic")]
        public Result TestRequestFromBodyDynamic([FromBody] dynamic? body)
        {
            return Result.OkData(body);
        }

        //==============================================================================================================

        [HttpGet("/api/test/request/from-query")]
        public Result TestRequestFromQuery([FromQuery][BindRequired] string id)
        {
            return Result.OkData(id);
        }
    }
}
