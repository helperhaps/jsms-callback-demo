package cn.jiguang.example.demo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController
@SpringBootApplication
public class JsmsCallbackDemoApplication {

	public final static String APPKEY = "dev_sample_appKey";
	public final static String APPMASTERSECRET = "dev_sample_appMasterSecret";

	// 校验回调地址
	@GetMapping("/callback")
	public String validate(@RequestParam(name = "echostr") String echostr){
		return echostr;
	}

	// 处理回调
	@PostMapping("/callback")
	public String callback(BaseParam param) {
		System.out.println(param);

		String signature = param.getSignature();
		String str = String.format("appKey=%s&appMasterSecret=%s&nonce=%s&timestamp=%s",
				APPKEY,
				APPMASTERSECRET,
				param.getNonce(),
				param.getTimestamp());

		if (signature.equals(JsmsCallbackDemoApplication.encrypt(str))) {
			System.out.println("AUTH PASS");
			String data = param.getData();

			switch (param.getType()) {
				case SMS_REPLY:
					ReplyParam replyObj = JSONObject.parseObject(data, ReplyParam.class);
					System.out.println(replyObj);
					break;
				case SMS_REPORT:
					ReportParam reportObj = JSONObject.parseObject(data, ReportParam.class);
					System.out.println(reportObj);
					break;
				case SMS_TEMPLATE:
					TemplateParam templateObj = JSONObject.parseObject(data, TemplateParam.class);
					System.out.println(templateObj);
					break;
				case SMS_SIGN:
					SignParam signObj = JSONObject.parseObject(data, SignParam.class);
					System.out.println(signObj);
					break;
				default:
					System.out.println("TYPE ERROR");
			}
			return "OK";
		} else {
			return "AUTH FAILED";
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(JsmsCallbackDemoApplication.class, args);
	}

	enum TYPE {
		SMS_REPLY,
		SMS_REPORT,
		SMS_TEMPLATE,
		SMS_SIGN
	}

	@Data
	public static class BaseParam {
		private Long nonce;
		private String signature;
		private long timestamp;
		private TYPE type;
		private String data;
	}
	@Data
	public static class ReplyParam {
		private String content;
		private String phone;
		private long replyTime;
	}
	@Data
	public static class ReportParam {
		private String msgId;
		private String phone;
		private long receiveTime;
		private int status;
	}
	@Data
	public static class TemplateParam {
		private int tempId;
		private int status;
		private String refuseReason;
	}
	@Data
	public static class SignParam {
		private int signId;
		private int status;
		private String refuseReason;
	}

	/**
	 * SHA1加密
	 *
	 * @param strSrc 明文
	 * @return 加密之后的密文
	 */
	public static String encrypt(String strSrc) {
		MessageDigest md = null;
		String strDes = null;
		byte[] bt = strSrc.getBytes();
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(bt);
			strDes = bytes2Hex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return strDes;
	}

	/**
	 * byte数组转换为16进制字符串
	 *
	 * @param bts 数据源
	 * @return 16进制字符串
	 */
	private static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}
}
