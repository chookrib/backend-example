using log4net;

using Microsoft.AspNetCore.Mvc;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 测试异常 Well Known Controller
    /// </summary>
    public class WellKnownTestExceptionController : ControllerBase
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(WellKnownTestExceptionController));

        /// <summary>
        /// 测试异常处理
        /// </summary>
        [HttpGet("/.well-known/test/exception")]
        public Result TestException()
        {
            throw new Exception("测试异常");
        }
    }
}
