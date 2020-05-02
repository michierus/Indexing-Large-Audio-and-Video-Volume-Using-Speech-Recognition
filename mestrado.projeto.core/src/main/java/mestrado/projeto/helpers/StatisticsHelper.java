package mestrado.projeto.helpers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class StatisticsHelper {
	
	private Path path = null;
	private String consumerHeader = "file_uuid,consumer_uuid,text_extraction_start,text_extraction_stop,send_to_elasticsearch_start,send_to_elasticsearch_stop";
	private String producerHeader = "file_uuid,producer_uuid,bin_conversion_start,bin_conversion_stop,send_to_kafka_start,send_to_kafka_stop";
	BufferedWriter writer = null;
	
	public StatisticsHelper(String filePath, String type) throws IOException{
		String fileUUID = java.util.UUID.randomUUID().toString();
		ZoneId z = ZoneId.of( "America/Sao_Paulo" );
		ZonedDateTime zdt = ZonedDateTime.now( z );
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
		String datetime = zdt.format(formatter);
		
		String fullPath = filePath + "\\" + type + "-" + datetime + "-" + fileUUID + ".csv"; 
		path = Paths.get(fullPath);
		
		//Create directory if ot exist
		if(!Files.exists(Paths.get(filePath))){
			Files.createDirectory(Paths.get(filePath));
		}
				
		//Files.createFile(path);		
		
		
		writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
		
		if(type.equalsIgnoreCase("producer")){			
			this.addLine(producerHeader);
		}else{			
			this.addLine(consumerHeader);
		}
	}
	
	public void addLine(String content) throws IOException{		
		writer.write(content);
		writer.newLine();
		writer.flush();
	}
	
	public void close() throws IOException{
		path = null;
		consumerHeader = null;
		producerHeader = null;
		writer.close();
	}
	
	public void addProducerStatistic(String fileUUID, String consumerUUID, String binConversiontStart, String binConversionStop, String sendToKafkaStart, String sendToKafkaStop) throws IOException{
		this.addLine(String.join(",", Arrays.asList(fileUUID, consumerUUID, binConversiontStart, binConversionStop, sendToKafkaStart, sendToKafkaStop)));
	}
	
	public void addConsumerStatistic(String fileUUID, String producerUUID, String textExtractionStart, String textExtractionStop, String sendToElasticSearchStart, String sendToElasticSearchStop) throws IOException{
		this.addLine(String.join(",", Arrays.asList(fileUUID, producerUUID, textExtractionStart, textExtractionStop, sendToElasticSearchStart, sendToElasticSearchStop)));
	}

}
