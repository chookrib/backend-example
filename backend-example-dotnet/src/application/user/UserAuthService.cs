using src.domain.user;
using src.utility;

namespace src.application.user
{
    /// <summary>
    /// 用户认证 Service
    /// </summary>
    public class UserAuthService
    {
        private readonly UserRepository userRepository;
        private readonly int jwtTtlMinute;
        private readonly string jwtSecret;

        public UserAuthService(IConfiguration configuration, UserRepository userRepository)
        {
            if (!ValueUtility.IsEmptyString(configuration.GetValue<string>("App:JwtSecret", string.Empty)))
            {
                this.jwtSecret = configuration.GetValue<string>("App:JwtSecret", string.Empty);
            }
            else
            {
                throw new ApplicationException("App:JwtSecret 配置错误");
            }
            try
            {
                this.jwtTtlMinute = CryptoUtility.JwtTtlToMinute(
                    configuration.GetValue<string>("App:JwtTtl", string.Empty)
                    );
            }
            catch
            {
                throw new ApplicationException("App:JwtTtl 配置错误");
            }
            this.userRepository = userRepository;
        }

        /// <summary>
        /// 登录，返回 AccessToken
        /// </summary>
        public async Task<string> Login(string username, string password)
        {
            User? user = await userRepository.SelectByUsername(username);
            if (user == null || !user.IsPasswordMatch(password))
                throw new ApplicationException("密码错误");
            return CryptoUtility.JwtEncode(new Dictionary<string, object>()
            {
                { "id", user.Id }
            }, jwtSecret, DateTime.Now.AddMinutes(jwtTtlMinute));
        }

        /// <summary>
        /// 根据 AccessToken 获取登录用户 Id
        /// </summary>
        public string GetLoginUserId(string accessToken)
        {
            if (ValueUtility.IsEmptyString(accessToken))
            {
                return string.Empty;
            }

            try
            {
                IDictionary<string, object> payload = CryptoUtility.JwtDecode(accessToken, jwtSecret);
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

        /// <summary>
        /// 根据 AccessToken 获取登录用户
        /// </summary>
        public async Task<User?> GetLoginUser(string accessToken)
        {
            string loginUserId = GetLoginUserId(accessToken);
            if (ValueUtility.IsEmptyString(loginUserId))
            {
                return null;
            }

            return await userRepository.SelectById(loginUserId);
        }
    }
}
