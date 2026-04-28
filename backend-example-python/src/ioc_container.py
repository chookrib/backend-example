import inspect
import logging
from abc import ABCMeta
from typing import TypeVar, Any, overload

from src.application.application_config import application_config

logger = logging.getLogger(__name__)

T = TypeVar("T")


class IocContainer:
    """依赖注入容器"""

    def __init__(self):
        self._instances = {}

    @overload
    def register(self, cls: type[T], provider_cls: None = None) -> T:
        ...

    @overload
    def register(self, cls: type[Any], provider_cls: type[T]) -> T:
        ...

    def register(self, cls: type[Any], provider_cls: type[Any] | None = None) -> Any:
        cls_text = f"{cls.__name__}{', ' + provider_cls.__name__ if provider_cls else ''}"
        if provider_cls is not None:
            if not issubclass(provider_cls, cls):
                raise TypeError(f"注册 {cls_text} 异常: {provider_cls.__name__} 必须继承自 {cls.__name__}")
            if inspect.isabstract(provider_cls):
                raise TypeError(f"注册 {cls_text} 异常: {provider_cls.__name__} 是抽象类, 无法实例化")
        else:
            if inspect.isabstract(cls):
                raise TypeError(f"注册 {cls_text} 异常: {cls.__name__} 是抽象类，无法实例化")

        if cls in self._instances:
            # 需注册的类有可能在构造函数参数实例化时已经构造，故不能判断重复
            # raise TypeError(f"注册 {cls_text} 异常: {cls.__name__} 不能重复注册")
            if application_config.is_app_env_dev():
                logger.info(f"注册 {cls_text}: IoC容器已存在 {cls.__name__} 实例，跳过注册")
            return self._instances[cls]
        else:
            if application_config.is_app_env_dev():
                logger.info(f"注册 {cls_text}: 准备实例化 >>>")

        if provider_cls is not None and provider_cls in self._instances:
            provider_cls_instance = self._instances[provider_cls]
            self._instances[cls] = provider_cls_instance
            if application_config.is_app_env_dev():
                logger.info(f"<<< IoC容器已存在 {provider_cls.__name__} 实例，完成注册")
            return provider_cls_instance

        target_cls = provider_cls or cls
        sig = inspect.signature(target_cls.__init__)
        kwargs = {}
        path = [target_cls]
        for name, param in sig.parameters.items():
            # *args 和 **kwargs 不需要处理
            if name == 'self' or param.kind in (inspect.Parameter.VAR_POSITIONAL, inspect.Parameter.VAR_KEYWORD):
                continue
            param_cls = param.annotation
            if param_cls is inspect.Parameter.empty:
                raise TypeError(f"{target_cls.__name__} __init__ 方法参数 {name} 缺少类型注解")
            kwargs[name] = self._register_param(name, param_cls, path)

        instance = target_cls(**kwargs)
        self._instances[cls] = instance

        # 如果 provider_cls 和 cls 不同，将 provider_cls 实例添加到实例字典，避免重复实例化
        if not target_cls in self._instances:
            self._instances[target_cls] = instance

        if application_config.is_app_env_dev():
            logger.info(f"<<< 成功实例化 {target_cls.__name__}，完成注册")

        return instance

    def _register_param(self, param_name: str, param_cls: type[T], path: list) -> T:
        # # python 循环依赖会自已抛异常，无需进行判断
        # if param_cls in path:
        #     raise TypeError(f"检测到循环依赖: {' -> '.join([c.__name__ for c in path + [param_cls]])}")

        param_text_prefix = f"{' ' * 4 * len(path)}"
        param_text = f"{param_text_prefix}参数 {param_name} 类型 {param_cls.__name__}"
        if param_cls in self._instances:
            if application_config.is_app_env_dev():
                logger.info(f"{param_text} 在IoC容器中已存在实例，直接使用")
            return self._instances[param_cls]
        elif inspect.isabstract(param_cls):
            raise TypeError(f"{param_text} 是抽象类，无法实例化")

        sig = inspect.signature(param_cls.__init__)
        kwargs = {}
        if application_config.is_app_env_dev():
            logger.info(f"{param_text} 准备实例化 >>>")

        path.append(param_cls)
        for name, param in sig.parameters.items():
            # *args 和 **kwargs 不需要处理
            if name == 'self' or param.kind in (inspect.Parameter.VAR_POSITIONAL, inspect.Parameter.VAR_KEYWORD):
                continue
            dep_cls = param.annotation
            if dep_cls is inspect.Parameter.empty:
                raise TypeError(f"{param_cls.__name__} __init__ 方法参数 {name} 缺少类型注解")

            kwargs[name] = self._register_param(name, dep_cls, path)
        path.pop()
        if application_config.is_app_env_dev():
            logger.info(f"{param_text_prefix}<<< 参数 {param_name} 类型 {param_cls.__name__} 成功实例化并加入IoC容器")

        instance = param_cls(**kwargs)
        # 将 param_cls 实例添加到实例字典，避免重复实例化
        if not param_cls in self._instances:
            self._instances[param_cls] = instance

        return instance

    @overload
    def resolve(self, cls: type[T]) -> T:
        ...

    @overload
    def resolve(self, cls: ABCMeta) -> Any:
        ...

    def resolve(self, cls: type[Any]) -> Any:
        if cls not in self._instances:
            raise TypeError(f"{cls} 未注册")
        return self._instances[cls]


