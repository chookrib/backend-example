import decimal
import json
import logging
import sys
from datetime import datetime

from fastapi import FastAPI, Request
from fastapi import encoders
from fastapi.concurrency import asynccontextmanager
from fastapi.encoders import jsonable_encoder
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from fastapi.staticfiles import StaticFiles

from app.adapter.driving import result_codes
from app.adapter.driving.result import Result
from app.config import settings

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

    # from app.ioc_container import ioc_container
    # from app.domain.wechat_api_handler import WechatApiHandler
    # wechat_api_handler = ioc_container.resolve(WechatApiHandler)    # type: ignore
    # img_data_url = await wechat_api_handler.get_unlimited_qrcode("test")
    # print(img_data_url)

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
from app.adapter.driving.not_login_exception import NotLoginException


@app.exception_handler(NotLoginException)
async def not_login_exception_handler(request: Request, e: NotLoginException):
    return JSONResponse(
        content=Result.error(
            code=result_codes.ERROR_NOT_LOGIN,
            message=f"未登录异常: {str(e)}",
        ).to_dict()
    )


# # FastAPI请求参数验证错误处理
# @app.exception_handler(RequestValidationError)
# async def validation_exception_handler(request: Request, e: RequestValidationError):
#     return JSONResponse(
#         content=api.api_result.error(
#             code=result_codes.ERROR_NOT_LOGIN,
#             message=f"RequestValidationError: {str(e)}",
#         )
#     )

# 全局异常处理
@app.middleware("http")
async def catch_all_exceptions_middleware(request: Request, call_next):
    try:
        response = await call_next(request)
        return response
    except Exception as e:
        logger.error(f"ASGI Exception: {str(e)}", exc_info=True)
        return JSONResponse(
            content=Result.error(
                code=result_codes.ERROR_NOT_LOGIN,
                message=f"ASGI Exception: {str(e)}"
            ).to_dict()
        )


# 用于FastAPI返回时转换数据的自定义jsonable_encoder
def custom_jsonable_encoder(obj, **kwargs):
    def custom_default(o):
        if isinstance(o, datetime):
            return o.strftime("%Y-%m-%d %H:%M:%S")
        if isinstance(o, decimal.Decimal):
            return str(o)
        if isinstance(o, int) and abs(o) > 2 ** 53 - 1:     # long转字符串
            return str(o)
        # if isinstance(o, dict):
        #     return {k: custom_default(v) for k, v in o.items()}
        # if isinstance(o, list):
        #     return [custom_default(item) for item in o]
        return jsonable_encoder(o)

    # try:
    #     result = json.loads(json.dumps(obj, default=custom_default))
    # except Exception as e:
    #     print(f"custom_jsonable_encoder {e}")
    # return result
    return json.loads(json.dumps(obj, default=custom_default))


# 替换FastAPI的jsonable_encoder
encoders.jsonable_encoder = custom_jsonable_encoder  # type: ignore

# 注册路由
from app.adapter.driving import well_known_controller

app.include_router(well_known_controller.router)
from app.adapter.driving import user_controller
app.include_router(user_controller.router)
from app.adapter.driving import user_manage_controller
app.include_router(user_manage_controller.router)

if __name__ == "__main__":
    # 启动服务器
    # import uvicorn
    # uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
    pass
