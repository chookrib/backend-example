using log4net;
using Microsoft.AspNetCore.Mvc;
using src.application.user;
using src.utility;

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

            JsonExtractor jsonExtractor = await RequestValueResolver.BuildJsonExtractor(Request);
            int pageNum = RequestValueResolver.NormalizePageNum(jsonExtractor.GetString("pageNum"));
            int pageSize = RequestValueResolver.NormalizePageNum(jsonExtractor.GetString("pageSize"));

            UserQueryCriteria criteria = new UserQueryCriteria();
            criteria.Keyword = jsonExtractor.GetStringOrEmpty("criteria.keyword");

            OrderedDictionary<string, string> sortOptions = RequestValueResolver.GetSortOptions(jsonExtractor.GetNode("sort"));

            int totalCount = await userQueryHandler.QueryCount(criteria);
            var paging = RequestValueResolver.NormalizePaging(pageNum, pageSize, totalCount);
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
            string id = ValueUtility.RequireNotBlank(Request.Query["id"], "Id 不能为空");
            UserDto userDto = await userQueryHandler.QueryByIdOrThrow(id);
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

            JsonExtractor jsonExtractor = await RequestValueResolver.BuildJsonExtractor(Request);
            string username = jsonExtractor.GetNotBlankStringOrThrow("username", "用户名不能为空");
            string password = jsonExtractor.GetNotBlankStringOrThrow("password", "密码不能为空");
            string nickname = jsonExtractor.GetNotBlankStringOrThrow("nickname", "昵称不能为空");
            string mobile = jsonExtractor.GetStringOrEmpty("mobile");
            bool isAdmin = ValueUtility.ToBoolOrThrow(jsonExtractor.GetString("isAdmin"), "是否管理员值无效");

            string userId = await userManageService.CreateUser(username, password, nickname, mobile, isAdmin);
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

            JsonExtractor jsonExtractor = await RequestValueResolver.BuildJsonExtractor(Request);
            string id = jsonExtractor.GetNotBlankStringOrThrow("id", "Id 不能为空");
            string username = jsonExtractor.GetNotBlankStringOrThrow("username", "用户名不能为空");
            string password = jsonExtractor.GetNotBlankStringOrThrow("password", "密码不能为空");
            string nickname = jsonExtractor.GetNotBlankStringOrThrow("nickname", "昵称不能为空");
            string mobile = jsonExtractor.GetStringOrEmpty("mobile");
            bool isAdmin = ValueUtility.ToBoolOrThrow(jsonExtractor.GetString("isAdmin"), "是否管理员值无效");

            await userManageService.ModifyUser(id, username, nickname, mobile, isAdmin);
            return Result.Ok();
        }


        /// <summary>
        /// 删除用户
        /// </summary>
        [HttpPost("/api/admin/user/remove")]
        public async Task<Result> UserRemove()
        {
            await requestIdentityResolver.CheckAdminPermission();

            JsonExtractor jsonExtractor = await RequestValueResolver.BuildJsonExtractor(Request);
            string id = jsonExtractor.GetNotBlankStringOrThrow("id", "Id 不能为空");

            await userManageService.RemoveUser(id);
            return Result.Ok();
        }
    }
}