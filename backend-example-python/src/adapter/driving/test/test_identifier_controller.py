import logging

from fastapi import APIRouter, Request

from src.adapter.driving.result import Result
from src.utility import identifier_utility

logger = logging.getLogger(__name__)
router = APIRouter()

@router.get("/api/test/id")
async def test_id(request: Request):
    request_body = await request.body()
    request_string = request_body.decode()
    return Result.ok(data={
        "join": {
            "e0": identifier_utility.wrap_join([]),
            "e1": identifier_utility.wrap_join([""]),
            "e2": identifier_utility.wrap_join(["", ""]),
            "e3": identifier_utility.wrap_join(["", "", ""]),
            "v1": identifier_utility.wrap_join(["a"]),
            "v2": identifier_utility.wrap_join(["a", "b"]),
            "v3": identifier_utility.wrap_join(["a", "b", "c"])
        },
        "split": {
            "e0": identifier_utility.split(""),
            "e1": identifier_utility.split(",,"),
            "e2": identifier_utility.split(",,,"),
            "e3": identifier_utility.split(",,,,"),
            "v1": identifier_utility.split(",a,"),
            "v2": identifier_utility.split(",a,b,"),
            "v3": identifier_utility.split(",a,b,c,")
        }
    })

