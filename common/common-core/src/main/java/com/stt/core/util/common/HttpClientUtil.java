package com.stt.core.util.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * http请求工具类
 */
@Slf4j
public class HttpClientUtil {

    public static String onRequest(String strEntity, String postUrl, String contentType) {
        return onRequest(strEntity, postUrl, contentType, null);
    }


    private static HttpClient defaultHttpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        RequestConfig.Builder reqConfigBuilder = RequestConfig.custom();
        reqConfigBuilder.setSocketTimeout(60000);
        reqConfigBuilder.setConnectTimeout(30000);
        reqConfigBuilder.setConnectionRequestTimeout(30000);
        RequestConfig reqConfig = reqConfigBuilder.build();
        httpClientBuilder.setDefaultRequestConfig(reqConfig);
        return httpClientBuilder.build();
    }

    public static String onRequest(MultipartEntityBuilder multipartEntityBuilder, String postUrl, Map<String, String> headers) {
        String respResult = null;
        HttpClient client = null;
        HttpResponse resp = null;
        try {
            client = defaultHttpClient();
            HttpPost httPost = new HttpPost(postUrl);
            httPost.setEntity(multipartEntityBuilder.build());
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httPost.addHeader(key, headers.get(key));
                }
            }
            resp = client.execute(httPost);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httEntity = resp.getEntity();
                respResult = EntityUtils.toString(httEntity);
            } else {
                log.error("http resp code:{}", resp.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error("{}", e);
        } finally {
            HttpClientUtils.closeQuietly(resp);
            HttpClientUtils.closeQuietly(client);
        }
        return respResult;

    }

    public static byte[] downloadFile(String strEntity, String postUrl, String contentType, Map<String, String> headers) {
        HttpClient client = null;
        HttpResponse resp = null;
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            client = defaultHttpClient();
            HttpPost httPost = new HttpPost(postUrl);
            String charset = "UTF-8" ;
            StringEntity entity = new StringEntity(strEntity, charset);
            entity.setContentEncoding(charset);
            entity.setContentType(contentType);
            httPost.setEntity(entity);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httPost.addHeader(key, headers.get(key));
                }
            }
            resp = client.execute(httPost);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = resp.getEntity();
                os = new ByteArrayOutputStream();
                is = httpEntity.getContent();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = is.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
                return os.toByteArray();
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            HttpClientUtils.closeQuietly(resp);
            HttpClientUtils.closeQuietly(client);
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.error("something error:{}", e);
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                log.error("something error:{}", e);
            }
        }
        return null;
    }


    public static String onRequest(Map<String, String> params, String postUrl, String contentType, Map<String, String> headers) {
        String respResult = null;
        HttpClient client = null;
        HttpResponse resp = null;
        try {
            client = defaultHttpClient();
            HttpPost httPost = new HttpPost(postUrl);
            if (params != null && params.size() > 0) {
                List<NameValuePair> parameters = new ArrayList<>();
                for (String key : params.keySet()) {
                    parameters.add(new BasicNameValuePair(key, params.get(key)));
                }
                HttpEntity entity = new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8);
                httPost.setEntity(entity);
            }
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httPost.addHeader(key, headers.get(key));
                }
            }
            resp = client.execute(httPost);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httEntity = resp.getEntity();
                respResult = EntityUtils.toString(httEntity);
                log.info("post url:" + postUrl + ",param:" + params + ",resp:{}" + respResult);
            } else {
                log.info("post url:" + postUrl + ",param:" + params + ",respStatus:{}" + resp.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            HttpClientUtils.closeQuietly(resp);
            HttpClientUtils.closeQuietly(client);
        }
        return respResult;
    }

    public static String onRequest(String strEntity, String postUrl, String contentType, Map<String, String> headers) {
        String respResult = null;
        HttpClient client = null;
        HttpResponse resp = null;
        try {
            client = defaultHttpClient();
            HttpPost httPost = new HttpPost(postUrl);
            String charset = "UTF-8" ;
            if (strEntity != null) {
                StringEntity entity = new StringEntity(strEntity, charset);
                entity.setContentEncoding(charset);
                entity.setContentType(contentType);
                httPost.setEntity(entity);
            }
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httPost.addHeader(key, headers.get(key));
                }
            }
            resp = client.execute(httPost);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httEntity = resp.getEntity();
                respResult = EntityUtils.toString(httEntity);
                log.info("post url:" + postUrl + ",param:" + strEntity);
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            HttpClientUtils.closeQuietly(resp);
            HttpClientUtils.closeQuietly(client);
        }
        log.info("response:{}", respResult);
        return respResult;

    }

    public static String onRequestGet(String getUril) {
        return onRequestGet(getUril, null);
    }

    public static String onRequestGet(String getUril, Map<String, String> headers) {
        String respResult = null;
        InputStream is = null;
        HttpClient client = null;
        HttpResponse resp = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            client = defaultHttpClient();
            HttpGet httpGet = new HttpGet(getUril);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httpGet.addHeader(key, headers.get(key));
                }
            }
            log.info("request url:{},headers:{}", getUril, headers);
            resp = client.execute(httpGet);
            log.info("response status:{}", resp.getStatusLine().getStatusCode());
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                is = resp.getEntity().getContent();
                byte[] buffer = new byte[1024];
                byteArrayOutputStream = new ByteArrayOutputStream();
                int len = 0;
                while ((len = is.read(buffer)) > 0) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
                respResult = new String(byteArrayOutputStream.toByteArray());
                log.info("response:{}", respResult);
            }
        } catch (Exception e) {
            log.error("{}", e);
        } finally {
            HttpClientUtils.closeQuietly(client);
            HttpClientUtils.closeQuietly(resp);
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                log.error("{}", e);
            }
        }
        return respResult;
    }


    public static String onJsonBodyRequest(String body, String postUrl, Map<String, String> headers) {
        return onRequest(body, postUrl, MediaType.APPLICATION_JSON_VALUE, headers, StandardCharsets.UTF_8.name());
    }


    public static String onRequest(String body, String postUrl, String contentType, Map<String, String> headers, String respCharSet) {
        log.info("body:{}", body);
        StringEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
        entity.setContentEncoding(StandardCharsets.UTF_8.name());
        entity.setContentType(contentType);
        return onRequest(entity, postUrl, contentType, headers, respCharSet);
    }


    public static String onRequest(HttpEntity httpEntity, String postUrl, String contentType, Map<String, String> headers, String respCharSet) {
        String respResult = null;
        HttpClient client = null;
        HttpResponse resp = null;
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            client = defaultHttpClient();
            HttpPost httPost = new HttpPost(postUrl);
            if (httpEntity != null) {
                httPost.setEntity(httpEntity);
            }
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    httPost.addHeader(key, entry.getValue());
                }
            }
            httPost.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
            log.info("post url:{},headers:{},Content-Type:{}", postUrl, headers, contentType);
            resp = client.execute(httPost);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				HttpEntity httEntity = resp.getEntity();
