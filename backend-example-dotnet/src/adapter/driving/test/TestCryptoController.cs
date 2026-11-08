using log4net;
using Microsoft.AspNetCore.DataProtection.KeyManagement;
using Microsoft.AspNetCore.Mvc;
using src.utility;

namespace src.adapter.driving.test
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
            JsonExtractor jsonExtractor = await RequestValueResolver.BuildJsonExtractor(Request);
            string secret = jsonExtractor.GetNotBlankStringOrThrow("secret", "密钥不能为空");
            DateTime expiresAt = ValueUtility.ToDateTimeOrThrow(jsonExtractor.GetString("expiresAt"), "到期时间无效");
            var payloadNode = jsonExtractor.GetNode("payload");
            IDictionary<string, object> payload = JsonUtility.DeserializeDictionary(payloadNode);
            string token = CryptoUtility.JwtEncode(payload, secret, expiresAt);
            return Result.OkData(new { token });
        }

        /// <summary>
        /// JWT 解码
        /// </summary>
        [HttpPost("/api/test/crypto/jwt-decode")]
        public async Task<Result> TestCryptoJwtDecode()
        {
            JsonExtractor jsonExtractor = await RequestValueResolver.BuildJsonExtractor(Request);
            string secret = jsonExtractor.GetNotBlankStringOrThrow("secret", "密钥不能为空");
            string token = jsonExtractor.GetNotBlankStringOrThrow("token", "令牌不能为空");
            IDictionary<string, object> payloadDecode = CryptoUtility.JwtDecode(token, secret);

            string part0Decoded = string.Empty;
            string part1Decoded = string.Empty;
            string[] tokenParts = token.Split('.');
            if (tokenParts.Length > 1)
            {
                part0Decoded = CryptoUtility.Base64Decode(tokenParts[0]);
            }
            if (tokenParts.Length > 2)
            {
                part1Decoded = CryptoUtility.Base64Decode(tokenParts[1]);
            }

            return Result.OkData(new
            {
                payloadDecode = JsonUtility.Serialize(payloadDecode), // 手动序列化，防止受全局 json 转换影响,
                part0Decoded = part0Decoded,
                part1Decoded = part1Decoded
            });
        }

        /// <summary>
        /// BASE64 编码
        /// </summary>
        [HttpGet("/api/test/crypto/base64-encode")]
        public Result TestCryptoBase64Encode()
        {
            string text = ValueUtility.EmptyIfNull(Request.Query["text"]);
            string base64 = CryptoUtility.Base64Encode(text);
            return Result.OkData(new { base64 });
        }

        /// <summary>
        /// BASE64 解码
        /// </summary>
        [HttpGet("/api/test/crypto/base64-decode")]
        public Result TestCryptoBaseDecode()
        {
            string base64 = ValueUtility.EmptyIfNull(Request.Query["base64"]);
            string text = CryptoUtility.Base64Decode(base64);
            return Result.OkData(new { text });
        }

        /// <summary>
        /// MD5 编码
        /// </summary>
        [HttpGet("/api/test/crypto/md5-encode")]
        public Result TestCryptoMd5Encode()
        {
            string text = ValueUtility.EmptyIfNull(Request.Query["text"]);
            string md5 = CryptoUtility.Md5Encode(text);
            return Result.OkData(new { md5 });
        }
    }
}