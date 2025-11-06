using BackendExample.Utility;

using log4net;

using Microsoft.AspNetCore.Mvc;

namespace BackendExample.Adapter.Driving
{
    /// <summary>
    /// 测试 Crypto Well Known Controller
    /// </summary>
    public class WellKnownTestCryptoController : ControllerBase
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(WellKnownTestCryptoController));

        /// <summary>
        /// JWT 编码
        /// </summary>
        [HttpPost("/.well-known/test/crypto/jwt-encode")]
        public async Task<Result> TestCryptoJwtEncode()
        {
            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string secret = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "secret");
            DateTime expiresAt = RequestValueHelper.GetRequestJsonDateTimeReq(requestJson, "expiresAt");
            IDictionary<string, object> payload = JsonUtility.JsonNodeToDictionary(requestJson["payload"]);
            string token = CryptoUtility.JwtEncode(payload, secret, expiresAt);
            return Result.OkData(new { token });
        }

        /// <summary>
        /// JWT 解码
        /// </summary>
        [HttpPost("/.well-known/test/crypto/jwt-decode")]
        public async Task<Result> TestCryptoJwtDecode()
        {
            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string secret = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "secret");
            string token = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "token");
            IDictionary<string, object> payload = CryptoUtility.JwtDecode(token, secret);
            string payloadString = JsonUtility.Serialize(payload); // 手动序列化，防止受全局 json 转换影响
            return Result.OkData(new { payload = payloadString });
        }

        /// <summary>
        /// MD5 解码
        /// </summary>
        [HttpGet("/.well-known/test/crypto/md5-encode")]
        public Result TestCryptoMd5Encode()
        {
            string text = RequestValueHelper.GetRequestParamStringTrimReq(Request, "text");
            string result = CryptoUtility.Md5Encode(text);
            return Result.OkData(new { result });
        }
    }
}