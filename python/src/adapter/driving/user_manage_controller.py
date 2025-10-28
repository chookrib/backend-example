import logging

from fastapi import APIRouter, Request

from src.adapter.driving import request_auth_helper, request_value_helper
from src.adapter.driving.result import Result
from src.application.user_manage_service import UserManageService
from src.application.user_query_criteria import UserQueryCriteria
from src.application.user_query_handler import UserQueryHandler
from src.ioc_container import ioc_container

logger = logging.getLogger(__name__)
router = APIRouter()

user_manage_service = ioc_container.resolve(UserManageService)  # type: ignore
user_query_handler = ioc_container.resolve(UserQueryHandler)  # type: ignore


@router.post("/api/admin/user/list")
async def user_list(request: Request):
    """用户列表"""
    await request_auth_helper.require_login_user_admin(request)

    request_json = await request_value_helper.get_request_json(request)
    page_num = request_value_helper.get_request_json_int(request_json, 1, "pageNum")
    page_size = request_value_helper.get_request_json_int(request_json, 1, "pageSize")
    # criteria_json = request_json.get("criteria", {})

    criteria = UserQueryCriteria()
    # criteria.keyword = request_value_helper.get_request_json_string_trim(criteria_json, "", "keyword")
    criteria.keyword = request_value_helper.get_request_json_string_trim_or_empty(request_json, "criteria", "keyword")

    total_count = await user_query_handler.query_count(criteria)
    page_num, page_size, total_count = request_value_helper.fix_paging(page_num, page_size, total_count)
    list = await user_query_handler.query_by_page(page_num, page_size, criteria)
    return Result.ok(data={
        # "list": [user.to_json() for user in list],
        "list": list,
        "paging": {
            "pageNum": page_num,
            "pageSize": page_size,
            "totalCount": total_count
        }
    })


@router.get("/api/admin/user/get")
async def user_get(request: Request, id: str):
    """用户详情"""
    await request_auth_helper.require_login_user_admin(request)

    user_dto = await user_query_handler.query_by_id_req(id)
    # return Result.ok(data=user_dto.to_json())
    return Result.ok(data={"detail": user_dto})


@router.post("/api/admin/user/create")
async def user_create(request: Request):
    """创建用户"""
    await request_auth_helper.require_login_user_admin(request)

    request_json = await request_value_helper.get_request_json(request)
    username = request_value_helper.get_request_json_string_trim_req(request_json, "username")
    password = request_value_helper.get_request_json_string_trim_req(request_json, "password")
    nickname = request_value_helper.get_request_json_string_trim_req(request_json, "nickname")
    mobile = request_value_helper.get_request_json_string_trim_or_empty(request_json, "mobile")

    user_id = await user_manage_service.create_user(username, password, nickname, mobile)
    return Result.ok(data={"id": user_id})


@router.post("/api/admin/user/modify")
async def user_modify(request: Request):
    """修改用户"""
    await request_auth_helper.require_login_user_admin(request)

    request_json = await request_value_helper.get_request_json(request)
    id = request_value_helper.get_request_json_string_trim_req(request_json, "id")
    username = request_value_helper.get_request_json_string_trim_req(request_json, "username")
    nickname = request_value_helper.get_request_json_string_trim_req(request_json, "nickname")
    mobile = request_value_helper.get_request_json_string_trim_or_empty(request_json, "mobile")

    await user_manage_service.modify_user(id, username, nickname, mobile)
    return Result.ok()


@router.post("/api/admin/user/remove")
async def user_remove(request: Request):
    """删除用户"""
    await request_auth_helper.require_login_user_admin(request)

    request_json = await request_value_helper.get_request_json(request)
    id = request_value_helper.get_request_json_string_trim_req(request_json, "id")

    await user_manage_service.remove_user(id)
    return Result.ok()
