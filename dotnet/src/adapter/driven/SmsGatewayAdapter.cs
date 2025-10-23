using log4net;

namespace DddExample.Adapter.Driven
{
    /// <summary>
    /// 短信 Gateway 接口 Adapter
    /// </summary>
    public class SmsGatewayAdapter
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(SmsGatewayAdapter));

        public void SendCode(string mobile, string code)
        {
            logger.Info($"发送手机验证码 {code} 到 {mobile}");
        }
    }
}
