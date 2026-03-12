from src.application.test.test_ioc_class_3 import TestIocClass3


class TestIocClass2:
    """IoC测试类2"""

    def __init__(self, class3: TestIocClass3):
        self.class3 = class3
