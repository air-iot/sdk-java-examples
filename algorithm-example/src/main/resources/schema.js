[
    {
        "title": "函数1-加法",
        "function": "add",
        "input": {
            "type": "object",
            "properties": {
                "num1": {
                    "title": "参数1",
                    "type": "number"
                },
                "num2": {
                    "title": "参数2",
                    "type": "number"
                }
            },
            "required": [
                "num1",
                "num2"
            ]
        },
        "output": {
            "type": "object",
            "properties": {
                "num1": {
                    "title": "结果",
                    "type": "number"
                }
            }
        }
    },
    {
        "title": "函数2-绝对值",
        "function": "abs",
        "input": {
            "type": "object",
            "properties": {
                "num1": {
                    "title": "参数1",
                    "type": "number"
                }
            },
            "required": [
                "num1"
            ]
        },
        "output": {
            "type": "object",
            "properties": {
                "res": {
                    "title": "结果",
                    "type": "number"
                }
            }
        }
    },
    {
        "title": "函数3-获取当前系统时间",
        "function": "now",
        "input": {
            "type": "object",
            "properties": {},
            "required": []
        },
        "output": {
            "type": "object",
            "properties": {
                "sysdate": {
                    "title": "当前系统时间",
                    "type": "string"
                }
            }
        }
    },
    {
        "title": "函数4-接收Map",
        "function": "recvMap",
        "input": {
            "type": "object",
            "properties": {
                "name": {
                    "title": "姓名",
                    "type": "string"
                }
            },
            "required": ["name"]
        },
        "output": {
            "type": "object",
            "properties": {
                "result": {
                    "title": "输出结果",
                    "type": "string"
                }
            }
        }
    },
    {
        "title": "函数4-接收字符串",
        "function": "recvString",
        "input": {
            "type": "object",
            "properties": {
                "name": {
                    "title": "姓名",
                    "type": "string"
                }
            },
            "required": ["name"]
        },
        "output": {
            "type": "object",
            "properties": {
                "result": {
                    "title": "输出结果",
                    "type": "string"
                }
            }
        }
    }
]