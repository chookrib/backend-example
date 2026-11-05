using src.utility;

namespace src.domain.user
{
    /// <summary>
    /// 用户 Entity
    /// </summary>
    public class User
    {
        public string Id { get; private set; } = string.Empty;

        public string Username { get; private set; } = string.Empty;

        /// <summary>
        /// 加密后的密码
        /// </summary>
        public string Password { get; private set; } = string.Empty;

        public string Nickname { get; private set; } = string.Empty;

        public string Mobile { get; private set; } = string.Empty;

        public bool IsAdmin { get; private set; }

        public DateTime CreatedAt { get; private set; }

        public DateTime? UpdatedAt { get; private set; }

        /// <summary>
        /// 还原用户
        /// </summary>
        public static User Restore(string id, string username, string password, string nickname, string mobile,
            bool isAdmin, DateTime createdAt, DateTime? updatedAt)
        {
            return new User
            {
                Id = ValueUtility.RequireNotBlank(id),
                Username = ValueUtility.RequireNotBlank(username),
                Password = ValueUtility.RequireNotBlank(password),
                Nickname = ValueUtility.RequireNotBlank(nickname),
                Mobile = mobile,
                IsAdmin = isAdmin,
                CreatedAt = createdAt,
                UpdatedAt = updatedAt
            };
        }

        /// <summary>
        /// 注册用户
        /// </summary>
        public static async Task<User> Register(string id, string username, string password, string nickname,
            UserUniquenessValidator? userUniqueSpecification)
        {
            username = ValueUtility.RequireNotBlank(username, "用户名不能为空");
            password = ValueUtility.RequireNotBlank(password, "密码不能为空");
            nickname = ValueUtility.RequireNotBlank(nickname, "昵称不能为空");

            if (userUniqueSpecification != null)
            {
                if (!await userUniqueSpecification.IsUsernameAvailable(username))
                    throw new DomainException("用户名已存在");

                if (!await userUniqueSpecification.IsNicknameAvailable(nickname))
                    throw new DomainException("昵称已存在");
            }

            return new User
            {
                Id = id,
                Username = username,
                Password = CryptoUtility.Md5Encode(password),
                Nickname = nickname,
                Mobile = "",
                IsAdmin = false,
                CreatedAt = ValueUtility.GetCurrentDateTime(),
                UpdatedAt = null
            };
        }

        /// <summary>
        /// 检查密码是否匹配
        /// </summary>
        public bool IsPasswordMatch(string password)
        {
            if (ValueUtility.IsBlank(password))
                return false;
            return this.Password == CryptoUtility.Md5Encode(password);
        }

        /// <summary>
        /// 设置是否管理员
        /// </summary>
        public void SetAdmin(bool isAdmin)
        {
            this.IsAdmin = isAdmin;
            this.UpdatedAt = ValueUtility.GetCurrentDateTime();
        }

        /// <summary>
        /// 修改密码
        /// </summary>
        public void ModifyPassword(string oldPassword, string newPassword)
        {
            oldPassword = ValueUtility.RequireNotBlank(oldPassword, "旧密码不能为空");
            newPassword = ValueUtility.RequireNotBlank(newPassword, "新密码不能为空");

            if (!IsPasswordMatch(oldPassword))
                throw new DomainException("密码错误");

            this.Password = CryptoUtility.Md5Encode(newPassword);
            this.UpdatedAt = ValueUtility.GetCurrentDateTime();
        }

        /// <summary>
        /// 修改昵称
        /// </summary>
        public async Task ModifyNickname(string nickname, UserUniquenessValidator? userUniqueSpecification)
        {
            nickname = ValueUtility.RequireNotBlank(nickname, "昵称不能为空");

            if (nickname.ToLower() != this.Nickname.ToLower() && userUniqueSpecification != null)
            {
                if (!await userUniqueSpecification.IsNicknameAvailable(nickname))
                    throw new DomainException("昵称已存在");
            }

            this.Nickname = nickname;
            this.UpdatedAt = ValueUtility.GetCurrentDateTime();
        }

        /// <summary>
        /// 修改手机
        /// </summary>
        public async Task ModifyMobile(string mobile, UserUniquenessValidator? userUniqueSpecification)
        {
            mobile = ValueUtility.RequireNotBlank(mobile, "手机不能为空");

            if (mobile.ToLower() != this.Mobile.ToLower() && userUniqueSpecification != null)
            {
                if (!await userUniqueSpecification.IsMobileAvailable(mobile))
                    throw new DomainException("手机已存在");
            }

            this.Mobile = mobile;
            this.UpdatedAt = ValueUtility.GetCurrentDateTime();
        }

        /// <summary>
        /// 创建用户
        /// </summary>
        public static async Task<User> Create(string id, string username, string password, string nickname, string mobile, bool isAdmin,
            UserUniquenessValidator? userUniqueSpecification)
        {
            username = ValueUtility.RequireNotBlank(username, "用户名不能为空");
            password = ValueUtility.RequireNotBlank(password, "密码不能为空");
            nickname = ValueUtility.RequireNotBlank(nickname, "昵称不能为空");

            if (userUniqueSpecification != null)
            {
                if (!await userUniqueSpecification.IsUsernameAvailable(username))
                    throw new DomainException("用户名已存在");

                if (!await userUniqueSpecification.IsNicknameAvailable(nickname))
                    throw new DomainException("昵称已存在");

                if (!ValueUtility.IsBlank(mobile) && !await userUniqueSpecification.IsMobileAvailable(mobile))
                    throw new DomainException("手机已存在");
            }

            return new User
            {
                Id = id,
                Username = username,
                Password = CryptoUtility.Md5Encode(password),
                Nickname = nickname,
                Mobile = mobile,
                IsAdmin = isAdmin,
                CreatedAt = ValueUtility.GetCurrentDateTime(),
                UpdatedAt = null
            };
        }

        /// <summary>
        /// 修改用户
        /// </summary>
        public async Task Modify(string username, string nickname, string mobile, bool isAdmin,
            UserUniquenessValidator? userUniqueSpecification)
        {
            username = ValueUtility.RequireNotBlank(username, "用户名不能为空");
            nickname = ValueUtility.RequireNotBlank(nickname, "昵称不能为空");

            if (userUniqueSpecification != null)
            {
                if (username.ToLower() != this.Username.ToLower() &&
                    !await userUniqueSpecification.IsUsernameAvailable(username))
                    throw new DomainException("用户名已存在");

                if (nickname.ToLower() != this.Nickname.ToLower() &&
                    !await userUniqueSpecification.IsNicknameAvailable(nickname))
                    throw new DomainException("昵称已存在");

                if (!ValueUtility.IsBlank(mobile) &&
                    mobile.ToLower() != this.Mobile.ToLower() &&
                    !await userUniqueSpecification.IsMobileAvailable(mobile))
                    throw new DomainException("手机已存在");
            }

            this.Username = username;
            this.Nickname = nickname;
            this.Mobile = mobile;
            this.IsAdmin = isAdmin;
            this.UpdatedAt = ValueUtility.GetCurrentDateTime();
        }
    }
}
