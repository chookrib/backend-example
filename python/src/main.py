import logging
import sys
from json import JSONDecodeError

from fastapi import FastAPI, Request
from fastapi import encoders
from fastapi.concurrency import asynccontextmanager
from fastapi.encoders import jsonable_encoder
from fastapi.exceptions import RequestValidationError
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from fastapi.staticfiles import StaticFiles

from src.adapter.driving import result_codes
from src.adapter.driving.result import Result
from src.config import settings
from src.utility import json_utility

# 设置日志格式
logging.basicConfig(
    level=logging.INFO,  # 默认日志级别 WARNING
    format="%(asctime)s - %(levelname)s - %(filename)s:%(lineno)d - %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
    handlers=[logging.StreamHandler(stream=sys.stdout)]
)
logger = logging.getLogger(__name__)

# 设置 uvicorn 相关的所有日志格式
for logger_name in [
    "uvicorn",
    "uvicorn.access",
    # "uvicorn.error", "uvicorn.asgi"
]:
    uvicorn_logger = logging.getLogger(logger_name)
    uvicorn_logger.handlers.clear()
    uvicorn_logger_handler = logging.StreamHandler(stream=sys.stdout)
    uvicorn_logger_handler.setFormatter(
        logging.Formatter(
            fmt="%(asctime)s - %(levelname)s - " + logger_name + " - %(message)s",
            datefmt="%Y-%m-%d %H:%M:%S"
        )
    )
    uvicorn_logger.addHandler(uvicorn_logger_handler)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """自定义FastAPI生命周期管理器"""

    # yield 之前的代码会在 FastAPI 启动前执行
    # 打印 pydantic_settings
    logger.info(
        "pydantic_settings:\n"
        + "\n".join(f"{key}={value}" for key, value in settings.__dict__.items())
    )

    from src.ioc_container import ioc_container
    from src.domain.user_repository import UserRepository
    user_repository = ioc_container.resolve(UserRepository)    # type: ignore
    await user_repository.init()

    yield
    # yield 之后的代码会在 FastAPI 关闭前执行


# 创建 FastAPI 实例
app = FastAPI(
    debug=True,
    docs_url=None,  # Swagger UI 文档的路径 /docs
    redoc_url=None,  # ReDoc 文档的路径 /redoc
    openapi_url=None,  # OpenAPI 文档的路径 /openapi.json
    lifespan=lifespan,
)

# 配置 FastAPI CORS
# app.add_middleware(
#     CORSMiddleware,
#     allow_origins=["*"],
#     allow_credentials=True,
#     allow_methods=["*"],
#     allow_headers=["*"],
# )


# 未登录异常处理
from src.adapter.driving.not_login_exception import NotLoginException


@app.exception_handler(NotLoginException)
async def not_login_exception_handler(request: Request, e: NotLoginException):
    return JSONResponse(
        content=Result.error(
            code=result_codes.ERROR_NOT_LOGIN,
            message=f"未登录异常: {str(e)}",
        ).to_dict()
    )


# FastAPI请求验证错误处理
# @app.exception_handler(RequestValidationError)
# async def request_validation_error_handler(request: Request, e: RequestValidationError):
#     return JSONResponse(
#         content=Result.error(
#             code=result_codes.ERROR_DEFAULT,
#             message=f"RequestValidationError: {str(e)}",
#         ).to_dict()
#     )

# @app.exception_handler(JSONDecodeError)
# async def json_decode_error_handler(request: Request, e: JSONDecodeError):
#     return JSONResponse(
#         content=Result.error(
#             code=result_codes.ERROR_DEFAULT,
#             message=f"JSONDecodeError: {str(e)}",
#         ).to_dict()
#     )

# 全局异常处理
@app.middleware("http")
async def catch_all_exceptions_middleware(request: Request, call_next):
    try:
        response = await call_next(request)
        return response
    except Exception as e:
        logger.error(f"捕捉到未处理的异常: {str(e)}", exc_info=True)
        return JSONResponse(
            content=Result.error(
                code=result_codes.ERROR_DEFAULT,
                message=f"{str(e)}"
            ).to_dict()
        )

# 替换 FastAPI 默认 jsonable_encoder
encoders.jsonable_encoder = json_utility.custom_jsonable_encoder  # type: ignore

# 注册路由
from src.adapter.driving import well_known_controller
from src.adapter.driving import well_known_test_exception_controller
from src.adapter.driving import well_known_test_json_controller
from src.adapter.driving import well_known_test_lock_controller
from src.adapter.driving import well_known_test_request_controller
from src.adapter.driving import user_controller
from src.adapter.driving import user_manage_controller
app.include_router(well_known_controller.router)
app.include_router(well_known_test_exception_controller.router)
app.include_router(well_known_test_json_controller.router)
app.include_router(well_known_test_lock_controller.router)
app.include_router(well_known_test_request_controller.router)
app.include_router(user_controller.router)
app.include_router(user_manage_controller.router)

if __name__ == "__main__":
    # 启动服务器
    # import uvicorn
    # uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)

    pass
