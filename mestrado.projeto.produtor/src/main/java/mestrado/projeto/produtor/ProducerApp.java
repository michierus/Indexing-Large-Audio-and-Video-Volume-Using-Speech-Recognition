package mestrado.projeto.produtor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.StreamsConfig;

import mestrado.projeto.core.entities.FileAttributes;
import mestrado.projeto.helpers.FileAttributesHelper;
import mestrado.projeto.helpers.GeneralHelper;
import mestrado.projeto.helpers.JsonHelper;
import mestrado.projeto.helpers.StatisticsHelper;

/**
 * filePath = "C:\\Google Drive\\Mestrado - Ciência da Computação\\ProjetoOficial\\indir\\test.wav";
 * kafkaServers = "localhost:9092";
 * kafkaTopic = "topic-index001";
 * statisticsPath = "C:\\Google Drive\\Mestrado - Ciência da Computação\\ProjetoOficial\\statistics\\";
 * "C:\\Google Drive\\Mestrado - Ciência da Computação\\ProjetoOficial\\indir\\test.wav" "localhost:9092" "topic-index001" "C:\\Google Drive\\Mestrado - Ciência da Computação\\ProjetoOficial\\statistics\\"
 */
public class ProducerApp 
{
	
	private static boolean isOSWindows = false;
	
    public static void main( String[] args ) throws IOException
    {    	
    	
    	System.out.println("#############################################");
    	System.out.println("Master Degree Project\n");
    	System.out.println("   Software: Kafka Consumer");    	
    	System.out.println("    Version: v1.0");
    	System.out.println("Description: Productor processes audio files and send the binary to Kafka topic.");
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
    	 
    	 String fileUUID = "";
    	 String producerUUID = java.util.UUID.randomUUID().toString();
    	 String binConversionStart = "";
    	 String binConversionStop = "";
    	 String sendToKafkaStart = "";
    	 String sendToKafkaStop = "";
    	 
        System.out.println( "\n### BEGIN ###" );
                
        System.out.println( "Arguments:" );
        
        String filePath = "";
        String kafkaServers = "";
        String kafkaTopic = "";
        String statisticsPath = "";
        
        if(args.length == 4){
        	filePath = args[0];
        	kafkaServers = args[1];
        	kafkaTopic = args[2];
        	statisticsPath = args[3];
        }else{
        	try (InputStream input = ProducerApp.class.getClassLoader().getResourceAsStream("config.properties")) {
        		Properties prop = new Properties();

                // load a properties file
                prop.load(input);
                kafkaServers = prop.getProperty("kafka-broker");
                kafkaTopic = prop.getProperty("kafka-topic");
                
                if(isOSWindows){
                	filePath = prop.getProperty("windows.file.path");
                	statisticsPath = prop.getProperty("windows.statistics.path");
                }
                else{
                	filePath = prop.getProperty("linux.file.path");
                	statisticsPath = prop.getProperty("linux.statistics.path");
                }                
        	}        	
        }
        
        System.out.println( "File path:" + filePath );        
        System.out.println( "Kafka servers:" + kafkaServers );
        System.out.println( "Kafka topic:" + kafkaTopic );
        System.out.println( "Statistics path:" + statisticsPath);
        
        StatisticsHelper statisticHelper = new StatisticsHelper(statisticsPath, "producer");
                        
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);        
        props.setProperty("group.id","mestrado.projeto.processor");
        props.put("acks", "1");
        props.put("compression.type","snappy");
        
        props.put("key.serializer", 
                "org.apache.kafka.common.serialization.StringSerializer");
                
        props.put("value.serializer", 
                "org.apache.kafka.common.serialization.ByteArraySerializer");
       
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		
		System.out.println( "Creating producer..." );
		KafkaProducer<String, byte[]> producer = new KafkaProducer<String, byte[]>(props);
		ProducerRecord<String, byte[]> data = null;
		
		int count = 0;
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	count++;
		    	System.out.println("Count: " + count + " - " + file.getName());
		    	
		    	binConversionStart = ZonedDateTime.now( z ).format(formatter);
		        		        
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        InputStream in = new FileInputStream(file);
		        int read;
		        byte[] buff = new byte[1024];
		        while ((read = in.read(buff)) > 0)
		        {
		            out.write(buff, 0, read);
		        }
		        out.flush();
		        byte[] audioBytes = out.toByteArray();
		        in.close();
		        
		        FileAttributes fileAttrs = FileAttributesHelper.getFileAttributes(file);
		        String fileAttrsJson = JsonHelper.convertToJson(fileAttrs);
		        
		        binConversionStop = ZonedDateTime.now( z ).format(formatter);
		        
		        sendToKafkaStart = ZonedDateTime.now( z ).format(formatter);
		        
		        data = new ProducerRecord<String, byte[]>(kafkaTopic, fileAttrsJson, audioBytes);
		        		        
		        producer.send(data);
		        System.out.println( "File sent to kafka topic" );
		        
		        sendToKafkaStop = ZonedDateTime.now( z ).format(formatter);
		        
		        fileUUID = fileAttrs.getUUID();		        
		        statisticHelper.addProducerStatistic(fileUUID, producerUUID, binConversionStart, binConversionStop, sendToKafkaStart, sendToKafkaStop);
		        
		    }
		}	
		
		System.out.println("All files sent.");
        producer.close();
        
        System.out.println( "### END ###" );
        
        ZonedDateTime zdtStop = ZonedDateTime.now( z );        
        System.out.println( "Stop: " + zdtStop.format(formatter) );
        
        long millis = Duration.between(zdt, zdtStop).toMillis();             
        System.out.println( "\nDuration: " + GeneralHelper.convertMilliToTime(millis));
    }
}
