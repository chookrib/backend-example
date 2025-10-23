using System.Collections;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;

using Microsoft.IdentityModel.Tokens;

namespace DddExample.Utility
{
    /// <summary>
    /// 加密 Utility
    /// </summary>
    public class CryptoUtility
    {
        /// <summary>
        /// JWT 编码
        /// </summary>
        public static string EncodeJwt(Dictionary<string, string> payload, DateTime expiresAt, string secret)
        {
            //var claims = payload.Select(kvp => new Claim(kvp.Key, kvp.Value?.ToString() ?? "")).ToList();
            var claims = payload.Select(kvp => new Claim(kvp.Key, kvp.Value)).ToList();
            var securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secret));
            var credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);

            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(claims),
                Expires = expiresAt,
                SigningCredentials = credentials
            };

            var tokenHandler = new JwtSecurityTokenHandler();
            var token = tokenHandler.CreateToken(tokenDescriptor);
            return tokenHandler.WriteToken(token);
        }

        /// <summary>
        /// JWT 解码
        /// </summary>
        public static Dictionary<string, string> DecodeJwt(string jwt, string secret)
        {
            var tokenHandler = new JwtSecurityTokenHandler();
            var key = Encoding.UTF8.GetBytes(secret);
            var validationParameters = new TokenValidationParameters
            {
                ValidateIssuer = false,
                ValidateAudience = false,
                ValidateLifetime = true,
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(key),
                //ClockSkew = TimeSpan.FromMinutes(5) // 可选，默认5分钟
                ClockSkew = TimeSpan.Zero
            };

            tokenHandler.ValidateToken(jwt, validationParameters, out SecurityToken validatedToken);
            var jwtToken = validatedToken as JwtSecurityToken;
            if (jwtToken == null)
                //throw new SecurityTokenException("无效的 JWT");
                throw new Exception("无效的 JWT");

            return jwtToken.Claims.ToDictionary(c => c.Type, c => c.Value);
        }

        /// <summary>
        /// MD5 编码
        /// </summary>
        public static string EncodeMd5(string input)
        {
            using var md5 = MD5.Create();
            var inputBytes = Encoding.UTF8.GetBytes(input);
            var hashBytes = md5.ComputeHash(inputBytes);
            return Convert.ToHexString(hashBytes);
        }
    }
}
