using BackendExample.Domain;
using BackendExample.Utility;

namespace BackendExample.Application
{
    /// <summary>
    /// 用户认证 Service
    /// </summary>
    public class UserAuthService
    {
        private readonly UserRepository userRepository;
        private readonly int jwtExpiresDay;
        private readonly string jwtSecretKey;

        public UserAuthService(IConfiguration configuration, UserRepository userRepository)
        {
            int? jwtExpiresDayValue = ValueUtility.ToIntOrNull(
                configuration.GetValue<string>("App:JwtExpiresDay", string.Empty)
                );
            if (!jwtExpiresDayValue.HasValue || jwtExpiresDayValue < 0)
                throw new ApplicationException("App:JwtExpiresDay 配置错误");
            this.jwtExpiresDay = jwtExpiresDayValue.Value;
            this.jwtSecretKey = configuration.GetValue<string>("App:JwtSecretKey", string.Empty);
            if(ValueUtility.IsBlank(this.jwtSecretKey))
                throw new ApplicationException("App:JwtSecretKey 配置错误");
            // secret key 必须要大于 128 位 (16 字节)
            if(this.jwtSecretKey.Length < 16)
                throw new ApplicationException("App:JwtSecretKey 配置错误，不足 16 位");

            this.userRepository = userRepository;
        }

        /// <summary>
        /// 登录，返回 AccessToken
        /// </summary>
        public async Task<string> Login(string username, string password)
        {
            User? user = await this.userRepository.SelectByUsername(username);
            if (user == null || !user.IsPasswordMatch(password))
                throw new ApplicationException("密码错误");
            return CryptoUtility.EncodeJwt(new Dictionary<string, string>()
            {
                { "id", user.Id }
            }, DateTime.Now.AddDays(this.jwtExpiresDay), this.jwtSecretKey);
        }

        /// <summary>
        /// 根据 AccessToken 获取登录用户 Id
        /// </summary>
        public string GetLoginUserId(string accessToken)
        {
            try
            {
                Dictionary<string, string> payload = CryptoUtility.DecodeJwt(accessToken, this.jwtSecretKey);
                //return payload["id"];
                if (payload.TryGetValue("id", out string? id) && id != null)
                    return id;
                return string.Empty;
            }
            catch
            {
                return string.Empty;
            }
        }
    }
}