# 实例化容器
ioc_container = IocContainer()

# 注册接口有顺序要求，接口的实现类构造函数参数中依赖的接口必须先注册
# 注册非接口类没有顺序要求，会自动递归注册

# ======================================================================================================================
# 注册锁 Service
from src.application.lock.lock_service import LockService

if application_config.APP_LOCK_SERVICE == "asyncio":
    from src.application.lock.asyncio_lock_service import AsyncioLockService

    ioc_container.register(cls=LockService, provider_cls=AsyncioLockService)
elif application_config.APP_LOCK_SERVICE == "redis":
    from src.application.lock.redis_lock_service import RedisLockService

    ioc_container.register(cls=LockService, provider_cls=RedisLockService)
else:
    raise ValueError(f"APP_LOCK_SERVICE 配置错误")

from src.application.test.test_lock_service import TestLockService

ioc_container.register(cls=TestLockService)

# ======================================================================================================================
# 注册 Driven Adapter - Gateway
from src.domain.sms_gateway import SmsGateway
from src.adapter.driven.sms_gateway_adapter import SmsGatewayAdapter

ioc_container.register(cls=SmsGateway, provider_cls=SmsGatewayAdapter)

# ======================================================================================================================
# 注册 Driven Adapter - Persistence
from src.domain.user_repository import UserRepository
from src.domain.user_unique_specification import UserUniqueSpecification
from src.application.user_query_handler import UserQueryHandler
from src.adapter.driven.user_persistence_adapter import UserPersistenceAdapter

ioc_container.register(cls=UserRepository, provider_cls=UserPersistenceAdapter)
ioc_container.register(cls=UserUniqueSpecification, provider_cls=UserPersistenceAdapter)
ioc_container.register(cls=UserQueryHandler, provider_cls=UserPersistenceAdapter)

# ======================================================================================================================
# 注册 Application Service
from src.application.user_auth_service import UserAuthService
from src.application.user_profile_service import UserProfileService
from src.application.user_manage_service import UserManageService

ioc_container.register(cls=UserAuthService)
ioc_container.register(cls=UserProfileService)
ioc_container.register(cls=UserManageService)

# 测试IoC注册，循环依赖会报错
from src.application.test.test_ioc_class_1 import TestIocClass1

ioc_container.register(cls=TestIocClass1)

# for k, v in ioc_container._instances.items():
#     print(f"{k}: {v}")
