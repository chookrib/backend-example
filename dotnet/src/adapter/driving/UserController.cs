using System.Text.Json;
using DddExample.Application;
using Microsoft.AspNetCore.Mvc;


namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 用户 Controller
    /// </summary>
    public class UserController : ControllerBase
    {
        private readonly UserAuthService userAuthService;
        private readonly UserProfileService userProfileService;
        private readonly UserQueryHandler userQueryHandler;

        public UserController(
            UserAuthService userAuthService,
            UserProfileService userProfileService,
            UserQueryHandler userQueryHandler
            )
        {
            this.userAuthService = userAuthService;
            this.userProfileService = userProfileService;
            this.userQueryHandler = userQueryHandler;
        }

        //[HttpPost("/api/user/register")]
        //public Result Register()
        //{
        //    JObject requestJson = RequestValueHelper.GetRequestJson(Request);


        //    string username = (requestJson.Value<string>("username") ?? "").Trim();
        //    string password = (requestJson.Value<string>("password") ?? "").Trim();
        //    string confirmPassword = (requestJson.Value<string>("confirmPassword") ?? "").Trim();
        //    string nickname = (requestJson.Value<string>("nickname") ?? "").Trim();

        //    try
        //    {
        //        string userId = userProfileService.Register(username, password, nickname);
        //        return Result.Ok("注册成功", new { UserId = userId });
        //    }
        //    catch (Exception ex)
        //    {
        //        return Result.Error(500, ex.Message);
        //    }
        //}


        //[HttpPost("/api/user/register")]
        //public Result Register([FromBody] JsonElement body)
        //{
        //    // 假设 body 是 JSON 格式，解析字段
        //    string username = body.GetProperty("username").GetString() ?? "";
        //    string password = body.GetProperty("password").GetString() ?? "";
        //    string nickname = body.GetProperty("nickname").GetString() ?? "";

        //    // 业务逻辑处理
        //    try
        //    {
        //        string userId = userProfileService.Register(username, password, nickname);
        //        return Result.Ok("注册成功", new { UserId = userId });
        //    }
        //    catch (Exception ex)
        //    {
        //        return Result.Error(500, ex.Message);
        //    }
        //}
    }
}