//				if(StringUtils.isBlank(respCharSet)){
//					respResult = EntityUtils.toString(httEntity);
//				}else{
//					respResult = EntityUtils.toString(httEntity,respCharSet);
//				}
                inputStream = resp.getEntity().getContent();
                outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                if (StringUtils.isBlank(respCharSet)) {
                    String respContentType = resp.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
                    if (StringUtils.isNotBlank(respContentType)) {
                        int index = respContentType.indexOf(';');
                        if (index > 0) {
                            respContentType = respContentType.substring(index + 1);
                        }
                        index = respContentType.indexOf("charset");
                        if (index > 0) {
                            respCharSet = respContentType.split("=")[1];
                        }
                    }
                    if (respCharSet == null) {
                        respCharSet = StandardCharsets.UTF_8.name();
                    }
                }
                respResult = new String(outputStream.toByteArray(), respCharSet);
                log.info("resp:" + respResult);
            } else {
                log.error("http 响应码:{}", resp.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            HttpClientUtils.closeQuietly(resp);
            HttpClientUtils.closeQuietly(client);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
        return respResult;

    }


    public static String postFileMultiPart(String url, Map<String, String> header, Map<String, ContentBody> reqParam) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            // 创建httpget.
            HttpPost httppost = new HttpPost(url);

            //setConnectTimeout：设置连接超时时间，单位毫秒。setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
            RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(15000).build();
            httppost.setConfig(defaultRequestConfig);

            System.out.println("executing request " + httppost.getURI());

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            for (Map.Entry<String, ContentBody> param : reqParam.entrySet()) {
                multipartEntityBuilder.addPart(param.getKey(), param.getValue());
            }
            HttpEntity reqEntity = multipartEntityBuilder.build();
            httppost.setEntity(reqEntity);
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httppost.addHeader(entry.getKey(), entry.getValue());
            }
            // 执行post请求.
            CloseableHttpResponse response = httpclient.execute(httppost);

            System.out.println("got response");

            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                //System.out.println("--------------------------------------");
                // 打印响应状态
                //System.out.println(response.getStatusLine());
                if (entity != null) {
                    return EntityUtils.toString(entity, Charset.forName("UTF-8"));
                }
                //System.out.println("------------------------------------");
            } finally {
                response.close();

            }
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
