package mestrado.projeto.statistics.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;

import mestrado.projeto.helpers.GeneralHelper;
import mestrado.projeto.helpers.JsonHelper;
import mestrado.projeto.helpers.elasticsearch.ElasticSearchHelper;

/**
 * Hello world!
 *
 */
public class StatisticsProcessorApp 
{
	private static boolean isOSWindows = false;
	private static boolean isConsumer = false;
	
	private static String producerPath = "";
	private static String consumerPath = "";
	private static String producerOutPath = "";
	private static String producerErrorPath = "";
	private static String consumerOutPath = "";
	private static String consumerErrorPath = "";	
	private static String processingPath = "";
	private static String processingCompletePath = "";
	private static String processingOutPath = "";
	private static String sentToElasticsearch = "";
	
	private static String producerHeader = "";
	private static String consumerHeader= "";
	
	private static String elasticsearchIndex = "";
	
	
	private static List<String> errorList = new ArrayList<String>();
	private static boolean moveToOut = true;
	
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Hello World!" );
        
        System.out.println("#############################################");
    	System.out.println("Master Degree Project\n");
    	System.out.println("   Software: Statistics Processor");    	
    	System.out.println("    Version: v1.0");
    	System.out.println("Description: Process the statistics files and move the data to elasticsearch.");
    	System.out.println("     Author: Michel Vieira Batista");    	
    	System.out.println("#############################################\n");
    	
    	 if(SystemUtils.IS_OS_WINDOWS){
    		 isOSWindows = true;
    		 System.out.println("\nOperational system: Windows \n");
    	 }else{
    		 if(SystemUtils.IS_OS_LINUX)
    			 System.out.println("\nOperational system: Linux\n");
    		 else{
    			 System.out.println("\nOperational system: Unknown\n");
    		 }
    	 } 
    	 
    	 ZoneId z = ZoneId.of( "America/Sao_Paulo" );    	 
    	 DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    	 ZonedDateTime zdt = ZonedDateTime.now( z );
    	 System.out.println( "Start: " + zdt.format(formatter) ); 
    	 
    	 String totalProcessingStart = "";
    	 String totalProcessingStop = "";
    	 String processingStart = "";
    	 String processingStop = "";
    	 String sendToelasticSearchStart = "";
    	 String sendToelasticSearchStop = "";
    	 
    	 System.out.println( "\n### BEGIN ###" );
         
         System.out.println( "Arguments:" );
         
         if(args.length == 4){
        	producerPath = args[0];
        	consumerPath = args[1];
        	producerOutPath = args[2];
        	producerErrorPath = args[3];
        	consumerOutPath = args[4];
        	consumerErrorPath = args[5];	
        	processingPath = args[6];       
        	processingCompletePath = args[7];
        	producerHeader = args[8];
        	consumerHeader= args[9];
        	processingOutPath = args[10];
        	
         }else{
         	try (InputStream input = StatisticsProcessorApp.class.getClassLoader().getResourceAsStream("config.properties")) {
         		Properties prop = new Properties();

                 // load a properties file
                 prop.load(input);
                 
                 producerHeader = prop.getProperty("producer.header");
                 consumerHeader = prop.getProperty("consumer.header");
                 elasticsearchIndex = prop.getProperty("elasticsearch.index");
                                 
                 if(isOSWindows){
                	 producerPath = prop.getProperty("windows.statistics.path.producer");
                	 consumerPath = prop.getProperty("windows.statistics.path.consumer");
                	 producerOutPath = prop.getProperty("windows.file.path.producer.out");
                	 producerErrorPath = prop.getProperty("windows.file.path.producer.error");
                	 consumerOutPath = prop.getProperty("windows.file.path.consumer.out");
                	 consumerErrorPath = prop.getProperty("windows.file.path.consumer.error");
                	 
                	 processingPath = prop.getProperty("windows.file.path.processing"); 
                	 processingCompletePath = prop.getProperty("windows.file.path.processing.complete");
                	 processingOutPath = prop.getProperty("windows.file.path.processing.out");
                	 sentToElasticsearch = prop.getProperty("windows.file.path.processing.sentToElasticsearch");
                	 
                	 
                 }
                 else{
                	 producerPath = prop.getProperty("linux.statistics.path.producer");
                	 consumerPath = prop.getProperty("linux.statistics.path.consumer");
                	 producerOutPath = prop.getProperty("linux.file.path.producer.out");
                	 producerErrorPath = prop.getProperty("linux.file.path.producer.error");
                	 consumerOutPath = prop.getProperty("linux.file.path.consumer.out");
                	 consumerErrorPath = prop.getProperty("linux.file.path.consumer.error");
                	 
                	 processingPath = prop.getProperty("linux.file.path.processing");
                	 processingCompletePath = prop.getProperty("linux.file.path.processing.complete");
                	 processingOutPath = prop.getProperty("linux.file.path.processing.out");
                	 sentToElasticsearch = prop.getProperty("linux.file.path.processing.sentToElasticsearch");
                 }                
         	}        	
         }
         
         
         //DELETE
         //GET FILENAMES
        /* File folder = new File(processingPath);
		 File[] listOfFiles = folder.listFiles();
		 int count = 0;
		 for (File file : listOfFiles) {
			 if (file.isFile()) {
				 System.out.println(file.getName());
			 }
		 }*/
         
