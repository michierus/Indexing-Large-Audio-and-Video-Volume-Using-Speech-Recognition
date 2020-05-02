package mestrado.projeto.helpers.elasticsearch;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;



public class ElasticSearchHelper {
	
	private RestHighLevelClient client = null;
	private String elasticsearchUrl = ""; 
	private Integer elasticsearchPort = null;
	private String elasticsearchProtocol= ""
	;
	public ElasticSearchHelper (boolean initialize) throws IOException{
		/*RestHighLevelClient client = new RestHighLevelClient(
        RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9201, "http")));*/

		InputStream input = null;
		try {
			input = ElasticSearchHelper.class.getClassLoader().getResourceAsStream("config.properties");
		    Properties prop = new Properties();
		
		    // load a properties file
		    prop.load(input);
		    
		    System.out.println("");
		    System.out.println("### BEGIN: Elasticseach configs ###");
		    
		    // get the property value and print it out
		    elasticsearchUrl = prop.getProperty("elasticsearch.url"); 
		    System.out.println("elasticsearchUrl: " + elasticsearchUrl);            
		    
		    elasticsearchPort = Integer.parseInt(prop.getProperty("elasticsearch.port")); 
		    System.out.println("elasticsearchPort: " + elasticsearchPort);
		    
		    elasticsearchProtocol = prop.getProperty("elasticsearch.protocol"); 
		    System.out.println("elasticsearchProtocol: " + elasticsearchProtocol);            
		    
		    System.out.println("### END: Elasticseach configs ###");
		    System.out.println("");
		    
		    if(initialize){
		        client = new RestHighLevelClient(
				        RestClient.builder(
				                new HttpHost(elasticsearchUrl, elasticsearchPort, elasticsearchProtocol)));
		    }
		    
		} catch (IOException ex) {
		    ex.printStackTrace();
		}finally{
			if
			(input != null)
				input.close();
		}
	}

	
	public boolean put(String index, String jsonString, int retry) throws Exception{
		try{
			
			IndexRequest request = new IndexRequest(index);
			//Datetime uuid based
			String id = org.elasticsearch.common.UUIDs.base64UUID();
			request.id(id);
			request.source(jsonString, XContentType.JSON);
			IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
			System.out.println("Id of document send to elasticsearch: " + indexResponse.getId());
			
			return true;
		}catch(Exception ex){
			System.out.println("Issue to send data to elasticsearch. Details: " + ex.getMessage() + "Stack trace: " + ex.getStackTrace());
			
			if(retry == 3)
				throw new Exception("Data could not be sent to elastic search after " + retry + " time(s).");
			
			return this.put(index, jsonString, retry++);						
		}		 
	}
	
	
	public void open() throws IOException{
		
		client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost(elasticsearchUrl, elasticsearchPort, elasticsearchProtocol)));
		
	}
	
	public void close() throws IOException{
		if(client != null)
			client.close();		
	}
	

}
