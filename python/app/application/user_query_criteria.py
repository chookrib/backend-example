from pydantic import BaseModel

class UserQueryCriteria(BaseModel):
    """用户查询Criteria"""

    keyword: str = ""
