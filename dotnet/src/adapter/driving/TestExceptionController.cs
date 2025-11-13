using log4net;

using Microsoft.AspNetCore.Mvc;

namespace BackendExample.Adapter.Driving
{
    /// <summary>
    /// 测试异常 Controller
    /// </summary>
    public class TestExceptionController : ControllerBase
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(TestExceptionController));

        /// <summary>
        /// 测试异常处理
        /// </summary>
        [HttpGet("/api/test/exception")]
        public Result TestException()
        {
            throw new Exception("测试异常");
        }
    }
}
