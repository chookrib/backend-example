import logging

from fastapi import APIRouter, Request

from src.adapter.driving.result import Result
from src.utility.json_extractor import JsonExtractor

logger = logging.getLogger(__name__)
router = APIRouter()

@router.get("/api/test/json")
async def test_json(request: Request):
    json_string = """
                {
                    "1": {
                        "1_1": "1-1",
                        "1_2": "1-2"
                    },
                    "2":{
                        "2_1": 21,
                        "2_2": 22
                    },
                    "3": ["31", 32]
                }
                """
    json_extractor = JsonExtractor(json_string)
    return Result.ok(data={
        "test1": json_extractor.get_string("1"),
        "test2": json_extractor.get_string("1.1_1"),
        "test3": json_extractor.get_string_or_empty("1.1_1.1_1_1"),
        "test4": json_extractor.get_string("2.2_1"),
        "test5": json_extractor.get_string_list("3")
    })
