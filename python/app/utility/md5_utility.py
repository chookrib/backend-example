import hashlib

def generate_md5(input: str) -> str:
    """生成字符串的MD5哈希值"""

    return hashlib.md5(input.encode('utf-8')).hexdigest()