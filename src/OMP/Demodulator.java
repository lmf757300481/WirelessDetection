package OMP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Demodulator {
	public static double linearEquation(HashMap<Double, Double> standard, double x1, double x2, double x)
    {
          double numerator=x * (x2 - x1)+((double)standard.get(x2) * x1 - (double)standard.get(x1) * x2);
          double denominator= (standard.get(x2)- standard.get(x1));
        return  numerator/denominator;
//     ((standard.get(x2) - standard.get(x1)) + (standard.get(x2) * x1 - standard.get(x1) * x2) / (standard.get(x2)- standard.get(x1))  );
    }
	
	
	public static double getTemperature(FBG.FBGSensor fbgSensor, FBG.FBGSignal fbgSignal) {
		// 非要采用for的方法也可
		ArrayList<Double> listTemperature=new ArrayList<Double>();
		ArrayList<Double> listWavelength=new ArrayList<Double>();		    
		Iterator<?> iter = fbgSensor.standard.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			listTemperature.add((Double) entry.getKey());
			listWavelength.add((Double) entry.getValue());
		}
		//升序排列；//逆序输出 Collections.reverse();  
		Collections.sort(listTemperature);
		Collections.sort(listWavelength);
//		for (int i = 0; i < listTemperature.size(); i++) {
//			System.out.println(listTemperature.get(i)+"   "+listWavelength.get(i));
//		}
		for (int i = 0; i < listTemperature.size()-1; i++)
        {
            if (fbgSignal.getPeakWavelength() >= listWavelength.get(i) && fbgSignal.getPeakWavelength() <= listWavelength.get(i+1))
            {
               
                return linearEquation(fbgSensor.standard, listTemperature.get(i), listTemperature.get(i+1), fbgSignal.getPeakWavelength());
            }

            if (fbgSignal.getPeakWavelength() < listWavelength.get(0))
            {
            	return linearEquation(fbgSensor.standard, listTemperature.get(0), listTemperature.get(1), fbgSignal.getPeakWavelength()); 
            }

            if (fbgSignal.getPeakWavelength() > listWavelength.get(listWavelength.size()-1))
            { return linearEquation(fbgSensor.standard, listTemperature.get(listWavelength.size()-2), listTemperature.get(listWavelength.size()-1), fbgSignal.getPeakWavelength()); }
        }
        return 0;
	}
	
	public static double getTemperature(double targetY,double slope,double givenX, double givenY)
	{
		double targetX=(targetY-givenY)*1000.0/slope+givenX;
		return targetX;
	}
	
	


	


	


	
}
