using src.application.user;
using src.utility;

namespace src.adapter.driving
{
    /// <summary>
    /// 请求身份 Resolver
    /// </summary>
    public class RequestIdentityResolver
    {
        private readonly IHttpContextAccessor httpContextAccessor;
        private readonly UserAuthService userAuthService;
        private readonly UserQueryHandler userQueryHandler;

        public RequestIdentityResolver(
            IHttpContextAccessor httpContextAccessor,
            UserAuthService userAuthService,
            UserQueryHandler userQueryHandler
            )
        {
            this.httpContextAccessor = httpContextAccessor;
            this.userAuthService = userAuthService;
            this.userQueryHandler = userQueryHandler;
        }

        private HttpRequest Request => httpContextAccessor.HttpContext!.Request;

        /// <summary>
        /// 获取登录用户 Id，未登录返回空字符串
        /// </summary>
        public string GetLoginUserId()
        {
            //string accessToken = string.Empty;
            //if (Request.Headers.TryGetValue("Access-Token", out StringValues values))
            //    accessToken = values.FirstOrDefault() ?? string.Empty;

            string accessToken = Request.Headers["Access-Token"].FirstOrDefault() ?? string.Empty;
            return userAuthService.GetLoginUserId(accessToken);
        }

        /// <summary>
        /// 获取登录用户 Id，未登录抛出异常
        /// </summary>
        public string RequireLoginUserId()
        {
            string accessToken = Request.Headers["Access-Token"].FirstOrDefault() ?? string.Empty;
            string userId = userAuthService.GetLoginUserId(accessToken);
            if (ValueUtility.IsEmptyString(userId))
                throw new NotLoginException();
            return userId;
        }

        /// <summary>
        /// 判断管理员权限
        /// </summary>
        public async Task CheckAdminPermission()
        {
            string userId = RequireLoginUserId();
            UserDto? user = await userQueryHandler.QueryById(userId);
            if (user == null || !user.IsAdmin)
                throw new NotLoginException();
        }

    }
}
