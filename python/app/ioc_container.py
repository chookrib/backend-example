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

from app.domain.sms_gateway import SmsGateway
from app.adapter.driven.sms_gateway_adapter import SmsGatewayAdapter
ioc_container.register(cls=SmsGateway, provider_cls=SmsGatewayAdapter)  # type: ignore

from app.domain.user_repository import UserRepository
from app.adapter.driven.user_persistence_adapter import UserPersistenceAdapter
ioc_container.register(cls=UserRepository, provider_cls=UserPersistenceAdapter)  # type: ignore

from app.domain.user_unique_checker import UserUniqueChecker
ioc_container.register(cls=UserUniqueChecker, provider_cls=UserPersistenceAdapter)  # type: ignore

from app.application.user_query_handler import UserQueryHandler
ioc_container.register(cls=UserQueryHandler, provider_cls=UserPersistenceAdapter)  # type: ignore

from app.application.user_auth_service import UserAuthService
from app.application.user_profile_service import UserProfileService
from app.application.user_manage_service import UserManageService
ioc_container.register(cls=UserAuthService)
ioc_container.register(cls=UserProfileService)
ioc_container.register(cls=UserManageService)

# for k, v in ioc_container._instances.items():
#     print(f"{k}: {v}")

