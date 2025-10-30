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
        public async Task<Result> UserList()
        {
            await RequestAuthHelper.RequireLoginUserAdmin(Request);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
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

            int totalCount = await this.userQueryHandler.QueryCount(criteria);
            var paging = RequestValueHelper.FixPaging(pageNum, pageSize, totalCount);
            IList<UserDto> list = await this.userQueryHandler.QueryByPage(paging.pageNum, paging.pageSize, criteria);
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
        public async Task<Result> UserGet()
        {
            await RequestAuthHelper.RequireLoginUserAdmin(Request);

            string id = RequestValueHelper.GetRequestParamStringTrimReq(Request, "id");
            UserDto userDto = await this.userQueryHandler.QueryByIdReq(id);
            return Result.OkData(new
            {
                detail = userDto
            });
        }

        /// <summary>
        /// 创建用户
        /// </summary>
        [HttpPost("/api/admin/user/create")]
        public async Task<Result> UserCreate()
        {
            await RequestAuthHelper.RequireLoginUserAdmin(Request);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string username = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "username");
            string password = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "password");
            string nickname = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "nickname");
            string mobile = RequestValueHelper.GetRequestJsonStringTrimOrEmpty(requestJson, "mobile");

            string userId = await this.userManageService.CreateUser(username, password, nickname, mobile);
            return Result.OkData(new
            {
                id = userId
            });
        }

        /// <summary>
        /// 修改用户
        /// </summary>
        [HttpPost("/api/admin/user/modify")]
        public async Task<Result> UserModify()
        {
            await RequestAuthHelper.RequireLoginUserAdmin(Request);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string id = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "id");
            string username = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "username");
            string nickname = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "nickname");
            string mobile = RequestValueHelper.GetRequestJsonStringTrimOrEmpty(requestJson, "mobile");

            await this.userManageService.ModifyUser(id, username, nickname, mobile);
            return Result.Ok();
        }


        /// <summary>
        /// 删除用户
        /// </summary>
        [HttpPost("/api/admin/user/remove")]
        public async Task<Result> UserRemove()
        {
            await RequestAuthHelper.RequireLoginUserAdmin(Request);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string id = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "id");

            await this.userManageService.RemoveUser(id);
            return Result.Ok();
        }
    }
}