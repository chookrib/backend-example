from starlette.requests import Request

from src.adapter.driving.not_login_exception import NotLoginException
from src.application.user_auth_service import UserAuthService
from src.application.user_dto import UserDto
from src.application.user_query_handler import UserQueryHandler
from src.ioc_container import ioc_container
from src.utility import value_utility

user_auth_service = ioc_container.resolve(UserAuthService)  # type: ignore
user_query_handler = ioc_container.resolve(UserQueryHandler)  # type: ignore


def get_login_user_id(request: Request) -> str:
    """获取登录用户Id，失败返回空字符串"""
    access_token = request.headers.get("Access-Token", "")
    return user_auth_service.get_login_user_id(access_token)


def require_login_user_id(request: Request) -> str:
    """获取登录用户Id，失败抛出异常"""
    user_id = get_login_user_id(request)
    if value_utility.is_blank(user_id):
        raise NotLoginException()
    return user_id


async def require_login_user_admin(request: Request) -> UserDto:
    """获取登录用户（管理员），失败抛出异常"""
    user_id = get_login_user_id(request)
    user_dto = await user_query_handler.query_by_id(user_id)
    if user_dto is None or not user_dto.is_admin:
        raise NotLoginException()
    return user_dto
