package FBG;

import java.util.Date;

public class FBGSignal {
	
	
	private double peakWavelength;
	private Date date;

	public FBGSignal(double peakWavelength, Date date) {
		super();
		this.peakWavelength = peakWavelength;
		this.date = date;
	}
	
	public FBGSignal(double peakWavelength) {
		super();
		this.peakWavelength = peakWavelength;
		this.date = new Date(); 
	}
	
	public FBGSignal() {
		super();
		this.date = new Date(); 
	}

	public FBGSignal(Date date) {
		super();
		this.peakWavelength = 0;
		this.date = date;
	}
	
	public double getPeakWavelength() {
		return peakWavelength;
	}

	public void setPeakWavelength(double peakWavelength) {
		this.peakWavelength = peakWavelength;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	
}
