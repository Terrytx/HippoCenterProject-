package tw.com.hippo_center_backend.h5util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class EcpayProductFunctions {

	// 測試用付款連結
	private static final String ACTION_URL = "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5";

	// 綠界付款成功後，要通知後端的路徑 (請自行改成你的 ngrok 或正式域名對應的路徑)
	private static final String RETURN_URL = "--ngrok--/pecpay/return";
	private static final String ClientBack_URL = "--yourIP--/shopcart?payment=success";

	private static final String MERCHANT_ID = "3002607";
	private static final String HASH_KEY = "pwFHCqoQZGmho4w6";
	private static final String HASH_IV = "EkRm7iFT261dpevs";

	/**
	 * 接收 JSON字串(body)，其中應包含:
	 * { "id": "訂單編號", "name": "商品名稱 (可含#分隔)", "total": "金額", "desc": "交易描述",
	 * "date": "交易時間 yyyy/MM/dd HH:mm:ss" }
	 * 組成 ECPay 的表單 <form>...</form> 並自動 submit
	 */
	public String buildEcpayForm(String body) {
		System.out.println("\n===[EcpayFunctions] buildEcpayForm() 被呼叫===");
		System.out.println("[EcpayFunctions] 接收到的 body: " + body);

		JSONObject obj = new JSONObject(body);

		String id = obj.optString("id", "iSpan" + System.currentTimeMillis());
		String name = obj.optString("name", "測試商品");
		String total = obj.optString("total", "1000");
		String desc = obj.optString("desc", "測試用交易");
		String date = obj.optString("date", "2025/01/01 12:00:00");

		// 1. 組出 ecpay 參數
		Map<String, String> parameters = createEcpayData(id, name, total, desc, date);

		// 2. 用 <form> ... 產生自動送出表單
		StringBuilder sb = new StringBuilder();
		sb.append("<form id='payForm' action='")
				.append(ACTION_URL)
				.append("' method='POST'>");

		for (String key : parameters.keySet()) {
			sb.append("<input type='hidden' name='")
					.append(key).append("' value='")
					.append(parameters.get(key)).append("'/>");
		}
		// submit
		sb.append("<script>document.getElementById('payForm').submit();</script>");
		sb.append("</form>");

		System.out.println("[EcpayFunctions] 最終組合的 form:\n" + sb);
		return sb.toString();
	}

	/**
	 * 組出 ECPay 參數
	 */
	private Map<String, String> createEcpayData(String id, String name, String total, String desc, String date) {
		Map<String, String> map = new HashMap<>();
		map.put("MerchantID", MERCHANT_ID);
		map.put("MerchantTradeNo", id);
		map.put("ItemName", name);
		map.put("TotalAmount", total);
		map.put("TradeDesc", desc);
		map.put("MerchantTradeDate", date);
		map.put("PaymentType", "aio");
		map.put("ChoosePayment", "ALL");
		map.put("ReturnURL", RETURN_URL);
		map.put("ClientBackURL", ClientBack_URL);

		String checkMacValue = genCheckMacValue(map, HASH_KEY, HASH_IV);
		map.put("CheckMacValue", checkMacValue);

		return map;
	}

	/**
	 * 產生 CheckMacValue
	 */
	private String genCheckMacValue(Map<String, String> params, String hashKey, String hashIV) {
		// 1. 依照 key 排序
		TreeSet<String> treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		treeSet.addAll(params.keySet());

		// 2. 組成字串
		StringBuilder paramStr = new StringBuilder();
		for (String key : treeSet) {
			if (!"CheckMacValue".equalsIgnoreCase(key)) {
				paramStr.append("&").append(key).append("=").append(params.get(key));
			}
		}

		// 前後加上 HashKey, HashIV
		String raw = "HashKey=" + hashKey + paramStr + "&HashIV=" + hashIV;
		// URL Encode
		String urlEncode = urlEncode(raw).toLowerCase()
				.replaceAll("%21", "!")
				.replaceAll("%28", "(")
				.replaceAll("%29", ")");

		// SHA-256 雜湊
		return hash(urlEncode.getBytes(), "SHA-256");
	}

	private static String urlEncode(String data) {
		try {
			return URLEncoder.encode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	private String hash(byte[] data, String mode) {
		try {
			MessageDigest md = MessageDigest.getInstance(mode);
			return bytesToHex(md.digest(data));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	private String bytesToHex(byte[] bytes) {
		final char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
