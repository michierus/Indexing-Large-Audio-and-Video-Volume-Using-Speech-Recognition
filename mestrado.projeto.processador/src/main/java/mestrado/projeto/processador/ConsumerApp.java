package mestrado.projeto.processador;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.lang3.SystemUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import mestrado.projeto.core.entities.FileAttributes;
import mestrado.projeto.core.entities.FileProcessed;
import mestrado.projeto.helpers.GeneralHelper;
import mestrado.projeto.helpers.JsonHelper;
import mestrado.projeto.helpers.StatisticsHelper;
import mestrado.projeto.helpers.StopwordsHelper;
import mestrado.projeto.helpers.elasticsearch.ElasticSearchHelper;


/**
 * Hello world!
 *
 */
public class ConsumerApp 
{	
	
	private static boolean isOSWindows = false;
	
    public static void main( String[] args ) throws Exception
    {
    	
    	System.out.println("#############################################");
    	System.out.println("Master Degree Project\n");
    	System.out.println("   Software: Kafka Consumer");    	
    	System.out.println("    Version: v1.0");
    	System.out.println("Description: Consumer processes messages from Kafka topic, extract the text from audio and sent it to elasticsearch.");
    	System.out.println("     Author: Michel Vieira Batista");    	
    	System.out.println("#############################################");
    	
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
    	 
    	 String fileUUID = "";
    	 String producerUUID = java.util.UUID.randomUUID().toString();
    	 String textExtractionStart = "";
    	 String textExtractionStop = "";
    	 String sendToElasticSearchStart = "";
    	 String sendToElasticSearchStop = "";
    	     	     	
    	String AcousticModelPath = "";    	
    	String LanguageModelPath = "";
    	String LexiconModelPath = "";
    	
    	String kafkaBroker = "";
    	String topicName = "";
    	
    	String elasticsearchIndex = "";
    	
    	String statisticsPath = "";
    	
    	 //try (InputStream input = new FileInputStream("/resources/config.properties")) {
    	try (InputStream input = ConsumerApp.class.getClassLoader().getResourceAsStream("config.properties")) {
             Properties prop = new Properties();

             // load a properties file
             prop.load(input);
             
             System.out.println("");
             System.out.println("### BEGIN: CMU SPhinx Models paths ###");
             
             if(isOSWindows){
            	 AcousticModelPath = prop.getProperty("windows.acoustic.model.path");
            	 LexiconModelPath = prop.getProperty("windows.lexicon.model.path");
            	 LanguageModelPath = prop.getProperty("windows.language.model.path");
            	 
            	 statisticsPath = prop.getProperty("windows.statistics.path");
             }else{
            	 AcousticModelPath = prop.getProperty("linux.acoustic.model.path");
            	 LexiconModelPath = prop.getProperty("linux.lexicon.model.path");
            	 LanguageModelPath = prop.getProperty("linux.language.model.path");
            	 
            	 statisticsPath = prop.getProperty("linux.statistics.path");
             }
             
             // get the property value and print it out
              
             System.out.println("AcousticModelPath: " + AcousticModelPath);     
             System.out.println("LexiconModelPath: " + LexiconModelPath);
             System.out.println("LanguageModelPath: " + LanguageModelPath);
             
             System.out.println("Statistics path: " + statisticsPath); 
                          
             System.out.println("### END: CMU SPhinx Models paths ###");
             System.out.println("");
             
             System.out.println("### BEGIN: General configs ###");
             
             kafkaBroker = prop.getProperty("kafka-broker");
             System.out.println("KafkaBroker: " + kafkaBroker);
             topicName = prop.getProperty("kafka-topic");      
             System.out.println("KafkaTopicName: " + topicName);
             elasticsearchIndex = prop.getProperty("elasticsearch.index");
             System.out.println("ElasticSearchIndex: " + elasticsearchIndex);
             
             System.out.println("### END: General configs ###\n");

         } catch (IOException ex) {
             ex.printStackTrace();
         }
    	
    	StatisticsHelper statisticHelper = new StatisticsHelper(statisticsPath, "consumer");
    	    	
    	System.out.println("\n### BEGIN: Initializing kafka setup... ###");
                                
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);        
        
        props.setProperty("group.id","mestrado.projeto.processor");
                
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("compression.type","snappy");
        
