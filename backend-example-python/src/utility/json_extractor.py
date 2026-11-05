from src.utility import value_utility, json_utility
from src.utility.utility_exception import UtilityException


class JsonExtractor:
    """JSON Extractor"""

    def __init__(self, json_string: str):
        self.root_node = json_utility.deserialize(json_string)

    def get_node(self, path: str):
        """获取节点，失败返回 None，多级路径以 . 拼接"""
        if value_utility.is_blank(path):
            return None

        paths = path.split(".")
        if len(paths) == 0:
            return None

        current_node = self.root_node
        for p in paths:
            if not hasattr(current_node, "get"):
                return None
            current_node = current_node.get(p)
            if current_node is None:
                return None
        return current_node

    def get_node_or_throw(self, path: str, message: str | None = None):
        """获取节点，失败抛出指定消息异常，多级路径以 . 拼接"""
        node = self.get_node(path)
        if node is None:
            if message is None:
                message = f"JSON 中 {path} 不存在"
            raise UtilityException(message)
        return node

    def get_string(self, path: str) -> str | None:
        """获取字符串值，失败返回 None，多级路径以 . 拼接"""
        node = self.get_node(path)
        if node is None:
            return None
        elif isinstance(node, (str, int, float, bool)):
            return str(node)
        else:
            try:
                return json_utility.serialize(node)
            except:
                return None

    def get_not_blank_string_or_throw(self, path: str, message: str | None = None) -> str:
        """获取字符串值，如果为空字符串抛出指定消息异常，多级路径以 . 拼接"""
        value = self.get_string(path)
        if value is None or value_utility.is_blank(value):
            if message is None:
                message = f"JSON 中 {path} 为空字符串"
            raise UtilityException(message)
        return value

    def get_string_or_empty(self, path: str) -> str:
        """获取字符串值，失败返回空字符串，多级路径以 . 拼接"""
        value = self.get_string(path)
        if value is None:
            return ""
        return value
