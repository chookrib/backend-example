using DddExample.Domain;
using DddExample.Utility;

namespace DddExample.Application
{
    /// <summary>
    /// 用户认证 Service
    /// </summary>
    public class UserAuthService
    {
        private readonly UserRepository userRepository;
        private readonly int jwtExpiresDay;
        private readonly string jwtSecret;

        public UserAuthService(IConfiguration configuration, UserRepository userRepository)
        {
            this.jwtExpiresDay = configuration.GetValue<int>("app:user-jwt-expires-day");
            this.jwtSecret = configuration.GetValue<string>("app:user-jwt-secret", string.Empty);
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
            }, DateTime.Now.AddDays(this.jwtExpiresDay), this.jwtSecret);
        }

        /// <summary>
        /// 根据 AccessToken 获取登录用户 Id
        /// </summary>
        public string GetLoginUserId(string accessToken)
        {
            try
            {
                Dictionary<string, string> payload = CryptoUtility.DecodeJwt(accessToken, this.jwtSecret);
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
