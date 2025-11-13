using System.Dynamic;
using BackendExample.Utility;
using Microsoft.AspNetCore.Mvc;

namespace BackendExample.Adapter.Driving
{
    /// <summary>
    /// 测试响应状态码 Controller
    /// </summary>
    public class TestResponseCodeController : ControllerBase
    {
        /// <summary>
        /// 测试响应 500
        /// </summary>
        [HttpGet("/api/test/response/code/500")]
        [Produces("text/plain")]
        public IActionResult testResponseCode500()
        {
            return StatusCode(500, "测试500错误");
        }

    }
}
