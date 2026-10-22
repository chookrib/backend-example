from src.application.application_exception import ApplicationException
from src.utility import value_utility


class QuerySortOption:
    """排序项"""

    def __init__(self, field: str, order: str):
        if value_utility.is_empty_string(field):
            raise ApplicationException("排序字段不能为空")

        if order is None or order.strip().lower() not in ("asc", "desc"):
            raise ApplicationException("排序方向只能为 asc 或 desc")

        self._field: str = field
        self._order: str = order.strip().upper()

    @property
    def field(self) -> str:
        return self._field

    @property
    def order(self) -> str:
        return self._order
