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
        public Result Register()
        {
            var requestJson = RequestValueHelper.GetRequestJson(Request);
            string username = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "username");
            string password = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "password");
            string confirmPassword = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "confirmPassword");
            string nickname = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "nickname");

            if (confirmPassword != password)
                throw new ControllerException("两次输入的密码不一致");

            string userId = this.userProfileService.Register(username, password, nickname);
            return Result.OkData(new { id = userId });
        }

        /// <summary>
        /// 登录
        /// </summary>
        [HttpPost("/api/user/login")]
        public Result Login()
        {
            var requestJson = RequestValueHelper.GetRequestJson(Request);
            string username = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "username");
            string password = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "password");

            string accessToken = this.userAuthService.Login(username, password);
            return Result.OkData(new { accessToken = accessToken });
        }

        /// <summary>
        /// 取用户资料
        /// </summary>
        [HttpGet("/api/user/profile")]
        public Result Profile()
        {
            string userId = RequestAuthHelper.RequireLoginUserId(Request);

            UserDto userDto = this.userQueryHandler.QueryByIdReq(userId);
            return Result.OkData(new { profile = userDto});
        }

        /// <summary>
        /// 修改密码
        /// </summary>
        [HttpPost("/api/user/modify-password")]
        public Result ModifyPassword()
        {
            string userId = RequestAuthHelper.RequireLoginUserId(Request);

            var requestJson = RequestValueHelper.GetRequestJson(Request);
            string oldPassword = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "oldPassword");
            string newPassword = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "newPassword");
            string confirmPassword = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "confirmPassword");

            if (confirmPassword != newPassword)
                throw new ControllerException("两次输入的密码不一致");

            this.userProfileService.ModifyPassword(userId, oldPassword, newPassword);
            return Result.Ok();
        }

        /// <summary>
        /// 修改昵称
        /// </summary>
        [HttpPost("/api/user/modify-nickname")]
        public Result ModifyNickname()
        {
            string userId = RequestAuthHelper.RequireLoginUserId(Request);

            var requestJson = RequestValueHelper.GetRequestJson(Request);
            string nickname = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "nickname");
            this.userProfileService.ModifyNickname(userId, nickname);
            return Result.Ok();
        }

        /// <summary>
        /// 发送手机验证码
        /// </summary>
        [HttpPost("/api/user/send-mobile-code")]
        public Result SendMobileCode()
        {
            string userId = RequestAuthHelper.RequireLoginUserId(Request);

            var requestJson = RequestValueHelper.GetRequestJson(Request);
            string mobile = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "mobile");

            this.userProfileService.SendMobileCode(userId, mobile);
            return Result.Ok();
        }

        /// <summary>
        /// 绑定手机
        /// </summary>
        [HttpPost("/api/user/bind-mobile")]
        public Result BindMobile()
        {
            string userId = RequestAuthHelper.RequireLoginUserId(Request);

            var requestJson = RequestValueHelper.GetRequestJson(Request);
            string mobile = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "mobile");
            string code = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "code");
            this.userProfileService.BindMobile(userId, mobile, code);
            return Result.Ok();
        }
    }
}
