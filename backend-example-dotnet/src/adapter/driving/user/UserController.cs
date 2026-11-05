using log4net;
using Microsoft.AspNetCore.Mvc;
using src.application.user;
using src.utility;


namespace src.adapter.driving.user
{
    /// <summary>
    /// 用户 Controller
    /// </summary>
    public class UserController : ControllerBase
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(UserController));

        private readonly RequestIdentityResolver requestIdentityResolver;
        private readonly UserAuthService userAuthService;
        private readonly UserProfileService userProfileService;
        private readonly UserQueryHandler userQueryHandler;

        public UserController(
            RequestIdentityResolver requestIdentityResolver,
            UserAuthService userAuthService,
            UserProfileService userProfileService,
            UserQueryHandler userQueryHandler
            )
        {
            this.requestIdentityResolver = requestIdentityResolver;
            this.userAuthService = userAuthService;
            this.userProfileService = userProfileService;
            this.userQueryHandler = userQueryHandler;
        }

        /// <summary>
        /// 注册
        /// </summary>
        [HttpPost("/api/user/register")]
        public async Task<Result> Register()
        {
            JsonExtractor jsonExtractor = await RequestValueResolver.BuildJsonExtractor(Request);
            string username = jsonExtractor.GetNotBlankStringOrThrow("username", "用户名不能为空");
            string password = jsonExtractor.GetNotBlankStringOrThrow("password", "密码不能为空");
            string confirmPassword = jsonExtractor.GetNotBlankStringOrThrow("confirmPassword", "确认密码不能为空");
            string nickname = jsonExtractor.GetNotBlankStringOrThrow("nickname", "昵称不能为空");

            if (confirmPassword != password)
                throw new ControllerException("两次输入的密码不一致");

            string userId = await userProfileService.Register(username, password, nickname);
            return Result.OkData(new { id = userId });
        }

        /// <summary>
        /// 登录
        /// </summary>
        [HttpPost("/api/user/login")]
        public async Task<Result> Login()
        {
            JsonExtractor jsonExtractor = await RequestValueResolver.BuildJsonExtractor(Request);
            string username = jsonExtractor.GetNotBlankStringOrThrow("username", "用户名不能为空");
            string password = jsonExtractor.GetNotBlankStringOrThrow("password", "密码不能为空");

            string accessToken = await userAuthService.Login(username, password);
            return Result.OkData(new { accessToken = accessToken });
        }

        /// <summary>
        /// 取用户资料
        /// </summary>
        [HttpGet("/api/user/profile")]
        public async Task<Result> Profile()
        {
            string userId = requestIdentityResolver.RequireLoginUserId();

            UserDto userDto = await userQueryHandler.QueryByIdOrThrow(userId);
            return Result.OkData(new { profile = userDto});
        }

        /// <summary>
        /// 修改密码
        /// </summary>
        [HttpPost("/api/user/modify-password")]
        public async Task<Result> ModifyPassword()
        {
            string userId = requestIdentityResolver.RequireLoginUserId();

            JsonExtractor jsonExtractor = await RequestValueResolver.BuildJsonExtractor(Request);
            string oldPassword = jsonExtractor.GetNotBlankStringOrThrow("oldPassword", "旧密码不能为空");
            string newPassword = jsonExtractor.GetNotBlankStringOrThrow("newPassword", "新密码不能为空");
            string confirmPassword = jsonExtractor.GetNotBlankStringOrThrow("confirmPassword", "确认密码不能为空");

            if (confirmPassword != newPassword)
                throw new ControllerException("两次输入的密码不一致");

            await userProfileService.ModifyPassword(userId, oldPassword, newPassword);
            return Result.Ok();
        }

        /// <summary>
        /// 修改昵称
        /// </summary>
        [HttpPost("/api/user/modify-nickname")]
        public async Task<Result> ModifyNickname()
        {
            string userId = requestIdentityResolver.RequireLoginUserId();

            JsonExtractor jsonExtractor = await RequestValueResolver.BuildJsonExtractor(Request);
            string nickname = jsonExtractor.GetNotBlankStringOrThrow("nickname", "昵称不能为空");
            await userProfileService.ModifyNickname(userId, nickname);
            return Result.Ok();
        }

        /// <summary>
        /// 发送手机验证码
        /// </summary>
        [HttpPost("/api/user/send-mobile-code")]
        public async Task<Result> SendMobileCode()
        {
            string userId = requestIdentityResolver.RequireLoginUserId();
            await userProfileService.SendMobileCode(userId);
            return Result.Ok();
        }

        /// <summary>
        /// 绑定手机
        /// </summary>
        [HttpPost("/api/user/bind-mobile")]
        public async Task<Result> BindMobile()
        {
            string userId = requestIdentityResolver.RequireLoginUserId();

            JsonExtractor jsonExtractor = await RequestValueResolver.BuildJsonExtractor(Request);
            String mobile = jsonExtractor.GetNotBlankStringOrThrow("mobile", "手机号码不能为空");
            String code = jsonExtractor.GetNotBlankStringOrThrow("code", "验证码不能为空");

            await userProfileService.BindMobile(userId, mobile, code);
            return Result.Ok();
        }
    }
}
