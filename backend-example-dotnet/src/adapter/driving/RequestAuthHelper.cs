using BackendExample.Application;
using BackendExample.Domain;
using BackendExample.Utility;

namespace BackendExample.Adapter.Driving
{
    /// <summary>
    /// 请求 Auth Helper
    /// </summary>
    public class RequestAuthHelper
    {
        /// <summary>
        /// 获取登录用户Id，未登录返回空字符串
        /// </summary>
        public static string GetLoginUserId(HttpRequest request, UserAuthService userAuthService)
        {
            //string accessToken = string.Empty;
            //if (request.Headers.TryGetValue("Access-Token", out StringValues values))
            //    accessToken = values.FirstOrDefault() ?? string.Empty;

            string accessToken = request.Headers["Access-Token"].FirstOrDefault() ?? string.Empty;
            return userAuthService.GetLoginUserId(accessToken);
        }

        /// <summary>
        /// 获取登录用户Id，未登录抛异常
        /// </summary>
        public static string RequireLoginUserId(HttpRequest request, UserAuthService userAuthService)
        {
            string accessToken = request.Headers["Access-Token"].FirstOrDefault() ?? string.Empty;
            string userId = userAuthService.GetLoginUserId(accessToken);
            if (ValueUtility.IsEmptyString(userId))
                throw new NotLoginException();
            return userId;
        }

        /// <summary>
        /// 获取登录用户（管理员），失败抛异常
        /// </summary>
        public static async Task<User> RequireLoginUserAdmin(HttpRequest request, UserAuthService userAuthService)
        {
            string accessToken = request.Headers["Access-Token"].FirstOrDefault() ?? string.Empty;
            User? user = await userAuthService.GetLoginUser(accessToken);
            if (user == null || !user.IsAdmin)
                throw new NotLoginException();
            return user;
        }
    }
}