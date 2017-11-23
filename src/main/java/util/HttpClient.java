package util;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClient {

	/*
    private static final String GET_URL = "http://localhost:9090/SpringMVCExample";
    private static final String POST_URL = "http://localhost:9090/SpringMVCExample/home";
    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    urlParameters.add(new BasicNameValuePair("user", "password"));
    urlParameters.add(new BasicNameValuePair("key", "value"));
    */
	private static final String USER_AGENT = "Mozilla/5.0";
	
	// HttpGet request
	public static void sendGET(String get_URL) throws IOException {
        
    	CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(get_URL);
        httpGet.addHeader("User-Agent", USER_AGENT);
        try{
        	CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        	try{
	        	HttpEntity entity = httpResponse.getEntity(); 
	        	//System.out.println("--------------------------------------");
	        	System.out.println("  GET Response Status:: " + httpResponse.getStatusLine().getStatusCode());
	        	
	        	if (entity != null) {  
	                //System.out.println("  Response content length: " + entity.getContentLength());  // 打印响应内容长度  
	                System.out.println("  Response content: " + EntityUtils.toString(entity)); // 打印响应内容  
	            } 
	        	
        	}
        	finally{  
        	httpResponse.close();
        	}
        }
        catch(Exception e){
            System.out.println(e);
        }
        finally{
        	httpClient.close(); 
        }

        /*
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        // print result
        System.out.println(response.toString());
        */
        
        //httpClient.close();
    }

    
    // HttpPost request
	public static void sendPOST(String post_URL, List<NameValuePair> urlParameters) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(post_URL);
        httpPost.addHeader("User-Agent", USER_AGENT);
        
        HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
        httpPost.setEntity(postParams);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        System.out.println("POST Response Status: " + httpResponse.getStatusLine().getStatusCode());

        /*
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        // print result
        System.out.println(response.toString());
        */
        
        httpClient.close();
    }
	
	// HttpGet request
		public static String sendSensorGET(String get_URL) throws IOException {
			String reslut = null;
	    	CloseableHttpClient httpClient = HttpClients.createDefault();
	        HttpGet httpGet = new HttpGet(get_URL);
	        httpGet.addHeader("User-Agent", USER_AGENT);
	        try{
	        	CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
	        	try{
		        	//System.out.println("--------------------------------------");
		        	System.out.println("  GET Response Status: " + httpResponse.getStatusLine().getStatusCode());
		        	final HttpEntity entity = httpResponse.getEntity(); 
		        	
		        	if (entity != null) {
		        		reslut = EntityUtils.toString(entity);
		                //System.out.println("  Response content length: " + entity.getContentLength());  // 打印响应内容长度
		                System.out.println("  Response content: " + reslut); // 打印响应内容
		            }
		        	else{
		        		System.out.println("  Response [NULL]"); 
		        	}
		        	
	        	}
	        	catch(Exception e){
	        		System.out.println("[1]:" + e);
	        	}
	        	finally{
	        	httpResponse.close();
	        	}
	        }
	        catch(Exception e){
	            System.out.println("[2]:" + e);
	        }
	        finally{
	        	httpClient.close(); 
	        }
	        return reslut;
	    }

}