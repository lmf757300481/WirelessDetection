package Temperature;

import java.util.Date;

public class TemperaturePoint {

	private Date time;
	private double temperature;
	
	
	public TemperaturePoint(double temperature,Date time) {
		super();
		this.time = time;
		this.temperature = temperature;
	}


	public TemperaturePoint() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Date getTime() {
		return time;
	}


	public void setTime(Date time) {
		this.time = time;
	}


	public double getTemperature() {
		return temperature;
	}


	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	
	

}
