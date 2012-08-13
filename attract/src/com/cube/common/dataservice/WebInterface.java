package com.cube.common.dataservice;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class WebInterface {

	public static String get(String url) {
		String str = "";

		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = null;
		try {
			httpResponse = new DefaultHttpClient().execute(httpGet);
			if (isHttpSuccessExecuted(httpResponse)) {
				str = EntityUtils.toString(httpResponse.getEntity());
				int start = str.indexOf(123);
				str = str.substring(start);//work around here to normalize the JSONStr
				JSONObject result = new JSONObject(str);
				str = result.optString("data");
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		} finally {
		}

		return str;
	}

	public static JSONObject getJSON(String url) {
		JSONObject result = null;
		String str = "";

		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = null;
		try {
			httpResponse = new DefaultHttpClient().execute(httpGet);
			if (isHttpSuccessExecuted(httpResponse)) {
				str = EntityUtils.toString(httpResponse.getEntity());
				int start = str.indexOf(123);
				str = str.substring(start);//work around here to normalize the JSONStr
				result = new JSONObject(str);
				str = result.optString("data");
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		} finally {
		}

		return result;
	}

	public static String post(String imei, String key, String secret, String url)

	{
		String flag = "";
		HttpResponse httpResponse = null;
		url = url + "?" + "imei" + "=" + imei + "&" + "appkey" + "=" + key + "&" + "clienttype" + "=" + "mobile" + "&" + "version" + "=" + "1.3.4";

		HttpPost httpRequest = new HttpPost(url);
		try {
			httpRequest.setHeader("imei", "012313");
			httpRequest.setHeader("appkey", "012313");
			httpRequest.setHeader("secret", "012313");

			httpResponse = new DefaultHttpClient().execute(httpRequest);
			if (isHttpSuccessExecuted(httpResponse)) {
				String str = EntityUtils.toString(httpResponse.getEntity());

				JSONObject result = new JSONObject(str);
				str = result.optString("data");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return flag;
	}

	public static DefaultHttpClient getHttpClient() {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
		HttpConnectionParams.setSoTimeout(httpParams, 20000);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

		return httpClient;
	}

	private static boolean isHttpSuccessExecuted(HttpResponse response) {
		int statusCode = response.getStatusLine().getStatusCode();

		return (statusCode > 199) && (statusCode < 400);
	}
}