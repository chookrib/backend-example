using log4net;
using Microsoft.AspNetCore.Mvc;
using src.application.user;

namespace src.adapter.driving.user
{
    /// <summary>
    /// 用户管理 Controller
    /// </summary>
    public class UserManageController : ControllerBase
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(UserManageController));
        private readonly UserAuthService userAuthService;
        private readonly UserQueryHandler userQueryHandler;
        private readonly UserManageService userManageService;

        public UserManageController(UserAuthService userAuthService, UserQueryHandler userQueryHandler, UserManageService userManageService)
        {
            this.userAuthService = userAuthService;
            this.userQueryHandler = userQueryHandler;
            this.userManageService = userManageService;
        }

        /// <summary>
        /// 用户列表
        /// </summary>
        [HttpPost("/api/admin/user/list")]
        public async Task<Result> UserList()
        {
            await RequestAuthHelper.RequireLoginUserAdmin(Request, userAuthService);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            int pageNum = RequestValueHelper.GetRequestJsonIntOrDefault(requestJson, 1, "pageNum");
            int pageSize = RequestValueHelper.GetRequestJsonIntOrDefault(requestJson, 1, "pageSize");

            //JsonNode? criteriaJson = requestJson["criteria"];
            UserQueryCriteria criteria = new UserQueryCriteria();
            //if (criteriaJson != null)
            //{
            //string? keyword = criteriaJson["keyword"]?.GetValue<string?>()?.Trim();
            string keyword = RequestValueHelper.GetRequestJsonStringTrimOrEmpty(requestJson, "criteria", "keyword");
            criteria.Keyword = keyword;
            //}

            int totalCount = await userQueryHandler.QueryCount(criteria);
            var paging = RequestValueHelper.FixPaging(pageNum, pageSize, totalCount);
            IList<UserDto> list = await userQueryHandler.QueryByPage(paging.pageNum, paging.pageSize, criteria);
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
            await RequestAuthHelper.RequireLoginUserAdmin(Request, userAuthService);

            string id = RequestValueHelper.GetRequestParamStringTrimReq(Request, "id");
            UserDto userDto = await userQueryHandler.QueryByIdReq(id);
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
            await RequestAuthHelper.RequireLoginUserAdmin(Request, userAuthService);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string username = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "username");
            string password = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "password");
            string nickname = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "nickname");
            string mobile = RequestValueHelper.GetRequestJsonStringTrimOrEmpty(requestJson, "mobile");

            string userId = await userManageService.CreateUser(username, password, nickname, mobile);
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
            await RequestAuthHelper.RequireLoginUserAdmin(Request, userAuthService);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string id = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "id");
            string username = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "username");
            string nickname = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "nickname");
            string mobile = RequestValueHelper.GetRequestJsonStringTrimOrEmpty(requestJson, "mobile");

            await userManageService.ModifyUser(id, username, nickname, mobile);
            return Result.Ok();
        }


        /// <summary>
        /// 删除用户
        /// </summary>
        [HttpPost("/api/admin/user/remove")]
        public async Task<Result> UserRemove()
        {
            await RequestAuthHelper.RequireLoginUserAdmin(Request, userAuthService);

            var requestJson = await RequestValueHelper.GetRequestJsonAsync(Request);
            string id = RequestValueHelper.GetRequestJsonStringTrimReq(requestJson, "id");

            await userManageService.RemoveUser(id);
            return Result.Ok();
        }
    }
}