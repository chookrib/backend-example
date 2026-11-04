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

        private readonly RequestIdentityResolver requestIdentityResolver;
        private readonly UserAuthService userAuthService;
        private readonly UserQueryHandler userQueryHandler;
        private readonly UserManageService userManageService;

        public UserManageController(
            RequestIdentityResolver requestIdentityResolver,
            UserAuthService userAuthService,
            UserQueryHandler userQueryHandler,
            UserManageService userManageService
            )
        {
            this.requestIdentityResolver = requestIdentityResolver;
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
            await requestIdentityResolver.CheckAdminPermission();

            var requestJson = await RequestValueExtractor.ParseRequestJsonAsync(Request);
            int pageNum = RequestValueExtractor.GetRequestJsonIntOrDefault(requestJson, 1, "pageNum");
            int pageSize = RequestValueExtractor.GetRequestJsonIntOrDefault(requestJson, 1, "pageSize");

            //JsonNode? criteriaJson = requestJson["criteria"];
            UserQueryCriteria criteria = new UserQueryCriteria();
            //if (criteriaJson != null)
            //{
            //string? keyword = criteriaJson["keyword"]?.GetValue<string?>()?.Trim();
            string keyword = RequestValueExtractor.GetRequestJsonStringTrimOrEmpty(requestJson, "criteria", "keyword");
            criteria.Keyword = keyword;
            //}

            OrderedDictionary<string, string> sortOptions = RequestValueExtractor.GetRequestJsonSortOptions(requestJson, "sort");

            int totalCount = await userQueryHandler.QueryCount(criteria);
            var paging = RequestValueExtractor.NormalizePaging(pageNum, pageSize, totalCount);
            IList<UserDto> list = await userQueryHandler.QueryPage(paging.pageNum, paging.pageSize, criteria, sortOptions);
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
            await requestIdentityResolver.CheckAdminPermission();

            string id = RequestValueExtractor.GetRequestParamStringTrimRequired(Request, "id");
            UserDto userDto = await userQueryHandler.QueryByIdRequired(id);
            return Result.OkData(new
            {
                item = userDto
            });
        }

        /// <summary>
        /// 创建用户
        /// </summary>
        [HttpPost("/api/admin/user/create")]
        public async Task<Result> UserCreate()
        {
            await requestIdentityResolver.CheckAdminPermission();

            var requestJson = await RequestValueExtractor.ParseRequestJsonAsync(Request);
            string username = RequestValueExtractor.GetRequestJsonStringTrimRequired(requestJson, "username");
            string password = RequestValueExtractor.GetRequestJsonStringTrimRequired(requestJson, "password");
            string nickname = RequestValueExtractor.GetRequestJsonStringTrimRequired(requestJson, "nickname");
            string mobile = RequestValueExtractor.GetRequestJsonStringTrimOrEmpty(requestJson, "mobile");

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
            await requestIdentityResolver.CheckAdminPermission();

            var requestJson = await RequestValueExtractor.ParseRequestJsonAsync(Request);
            string id = RequestValueExtractor.GetRequestJsonStringTrimRequired(requestJson, "id");
            string username = RequestValueExtractor.GetRequestJsonStringTrimRequired(requestJson, "username");
            string nickname = RequestValueExtractor.GetRequestJsonStringTrimRequired(requestJson, "nickname");
            string mobile = RequestValueExtractor.GetRequestJsonStringTrimOrEmpty(requestJson, "mobile");

            await userManageService.ModifyUser(id, username, nickname, mobile);
            return Result.Ok();
        }


        /// <summary>
        /// 删除用户
        /// </summary>
        [HttpPost("/api/admin/user/remove")]
        public async Task<Result> UserRemove()
        {
            await requestIdentityResolver.CheckAdminPermission();

            var requestJson = await RequestValueExtractor.ParseRequestJsonAsync(Request);
            string id = RequestValueExtractor.GetRequestJsonStringTrimRequired(requestJson, "id");

            await userManageService.RemoveUser(id);
            return Result.Ok();
        }
    }
}