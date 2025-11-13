using BackendExample.Utility;

using log4net;

using Microsoft.AspNetCore.Mvc;

namespace BackendExample.Adapter.Driving
{
    /// <summary>
    /// 测试加解密 Controller
    /// </summary>
    public class TestCryptoController : ControllerBase
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(TestCryptoController));

        /// <summary>
        /// JWT 编码
        /// </summary>
        [HttpPost("/api/test/crypto/jwt-encode")]
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
        [HttpPost("/api/test/crypto/jwt-decode")]
        public async Task<Result> TestCryptoJwtDecode()
        {
            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string secret = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "secret");
            string token = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "token");
            IDictionary<string, object> payload = CryptoUtility.JwtDecode(token, secret);

            string headerDecoded = string.Empty;
            string payloadDecoded = string.Empty;
            string[] tokenParts = token.Split('.');
            if (tokenParts.Length > 1)
            {
                headerDecoded = CryptoUtility.Base64Decode(tokenParts[0]);
            }
            if (tokenParts.Length > 2)
            {
                payloadDecoded = CryptoUtility.Base64Decode(tokenParts[1]);
            }

            return Result.OkData(new
            {
                payload = JsonUtility.Serialize(payload), // 手动序列化，防止受全局 json 转换影响,
                headerDecoded = headerDecoded,
                payloadDecoded = payloadDecoded
            });
        }

        /// <summary>
        /// BASE64 编码
        /// </summary>
        [HttpGet("/api/test/crypto/base64-encode")]
        public Result TestCryptoBase64Encode()
        {
            string text = RequestValueHelper.GetRequestParamStringTrimReq(Request, "text");
            string base64 = CryptoUtility.Base64Encode(text);
            return Result.OkData(new { base64 });
        }

        /// <summary>
        /// BASE64 解码
        /// </summary>
        [HttpGet("/api/test/crypto/base64-decode")]
        public Result TestCryptoBaseDecode()
        {
            string base64 = RequestValueHelper.GetRequestParamStringTrimReq(Request, "base64");
            string text = CryptoUtility.Base64Decode(base64);
            return Result.OkData(new { text });
        }

        /// <summary>
        /// MD5 编码
        /// </summary>
        [HttpGet("/api/test/crypto/md5-encode")]
        public Result TestCryptoMd5Encode()
        {
            string text = RequestValueHelper.GetRequestParamStringTrimReq(Request, "text");
            string md5 = CryptoUtility.Md5Encode(text);
            return Result.OkData(new { md5 });
        }
    }
}