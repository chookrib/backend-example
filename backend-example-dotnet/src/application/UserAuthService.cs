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
        private readonly int jwtExpiresMinute;
        private readonly string jwtSecret;

        public UserAuthService(IConfiguration configuration, UserRepository userRepository)
        {
            if (!ValueUtility.IsBlank(configuration.GetValue<string>("App:JwtSecret", string.Empty)))
            {
                this.jwtSecret = configuration.GetValue<string>("App:JwtSecret", string.Empty);
            }
            else
            {
                throw new ApplicationException("App:JwtSecret 配置错误");
            }
            try
            {
                this.jwtExpiresMinute = CryptoUtility.JwtExpiresMinute(
                    configuration.GetValue<string>("App:JwtExpires", string.Empty)
                    );
            }
            catch
            {
                throw new ApplicationException("App:JwtExpires 配置错误");
            }
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
            return CryptoUtility.JwtEncode(new Dictionary<string, object>()
            {
                { "id", user.Id }
            }, this.jwtSecret, DateTime.Now.AddMinutes(this.jwtExpiresMinute));
        }

        /// <summary>
        /// 根据 AccessToken 获取登录用户 Id
        /// </summary>
        public string GetLoginUserId(string accessToken)
        {
            try
            {
                IDictionary<string, object> payload = CryptoUtility.JwtDecode(accessToken, this.jwtSecret);
                //return payload["id"];
                if (payload.TryGetValue("id", out object? id) && id != null)
                    return id.ToString()!;
                return string.Empty;
            }
            catch
            {
                return string.Empty;
            }
        }
    }
}
