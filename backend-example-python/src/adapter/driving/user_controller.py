import logging

from fastapi import APIRouter, Request

from src.adapter.driving import request_auth_helper, request_value_helper
from src.adapter.driving.controller_exception import ControllerException
from src.adapter.driving.result import Result
from src.application.user_auth_service import UserAuthService
from src.application.user_profile_service import UserProfileService
from src.application.user_query_handler import UserQueryHandler
from src.ioc_container import ioc_container

logger = logging.getLogger(__name__)
router = APIRouter()

user_auth_service = ioc_container.resolve(UserAuthService)  # type: ignore
user_profile_service = ioc_container.resolve(UserProfileService)  # type: ignore
user_query_handler = ioc_container.resolve(UserQueryHandler)  # type: ignore


@router.post("/api/user/register")
async def register(request: Request):
    """注册"""
    request_json = await request_value_helper.get_request_json(request)
    username = request_value_helper.get_request_json_string_trim_req(request_json, "username")
    password = request_value_helper.get_request_json_string_trim_req(request_json, "password")
    confirm_password = request_value_helper.get_request_json_string_trim_req(request_json, "confirmPassword")
    nickname = request_value_helper.get_request_json_string_trim_req(request_json, "nickname")

    if password != confirm_password:
        raise ControllerException("两次输入的密码不一致")

    user_id = await user_profile_service.register(username, password, nickname)
    return Result.ok(data={"id": user_id})


@router.post("/api/user/login")
async def login(request: Request):
    """登录"""
    request_json = await request_value_helper.get_request_json(request)
    username = request_value_helper.get_request_json_string_trim_req(request_json, "username")
    password = request_value_helper.get_request_json_string_trim_req(request_json, "password")

    access_token = await user_auth_service.login(username, password)
    return Result.ok(data={"accessToken": access_token})


@router.get("/api/user/profile")
async def profile(request: Request):
    """取用户资料"""
    user_id = request_auth_helper.require_login_user_id(request)
    user_dto = await user_query_handler.query_by_id_req(user_id)
    # return Result.ok(data=user_dto.to_json())
    return Result.ok(data={"profile": user_dto})


@router.post("/api/user/modify-password")
async def modify_password(request: Request):
    """修改密码"""
    user_id = request_auth_helper.require_login_user_id(request)

    request_json = await request_value_helper.get_request_json(request)
    old_password = request_value_helper.get_request_json_string_trim_req(request_json, "oldPassword")
    new_password = request_value_helper.get_request_json_string_trim_req(request_json, "newPassword")
    confirm_password = request_value_helper.get_request_json_string_trim_req(request_json, "confirmPassword")

    if confirm_password != new_password:
        raise ControllerException("两次输入的密码不一致")

    await user_profile_service.modify_password(user_id, old_password, new_password)
    return Result.ok()


@router.post("/api/user/modify-nickname")
async def modify_nickname(request: Request):
    """修改昵称"""
    user_id = request_auth_helper.require_login_user_id(request)

    request_json = await request_value_helper.get_request_json(request)
    nickname = request_value_helper.get_request_json_string_trim_req(request_json, "nickname")
    await user_profile_service.modify_nickname(user_id, nickname)
    return Result.ok()


@router.post("/api/user/send-mobile-code")
async def send_mobile_code(request: Request):
    """发送手机验证码"""
    user_id = request_auth_helper.require_login_user_id(request)

    request_json = await request_value_helper.get_request_json(request)
    mobile = request_value_helper.get_request_json_string_trim_req(request_json, "mobile")
    await user_profile_service.send_mobile_code(user_id, mobile)
    return Result.ok()


@router.post("/api/user/bind-mobile")
async def bind_mobile(request: Request):
    """绑定手机"""
    user_id = request_auth_helper.require_login_user_id(request)

    request_json = await request_value_helper.get_request_json(request)
    mobile = request_value_helper.get_request_json_string_trim_req(request_json, "mobile")
    code = request_value_helper.get_request_json_string_trim_req(request_json, "code")
    await user_profile_service.bind_mobile(user_id, mobile, code)
    return Result.ok()
