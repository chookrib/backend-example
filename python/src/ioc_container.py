import inspect

from typing import TypeVar

T = TypeVar("T")


class IocContainer:
    """依赖注入容器"""

    def __init__(self):
        self._instances = {}

    def register(self, cls: type[T], provider_cls: type[T] | None = None) -> T:
        if cls in self._instances:
            return self._instances[cls]
        target_cls = provider_cls or cls
        # 如果实现类已注册，直接复用实例
        if target_cls in self._instances:
            instance = self._instances[target_cls]
            self._instances[cls] = instance
            return instance
        sig = inspect.signature(target_cls.__init__)
        kwargs = {}
        print(f"正在注册 {target_cls.__name__}，参数: {sig.parameters}")
        for name, param in sig.parameters.items():
            # *args 和 **kwargs 不需要处理
            if name == 'self' or param.kind in (inspect.Parameter.VAR_POSITIONAL, inspect.Parameter.VAR_KEYWORD):
                continue
            dep_cls = param.annotation
            if dep_cls is inspect.Parameter.empty:
                raise Exception(f"参数 {name} 缺少类型注解")
            kwargs[name] = self.register(dep_cls)
        instance = target_cls(**kwargs)
        self._instances[cls] = instance
        # 将实现类添加到实例字典，避免重复实例化
        self._instances[target_cls] = instance
        return instance

    def resolve(self, cls: type[T]) -> T:
        if cls not in self._instances:
            raise Exception(f"{cls} 未注册或未实例化")
        return self._instances[cls]


# 实例化容器
ioc_container = IocContainer()

# 注册接口有顺序要求，接口的实现类构造函数参数中依赖的接口必须先注册
# 注册非接口类没有顺序要求，会自动递归注册

# ======================================================================================================================
# 注册 Driven Adapter - Gateway
from src.domain.sms_gateway import SmsGateway
from src.adapter.driven.sms_gateway_adapter import SmsGatewayAdapter

ioc_container.register(cls=SmsGateway, provider_cls=SmsGatewayAdapter)  # type: ignore

# ======================================================================================================================
# 注册 Driven Adapter - Persistence
from src.domain.user_repository import UserRepository
from src.domain.user_unique_specification import UserUniqueSpecification
from src.application.user_query_handler import UserQueryHandler
from src.adapter.driven.user_persistence_adapter import UserPersistenceAdapter

ioc_container.register(cls=UserRepository, provider_cls=UserPersistenceAdapter)  # type: ignore
ioc_container.register(cls=UserUniqueSpecification, provider_cls=UserPersistenceAdapter)  # type: ignore
ioc_container.register(cls=UserQueryHandler, provider_cls=UserPersistenceAdapter)  # type: ignore

# ======================================================================================================================
# 注册锁 Service
from src.application.lock.lock_service import LockService
from src.application.lock.asyncio_lock_service import AsyncioLockService
# from src.application.lock.redis_lock_service import RedisLockService
from src.application.lock.test_lock_service import TestLockService

# 注入不同的锁实现
ioc_container.register(cls=LockService, provider_cls=AsyncioLockService)  # type: ignore
# ioc_container.register(cls=LockService, provider_cls=RedisLockService)  # type: ignore
ioc_container.register(cls=TestLockService)

# ======================================================================================================================
# 注册 Application Service
from src.application.user_auth_service import UserAuthService
from src.application.user_profile_service import UserProfileService
from src.application.user_manage_service import UserManageService

ioc_container.register(cls=UserAuthService)
ioc_container.register(cls=UserProfileService)
ioc_container.register(cls=UserManageService)

# for k, v in ioc_container._instances.items():
#     print(f"{k}: {v}")
