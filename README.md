# 极光短信回调 Demo

## Development

### 本地运行该 demo 项目中的其中一个

**运行 SpringBoot 项目**

略

> SpringBoot 项目中含有模拟上行回调的测试用例（开发者也可据此编写其他的测试用例），不熟悉命令行的开发者可以运行测试来进行开发测试。

**运行 Flask 项目**

$ pip install flask
$ export FLASK_ENV=development
$ flask run

> Springboot 项目默认监听 8080 端口，Flask 项目默认监听 5000 端口，需根据实际运行起来的项目的端口来修改下面命令中的端口号。

**模拟回调地址校验**

```
curl http://127.0.0.1:8080/callback?echostr=66666666
```

**模拟上行回调**

```
curl -d 'nonce=9078159024943867327&timestamp=1559050046277&signature=b2224c3307287bd354dc9761ceaaaa666eaeea2a&type=SMS_REPLY&data={"content":"TD","phone":"13800138000","replyTime":1559050046277}' http://localhost:8080/callback
```

**模拟下行回调**

```
curl -d 'nonce=9078159024943867327&timestamp=1559050046277&signature=b2224c3307287bd354dc9761ceaaaa666eaeea2a&type=SMS_REPORT&data={"msgId":"666","phone":"13800138000","receiveTime":1559050046277,"status":0}' http://localhost:8080/callback
```

**模拟模板审核结果回调**

```
curl -d 'nonce=9078159024943867327&timestamp=1559050046277&signature=b2224c3307287bd354dc9761ceaaaa666eaeea2a&type=SMS_TEMPLATE&data={"tempId":666,"status":1,"refuseReason":null}' http://localhost:8080/callback
```

**模拟签名审核结果回调**

```
curl -d 'nonce=9078159024943867327&timestamp=1559050046277&signature=b2224c3307287bd354dc9761ceaaaa666eaeea2a&type=SMS_SIGN&data={"signId":666,"status":1,"refuseReason":null}' http://localhost:8080/callback
```

## 极光短信回调接口使用步骤：

0. 线上运行该 demo 项目/或其他相关项目，需包含回调校验和回调处理两个接口

1. 极光控制台设置回调地址，例如：http://example.com/callback

2. 校验回调地址：

    填写回调地址，点击输入框旁边的「校验」按钮，极光服务器就会发起一个 get 请求到该回调地址，请求的 url 可能为 'http://example.com/callback？echostr=66666666' 其中 '66666666' 是一个随机字符串，开发者需要将该随机字符串返回给极光服务器。

** 开发者处理示例:**

Spring Boot 示例：

```java
// 参见 `JsmsCallbackDemoApplication` 类的 validate 方法

@GetMapping(value = "/callback")
public String validate(@RequestParam(name = "echostr") String echostr) {
    return echostr;
}
```

Flask 示例：

```python
@app.route('/callback')
def validate():
    return request.args.get('echostr')
```

3. 处理回调

当产生回调消息时，极光服务器就会发起一个 post 请求到开发者填写的回调地址（即上面的 http://example.com/callback），开发者需要接收并处理此类请求。

具体请参见 SpringBoot 项目中 `JsmsCallbackDemoApplication` 类的 `callback` 方法或 Flask 项目中的 `callback` 相关函数
