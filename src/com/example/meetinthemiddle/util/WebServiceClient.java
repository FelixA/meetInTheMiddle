package com.example.meetinthemiddle.util;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeaderIterator;
import org.apache.http.util.EntityUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class WebServiceClient {
	
	@SuppressWarnings("unused")
	private static final String LOG_TAG = WebServiceClient.class
			.getSimpleName();

	private enum HttpMethodType {
		GET, POST, PUT, DELETE
	}

	/**
	 * Baut eine fertige Basis-URL aus den Einstellungen zusammen
	 * 
	 * @return
	 */
	public static String getBaseUrl() {
		if (TextUtils.isEmpty(Constants.port)) {
			return Constants.protocol + "://" + Constants.host;
		}
		return Constants.protocol + "://" + Constants.host + ":" + Constants.port;
	}

	/**
	 * Gibt mir ein Objekt einer Klasse unter einer URL zurueck
	 * 
	 * @param clazz
	 * @param urlStr
	 * @param dateFormat
	 * @return
	 */
	public static <T> T get(Class<? extends T> clazz, String urlStr,
			Context ctx, String... dateFormat) {
		T result = read(clazz, urlStr, ctx, dateFormat);
		return result;
	}

	/**
	 * Anlegen
	 * 
	 * @param obj
	 * @param urlStr
	 * @return
	 */
	public static String post(Object obj, String urlStr, Context ctx) {
		final String location = write(obj, urlStr, HttpMethodType.POST, ctx);
		return location;
	}

	/**
	 * Aendern
	 * 
	 * @param obj
	 * @param urlStr
	 */
	public static void put(Object obj, String urlStr, Context ctx) {
		write(obj, urlStr, HttpMethodType.PUT, ctx);
	}

	/**
	 * Loeschen
	 * 
	 * @param urlStr
	 */
	public static void delete(String urlStr) {
		// TODO
	}

	/**
	 * Allgemeines Lesen
	 * 
	 * @param clazz
	 * @param urlStr
	 * @param dateFormat
	 * @return
	 */
	private static <T> T read(Class<? extends T> clazz, String urlStr,
			Context ctx, String... dateFormat) {
		T obj = null;
		try {

			String xmlData = doGetRequest(urlStr, ctx);
			Serializer serializer = null;
			
			if (dateFormat != null && dateFormat.length != 0) {
				Log.v("WebServiceClient", "Datumsformat " + dateFormat[0] + " wird verwendet");
				final DateFormat format = new SimpleDateFormat(dateFormat[0],
						Locale.getDefault());
				final RegistryMatcher registryMatcher = new RegistryMatcher();
				registryMatcher.bind(Date.class, new DateTransformer(format));

				serializer = new Persister(registryMatcher);
			}
			else {
				Log.v("WebServiceClient", "Kein Datumsformat wird verwendet");
				serializer = new Persister();
			}
			Reader reader = new StringReader(xmlData);

			obj = (T) serializer.read(clazz, reader, false);

		} catch (Exception exc) {
			exc.printStackTrace();
			throw new RuntimeException(exc);
		}
		return obj;
	}

	/**
	 * Einen GET-Request durchfuehren
	 * 
	 * @param url
	 * @return
	 */
	private static String doGetRequest(String url, Context ctx) {

		// Client definieren
		DefaultHttpClient client = new DefaultHttpClient();

		// Login-Daten festlegen
		
		// TODO

		// GetRequest definieren
		HttpGet getRequest = new HttpGet(url);

		try {

			// Antwort holen
			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();


			// Inhalt holen
			HttpEntity getResponseEntity = getResponse.getEntity();

			// Ok oder nicht?
			if (statusCode != HttpStatus.SC_OK)
				throw new RuntimeException(EntityUtils.toString(getResponseEntity));
			
			if (getResponseEntity != null)
				// TODO DateFormat
				return EntityUtils.toString(getResponseEntity);
			else
				throw new RuntimeException("No response, status cude: " + statusCode);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Allgemeines Schreiben, PUT und POST
	 * 
	 * @param obj
	 * @param urlStr
	 * @param httpMethod
	 * @return
	 */
	private static String write(Object obj, String urlStr,
			HttpMethodType httpMethod, Context ctx) {
		final Serializer serializer = new Persister();
		Writer writer = new StringWriter();

		try {
			serializer.write(obj, writer);
			String xmlString = writer.toString();
			writer.close();

			switch (httpMethod) {
			case POST:
				Log.v("WebserviceClient/urlStr aus static String write ", ""+urlStr);
				return doPostRequest(xmlString, urlStr, ctx);
			case PUT:
				Log.v("WebserviceClient/doPutRequest ", "xmlString, urlStr, ctx"+ xmlString+ " " +urlStr + " " + ctx);
				doPutRequest(xmlString, urlStr, ctx);
				return null;
			default:
				throw new RuntimeException(
						"No HTTP Method (post, put) selected");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Einen POST-Request durchfuehren
	 * 
	 * @param url
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private static String doPostRequest(String content, String url, Context ctx) {
		// Client definieren
		DefaultHttpClient client = new DefaultHttpClient();

		// Login-Daten festlegen
		// TODO

		// Request definieren
		HttpPost postRequest = new HttpPost(url);
		Log.v("WebserviceClient/Post-Request", ""+url);
		//postRequest.addHeader("accept", "text/xml");
		postRequest.addHeader("content-type", "application/xml");
		

		try {
			// Inhalt festlegen
			StringEntity entity = new StringEntity(content, "UTF-8");
			entity.setContentType("application/xml");
			postRequest.setEntity(entity);
			
			Log.v("WebServiceClient", "Post request, entity: " + content);
			HttpClientParams.setRedirecting(postRequest.getParams(), false);
			// Ausfuehren
			HttpResponse postResponse = client.execute(postRequest);

			// Statuscode holen
			final int statusCode = postResponse.getStatusLine().getStatusCode();


			if (statusCode != HttpStatus.SC_CREATED)
				throw new RuntimeException(EntityUtils.toString(postResponse.getEntity()));
			// Alle Header durchgehen
			Header[] headers = postResponse.getAllHeaders();
			HeaderIterator headerIterator = new BasicHeaderIterator(headers,
					null);

			while (headerIterator.hasNext()) {
				Header header = (Header) headerIterator.next();
				if (header.getName().toLowerCase(Locale.ENGLISH).equals("location")) { // Gibt es einen
															// Header mit
															// location?
					return header.getValue(); // Wert von location
				}
			}

			throw new RuntimeException("no location returned");

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Einen PUT-Request durchfuehren
	 * 
	 * @param url
	 * @return
	 */
	private static void doPutRequest(String content, String url, Context ctx) {
		// Client definieren
		DefaultHttpClient client = new DefaultHttpClient();

		// Login-Daten festlegen
		// TODO
		
		// Request definieren
		HttpPut putRequest = new HttpPut(url);
		putRequest.addHeader("Accept", "text/xml");
		putRequest.addHeader("content-type", "application/xml");

		try {
			// Inhalt festlegen
			StringEntity entity = new StringEntity(content, "UTF-8");
			entity.setContentType("application/xml");
			putRequest.setEntity(entity);

			// Ausfuehren
			HttpResponse putResponse = client.execute(putRequest);

			// Statuscode holen
			final int statusCode = putResponse.getStatusLine().getStatusCode();
			Log.v("WebServiceClient/StatusCode und putResponse", ""+statusCode + " "+putResponse);

			// No content oder nicht?
			if (statusCode != HttpStatus.SC_NO_CONTENT)
				throw new RuntimeException(EntityUtils.toString(putResponse.getEntity()));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
