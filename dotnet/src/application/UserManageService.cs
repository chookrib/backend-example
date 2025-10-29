using DddExample.Domain;

namespace DddExample.Application
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
        public void SetAdmin(string id, bool isAdmin)
        {
            User user = this.userRepository.SelectByIdReq(id);
            user.SetAdmin(isAdmin);
            this.userRepository.Update(user);
        }

        /// <summary>
        /// 创建用户
        /// </summary>
        public string CreateUser(string username, string password, string nickname, string mobile)
        {
            return this.lockService.ExecuteWithLock(LockKeys.USER, () =>
            {
                User user = User.CreateUser(IdGenerator.GenerateId(), username, password, nickname, mobile,
                    this.userUniqueSpecification);
                this.userRepository.Insert(user);
                return user.Id;
            });
        }

        /// <summary>
        /// 修改用户
        /// </summary>
        public void ModifyUser(string id, string username, string nickname, string mobile)
        {
            this.lockService.ExecuteWithLock(LockKeys.USER, () =>
            {
                User user = this.userRepository.SelectByIdReq(id);
                user.Modify(username, nickname, mobile, this.userUniqueSpecification);
                this.userRepository.Update(user);
            });
        }

        /// <summary>
        /// 删除用户
        /// </summary>
        public void RemoveUser(string id)
        {
            this.userRepository.DeleteById(id);
        }
    }
}
