package CrossheadBush;

import java.util.Date;

public class RotationalSpeed {
	
	private Date last;
	private Date now;	
	private double speed;
	
	private boolean  isInitialized;
	
	
	
	
	public RotationalSpeed(Date next) {		
		this.now=next;	
		this.setInitialized(false);
	}
	
	
	public double refreshSpeed(Date next) {

		if (this.isInitialized) {
			this.speed = 1000 / (next.getTime() - this.last.getTime());
			this.last = this.now;
			this.now = next;
			return this.speed;
		}

		else if (null == this.now && this.now.getTime() == 0) {
			this.now = next;			
			return 0;
		} else if (this.last == null && this.last.getTime() == 0) {
			this.last = this.now;
			this.now = next;
			this.setInitialized(true);
			return 0;
		}
		return speed;

	}
	
	
	public RotationalSpeed() {
		super();
		// TODO Auto-generated constructor stub
		this.last=new Date();
		this.now=new Date();
		this.setInitialized(false);
		
	}
	
	public Date getLast() {
		return last;
	}
	public void setLast(Date last) {
		this.last = last;
	}
	public Date getNow() {
		return now;
	}
	public void setNow(Date now) {
		this.now = now;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public boolean isInitialized() {
		return isInitialized;
	}


	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}
	
	
	

}
