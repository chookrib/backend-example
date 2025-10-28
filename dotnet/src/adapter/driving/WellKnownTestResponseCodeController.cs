using System.Dynamic;
using DddExample.Utility;
using Microsoft.AspNetCore.Mvc;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 测试响应状态码 Well Known Controller
    /// </summary>
    public class WellKnownTestResponseCodeController : ControllerBase
    {
        /// <summary>
        /// 测试响应 500
        /// </summary>
        [HttpGet("/.well-known/test/response/code/500")]
        [Produces("text/plain")]
        public IActionResult testResponseCode500()
        {
            return StatusCode(500, "测试500错误");
        }

    }
}
