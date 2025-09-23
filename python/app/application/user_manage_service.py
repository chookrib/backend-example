from app.domain.user_repository import UserRepository
from app.domain.user_unique_checker import UserUniqueChecker
from app.domain.user import User
from app.application import id_generator


class UserManageService:
    """用户管理Service"""

    def __init__(self, user_repository: UserRepository, user_unique_checker: UserUniqueChecker):
        self.user_repository = user_repository
        self.user_unique_checker = user_unique_checker

    def set_admin(self, id: str, is_admin: bool) -> None:
        """设置用户管理员状态"""
        user = self.user_repository.select_by_id_req(id)
        user.set_admin(is_admin)
        self.user_repository.update(user)

    def create_user(self, username: str, password: str, nickname: str, mobile: str = "") -> str:
        """创建用户"""
        user = User.create_user(
            id=id_generator.generate_id(),
            username=username, password=password, nickname=nickname, mobile=mobile, user_unique_checker=self.user_unique_checker)
        self.user_repository.insert(user)
        return user.id

    def modify_user(self, id: str, username: str, nickname: str, mobile: str) -> None:
        """修改用户"""
        user = self.user_repository.select_by_id_req(id)
        user.modify(username=username, nickname=nickname, mobile=mobile, user_unique_checker=self.user_unique_checker)
        self.user_repository.update(user)

    def remove_user(self, id: str) -> None:
        """删除用户"""
        return self.user_repository.delete_by_id(id)

