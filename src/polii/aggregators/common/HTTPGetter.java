package polii.aggregators.common;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class HTTPGetter {


	/**
	 * Get the json string from some URL
	 * @param url
	 * @return
	 */

	public static String getresource(String url)
	{
		//from: http://wiki.apache.org/HttpComponents/QuickStart
		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpResponse response = null;
		HttpEntity entity = null;

		HttpPost httpost = new HttpPost(url);

		try {
			response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		entity = response.getEntity();

		//System.out.println("Login form get: " + response.getStatusLine());

		//parse json
		try {
			//entity.consumeContent();

			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer);
			String data = writer.toString();

			return data;
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	}
	
}
