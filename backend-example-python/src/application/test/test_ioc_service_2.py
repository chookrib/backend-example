from src.application.test.test_ioc_service_1 import TestIocService1


class TestIocService2:
    """循环依赖测试类2"""

    def __init__(self, service_1: TestIocService1):
        self.service_1 = service_1