using BackendExample.Domain;

namespace BackendExample.Application
{
    /// <summary>
    /// 用户管理 Service
    /// </summary>
    public class UserManageService
    {
        private readonly UserRepository userRepository;
        private readonly UserUniqueSpecification userUniqueSpecification;
        private readonly LockService lockService;

        public UserManageService(
            UserRepository userRepository,
            UserUniqueSpecification userUniqueSpecification,
            LockService lockService)
        {
            this.userRepository = userRepository;
            this.userUniqueSpecification = userUniqueSpecification;
            this.lockService = lockService;
        }

        /// <summary>
        /// 设置用户管理员状态
        /// </summary>
        public async Task SetAdmin(string id, bool isAdmin)
        {
            User user = await this.userRepository.SelectByIdReq(id);
            user.SetAdmin(isAdmin);
            await this.userRepository.Update(user);
        }

        /// <summary>
        /// 创建用户
        /// </summary>
        public async Task<string> CreateUser(string username, string password, string nickname, string mobile)
        {
            await using(await this.lockService.LockAsync(LockKeys.USER))
            {
                User user = await User.Create(IdGenerator.GenerateId(), username, password, nickname, mobile,
                    this.userUniqueSpecification);
                await this.userRepository.Insert(user);
                return user.Id;
            }
        }

        /// <summary>
        /// 修改用户
        /// </summary>
        public async Task ModifyUser(string id, string username, string nickname, string mobile)
        {
            await using (await this.lockService.LockAsync(LockKeys.USER))
            {
                User user = await this.userRepository.SelectByIdReq(id);
                await user.Modify(username, nickname, mobile, this.userUniqueSpecification);
                await this.userRepository.Update(user);
            }
        }

        /// <summary>
        /// 删除用户
        /// </summary>
        public async Task RemoveUser(string id)
        {
            await this.userRepository.DeleteById(id);
        }
    }
}
