# ddd-example-python

## 代码检查
```shell
mypy --python-executable .\.venv\Scripts\python.exe --package src
```

## 运行项目
```shell
uv sync
uv run uvicorn src.main:app --host 0.0.0.0 --port 8000 --reload
```