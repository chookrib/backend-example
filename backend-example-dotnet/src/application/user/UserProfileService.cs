using src.application.@lock;
using src.domain.ext;
using src.domain.user;
using src.utility;
using System.Collections.Concurrent;
using System.Security.Cryptography;

namespace src.application.user
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
            await using (await lockService.LockAsync(LockKeys.USER))
            {
                User user = await User.Register(IdGenerator.GenerateId(), username, password, nickname,
                    userUniqueSpecification);
                await userRepository.Insert(user);
                return user.Id;
            }
        }

        /// <summary>
        /// 修改密码
        /// </summary>
        public async Task ModifyPassword(string userId, string oldPassword, string newPassword)
        {
            User user = await userRepository.SelectByIdReq(userId);
            user.ModifyPassword(oldPassword, newPassword);
            await userRepository.Update(user);
        }

        /// <summary>
        /// 修改昵称
        /// </summary>
        public async Task ModifyNickname(string userId, string nickname)
        {
            await using (await lockService.LockAsync(LockKeys.USER))
            {
                User user = await userRepository.SelectByIdReq(userId);
                await user.ModifyNickname(nickname, userUniqueSpecification);
                await userRepository.Update(user);
            }
        }

        /// <summary>
        /// 发送手机验证码
        /// </summary>
        public async Task SendMobileCode(string userId, string mobile)
        {
            if (ValueUtility.IsEmptyString(mobile))
                throw new ApplicationException("手机号不能为空");

            User user = await userRepository.SelectByIdReq(userId);
            string code = RandomNumberGenerator.GetInt32(0, 1_000_000).ToString("D6");
            mobileCodeDict.AddOrUpdate(user.Id + "_" + mobile, code, (k, v) => code);
            await smsGateway.SendCode(mobile, code);
        }

        /// <summary>
        /// 绑定手机
        /// </summary>
        public async Task BindMobile(string userId, string mobile, string code)
        {
            if (ValueUtility.IsEmptyString(code))
                throw new ApplicationException("验证码不能为空");

            User user = await userRepository.SelectByIdReq(userId);
            string key = user.Id + "_" + mobile;
            if (!mobileCodeDict.TryGetValue(key, out string? sendCode) || sendCode != code)
                throw new ApplicationException("验证码错误");

            await user.ModifyMobile(mobile, userUniqueSpecification);
            await userRepository.Update(user);
            mobileCodeDict.TryRemove(key, out _);
        }
    }
}
