import logging

from fastapi import APIRouter, Request

from src.adapter.driving import request_helper, paging_validator
from src.adapter.driving.result import Result
from src.application.user_manage_service import UserManageService
from src.application.user_query_criteria import UserQueryCriteria
from src.application.user_query_handler import UserQueryHandler
from src.ioc_container import ioc_container
from src.utility import value_utility

logger = logging.getLogger(__name__)
router = APIRouter()

user_manage_service = ioc_container.resolve(UserManageService)  # type: ignore
user_query_handler = ioc_container.resolve(UserQueryHandler)  # type: ignore


@router.post("/api/admin/user/list")
async def user_list(request: Request):
    """用户列表"""
    await request_helper.require_login_user_admin(request)

    request_json = await request_helper.get_json(request)
    page_num = value_utility.to_int_or_default(request_json.get("pageNum"), 1)
    page_size = value_utility.to_int_or_default(request_json.get("pageSize"), 1)
    criteria_json = request_json.get("criteria", {})

    criteria = UserQueryCriteria()
    criteria.keyword = value_utility.to_str_or_empty(criteria_json.get("keyword"))

    total_count = await user_query_handler.query_count(criteria)
    page_num, page_size, total_count = paging_validator.validation(page_num, page_size, total_count)
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
    await request_helper.require_login_user_admin(request)

    user_dto = await user_query_handler.query_by_id_req(id)
    # return Result.ok(data=user_dto.to_json())
    return Result.ok(data={"detail": user_dto})


@router.post("/api/admin/user/create")
async def user_create(request: Request):
    """创建用户"""
    await request_helper.require_login_user_admin(request)

    request_json = await request_helper.get_json(request)
    username = value_utility.to_str_or_empty(request_json.get("username"))
    password = value_utility.to_str_or_empty(request_json.get("password"))
    nickname = value_utility.to_str_or_empty(request_json.get("nickname"))
    mobile = value_utility.to_str_or_empty(request_json.get("mobile"))

    user_id = await user_manage_service.create_user(username, password, nickname, mobile)
    return Result.ok(data={"id": user_id})


@router.post("/api/admin/user/modify")
async def user_modify(request: Request):
    """修改用户"""
    await request_helper.require_login_user_admin(request)

    request_json = await request_helper.get_json(request)
    id = value_utility.to_str_or_empty(request_json.get("id"))
    username = value_utility.to_str_or_empty(request_json.get("username"))
    nickname = value_utility.to_str_or_empty(request_json.get("nickname"))
    mobile = value_utility.to_str_or_empty(request_json.get("mobile"))

    await user_manage_service.modify_user(id, username, nickname, mobile)
    return Result.ok()


@router.post("/api/admin/user/remove")
async def user_remove(request: Request):
    """删除用户"""
    await request_helper.require_login_user_admin(request)

    request_json = await request_helper.get_json(request)
    id = value_utility.to_str_or_empty(request_json.get("id"))

    await user_manage_service.remove_user(id)
    return Result.ok()
