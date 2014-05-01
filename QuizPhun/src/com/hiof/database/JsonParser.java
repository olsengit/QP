package com.hiof.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

/*
 * Parsing the JSON arrays from our python scripts.
 */
public class JsonParser {

	public JSONArray getJsonArray(String url) {
		// Make a HttpClient
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		try {
			// Gets the response of the http request
			response = httpclient.execute(new HttpGet(url));
			StatusLine statusLine = response.getStatusLine();
			// If the response is OK
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				// Make a JSONArray and return it
				JSONArray arr = new JSONArray(out.toString());
				return arr;
			} else {
				// If something went wrong, close the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