        props.put("key.deserializer", 
                "org.apache.kafka.common.serialization.StringDeserializer");
                
             props.put("value.deserializer", 
                "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        
        props.setProperty("auto.offset.reset","earliest");
        props.put("fetch.message.max.bytes","7340032");
        
        props.setProperty("max.poll.records","1");
             
        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<String, byte[]>(props);
                
        //Kafka Consumer subscribes list of topics here.
        consumer.subscribe(Arrays.asList(topicName));
        
        //print the topic name
        System.out.println("Subscribed to kafka topic: " + topicName);
        
        
        System.out.println("### END: Kafka setup done. ###\n");
                                    
        int i = 0;
        
        System.out.println("### BEGIN: CMU Sphinx setup initializing... ###");
        System.out.println("Loading CMU Sphinx models...");
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath(AcousticModelPath);
        configuration.setDictionaryPath(LexiconModelPath);
        configuration.setLanguageModelPath(LanguageModelPath);
        System.out.println("CMU Sphinx models loaded.");
        
        //Code used to disable the CMU SPhinx logs
        Logger cmRootLogger = Logger.getLogger("default.config");
        cmRootLogger.setLevel(java.util.logging.Level.OFF);
        String conFile = System.getProperty("java.util.logging.config.file");
        if (conFile == null) {
              System.setProperty("java.util.logging.config.file", "ignoreAllSphinx4LoggingOutput");
        }
        
        System.out.println("Initializing CMU Sphinx Speech Recognizer...");
       StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
       System.out.println("CMU Sphinx Speech Recognizer initialized.");
       
       System.out.println("### END: CMU Sphinx setup done... ###\n");
              
       //Initialize elasticsearch helper
       ElasticSearchHelper elasticsearchHelper = new ElasticSearchHelper(true);       
        
       try{
    	   System.out.println("");
	       System.out.println("Kafka topic pooling...\n");
	       InputStream stream = null;
	       SpeechResult result;
	       FileProcessed fileProcessed = null;
	       String json = null;
	       
	       long pollCount = 1;
	       ConsumerRecords<String, byte[]> records = ConsumerRecords.empty(); 
	       
	       //String fullText = "";
	       StringBuilder fullText = null;
	       long msgCount = 1;
	        while (true) {
	        	//System.out.println("\n" + poolCount + "º Kafka pooling... ");
	           records = consumer.poll(1000);
	               
	           if(records != null && records.count() > 0){
	        	   pollCount++;		           
		           System.out.println(records.count() + " Kafka message(s) received. " + pollCount + "º poll(s)");
	           }
	           
	           for (ConsumerRecord<String, byte[]> record : records){	        	   
	           
	        	   System.out.println("\n####################################### BEGIN: " + msgCount + " MESSAGE PROCESSING #######################################\n");
	        	   
	        	   //Close the consumer to allow other onsumer gets the files to be processed from this partition.
	        	   consumer.close();
	        	   
	           // print the offset,key and value for the consumer records.
	           //System.out.printf("offset = %d, key = %s, value = %s\n", 
	              //record.offset(), record.key(), record.value());
	        	   System.out.printf("partition = %d,\noffset = %d,\nkey = %s\n", 
	     	              record.partition(), record.offset(), record.key());
	        	   
	        			
				String fileId = java.util.UUID.randomUUID().toString();
								
				
				//Clear resources
		        if(stream != null)
		        	stream.close();	 
		        result = null;
		        fileProcessed = null;
		        json = null;
		        
				fileProcessed = new FileProcessed(); 
				fileProcessed.setFileAttributes(JsonHelper.convertFromJson(FileAttributes.class, record.key()));
				
				fileUUID = fileProcessed.getFileAttributes().getUUID();
				ZonedDateTime zdtTextExtractionStart = ZonedDateTime.now( z );
				textExtractionStart = zdtTextExtractionStart.format(formatter);
				
				System.out.println("Start: " + textExtractionStart);
		        
		        System.out.println("\nStarting speech recognition... ");
		        stream = new ByteArrayInputStream(record.value());
		        stream.skip(44);
		        		        
				recognizer.startRecognition(stream);
				
				//String textWithTime = "";
				 
				fullText = new StringBuilder();
				 while ((result = recognizer.getResult()) != null) {
				    	//fullText = fullText + " " + result.getHypothesis();
					 fullText.append(" ").append(result.getHypothesis());
					    //System.out.format("Hypothesis: %s\n", result.getHypothesis());		  
					    
					    // Get individual words and their times.
					    for (WordResult r : result.getWords()) {
					    	//System.out.println("get words: " + r);
						      
					        String word = r.getWord().toString();
					        double confidence = result.getResult().getLogMath().logToLinear((float)r.getConfidence());
					        Long begin = r.getTimeFrame().getStart();
					        Long end = r.getTimeFrame().getEnd();
					        
					        //String objJson = "{ \"word\": \"" + word + "\", \"confidence\": " + confidence + ", \"begin\": " + begin + ", \"end\": " + end + "}";
					        String beginStr = GeneralHelper.convertMilliToTime(begin);
					        String endStr = GeneralHelper.convertMilliToTime(end);
					        
					        fileProcessed.addWord(word,  beginStr);
					        
					        /*if(textWithTime != null && textWithTime != ""){
					        	textWithTime = textWithTime + ", " + objJson;
					        }else{
					        	textWithTime = textWithTime + objJson;
					        }*/		   
					    }
				    }
				 
				 System.out.println("Speech recognition done. ");
				 recognizer.stopRecognition();	
				 
				 ZonedDateTime zdtTextExtractionStop = ZonedDateTime.now( z );
				 textExtractionStop = zdtTextExtractionStop.format(formatter);
				 
				 System.out.format("Filename:" + fileId + " - " + "Text transcripted: %s\n", fullText.toString());
				 	        fullText = null;
				 
				
				 long millis = Duration.between(zdtTextExtractionStart, zdtTextExtractionStop).toMillis();
		         System.out.println("Processing duration to speech recognition: " + GeneralHelper.convertMilliToTime(millis));
		        
		         
		         System.out.println("Kafka message processed!\n");
		         		         
		         
		         //System.out.println("Complete final result:" + JsonHelper.convertToJson(fileProcessed));
		         
		         fileProcessed.setWords(StopwordsHelper.removeStopWords(fileProcessed.getWords()));
		         json = JsonHelper.convertToJson(fileProcessed);
		         //System.out.println("Complete final result without stopwords:" + json);
		         
		         ZonedDateTime zdtSendToElasticSearchStart = ZonedDateTime.now( z );
		         sendToElasticSearchStart = zdtSendToElasticSearchStart.format(formatter);
		         
		         System.out.println("Sending text to elasticsearch...");
		         //elasticsearchHelper.open();
		         elasticsearchHelper.put(elasticsearchIndex, json, 1);	         
		         //elasticsearchHelper.close();
		         System.out.println("Text sent to elasticsearch.");
		         
		         ZonedDateTime zdtSendToElasticSearchStop = ZonedDateTime.now( z );
		         sendToElasticSearchStop = zdtSendToElasticSearchStop.format(formatter);
		         
		         long millis2 = Duration.between(zdtSendToElasticSearchStart, zdtSendToElasticSearchStop).toMillis();		         
		         System.out.println("Processing duration to send to elasticsearch: " + GeneralHelper.convertMilliToTime(millis2));	
		         
		         if(fileUUID == "" || fileUUID == null)
		        	 fileUUID = fileId;
		         statisticHelper.addConsumerStatistic(fileUUID, producerUUID, textExtractionStart, textExtractionStop, sendToElasticSearchStart, sendToElasticSearchStop);
		         
		         System.out.println("Stop: " + sendToElasticSearchStop);
		         		         
		         System.out.println("\n####################################### END: " + msgCount + "  MESSAGE PROCESSED #######################################\n");
		         msgCount++;
		         		         
		         //System.out.println(dateFormat.format(Calendar.getInstance().getTime()));
		         
		         //Create connection with kafka again after process the file.
		         consumer = new KafkaConsumer<String, byte[]>(props);
		         consumer.subscribe(Arrays.asList(topicName));

		         
	           }
	           
	           if(records != null && records.count() > 0){
   		           
		           System.out.println("Waiting for more data...");
	           }
	           
	        } 
       
       }
       finally{
    	   elasticsearchHelper.close();
       }
       
    }
}
