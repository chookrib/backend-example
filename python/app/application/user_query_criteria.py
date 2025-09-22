from pydantic import BaseModel

class UserQueryCriteria(BaseModel):
    keyword: str = ""
