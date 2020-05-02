package mestrado.projeto.statistics.processor;

public class StatisticsEntities {

	//file_uuid,producer_uuid,text_extraction_start,text_extraction_stop,send_to_slasticsearch_start,send_to_slasticsearch_stop
	//file_uuid,consumer_uuid,bin_conversion_start,bin_conversion_stop,send_to_kafka_start,send_to_kafka_stop
	private String file_uuid;
	private String producer_uuid;
	private String bin_conversion_start;
	private String bin_conversion_stop;
	private String send_to_kafka_start;
	private String send_to_kafka_stop;
	private String consumer_uuid;
	private String text_extraction_start;
	private String text_extraction_stop;
	private String send_to_elasticsearch_start;
	private String send_to_elasticsearch_stop;
	
	public String getFile_uuid() {
		return file_uuid;
	}
	public void setFile_uuid(String file_uuid) {
		this.file_uuid = file_uuid;
	}
	public String getProducer_uuid() {
		return producer_uuid;
	}
	public void setProducer_uuid(String producer_uuid) {
		this.producer_uuid = producer_uuid;
	}
	public String getBin_conversion_start() {
		return bin_conversion_start;
	}
	public void setBin_conversion_start(String bin_conversion_start) {
		this.bin_conversion_start = bin_conversion_start;
	}
	public String getBin_conversion_stop() {
		return bin_conversion_stop;
	}
	public void setBin_conversion_stop(String bin_conversion_stop) {
		this.bin_conversion_stop = bin_conversion_stop;
	}
	public String getSend_to_kafka_start() {
		return send_to_kafka_start;
	}
	public void setSend_to_kafka_start(String send_to_kafka_start) {
		this.send_to_kafka_start = send_to_kafka_start;
	}
	public String getSend_to_kafka_stop() {
		return send_to_kafka_stop;
	}
	public void setSend_to_kafka_stop(String send_to_kafka_stop) {
		this.send_to_kafka_stop = send_to_kafka_stop;
	}
	public String getConsumer_uuid() {
		return consumer_uuid;
	}
	public void setConsumer_uuid(String consumer_uuid) {
		this.consumer_uuid = consumer_uuid;
	}
	public String getText_extraction_start() {
		return text_extraction_start;
	}
	public void setText_extraction_start(String text_extraction_start) {
		this.text_extraction_start = text_extraction_start;
	}
	public String getText_extraction_stop() {
		return text_extraction_stop;
	}
	public void setText_extraction_stop(String text_extraction_stop) {
		this.text_extraction_stop = text_extraction_stop;
	}
	public String getSend_to_elasticsearch_start() {
		return send_to_elasticsearch_start;
	}
	public void setSend_to_elasticsearch_start(String send_to_elasticsearch_start) {
		this.send_to_elasticsearch_start = send_to_elasticsearch_start;
	}
	public String getSend_to_elasticsearch_stop() {
		return send_to_elasticsearch_stop;
	}
	public void setSend_to_elasticsearch_stop(String send_to_elasticsearch_stop) {
		this.send_to_elasticsearch_stop = send_to_elasticsearch_stop;
	}			
}