         //DELETE
         
          System.out.println( "Producer path:" + producerPath );        
         System.out.println( "Consumer servers:" + consumerPath );
         System.out.println( "Producer Out topic:" + producerOutPath );
         System.out.println( "Producer Error path:" + producerErrorPath);
         System.out.println( "Consumer Out topic:" + consumerOutPath );
         System.out.println( "Consumer Error path:" + consumerErrorPath);
         System.out.println( "Processing path:" + processingPath);
         System.out.println( "Processing complete path:" + processingCompletePath);
         System.out.println( "Processing out path:" + processingOutPath);
         
 		totalProcessingStart = ZonedDateTime.now( z ).format(formatter);
 		 
 		processingStart = ZonedDateTime.now( z ).format(formatter);

 		//process(producerPath);
 		//process(consumerPath);
 		sendToElasticSearch(processingCompletePath);
 		
		processingStop = ZonedDateTime.now( z ).format(formatter);
		
		
		totalProcessingStop = ZonedDateTime.now( z ).format(formatter);
		System.out.println("Stop: " + totalProcessingStop);
		
		System.out.println( "### END ###" );
        
        ZonedDateTime zdtStop = ZonedDateTime.now( z );        
        System.out.println( "Stop: " + zdtStop.format(formatter) );
        
        long millis = Duration.between(zdt, zdtStop).toMillis();             
        
        
        
        int c = 0;
        for (String error : errorList) {
        	c++;
			System.out.println("Error " + c + " - " + error);
		}
        
