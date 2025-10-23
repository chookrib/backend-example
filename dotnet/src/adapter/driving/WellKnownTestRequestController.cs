using System.Text.Json;
using DddExample.Adapter.Driving;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 测试请求 Well Known Controller
    /// </summary>
    public class WellKnownTestRequestController: ControllerBase
    {
        [HttpPost("/.well-known/test/request/json")]
        public Result TestRequestJson([FromBody] JsonElement body)
        {
            //string name = body.Value<string>("name") ?? "";
            return Result.OkData(body);
        }
    }
}
