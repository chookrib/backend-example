using BackendExample.Utility;

namespace BackendExample.Domain
{
    /// <summary>
    /// 用户 Entity
    /// </summary>
    public class User
    {
        public string Id { get; private set; } = string.Empty;

        public string Username { get; private set; } = string.Empty;

        public string Password { get; private set; } = string.Empty;

        public string Nickname { get; private set; } = string.Empty;

        public string Mobile { get; private set; } = string.Empty;

        public bool IsAdmin { get; private set; }

        public DateTime CreatedAt { get; private set; }

        /// <summary>
        /// 还原用户
        /// </summary>
        public static User RestoreUser(string id, string username, string password, string nickname, string mobile,
            bool isAdmin, DateTime createdAt)
        {
            return new User
            {
                Id = id,
                Username = username,
                Password = password,
                Nickname = nickname,
                Mobile = mobile,
                IsAdmin = isAdmin,
                CreatedAt = createdAt
            };
        }

        /// <summary>
        /// 注册用户
        /// </summary>
        public static async Task<User> RegisterUser(string id, string username, string password, string nickname,
            UserUniqueSpecification? userUniqueSpecification)
        {
            if (ValueUtility.IsBlank(username))
                throw new DomainException("用户名不能为空");

            if (ValueUtility.IsBlank(password))
                throw new DomainException("密码不能为空");

            if (ValueUtility.IsBlank(nickname))
                throw new DomainException("昵称不能为空");

            if (userUniqueSpecification != null)
            {
                if (! await userUniqueSpecification.IsUsernameUnique(username))
                    throw new DomainException("用户名已存在");

                if (! await userUniqueSpecification.IsNicknameUnique(nickname))
                    throw new DomainException("昵称已存在");
            }

            return new User
            {
                Id = id,
                Username = username,
                Password = CryptoUtility.EncodeMd5(password),
                Nickname = nickname,
                Mobile = "",
                IsAdmin = false,
                CreatedAt = DateTime.Now
            };
        }

        /// <summary>
        /// 检查密码是否匹配
        /// </summary>
        public bool IsPasswordMatch(string password)
        {
            return this.Password == CryptoUtility.EncodeMd5(password);
        }

        /// <summary>
        /// 设置是否管理员
        /// </summary>
        public void SetAdmin(bool isAdmin)
        {
            this.IsAdmin = isAdmin;
        }

        /// <summary>
        /// 修改密码
        /// </summary>
        public void ModifyPassword(string oldPassword, string newPassword)
        {
            if (ValueUtility.IsBlank(newPassword))
                throw new DomainException("密码不能为空");

            if (!IsPasswordMatch(oldPassword))
                throw new DomainException("密码错误");

            this.Password = CryptoUtility.EncodeMd5(newPassword);
        }

        /// <summary>
        /// 修改昵称
        /// </summary>
        public async Task ModifyNickname(string nickname, UserUniqueSpecification? userUniqueSpecification)
        {
            if (ValueUtility.IsBlank(nickname))
                throw new DomainException("昵称不能为空");

            if (nickname.ToLower() != this.Nickname.ToLower() && userUniqueSpecification != null)
            {
                if (! await userUniqueSpecification.IsNicknameUnique(nickname))
                    throw new DomainException("昵称已存在");
            }

            this.Nickname = nickname;
        }

        /// <summary>
        /// 修改手机
        /// </summary>
        public async Task ModifyMobile(string mobile, UserUniqueSpecification? userUniqueSpecification)
        {
            if (ValueUtility.IsBlank(mobile))
                throw new DomainException("手机不能为空");

            if (mobile.ToLower() != this.Mobile.ToLower() && userUniqueSpecification != null)
            {
                if (! await userUniqueSpecification.IsMobileUnique(mobile))
                    throw new DomainException("手机已存在");
            }

            this.Mobile = mobile;
        }

        /// <summary>
        /// 创建用户
        /// </summary>
        public static async Task<User> CreateUser(string id, string username, string password, string nickname, string mobile,
            UserUniqueSpecification? userUniqueSpecification)
        {
            if (ValueUtility.IsBlank(username))
                throw new DomainException("用户名不能为空");

            if (ValueUtility.IsBlank(password))
                throw new DomainException("密码不能为空");

            if (ValueUtility.IsBlank(nickname))
                throw new DomainException("昵称不能为空");

            if (userUniqueSpecification != null)
            {
                if (! await userUniqueSpecification.IsUsernameUnique(username))
                    throw new DomainException("用户名已存在");

                if (! await userUniqueSpecification.IsNicknameUnique(nickname))
                    throw new DomainException("昵称已存在");

                if (!ValueUtility.IsBlank(mobile) && ! await userUniqueSpecification.IsMobileUnique(mobile))
                    throw new DomainException("手机已存在");
            }

            return new User
            {
                Id = id,
                Username = username,
                Password = CryptoUtility.EncodeMd5(password),
                Nickname = nickname,
                Mobile = mobile,
                IsAdmin = false,
                CreatedAt = DateTime.Now
            };
        }

        /// <summary>
        /// 修改用户
        /// </summary>
        public async Task Modify(string username, string nickname, string mobile,
            UserUniqueSpecification? userUniqueSpecification)
        {
            if (ValueUtility.IsBlank(username))
                throw new DomainException("用户名不能为空");

            if (ValueUtility.IsBlank(nickname))
                throw new DomainException("昵称不能为空");

            if (userUniqueSpecification != null)
            {
                if (username.ToLower() != this.Username.ToLower() &&
                    ! await userUniqueSpecification.IsUsernameUnique(username))
                    throw new DomainException("用户名已存在");

                if (nickname.ToLower() != this.Nickname.ToLower() &&
                    ! await userUniqueSpecification.IsNicknameUnique(nickname))
                    throw new DomainException("昵称已存在");

                if (!ValueUtility.IsBlank(mobile) &&
                    mobile.ToLower() != this.Mobile.ToLower() &&
                    ! await userUniqueSpecification.IsMobileUnique(mobile))
                    throw new DomainException("手机已存在");
            }

            this.Username = username;
            this.Nickname = nickname;
            this.Mobile = mobile;
        }
    }
}
