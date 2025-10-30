using System;
using System.Collections.Concurrent;
using BackendExample.Domain;
using BackendExample.Utility;

namespace BackendExample.Application
{
    /// <summary>
    /// 用户资料 Service
    /// </summary>
    public class UserProfileService
    {
        private readonly UserRepository userRepository;
        private readonly UserUniqueSpecification userUniqueSpecification;
        private readonly SmsGateway smsGateway;
        private readonly LockService lockService;

        private ConcurrentDictionary<string, string> mobileCodeDict = new ConcurrentDictionary<string, string>();

        public UserProfileService(
            UserRepository userRepository,
            UserUniqueSpecification userUniqueSpecification,
            SmsGateway smsGateway,
            LockService lockService
            )
        {
            this.userRepository = userRepository;
            this.userUniqueSpecification = userUniqueSpecification;
            this.smsGateway = smsGateway;
            this.lockService = lockService;
        }

        /// <summary>
        /// 注册
        /// </summary>
        public async Task<string> Register(string username, string password, string nickname)
        {
            return await this.lockService.GetWithLockAsync(LockKeys.USER, async () =>
            {
                User user = await User.RegisterUser(IdGenerator.GenerateId(), username, password, nickname,
                    this.userUniqueSpecification);
                await this.userRepository.Insert(user);
                return user.Id;
            });
        }

        /// <summary>
        /// 修改密码
        /// </summary>
        public async Task ModifyPassword(string userId, string oldPassword, string newPassword)
        {
            User user = await this.userRepository.SelectByIdReq(userId);
            user.ModifyPassword(oldPassword, newPassword);
            await this.userRepository.Update(user);
        }

        /// <summary>
        /// 修改昵称
        /// </summary>
        public async Task ModifyNickname(string userId, string nickname)
        {
            await this.lockService.RunWithLockAsync(LockKeys.USER, async () =>
            {
                User user = await this.userRepository.SelectByIdReq(userId);
                await user.ModifyNickname(nickname, this.userUniqueSpecification);
                await this.userRepository.Update(user);
            });
        }

        /// <summary>
        /// 发送手机验证码
        /// </summary>
        public async Task SendMobileCode(string userId, string mobile)
        {
            if (ValueUtility.IsBlank(mobile))
                throw new ApplicationException("手机号不能为空");

            User user = await this.userRepository.SelectByIdReq(userId);
            string code = new Random().Next(100000, 1000000).ToString();
            this.mobileCodeDict.AddOrUpdate(user.Id + "_" + mobile, code, (k, v) => code);
            await this.smsGateway.SendCode(mobile, code);
        }

        /// <summary>
        /// 绑定手机
        /// </summary>
        public async Task BindMobile(string userId, string mobile, string code)
        {
            if (ValueUtility.IsBlank(code))
                throw new ApplicationException("验证码不能为空");

            User user = await this.userRepository.SelectByIdReq(userId);
            string key = user.Id + "_" + mobile;
            if (!this.mobileCodeDict.TryGetValue(key, out string? sendCode) || sendCode != code)
                throw new ApplicationException("验证码错误");

            await user.ModifyMobile(mobile, this.userUniqueSpecification);
            await this.userRepository.Update(user);
            this.mobileCodeDict.TryRemove(key, out _);
        }
    }
}
