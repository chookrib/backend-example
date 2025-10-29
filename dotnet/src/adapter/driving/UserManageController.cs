using System.Text.Json.Nodes;
using DddExample.Application;
using Microsoft.AspNetCore.Mvc;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 用户管理 Controller
    /// </summary>
    public class UserManageController : ControllerBase
    {
        private readonly UserQueryHandler userQueryHandler;
        private readonly UserManageService userManageService;

        public UserManageController(UserQueryHandler userQueryHandler, UserManageService userManageService)
        {
            this.userQueryHandler = userQueryHandler;
            this.userManageService = userManageService;
        }

        /// <summary>
        /// 用户列表
        /// </summary>
        [HttpPost("/api/admin/user/list")]
        public Result UserList()
        {
            RequestAuthHelper.RequireLoginUserAdmin(Request);

            var requestJson = RequestValueHelper.GetRequestJson(Request);
            int pageNum = RequestValueHelper.GetRequestJsonInt(requestJson, 1, "pageNum");
            int pageSize = RequestValueHelper.GetRequestJsonInt(requestJson, 1, "pageSize");

            //JsonNode? criteriaJson = requestJson["criteria"];
            UserQueryCriteria criteria = new UserQueryCriteria();
            //if (criteriaJson != null)
            //{
            //string? keyword = criteriaJson["keyword"]?.GetValue<string?>()?.Trim();
            string keyword = RequestValueHelper.GetRequestJsonStringTrimOrEmpty(requestJson, "criteria", "keyword");
            criteria.Keyword = keyword;
            //}

            int totalCount = this.userQueryHandler.QueryCount(criteria);
            var paging = RequestValueHelper.FixPaging(pageNum, pageSize, totalCount);
            IList<UserDto> list = this.userQueryHandler.QueryByPage(paging.pageNum, paging.pageSize, criteria);
            return Result.OkData(new
            {
                list = list,
                paging = new
                {
                    pageNum = paging.pageNum,
                    pageSize = paging.pageSize,
                    totalCount = paging.totalCount
                }
            });
        }

        /// <summary>
        /// 用户详情
        /// </summary>
        [HttpGet("/api/admin/user/get")]
        public Result UserGet()
        {
            RequestAuthHelper.RequireLoginUserAdmin(Request);

            string id = RequestValueHelper.GetRequestParamStringTrimReq(Request, "id");
            UserDto userDto = this.userQueryHandler.QueryByIdReq(id);
            return Result.OkData(new
            {
                detail = userDto
            });
        }

        /// <summary>
        /// 创建用户
        /// </summary>
        [HttpPost("/api/admin/user/create")]
        public Result UserCreate()
        {
            RequestAuthHelper.RequireLoginUserAdmin(Request);

            var requestJson = RequestValueHelper.GetRequestJson(Request);
            string username = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "username");
            string password = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "password");
            string nickname = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "nickname");
            string mobile = RequestValueHelper.GetRequestJsonStringTrimOrEmpty(requestJson, "mobile");

            string userId = this.userManageService.CreateUser(username, password, nickname, mobile);
            return Result.OkData(new
            {
                id = userId
            });
        }

        /// <summary>
        /// 修改用户
        /// </summary>
        [HttpPost("/api/admin/user/modify")]
        public Result UserModify()
        {
            RequestAuthHelper.RequireLoginUserAdmin(Request);

            var requestJson = RequestValueHelper.GetRequestJson(Request);
            string id = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "id");
            string username = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "username");
            string nickname = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "nickname");
            string mobile = RequestValueHelper.GetRequestJsonStringTrimOrEmpty(requestJson, "mobile");

            this.userManageService.ModifyUser(id, username, nickname, mobile);
            return Result.Ok();
        }


        /// <summary>
        /// 删除用户
        /// </summary>
        [HttpPost("/api/admin/user/remove")]
        public Result UserRemove()
        {
            RequestAuthHelper.RequireLoginUserAdmin(Request);

            var requestJson = RequestValueHelper.GetRequestJson(Request);
            string id = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "id");

            this.userManageService.RemoveUser(id);
            return Result.Ok();
        }
    }
}