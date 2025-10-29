using DddExample.Application;
using DddExample.Utility;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 请求 Auth Helper
    /// </summary>
    public class RequestAuthHelper
    {
        private readonly UserAuthService userAuthService;
        private readonly UserQueryHandler userQueryHandler;

        private static RequestAuthHelper? INSTANCE;

        public RequestAuthHelper(UserAuthService userAuthService, UserQueryHandler userQueryHandler)
        {
            this.userAuthService = userAuthService;
            this.userQueryHandler = userQueryHandler;
            INSTANCE = this;
        }

        /// <summary>
        /// 获取静态实例
        /// </summary>
       private static RequestAuthHelper GetInstance()
        {
            if (INSTANCE == null)
                throw new ControllerException("RequestAuthHelper 静态实例未初始化");
            return INSTANCE;
        }

        /// <summary>
        /// 获取登录用户Id，未登录返回空字符串
        /// </summary>
        public static string getLoginUserId(HttpRequest request)
        {
            string? accessToken = null;
            if (request.Headers.TryGetValue("Access-Token", out var values))
                accessToken = values.FirstOrDefault();

            if (accessToken == null)
                return string.Empty;

            return GetInstance().userAuthService.GetLoginUserId(accessToken);
        }

        /// <summary>
        /// 获取登录用户Id，未登录抛异常
        /// </summary>
        public static string RequireLoginUserId(HttpRequest request)
        {
            string userId = getLoginUserId(request);
            if (ValueUtility.IsBlank(userId))
                throw new NotLoginException();
            return userId;
        }

        /// <summary>
        /// 获取登录用户（管理员），失败抛异常
        /// </summary>
        public static UserDto RequireLoginUserAdmin(HttpRequest request)
        {
            string userId = RequireLoginUserId(request);
            UserDto? userDto = GetInstance().userQueryHandler.QueryById(userId);
            if (userDto == null || !userDto.IsAdmin)
                throw new NotLoginException();
            return userDto;
        }
    }
}
