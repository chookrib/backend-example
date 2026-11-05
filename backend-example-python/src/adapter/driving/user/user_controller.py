import logging

from fastapi import APIRouter, Request

from src.adapter.driving import request_identity_resolver, request_value_resolver
from src.adapter.driving.controller_exception import ControllerException
from src.adapter.driving.result import Result
from src.application.user.user_auth_service import UserAuthService
from src.application.user.user_profile_service import UserProfileService
from src.application.user.user_query_handler import UserQueryHandler
from src.ioc_container import ioc_container

logger = logging.getLogger(__name__)
router = APIRouter()

user_auth_service = ioc_container.resolve(UserAuthService)
user_profile_service = ioc_container.resolve(UserProfileService)
user_query_handler = ioc_container.resolve(UserQueryHandler)


@router.post("/api/user/register")
async def register(request: Request):
    """注册"""
    json_extractor = await request_value_resolver.build_json_extractor(request)
    username = json_extractor.get_not_blank_string_or_throw("username", "用户名不能为空")
    password = json_extractor.get_not_blank_string_or_throw("password", "密码不能为空")
    confirm_password = json_extractor.get_not_blank_string_or_throw("confirmPassword", "确认密码不能为空")
    nickname = json_extractor.get_not_blank_string_or_throw("nickname", "昵称不能为空")

    if password != confirm_password:
        raise ControllerException("两次输入的密码不一致")

    user_id = await user_profile_service.register(username, password, nickname)
    return Result.ok(data={"id": user_id})


@router.post("/api/user/login")
async def login(request: Request):
    """登录"""
    json_extractor = await request_value_resolver.build_json_extractor(request)
    username = json_extractor.get_not_blank_string_or_throw("username", "用户名不能为空")
    password = json_extractor.get_not_blank_string_or_throw("password", "密码不能为空")

    access_token = await user_auth_service.login(username, password)
    return Result.ok(data={"accessToken": access_token})


@router.get("/api/user/profile")
async def profile(request: Request):
    """取用户资料"""
    user_id = request_identity_resolver.require_login_user_id(request)
    user_dto = await user_query_handler.query_by_id_req(user_id)
    # return Result.ok(data=user_dto.to_json())
    return Result.ok(data={"profile": user_dto})


@router.post("/api/user/modify-password")
async def modify_password(request: Request):
    """修改密码"""
    user_id = request_identity_resolver.require_login_user_id(request)

    json_extractor = await request_value_resolver.build_json_extractor(request)
    old_password = json_extractor.get_not_blank_string_or_throw("oldPassword", "旧密码不能为空")
    new_password = json_extractor.get_not_blank_string_or_throw("newPassword", "新密码不能为空")
    confirm_password = json_extractor.get_not_blank_string_or_throw("confirmPassword", "确认密码不能为空")

    if confirm_password != new_password:
        raise ControllerException("两次输入的密码不一致")

    await user_profile_service.modify_password(user_id, old_password, new_password)
    return Result.ok()


@router.post("/api/user/modify-nickname")
async def modify_nickname(request: Request):
    """修改昵称"""
    user_id = request_identity_resolver.require_login_user_id(request)

    json_extractor = await request_value_resolver.build_json_extractor(request)
    nickname = json_extractor.get_not_blank_string_or_throw("nickname", "昵称不能为空")

    await user_profile_service.modify_nickname(user_id, nickname)
    return Result.ok()


@router.post("/api/user/send-mobile-code")
async def send_mobile_code(request: Request):
    """发送手机验证码"""
    user_id = request_identity_resolver.require_login_user_id(request)
    await user_profile_service.send_mobile_code(user_id)
    return Result.ok()


@router.post("/api/user/bind-mobile")
async def bind_mobile(request: Request):
    """绑定手机"""
    user_id = request_identity_resolver.require_login_user_id(request)

    json_extractor = await request_value_resolver.build_json_extractor(request)
    mobile = json_extractor.get_not_blank_string_or_throw("mobile", "手机不能为空")
    code = json_extractor.get_not_blank_string_or_throw("code", "验证码不能为空")
    await user_profile_service.bind_mobile(user_id, mobile, code)
    return Result.ok()
