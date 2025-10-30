using System.Collections;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
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
        /// JWT 编码
        /// </summary>
        public static string EncodeJwt(Dictionary<string, string> payload, DateTime expiresAt, string secret)
        {
            if (string.IsNullOrEmpty(secret))
                throw new ArgumentException("jwt 编码 secret 不能为空", nameof(secret));

            byte[] keyBytes = Encoding.UTF8.GetBytes(secret);
            if (keyBytes.Length * 8 < 128)
                throw new ArgumentException($"jwt 编码 secret 长度 {keyBytes.Length * 8} 不足 128 位 (16 字节)", nameof(secret));

            //List<Claim> claims = payload.Select(kvp => new Claim(kvp.Key, kvp.Value?.ToString() ?? "")).ToList();
            List<Claim> claims = payload.Select(kvp => new Claim(kvp.Key, kvp.Value)).ToList();
            SymmetricSecurityKey securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secret));
            SigningCredentials credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);

            SecurityTokenDescriptor tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(claims),
                Expires = expiresAt,
                SigningCredentials = credentials
            };

            JwtSecurityTokenHandler tokenHandler = new JwtSecurityTokenHandler();
            SecurityToken token = tokenHandler.CreateToken(tokenDescriptor);
            return tokenHandler.WriteToken(token);
        }

        /// <summary>
        /// JWT 解码
        /// </summary>
        public static Dictionary<string, string> DecodeJwt(string jwt, string secret)
        {
            if (string.IsNullOrEmpty(secret))
                throw new ArgumentException("jwt 解码 secret 不能为空", nameof(secret));
            
            JwtSecurityTokenHandler tokenHandler = new JwtSecurityTokenHandler();
            byte[] key = Encoding.UTF8.GetBytes(secret);
            
            if (key.Length * 8 < 128)
                throw new ArgumentException($"jwt 解码 secret 长度 {key.Length * 8} 不足 128 位 (16 字节)",
                    nameof(secret));

            TokenValidationParameters validationParameters = new TokenValidationParameters
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
            JwtSecurityToken? jwtToken = validatedToken as JwtSecurityToken;
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
            using MD5 md5 = MD5.Create();
            byte[] inputBytes = Encoding.UTF8.GetBytes(input);
            byte[] hashBytes = md5.ComputeHash(inputBytes);
            return Convert.ToHexString(hashBytes);
        }
    }
}
