def validation(page_num: int, page_size: int, total_count: int) -> tuple[int, int, int]:
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

    return page_num, page_size, total_count
