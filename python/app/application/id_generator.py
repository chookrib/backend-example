import uuid_utils

def generate_id() -> str:
    return str(uuid_utils.uuid7())