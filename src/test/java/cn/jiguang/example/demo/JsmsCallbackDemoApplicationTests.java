package cn.jiguang.example.demo;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JsmsCallbackDemoApplicationTests {

	@Test
	public void contextLoads() {
	}

	private final static String URL = "http://localhost:8080/callback"; // 回调 URL
	private final static RestTemplate restTemplate = new RestTemplate();

	@Test
	public void testValidate() {
		String echoStr = "pppppppp";
		String str = restTemplate.getForObject(URL+"?echostr="+echoStr, String.class);
		System.out.println(str);
		if (echoStr.equals(str)) {
			System.out.println("SUCCESS");
		} else {
			System.out.println("FAILD");
		}
	}

	@Test
	public void testCallback() {
		// 上行消息
		JsmsCallbackDemoApplication.BaseParam obj = baseParam();
		obj.setType(JsmsCallbackDemoApplication.TYPE.SMS_REPLY);

		JsmsCallbackDemoApplication.ReplyParam data = new JsmsCallbackDemoApplication.ReplyParam();
		data.setContent("TD");
		data.setPhone("13800138000");
		data.setReplyTime(obj.getTimestamp());

		MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
		map.add("nonce", obj.getNonce().toString());
		map.add("signature", obj.getSignature());
		map.add("timestamp", String.valueOf(obj.getTimestamp()));
		map.add("type", obj.getType().name());
		map.add("data", JSON.toJSONString(data));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> r = new HttpEntity<>(map, headers);

		String str = restTemplate.postForObject(URL, r, String.class);
		System.out.println(str);
	}

	private JsmsCallbackDemoApplication.BaseParam baseParam() {
		long nonce = new Random().nextLong();
		long timestamp = System.currentTimeMillis();

		String str = String.format("appKey=%s&appMasterSecret=%s&nonce=%s&timestamp=%s",
				JsmsCallbackDemoApplication.APPKEY,
				JsmsCallbackDemoApplication.APPMASTERSECRET,
				nonce,
				timestamp);
		String signature = JsmsCallbackDemoApplication.encrypt(str);
		JsmsCallbackDemoApplication.BaseParam obj = new JsmsCallbackDemoApplication.BaseParam();
		obj.setNonce(nonce);
		obj.setSignature(signature);
		obj.setTimestamp(timestamp);
		return obj;
	}
}
