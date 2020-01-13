package cn.com.hesc.webserviceutils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** 
 * @author  liujunlin
 */
public class MyAndroidHttpTransport extends HttpTransportSE {

	 private int timeout = 30000; //设置超时时间30S
	 private String url = "";
	
     
	    public MyAndroidHttpTransport(String url) {  
	        super(url);  
	    }  
	      
	    public MyAndroidHttpTransport(String url, int timeout) {  
	        super(url);  
	        this.timeout = timeout; 
	        this.url = url;
	    }  
	   
	    public void call(String soapAction, SoapEnvelope envelope)
	    	    throws IOException, XmlPullParserException
	    	  {
	    	    if (soapAction == null)
	    	      soapAction = "\"\""; byte[] requestData = createRequestData(envelope);
	    	    this.requestDump = (this.debug ? new String(requestData) : null);
	    	    this.responseDump = null;
	    	    ServiceConnection connection = getServiceConnection();
	    	    connection.setRequestProperty("User-Agent", "kSOAP/2.0");
	    	    connection.setRequestProperty("SOAPAction", soapAction);
	    	    connection.setRequestProperty("Content-Type", "text/xml");
	    	    connection.setRequestProperty("Connection", "close");
	    	    connection.setRequestProperty("Content-Length", "" + requestData.length);
	    	    connection.setRequestMethod("POST");
	    	    connection.connect();
	    	    OutputStream os = connection.openOutputStream();
	    	    os.write(requestData, 0, requestData.length);
	    	    os.flush();
	    	    os.close();
	    	    requestData = null;
	    	    InputStream is;
	    	    try { connection.connect();
	    	      is = connection.openInputStream();
	    	    } catch (IOException e) {
	    	      is = connection.getErrorStream();
	    	      if (is == null) {
	    	        connection.disconnect();
	    	        throw e;
	    	      }
	    	    }
	    	    if (this.debug) {
	    	      ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    	      byte[] buf = new byte[256];
	    	      while (true) {
	    	        int rd = is.read(buf, 0, 256);
	    	        if (rd == -1)
	    	          break;
	    	        bos.write(buf, 0, rd);
	    	      }
	    	      bos.flush();
	    	      buf = bos.toByteArray();
	    	      this.responseDump = new String(buf);
	    	      is.close();
	    	      is = new ByteArrayInputStream(buf);
	    	    }
	    	    parseResponse(envelope, is);
	    	  }
	    
	    public ServiceConnection getServiceConnection() throws IOException {
	        ServiceConnectionSE serviceConnection = new ServiceConnectionSE(url,timeout);  
	        return serviceConnection;  
	    }  

}
