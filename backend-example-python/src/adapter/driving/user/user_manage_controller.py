import logging

from fastapi import APIRouter, Request

from src.adapter.driving import request_identity_resolver, request_value_resolver
from src.adapter.driving.result import Result
from src.application.user.user_manage_service import UserManageService
from src.application.user.user_query_criteria import UserQueryCriteria
from src.application.user.user_query_handler import UserQueryHandler
from src.ioc_container import ioc_container
from src.utility import value_utility

logger = logging.getLogger(__name__)
router = APIRouter()

user_manage_service = ioc_container.resolve(UserManageService)
user_query_handler = ioc_container.resolve(UserQueryHandler)


@router.post("/api/admin/user/list")
async def user_list(request: Request):
    """用户列表"""
    await request_identity_resolver.check_admin_permission(request)

    json_extractor = await request_value_resolver.build_json_extractor(request)
    page_num = request_value_resolver.normalize_page_num(json_extractor.get_string_or_empty("pageNum"))
    page_size = request_value_resolver.normalize_page_size(json_extractor.get_string_or_empty("pageSize"))

    criteria = UserQueryCriteria()
    criteria.keyword = json_extractor.get_string_or_empty("criteria.keyword")

    sort_options = request_value_resolver.get_sort_options(json_extractor.get_node("sort"))

    total_count = await user_query_handler.query_count(criteria)
    page_num, page_size, total_count = request_value_resolver.normalize_paging(page_num, page_size, total_count)
    list = await user_query_handler.query_by_page(page_num, page_size, criteria, sort_options)
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
async def user_get(request: Request):
    """用户详情"""
    await request_identity_resolver.check_admin_permission(request)

    id = value_utility.empty_if_none(request.query_params.get("id"))

    user_dto = await user_query_handler.query_by_id_req(id)
    # return Result.ok(data=user_dto.to_json())
    return Result.ok(data={"item": user_dto})


@router.post("/api/admin/user/create")
async def user_create(request: Request):
    """创建用户"""
    await request_identity_resolver.check_admin_permission(request)

    json_extractor = await request_value_resolver.build_json_extractor(request)
    username = json_extractor.get_not_blank_string_or_throw("username", "用户名不能为空")
    password = json_extractor.get_not_blank_string_or_throw("password", "密码不能为空")
    nickname = json_extractor.get_not_blank_string_or_throw("nickname", "昵称不能为空")
    mobile = json_extractor.get_string_or_empty("mobile")
    is_admin = value_utility.to_bool_or_throw(json_extractor.get_string_or_empty("isAdmin"), "是否管理员值无效")

    user_id = await user_manage_service.create_user(username, password, nickname, mobile, is_admin)
    return Result.ok(data={"id": user_id})


@router.post("/api/admin/user/update")
async def user_update(request: Request):
    """修改用户"""
    await request_identity_resolver.check_admin_permission(request)

    json_extractor = await request_value_resolver.build_json_extractor(request)
    id = json_extractor.get_not_blank_string_or_throw("id", "Id 不能为空")
    username = json_extractor.get_not_blank_string_or_throw("username", "用户名不能为空")
    nickname = json_extractor.get_not_blank_string_or_throw("nickname", "昵称不能为空")
    mobile = json_extractor.get_string_or_empty("mobile")
    is_admin = value_utility.to_bool_or_throw(json_extractor.get_string_or_empty("isAdmin"), "是否管理员值无效")

    await user_manage_service.update_user(id, username, nickname, mobile, is_admin)
    return Result.ok()


@router.post("/api/admin/user/delete")
async def user_delete(request: Request):
    """删除用户"""
    await request_identity_resolver.check_admin_permission(request)

    json_extractor = await request_value_resolver.build_json_extractor(request)
    id = json_extractor.get_not_blank_string_or_throw("id", "Id 不能为空")

    await user_manage_service.delete_user(id)
    return Result.ok()
