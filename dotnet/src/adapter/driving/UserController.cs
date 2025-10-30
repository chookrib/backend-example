using System;
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

        /// <summary>
        /// 注册
        /// </summary>
        [HttpPost("/api/user/register")]
        public async Task<Result> Register()
        {
            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string username = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "username");
            string password = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "password");
            string confirmPassword = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "confirmPassword");
            string nickname = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "nickname");

            if (confirmPassword != password)
                throw new ControllerException("两次输入的密码不一致");

            string userId = await this.userProfileService.Register(username, password, nickname);
            return Result.OkData(new { id = userId });
        }

        /// <summary>
        /// 登录
        /// </summary>
        [HttpPost("/api/user/login")]
        public async Task<Result> Login()
        {
            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string username = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "username");
            string password = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "password");

            string accessToken = await this.userAuthService.Login(username, password);
            return Result.OkData(new { accessToken = accessToken });
        }

        /// <summary>
        /// 取用户资料
        /// </summary>
        [HttpGet("/api/user/profile")]
        public async Task<Result> Profile()
        {
            string userId = RequestAuthHelper.RequireLoginUserId(Request);

            UserDto userDto = await this.userQueryHandler.QueryByIdReq(userId);
            return Result.OkData(new { profile = userDto});
        }

        /// <summary>
        /// 修改密码
        /// </summary>
        [HttpPost("/api/user/modify-password")]
        public async Task<Result> ModifyPassword()
        {
            string userId = RequestAuthHelper.RequireLoginUserId(Request);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string oldPassword = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "oldPassword");
            string newPassword = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "newPassword");
            string confirmPassword = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "confirmPassword");

            if (confirmPassword != newPassword)
                throw new ControllerException("两次输入的密码不一致");

            await this.userProfileService.ModifyPassword(userId, oldPassword, newPassword);
            return Result.Ok();
        }

        /// <summary>
        /// 修改昵称
        /// </summary>
        [HttpPost("/api/user/modify-nickname")]
        public async Task<Result> ModifyNickname()
        {
            string userId = RequestAuthHelper.RequireLoginUserId(Request);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string nickname = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "nickname");
            await this.userProfileService.ModifyNickname(userId, nickname);
            return Result.Ok();
        }

        /// <summary>
        /// 发送手机验证码
        /// </summary>
        [HttpPost("/api/user/send-mobile-code")]
        public async Task<Result> SendMobileCode()
        {
            string userId = RequestAuthHelper.RequireLoginUserId(Request);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string mobile = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "mobile");

            await this.userProfileService.SendMobileCode(userId, mobile);
            return Result.Ok();
        }

        /// <summary>
        /// 绑定手机
        /// </summary>
        [HttpPost("/api/user/bind-mobile")]
        public async Task<Result> BindMobile()
        {
            string userId = RequestAuthHelper.RequireLoginUserId(Request);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string mobile = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "mobile");
            string code = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "code");
            await this.userProfileService.BindMobile(userId, mobile, code);
            return Result.Ok();
        }
    }
}
