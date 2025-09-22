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
        return instance

    def resolve(self, cls: type[T]) -> T:
        if cls not in self._instances:
            raise Exception(f"{cls} 未注册或未实例化")
        return self._instances[cls]

# 实例化容器
ioc_container = IocContainer()

