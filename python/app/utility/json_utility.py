def to_camel(string: str) -> str:
    """转为camel风格"""
    # 保留前导下划线
    # prefix = ""
    # while string.startswith("_"):
    #     prefix += "_"
    #     string = string[1:]
    # if not string:
    #     return prefix

    # 删除前导下划线
    string = string.lstrip("_")
    if not string:
        return ""

    parts = string.split("_")
    return parts[0] + "".join(word.capitalize() for word in parts[1:])

