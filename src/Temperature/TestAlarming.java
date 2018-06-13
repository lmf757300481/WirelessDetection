package Temperature;

import java.util.Date;
import java.util.Random;

public class TestAlarming {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		TemperaturePoints tps=new TemperaturePoints();
		 Random random = new Random();
		
		for (int i = 0; i < 120; i++) {
			System.out.print("第"+i+"个温度点");
			TemperaturePoint current=new TemperaturePoint(random.nextInt(100),new Date());
			tps.add(current);
			tps.check(70, 10);
			Thread.sleep(1000);
			
		}

	}

}
