from src.application.test.test_ioc_class_2 import TestIocClass2


class TestIocClass1:
    """IoC测试类1"""

    def __init__(self, class2: TestIocClass2) -> None:
        self.class2 = class2
