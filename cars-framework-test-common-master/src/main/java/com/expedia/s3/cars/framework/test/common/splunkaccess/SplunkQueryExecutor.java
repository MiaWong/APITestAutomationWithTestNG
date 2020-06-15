package com.expedia.s3.cars.framework.test.common.splunkaccess;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * Created by alex on 3/15/2017.
 */
public class SplunkQueryExecutor {


  Logger logger = Logger.getLogger(getClass());
  /**
   *
   * @param request splunk request.
   * @return return JSONArray response.
   */
  public String execute(SplunkQueryRequest request) {

    try {
      final String sid = submitQueryJob(request);
      
      int tryTimes = 0;
      while (tryTimes < request.getMaxTryCount()) {
        tryTimes++;
        try {
          if (isQueryJobFinished(request, sid)) {
            return getQueryJobResult(request, sid);
          }
        } catch (Exception e) {
          logger.info(e);
        }
        
        Thread.sleep(request.getTryIntervalInSeconds() * 1000);
        
      }
    } catch (Exception e) {
      logger.info(e);
    }

    return null;
  }


  private String submitQueryJob(SplunkQueryRequest request) throws URISyntaxException,
      ClientProtocolException, IOException {

    final boolean isRawQuery = request.isIsRawQuery();
    final String searchStr = isRawQuery ? "search=search" : "search=";
    final String query =
        String.format(searchStr + " %s",
            URLEncoder.encode(request.getRawQuery().trim(), Consts.UTF_8.toString()));
    final URI uri =
        new URI(String.format("%s:%s/services/search/jobs?output_mode=json",
            request.getSplunkServer(), request.getPort()));

    final HttpPost httpPost = new HttpPost(uri);
    httpPost.addHeader("Authorization", "Basic " + getBasicAuth(request));
    httpPost.setEntity(new StringEntity(query, Consts.UTF_8));

    final HttpClient httpClient = getHttpClient(request);

    final HttpResponse httpResponse = httpClient.execute(httpPost);

    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
      final String responseStr = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
      final JSONObject responseJson = new JSONObject(responseStr);
      return responseJson.getString("sid");
    }

    return null;
  }

  private HttpClient getHttpClient(SplunkQueryRequest request) {

    SSLContext sslContext = null;
    HttpClient client = null;
    try {
      sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
        @Override
        public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
          return true;
        }

      }).build();

      final HttpClientBuilder builder = HttpClientBuilder.create();

      builder.setSslcontext(sslContext);
      final int timeout = request.getTimeoutInSeconds() * 1000;
      final RequestConfig requestConfig =
          RequestConfig.custom().setConnectionRequestTimeout(timeout).setConnectTimeout(timeout)
              .setSocketTimeout(timeout).build();
      builder.setDefaultRequestConfig(requestConfig);

      final HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

      final SSLConnectionSocketFactory sslSocketFactory =
          new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
      final Registry<ConnectionSocketFactory> socketFactoryRegistry =
          RegistryBuilder.<ConnectionSocketFactory>create()
              .register("http", PlainConnectionSocketFactory.getSocketFactory())
              .register("https", sslSocketFactory).build();

      final PoolingHttpClientConnectionManager connMgr =
          new PoolingHttpClientConnectionManager(socketFactoryRegistry);
      builder.setConnectionManager(connMgr);

      client = builder.build();

    } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
      logger.info(e);
    }

    return client;
  }

  private boolean isQueryJobFinished(SplunkQueryRequest request, String sid)
      throws ParseException, IOException, URISyntaxException {

    final URI uri =
        new URI(String.format("%s:%s/services/search/jobs/%s?output_mode=json",
            request.getSplunkServer(), request.getPort(), sid));

    final HttpGet httpGet = new HttpGet(uri);
    httpGet.addHeader("Authorization", "Basic " + getBasicAuth(request));

    final HttpClient httpClient = getHttpClient(request);

    final HttpResponse httpResponse = httpClient.execute(httpGet);

    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
      final String responseStr = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
      final JSONObject responseJson = new JSONObject(responseStr);
      final JSONArray array = responseJson.optJSONArray("entry");
      if (isArrayNotNull(array)) {
        return true;
      }
    }
    return false;
  }

  private boolean isArrayNotNull(JSONArray array) {

    if (array != null && !array.isNull(0)) {
      final JSONObject content = array.optJSONObject(0);
      final JSONObject oPtContent = content.optJSONObject("content");
      final boolean isDone = "DONE".equalsIgnoreCase(content.optJSONObject("content")
              .optString("dispatchState"));
      if (content != null && oPtContent != null && isDone) {
        return true;
      }
    }
    return false;
  }

  private String getQueryJobResult(SplunkQueryRequest request, String sid)
      throws URISyntaxException, ParseException, IOException {

    final URI uri =
        new URI(String.format("%s:%s/services/search/jobs/%s/results?output_mode=json&count=0",
            request.getSplunkServer(), request.getPort(), sid));

    final HttpGet httpGet = new HttpGet(uri);
    httpGet.addHeader("Authorization", "Basic " + getBasicAuth(request));

    final HttpClient httpClient = getHttpClient(request);

    final HttpResponse httpResponse = httpClient.execute(httpGet);

    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
      return EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
    }

    return null;
  }

  private String getBasicAuth(SplunkQueryRequest request) {
    final String auth = String.format("%s:%s", request.getUsername(), request.getPassword());
    return Base64.encodeBase64String(auth.getBytes());
  }
}