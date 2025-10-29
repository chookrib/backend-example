using System;
using System.Collections.Concurrent;
using DddExample.Domain;
using DddExample.Utility;

namespace DddExample.Application
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
        public string Register(string username, string password, string nickname)
        {
            return this.lockService.ExecuteWithLock(LockKeys.USER, () =>
            {
                User user = User.RegisterUser(IdGenerator.GenerateId(), username, password, nickname,
                    this.userUniqueSpecification);
                this.userRepository.Insert(user);
                return user.Id;
            });
        }

        /// <summary>
        /// 修改密码
        /// </summary>
        public void ModifyPassword(string userId, string oldPassword, string newPassword)
        {
            User user = this.userRepository.SelectByIdReq(userId);
            user.ModifyPassword(oldPassword, newPassword);
            this.userRepository.Update(user);
        }

        /// <summary>
        /// 修改昵称
        /// </summary>
        public void ModifyNickname(string userId, string nickname)
        {
            this.lockService.ExecuteWithLock(LockKeys.USER, () =>
            {
                User user = this.userRepository.SelectByIdReq(userId);
                user.ModifyNickname(nickname, this.userUniqueSpecification);
                this.userRepository.Update(user);
            });
        }

        /// <summary>
        /// 发送手机验证码
        /// </summary>
        public void SendMobileCode(string userId, string mobile)
        {
            if (ValueUtility.IsBlank(mobile))
                throw new ApplicationException("手机号不能为空");

            User user = this.userRepository.SelectByIdReq(userId);
            string code = new Random().Next(100000, 1000000).ToString();
            this.mobileCodeDict.AddOrUpdate(user.Id + "_" + mobile, code, (k, v) => code);
            this.smsGateway.SendCode(mobile, code);
        }

        /// <summary>
        /// 绑定手机
        /// </summary>
        public void BindMobile(string userId, string mobile, string code)
        {
            if (ValueUtility.IsBlank(code))
                throw new ApplicationException("验证码不能为空");

            User user = this.userRepository.SelectByIdReq(userId);
            string key = user.Id + "_" + mobile;
            if (!this.mobileCodeDict.TryGetValue(key, out string? sendCode) || sendCode != code)
                throw new ApplicationException("验证码错误");

            user.ModifyMobile(mobile, this.userUniqueSpecification);
            this.userRepository.Update(user);
            this.mobileCodeDict.TryRemove(key, out _);
        }
    }
}
