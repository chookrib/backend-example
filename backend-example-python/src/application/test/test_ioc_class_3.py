class TestIocClass3:
    """IoC测试类3"""

    def __init__(self):
        """不会引起循环依赖的构造函数"""
        pass

    # from src.application.test.test_ioc_class_1 import TestIocClass1
    #
    # def __init__(self, class1: TestIocClass1):
    #     """会引起循环依赖的构造函数"""
    #     self.class1 = class1
