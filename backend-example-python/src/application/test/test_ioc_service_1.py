from src.application.test.test_ioc_service_2 import TestIocService2


class TestIocService1:
    """循环依赖测试类1"""

    def __init__(self, service_2: TestIocService2) -> None:
        self.service_2 = service_2