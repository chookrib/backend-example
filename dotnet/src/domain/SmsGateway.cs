namespace DddExample.Domain
{
    /// <summary>
    /// 短信 Gateway 接口
    /// </summary>
    public interface SmsGateway
    {
        /// <summary>
        /// 发送验证码
        /// </summary>
        void SendCode(string mobile, string code);
    }
}
