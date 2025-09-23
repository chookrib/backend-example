import logging

from fastapi import APIRouter, Request

from app.adapter.driving import request_helper
from app.adapter.driving.controller_exception import ControllerException
from app.adapter.driving.result import Result
from app.application.user_auth_service import UserAuthService
from app.application.user_profile_service import UserProfileService
from app.application.user_query_handler import UserQueryHandler
from app.ioc_container import ioc_container
from app.utility import value_utility

logger = logging.getLogger(__name__)
router = APIRouter()

user_auth_service = ioc_container.resolve(UserAuthService)          # type: ignore
user_profile_service = ioc_container.resolve(UserProfileService)    # type: ignore
user_query_handler = ioc_container.resolve(UserQueryHandler)        # type: ignore


@router.post("/api/user/register")
async def register(request: Request):
    """注册"""
    request_json = await request.json()
    username = value_utility.to_str_or_empty(request_json.get("username"))
    password = value_utility.to_str_or_empty(request_json.get("password"))
    confirm_password = value_utility.to_str_or_empty(request_json.get("confirmPassword"))
    nickname = value_utility.to_str_or_empty(request_json.get("nickname"))

    if password != confirm_password:
        raise ControllerException("两次输入的密码不一致")

    user_id = user_profile_service.register(username, password, nickname)
    return Result.ok(data=user_id)


@router.post("/api/user/login")
async def login(request: Request):
    """登录"""
    request_json = await request.json()
    username = value_utility.to_str_or_empty(request_json.get("username"))
    password = value_utility.to_str_or_empty(request_json.get("password"))

    access_token = user_auth_service.login(username, password)
    return Result.ok(data=access_token)


@router.get("/api/user/profile")
async def profile(request: Request):
    """取用户资料"""
    user_id = request_helper.require_login_user_id(request)
    user_dto = user_query_handler.query_by_id_req(user_id)
    return Result.ok(data=user_dto.to_json())


@router.post("/api/user/modify-password")
async def modify_password(request: Request):
    """修改密码"""
    user_id = request_helper.require_login_user_id(request)

    request_json = await request.json()
    old_password = value_utility.to_str_or_empty(request_json.get("oldPassword"))
    new_password = value_utility.to_str_or_empty(request_json.get("newPassword"))
    confirm_password = value_utility.to_str_or_empty(request_json.get("confirmPassword"))

    if confirm_password != new_password:
        raise ControllerException("两次输入的密码不一致")

    user_profile_service.modify_password(user_id, old_password, new_password)
    return Result.ok()


@router.post("/api/user/modify-nickname")
async def modify_nickname(request: Request):
    """修改昵称"""
    user_id = request_helper.require_login_user_id(request)

    request_json = await request.json()
    nickname = value_utility.to_str_or_empty(request_json.get("nickname"))
    user_profile_service.modify_nickname(user_id, nickname)
    return Result.ok()


@router.post("/api/user/send-mobile-code")
async def send_mobile_code(request: Request):
    """发送手机验证码"""
    user_id = request_helper.require_login_user_id(request)

    request_json = await request.json()
    mobile = value_utility.to_str_or_empty(request_json.get("mobile"))
    user_profile_service.send_mobile_code(user_id, mobile)
    return Result.ok()


@router.post("/api/user/bind-mobile")
async def bind_mobile(request: Request):
    """绑定手机"""
    user_id = request_helper.require_login_user_id(request)

    request_json = await request.json()
    mobile = value_utility.to_str_or_empty(request_json.get("mobile"))
    code = value_utility.to_str_or_empty(request_json.get("code"))
    user_profile_service.bind_mobile(user_id, mobile, code)
    return Result.ok()
