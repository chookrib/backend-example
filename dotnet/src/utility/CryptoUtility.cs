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
        public static string EncodeJwt(Dictionary<string, string> payload, DateTime expires, string key)
        {
            //List<Claim> claims = payload.Select(kvp => new Claim(kvp.Key, kvp.Value?.ToString() ?? "")).ToList();
            List<Claim> claims = payload.Select(kvp => new Claim(kvp.Key, kvp.Value)).ToList();
            SymmetricSecurityKey securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(key));
            SigningCredentials credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);

            SecurityTokenDescriptor tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(claims),
                Expires = expires,
                SigningCredentials = credentials
            };

            JwtSecurityTokenHandler tokenHandler = new JwtSecurityTokenHandler();
            SecurityToken token = tokenHandler.CreateToken(tokenDescriptor);
            return tokenHandler.WriteToken(token);
        }

        /// <summary>
        /// JWT 解码
        /// </summary>
        public static Dictionary<string, string> DecodeJwt(string token, string key)
        {
            JwtSecurityTokenHandler tokenHandler = new JwtSecurityTokenHandler();
            byte[] keyBytes = Encoding.UTF8.GetBytes(key);
            TokenValidationParameters validationParameters = new TokenValidationParameters
            {
                ValidateIssuer = false,
                ValidateAudience = false,
                ValidateLifetime = true,
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(keyBytes),
                //ClockSkew = TimeSpan.FromMinutes(5) // 可选，默认5分钟
                ClockSkew = TimeSpan.Zero
            };

            tokenHandler.ValidateToken(token, validationParameters, out SecurityToken validatedToken);
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
