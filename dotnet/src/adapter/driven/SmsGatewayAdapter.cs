using BackendExample.Domain;
using log4net;

namespace BackendExample.Adapter.Driven
{
    /// <summary>
    /// 短信 Gateway 接口 Adapter
    /// </summary>
    public class SmsGatewayAdapter : SmsGateway
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(SmsGatewayAdapter));

        public async Task SendCode(string mobile, string code)
        {
            await Task.Delay(1000);
            logger.Info($"发送手机验证码 {code} 到 {mobile}");
        }
    }
}
