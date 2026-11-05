from starlette.requests import Request

from src.adapter.driving.controller_exception import ControllerException
from src.utility import value_utility
from src.utility.json_extractor import JsonExtractor


async def build_json_extractor(request: Request) -> JsonExtractor:
    try:
        body_bytes = await request.body()  # await request.json()
        body_str = body_bytes.decode("utf-8")
        return JsonExtractor(body_str)
    except Exception as ex:
        raise ControllerException("请求体不是合法的 JSON") from ex


# ======================================================================================================================

DEFAULT_PAGE_NUM = 1
DEFAULT_PAGE_SIZE = 10


def normalize_page_num(page_num: str) -> int:
    """规范化页码"""
    pn = value_utility.to_int_or_default(page_num, DEFAULT_PAGE_NUM)
    return pn if pn >= 1 else DEFAULT_PAGE_NUM


def normalize_page_size(page_size: str) -> int:
    """规范化页大小"""
    ps = value_utility.to_int_or_default(page_size, DEFAULT_PAGE_SIZE)
    return ps if ps >= 1 and ps <= 100 else DEFAULT_PAGE_SIZE


def normalize_paging(page_num: int, page_size: int, total_count: int) -> tuple[int, int, int]:
    """验证并修正分页参数"""
    if page_size < 1:
        page_size = 1
    if total_count < 0:
        total_count = 0

    max_page_num = total_count // page_size
    if total_count % page_size > 0:
        max_page_num += 1

    if page_num > max_page_num:
        page_num = max_page_num
    if page_num < 1:
        page_num = 1

    return page_num, page_size, total_count  # 与入参顺序一致


# =======================================================================================================================

def get_sort_options(json_node) -> dict[str, str]:
    """取排序参数"""
    sort_options: dict[str, str] = {}

    # 检查节点是否为空或不是数组
    if json_node is None or not isinstance(json_node, list):
        return sort_options

    for n in json_node:
        if not isinstance(n, dict):
            continue

        field = n.get("field", "")
        order = n.get("order", "")

        if value_utility.is_blank(field):
            raise ControllerException("排序字段不能为空")

        if order is None or order.lower() not in ("asc", "desc"):
            raise ControllerException("排序方向只能为 asc 或 desc")

        sort_options[field] = order.upper()

    return sort_options
