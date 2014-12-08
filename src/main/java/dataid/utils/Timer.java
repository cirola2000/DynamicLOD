package dataid.utils;

import java.text.DecimalFormat;

public class Timer {
	private double startTime = 0;
	
	public void startTimer(){
		if (startTime == 0)
			startTime = System.currentTimeMillis();

	}
	
	public String stopTimer(){
		double time = System.currentTimeMillis()-startTime;
		startTime = 0;
		 DecimalFormat df = new DecimalFormat("#.##");
		return df.format(time/100);
		
	}
	
}