        System.out.println("quantity of errors: " + errorList.size());
        System.out.println( "\nDuration: " + GeneralHelper.convertMilliToTime(millis));
        
    	     	 
    }
    
    public static void process(String folderPath) throws IOException{
    	File folder = new File(folderPath);
		 File[] listOfFiles = folder.listFiles();
		int count = 0;
		System.out.println( "Join the processed files from producer and consumer...");
		for (File file : listOfFiles) {
			if (file.isFile()) {
				moveToOut = true;
				count++;
		    	System.out.println("Count: " + count + " - " + file.getName());
		    	
		    	String row = "";
		    	BufferedReader csvReader = new BufferedReader(new FileReader(file));
		    	int countLine = 0;
		    	while ((row = csvReader.readLine()) != null) {
		    		System.out.println("Count row: " + countLine + " - " + file.getName());
		    		if(countLine ==0){
		    			//This is the header of the csv.
		    			if(row.equalsIgnoreCase(producerHeader)){
		    				//It is a producer header
		    				System.out.println( "Processing PRODUCER file. file name:" + file.getName());
		    				isConsumer = false;
		    			}else{
		    				if(row.equalsIgnoreCase(consumerHeader)){
		    					//It is a consumer header
			    				System.out.println( "Processing CONSUMER file. file name:" + file.getName());
			    				isConsumer = true;
		    				}else{
		    					//throw new Exception("File does not contains producer neither consumer header.");
		    					System.out.println("File does not contains producer neither consumer header. File name:" + file.getName());
		    					errorList.add("File does not contains producer neither consumer header. File name:" + file.getName());
		    					file.renameTo(new File(producerErrorPath + file.getName()));
		    				}
		    			}
		    		}else{
		    			//Read file lines
		    			String[] data = row.split(",");
		    			
		    			if(isConsumer == false){
		    				processProducer(row, data);
		    			}else{
		    				processConsumer(row, data, file);
		    			}
		    		}
		    		countLine++;
		    	}
		    	csvReader.close();
		    	
		    	String outPath = "";
		    	String errorPath = "";
		    	Path path = null;
		    	if(isConsumer == false){
		    		outPath = producerOutPath;
		    		errorPath = producerErrorPath;
		    				    		
		    		path = Paths.get(outPath);
		    		//Create directory if not exist
		    		if(!Files.exists(path)){
		    			Files.createDirectory(path);
		    		}
		    		
		    		path = Paths.get(errorPath);
		    		//Create directory if not exist
		    		if(!Files.exists(path)){
		    			Files.createDirectory(path);
		    		}
		    		
		    	}else{
		    		outPath = consumerOutPath;
		    		errorPath = consumerErrorPath;
		    		
		    		path = Paths.get(outPath);
		    		//Create directory if not exist
		    		if(!Files.exists(path)){
		    			Files.createDirectory(path);
		    		}
		    		
		    		path = Paths.get(errorPath);
		    		//Create directory if not exist
		    		if(!Files.exists(path)){
		    			Files.createDirectory(path);
		    		}
		    	}
		    	
		    	if(moveToOut == true){
		    		file.renameTo(new File(outPath + file.getName()));
		    	}else{
		    		file.renameTo(new File(errorPath + file.getName()));
		    	}
			}
		}
    }
    
    public static void processProducer(String row, String[] data) throws IOException{
    	
    	String fileUuid = data[0];
    	Path path = null;
    	
    	String fullPath = processingPath + fileUuid + ".csv"; 
		path = Paths.get(fullPath);
		
		//Create directory if not exist
		if(!Files.exists(Paths.get(processingPath))){
			Files.createDirectory(Paths.get(processingPath));
		}
		
		BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
		writer.write(producerHeader);		
		writer.newLine();
		writer.flush();
		writer.write(row);
		writer.newLine();
		writer.flush();
		writer.close();
    }

    public static void processConsumer(String row, String[] data, File file) throws IOException{
    	String fileUuid = data[0];
    	Path path = null;
    	
    	path = Paths.get(processingPath + fileUuid + ".csv");
    	
    	Path target = Paths.get(processingCompletePath + fileUuid + ".csv");
    	if(!Files.exists(Paths.get(processingCompletePath))){
			Files.createDirectory(Paths.get(processingCompletePath));
		}
    	  
    	if(Files.exists(path) == true){
    		List<String> fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
    		
    		for (int i = 0; i < fileContent.size(); i++) {
    			
    			if(i ==0){
    				String header = fileContent.get(i);
    				fileContent.set(i, header + "," + consumerHeader);
    			}
    			else{
    				String content = fileContent.get(i);
    				fileContent.set(i, content + "," + row);
    			}    		    
    		}

    		Files.write(target, fileContent, StandardCharsets.UTF_8);
    		
    		File fileToMove = new File(processingPath + fileUuid + ".csv");    		
    		fileToMove.renameTo(new File(processingOutPath + fileToMove.getName()));
    		
    	}else{
    		System.out.println("Consumer file processed not found. UUID: " + fileUuid);
    		errorList.add("Consumer file processed not found. UUID: " + fileUuid + " Filename: " + file.getName());    		
    		moveToOut = false;    		    		
    	}
    	
    }    
 
    private static int file_uuid_index=0;
    private static int consumer_uuid_index=1;
    private static int bin_conversion_start_index=2;
    private static int bin_conversion_stop_index=3;
    private static int send_to_kafka_start_index=4;
    private static int send_to_kafka_stop_index=5;
    private static int file_uuid_index2=6;
    private static int producer_uuid_index=7;
    private static int text_extraction_start_index=8;
    private static int text_extraction_stop_index=9;
    private static int send_to_elasticsearch_start_index=10;
    private static int send_to_elasticsearch_stop_index=11;
    
    
    public static void sendToElasticSearch(String folderPath) throws Exception{
    	File folder = new File(folderPath);
		 File[] listOfFiles = folder.listFiles();
		int count = 0;
		System.out.println( "Send data to elasticsearch...");
		ElasticSearchHelper elasticsearchHelper = new ElasticSearchHelper(true);
		
		for (File file : listOfFiles) {
			if (file.isFile()) {
				count++;
		    	System.out.println("Count: " + count + " - " + file.getName());
				
		    	String row = "";
		    	BufferedReader csvReader = new BufferedReader(new FileReader(file));
		    	
		    	StatisticsEntities entity = new StatisticsEntities(); 
		    	int countLine = 0;
		    	String json = "";
		    	while ((row = csvReader.readLine()) != null) {
		    		System.out.println("Count row: " + countLine + " - " + file.getName());
		    		if(countLine ==0){
		    			//header do nothing
		    		}else{
		    			//Send to elasticsearch
		    			//Read file lines
		    			String[] data = row.split(",");
		    			
		    			//String to replace this is just for this proof of concept, this is not recomended.
		    			String strToReplace = "-03:00[America/Sao_Paulo]";
		    			
		    			entity.setFile_uuid(data[file_uuid_index]);
		    			
		    			entity.setProducer_uuid(data[consumer_uuid_index]);//OBS: no primeiro processamento eu invert os valores de consumerUUID com producerUUID
		    			entity.setBin_conversion_start(data[bin_conversion_start_index].replace(strToReplace, ""));
		    			entity.setBin_conversion_stop(data[bin_conversion_stop_index].replace(strToReplace, ""));
		    			entity.setSend_to_kafka_start(data[send_to_kafka_start_index].replace(strToReplace, ""));
		    			entity.setSend_to_kafka_stop(data[send_to_kafka_stop_index].replace(strToReplace, ""));
		    					    					    			
		    			entity.setConsumer_uuid(data[producer_uuid_index]);//OBS: no primeiro processamento eu invert os valores de consumerUUID com producerUUID
		    			entity.setText_extraction_start(data[text_extraction_start_index].replace(strToReplace, ""));
		    			entity.setText_extraction_stop(data[text_extraction_stop_index].replace(strToReplace, ""));
		    			entity.setSend_to_elasticsearch_start(data[send_to_elasticsearch_start_index].replace(strToReplace, ""));
		    			entity.setSend_to_elasticsearch_stop(data[send_to_elasticsearch_stop_index].replace(strToReplace, ""));
		    			
		    			json = JsonHelper.convertToJson(entity);
		    			
		    			//Initialize elasticsearch helper
		    		    
		    			boolean result = elasticsearchHelper.put(elasticsearchIndex, json, 1);
		    			if(result == true){
		    				file.renameTo(new File(sentToElasticsearch + file.getName()));		    				
		    			}	    			
		    			
		    		}
		    		countLine++;
		    	}
			}
		}
		
		elasticsearchHelper.close();		
    }   
}
