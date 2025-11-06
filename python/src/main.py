import logging
import os
import sys

from fastapi import FastAPI, Request
from fastapi import encoders
from fastapi.concurrency import asynccontextmanager
from fastapi.responses import JSONResponse

from src.accessor import accessor
from src.adapter.driving import result_codes
from src.adapter.driving.result import Result
from src.config import settings
from src.utility import json_utility, value_utility

# 设置日志格式
logging.basicConfig(
    level=logging.INFO,  # 默认日志级别 WARNING
    format="%(asctime)s - %(levelname)s - %(filename)s:%(lineno)d - %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
    handlers=[
        logging.StreamHandler(stream=sys.stdout),
        # logging.FileHandler("log\log.log", encoding="utf-8", mode="a")
    ]
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

    if accessor.app_is_dev:
        # 打印 pydantic_settings
        print(
            # "\033[31m" +
            "pydantic_settings:\n"
            + "\n".join(f"    {key} = {value}" for key, value in settings.__dict__.items())
            # + "\033[0m"
        )

        # 打印 os.environ
        print(
            # "\033[31m" +
            "os.environ:\n"
            + "\n".join(f"    {key} = {value}" for key, value in os.environ.items())
            # + "\033[0m"
        )

    # 服务初始化
    from src.ioc_container import ioc_container
    from src.domain.user_repository import UserRepository
    user_repository = ioc_container.resolve(UserRepository)  # type: ignore
    await user_repository.init()

    if value_utility.is_blank(settings.APP_NAME):
        logger.warning(f"APP_NAME 配置缺失")
    else:
        logger.info(f"{settings.APP_NAME} 应用启动成功")

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
async def not_login_exception_handler(request: Request, ex: NotLoginException):
    return JSONResponse(
        content=Result.error(
            code=result_codes.ERROR_NOT_LOGIN,
            message=f"未登录异常: {str(ex)}",
        ).to_dict()
    )


from src.application.lock.lock_exception import LockException


@app.exception_handler(LockException)
async def lock_exception_handler(request: Request, ex: LockException):
    logger.error(f"捕捉到 LockException 异常: {str(ex)}", exc_info=True)
    return JSONResponse(
        content=Result.error(
            code=result_codes.ERROR_DEFAULT,
            message=f"系统繁忙，请稍后再试"
        ).to_dict()
    )


# FastAPI请求验证错误处理
# @app.exception_handler(RequestValidationError)
# async def request_validation_error_handler(request: Request, ex: RequestValidationError):
#     return JSONResponse(
#         content=Result.error(
#             code=result_codes.ERROR_DEFAULT,
#             message=f"RequestValidationError: {str(ex)}",
#         ).to_dict()
#     )

# @app.exception_handler(JSONDecodeError)
# async def json_decode_error_handler(request: Request, ex: JSONDecodeError):
#     return JSONResponse(
#         content=Result.error(
#             code=result_codes.ERROR_DEFAULT,
#             message=f"JSONDecodeError: {str(ex)}",
#         ).to_dict()
#     )

# 全局异常处理
@app.middleware("http")
async def catch_all_exceptions_middleware(request: Request, call_next):
    try:
        response = await call_next(request)
        # if response.status_code == 404:
        #     return JSONResponse(
        #         content=Result.error(
        #             code=result_codes.ERROR_DEFAULT,
        #             message=f"HTTP 404: {request.url.path}"
        #         ).to_dict()
        #     )
        # elif response.status_code == 500:
        #     # print(response)
        #     # 读取响应内容
        #     body = b""
        #     async for chunk in response.body_iterator:
        #         body += chunk
        #     return JSONResponse(
        #         content=Result.error(
        #             code=result_codes.ERROR_DEFAULT,
        #             message=f"HTTP 500: {body.decode(errors='ignore')}"
        #         ).to_dict()
        #     )
        return response
    except Exception as ex:
        logger.error(f"捕捉到未处理的异常: {str(ex)}", exc_info=True)
        return JSONResponse(
            content=Result.error(
                code=result_codes.ERROR_DEFAULT,
                message=f"{str(ex)}"
            ).to_dict()
        )


# 替换 FastAPI 默认 jsonable_encoder
encoders.jsonable_encoder = json_utility.custom_jsonable_encoder  # type: ignore

# 注册路由
from src.adapter.driving import well_known_controller
from src.adapter.driving import well_known_test_crypto_controller
from src.adapter.driving import well_known_test_exception_controller
from src.adapter.driving import well_known_test_lock_controller
from src.adapter.driving import well_known_test_request_controller
from src.adapter.driving import well_known_test_response_code_controller
from src.adapter.driving import well_known_test_response_json_controller
from src.adapter.driving import user_controller
from src.adapter.driving import user_manage_controller

app.include_router(well_known_controller.router)
app.include_router(well_known_test_crypto_controller.router)
app.include_router(well_known_test_exception_controller.router)
app.include_router(well_known_test_lock_controller.router)
app.include_router(well_known_test_request_controller.router)
app.include_router(well_known_test_response_code_controller.router)
app.include_router(well_known_test_response_json_controller.router)
app.include_router(user_controller.router)
app.include_router(user_manage_controller.router)

if __name__ == "__main__":
    # 启动服务器
    # import uvicorn
    # uvicorn.run("src.main:app", host="0.0.0.0", port=8000, reload=True)

    pass
