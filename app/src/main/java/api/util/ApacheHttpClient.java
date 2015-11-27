package api.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;


/**
 * 以同步方式发送Http请求的工具类
 * @author zzhan@linewell.com
 * @since 2013-03-20
 */
public class ApacheHttpClient{
	
	public final static String CHARSET = "UTF-8";  
	/**
	 * 以Get方式发送请求
	 * @param url 请求路径
	 * @return 响应结果字符串
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String httpGet(String url) throws ClientProtocolException, IOException{
		
		String response = null;
		HttpClient httpclient = new DefaultHttpClient();
		
		//设置超时时间。默认10000毫秒。
		httpclient.getParams().setParameter("http.socket.timeout",30000);
		httpclient.getParams().setParameter("http.connection.timeout",30000);
		
		//创建HttpGet对象
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse;

		//使用execute方法发送HTTP GET请求，并返回HttpResponse对象
		httpResponse = httpclient.execute(httpGet);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if(statusCode==HttpStatus.SC_OK){
			
			//获得返回结果
			response = "200";
		}else{
			response = "返回码："+statusCode;
		}

		return response;
		
	}

}
