package util;

import javax.annotation.PostConstruct;

import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;


public class ClientUtil {
	private static PoolingHttpClientConnectionManager clientConnectionManager = null;
	private static CloseableHttpClient httpClient = null;
	private static RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).setConnectTimeout(3000).setConnectionRequestTimeout(3000).build();
	private final static Object syncLock = new Object();

	@PostConstruct
    private void init(){

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .build();
        clientConnectionManager =new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        clientConnectionManager.setMaxTotal(50);
        clientConnectionManager.setDefaultMaxPerRoute(25);
    }
	
	  public static CloseableHttpClient getHttpClient(CookieStore cookieStore){
	        if(httpClient == null){
	            synchronized (syncLock){
	                if(httpClient == null){
//	                    BasicClientCookie cookie = new BasicClientCookie("sessionID", "######");
//	                    cookie.setDomain("#####");
//	                    cookie.setPath("/");
//	                    cookieStore.addCookie(cookie);
	                    httpClient =HttpClients.custom().setConnectionManager(clientConnectionManager).setDefaultCookieStore(cookieStore).setDefaultRequestConfig(config).build();
	                }
	                }
	            }
	        return httpClient;
	        }
	  
	  public static void clear(){
		  httpClient=null;
	  }
}

