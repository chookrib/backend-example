namespace DddExample.Application
{
    /// <summary>
    /// 用户 DTO
    /// </summary>
    public class UserDto
    {
        public string Id { get; private set; }

        public string Username { get; private set; }

        public string Nickname { get; private set; }

        public string Mobile { get; private set; }

        public bool IsAdmin { get; private set; }

        public DateTime CreatedAt { get; private set; }

        public UserDto(string id, string username, string nickname, string mobile, bool isAdmin, DateTime createdAt)
        {
            Id = id;
            Username = username;
            Nickname = nickname;
            Mobile = mobile;
            IsAdmin = isAdmin;
            CreatedAt = createdAt;
        }
    }
}
