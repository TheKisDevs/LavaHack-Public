package com.kisman.cc.util.protect.keyauth.api;

import net.minecraft.client.Minecraft;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import com.kisman.cc.util.protect.keyauth.user.UserData;
import com.kisman.cc.util.protect.keyauth.util.HWID;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class KeyAuth {

	public final String appname;
	public final String ownerid;
	public final String version;
	public final String url;

	protected String sessionid;
	protected boolean initialized;

	protected UserData userData;

	public KeyAuth(String appname, String ownerid, String version, String url) {
		this.appname = appname;
		this.ownerid = ownerid;
		this.version = version;
		this.url = url;
	}

	public UserData getUserData() {
		return userData;
	}

	static {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {}
			}};

			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			Unirest.setHttpClient(httpclient);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {
		HttpResponse<String> response;
		try {
			response = Unirest.post(url).field("type", "init").field("ver", version).field("name", appname)
					.field("ownerid", ownerid).asString();

			System.out.println(response.getBody());
			try {
				JSONObject responseJSON = new JSONObject(response.getBody());

				if (response.getBody().equalsIgnoreCase("KeyAuth_Invalid")) {
					Minecraft.getMinecraft().shutdown();
					System.out.println("invalid");
				}

				if (responseJSON.getBoolean("success")) {
					sessionid = responseJSON.getString("sessionid");
					initialized = true;
					System.out.println("Session ID: " + responseJSON.getString("sessionid"));

				} else if (responseJSON.getString("message").equalsIgnoreCase("invalidver")) {
				} else {
					System.out.println(responseJSON.getString("message"));
					Minecraft.getMinecraft().shutdown();
				}
			} catch (Exception ignored) {}
		} catch (UnirestException e) {e.printStackTrace();}
	}

	public boolean license(String key) {
		if (!initialized) {
			System.out.println("\n\n Please initzalize first");
			init();
			return false;
		}

		HttpResponse<String> response;
		try {
			String hwid = HWID.getHWID();

			response = Unirest.post(url).field("type", "license").field("key", key).field("hwid", hwid)
					.field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid).asString();

			System.out.println(response.getBody());

			try {
				JSONObject responseJSON = new JSONObject(response.getBody());

				if (!responseJSON.getBoolean("success")) {
					System.out.println("the license does not exist");
					Minecraft.getMinecraft().shutdown();
				} else userData = new UserData(responseJSON);
			} catch (Exception e) {return false;}
		} catch (UnirestException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void ban() {
		if (!initialized) {
			System.out.println("\n\n Please initzalize first");
			return;
		}

		HttpResponse<String> response;
		try {
			String hwid = HWID.getHWID();

			response = Unirest.post(url).field("type", "ban").field("sessionid", sessionid).field("name", appname)
					.field("ownerid", ownerid).asString();

			try {
				JSONObject responseJSON = new JSONObject(response.getBody());

				if (!responseJSON.getBoolean("success")) {
					System.out.println("Error");
					// System.exit(0);
				} else {

					// optional success msg
				}

			} catch (Exception e) {

			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public void webhook(String webid, String param) {
		if (!initialized) {
			System.out.println("\n\n Please initzalize first");
			return;
		}

		HttpResponse<String> response;
		try {
			String hwid = HWID.getHWID();

			response = Unirest.post(url).field("type", "webhook").field("webid", webid).field("params", param)
					.field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid).asString();

			try {
				JSONObject responseJSON = new JSONObject(response.getBody());

				if (!responseJSON.getBoolean("success")) {
					System.out.println("Error");
					// System.exit(0);
				} else {

					// optional success msg
				}

			} catch (Exception e) {

			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}
}
