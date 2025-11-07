using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Cryptography;
using System.Text;

using Microsoft.IdentityModel.Tokens;

namespace BackendExample.Utility
{
    /// <summary>
    /// 加密 Utility
    /// </summary>
    public class CryptoUtility
    {
        /// <summary>
        /// JWT 编码，secret 要大于16位
        /// </summary>
        public static string JwtEncode(IDictionary<string, object>? payload, string secret, DateTime expiresAt)
        {
            if(payload == null)
                payload = new Dictionary<string, object>();

            var handler = new JwtSecurityTokenHandler();
            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secret));
            var credentials = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            var jwtPayload = new JwtPayload();
            foreach (var pair in payload)
            {
                jwtPayload[pair.Key] = pair.Value;
            }
            jwtPayload["exp"] = new DateTimeOffset(expiresAt).ToUnixTimeSeconds();

            var header = new JwtHeader(credentials);
            var token = new JwtSecurityToken(header, jwtPayload);
            return handler.WriteToken(token);
        }

        /// <summary>
        /// JWT 解码，secret 要大于16位
        /// </summary>
        public static IDictionary<string, object> JwtDecode(string token, string secret)
        {
            var handler = new JwtSecurityTokenHandler();
            var validationParameters = new TokenValidationParameters
            {
                ValidateAudience = false,
                ValidateIssuer = false,
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secret)),
                ValidateLifetime = true,
                // ClockSkew 默认为5分钟 TimeSpan.FromMinutes(5)
                ClockSkew = TimeSpan.Zero
            };

            try
            {
                handler.ValidateToken(token, validationParameters, out SecurityToken validatedToken);
                var jwtToken = validatedToken as JwtSecurityToken;
                if (jwtToken == null)
                {
                    throw new UtilityException("JWT 解码失败");
                }
                return jwtToken.Payload;
            }
            catch (Exception ex)
            {
                throw new UtilityException("JWT 解码失败", ex);
            }
        }

        /// <summary>
        /// JWT 计算过期时长（分钟）
        /// </summary>
        public static int JwtExpiresMinute(string expires)
        {
            var match = System.Text.RegularExpressions.Regex.Match(expires.ToLower(), @"^(\d+)([dhm])$");
            if (!match.Success)
            {
                throw new UtilityException("JWT EXPIRES 配置错误，值应为时长（正整数）加时长单位（d/h/m）");
            }
            int num;
            if (!int.TryParse(match.Groups[1].Value, out num))
            {
                throw new UtilityException("JWT EXPIRES 配置错误，时长应为正整数");
            }
            string unit = match.Groups[2].Value;
            if (unit == "d")
            {
                return num * 24 * 60;
            }
            if (unit == "h")
            {
                return num * 60;
            }
            return num;
        }

        /// <summary>
        /// BASE64 编码
        /// </summary>
        public static string Base64Encode(string text)
        {
            byte[] textBytes = Encoding.UTF8.GetBytes(text);
            return Convert.ToBase64String(textBytes);
        }

        /// <summary>
        /// BASE64 解码
        /// </summary>
        public static string Base64Decode(string base64Text)
        {
            int mod = base64Text.Length % 4;
            if (mod > 0)
            {
                base64Text += new string('=', 4 - mod);
            }
            byte[] bytes = Convert.FromBase64String(base64Text);
            return Encoding.UTF8.GetString(bytes);
        }

        /// <summary>
        /// MD5 编码
        /// </summary>
        public static string Md5Encode(string text)
        {
            using MD5 md5 = MD5.Create();
            byte[] textBytes = Encoding.UTF8.GetBytes(text);
            byte[] hashBytes = md5.ComputeHash(textBytes);
            return Convert.ToHexString(hashBytes).ToLower();    // 默认为大写，转为小写，保持统一
        }
    }
}
