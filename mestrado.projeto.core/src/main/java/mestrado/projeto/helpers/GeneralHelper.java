package mestrado.projeto.helpers;

public class GeneralHelper {

	public static String convertMilliToTime(Long duration){
		
		String time = String.format("%02d:%02d:%02d.%03d", duration/(3600*1000),
				duration/(60*1000) % 60,
				duration/1000 % 60,
				duration%1000);
		
		return time;
	}
}
