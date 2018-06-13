package realtimecharts_java;

import java.io.IOException;
import java.net.SocketException;
import java.util.Random;

import OMP.Command_OPM102L;
import OMP.Demodulator;
import OMP.UdpCommuncator;
import Wireless.UShapeSequence;

///////////////////////////////////////////////////////////////////////////////////////////////////
//Copyright 2018 Advanced Software Engineering Limited
//
//You may use and modify the code in this file in your application, provided the code and
//its modifications are used only in conjunction with ChartDirector. Usage of this software
//is subjected to the terms and condition of the ChartDirector license.
///////////////////////////////////////////////////////////////////////////////////////////////////   

public class RandomWave implements Runnable
{  
	// The callback function to handle the generated data
	public interface DataHandler {
		void onData(double elapsedTime, double series0, double series1);
	}
	private DataHandler handler;

	// Random number genreator thread
	Thread pingThread;
	private boolean stopThread;

	// The period of the data series in milliseconds. This random series implementation just use the 
	// windows timer for timing. In many computers, the default windows timer resolution is 1/64 sec,
	// or 15.6ms. This means the interval may not be exactly accurate.
	final int interval = 100;
	
	public RandomWave(DataHandler handler)
    {
        this.handler = handler;
    }

    //
    // Start the random generator thread
    //        
    public void start()
    {
		if (null != pingThread)
			return;

		pingThread = new Thread(this);
		pingThread.start();   
    }

    //
    // Stop the random generator thread
    //
    public void stop()
    {
		stopThread = true;
		if (null != pingThread)
			try { pingThread.join(); } catch (InterruptedException e) {} 
		pingThread = null;
		stopThread = false;
    }

    //
	// The random generator thread
	//
	public void run()
    {
        long currentTime = 0;
        long nextTime = 0;

		// Variable to keep track of the timing
		long timer = System.nanoTime();
		Command_OPM102L command=Command_OPM102L.Start_Control_Get_Peak_WaveLength;
		UdpCommuncator udp = null;
		try {
			udp = new UdpCommuncator();
			udp.Initializate(command);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        byte[] data=new byte[1024]; 
		
		
		double WL = 0;
		double temperature=20;
		
        while (!stopThread)
        {
            
        	
        	//
			try {
				data=udp.recieve();
				WL=udp.getWaveLength(data);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//temperature = Demodulator.getTemperature(WL, 10.2, 30.6, 1543.331);
			//temperature = Demodulator.getTemperature(WL, 10.6, 40, 1534.214); //传感器FBG-1534
			temperature = Demodulator.getTemperature(WL, 10.7, 40, 1536.308); //传感器FBG-1536
			//temperature = Demodulator.getTemperature(WL, 10.6, 40, 1538.284); //传感器FBG-1538
        	// Compute the next data value
            currentTime = (System.nanoTime() - timer) / 1000000;

            double p = currentTime / 1000.0 * 4;
         //   double series0 = 20 + Math.cos(p * 2.2) * 10 + 1 / (Math.cos(p) * Math.cos(p) + 0.01);
            double series0 = temperature;
          //  double series1 = 210 + 60 * Math.sin(p / 21.7) * Math.sin(p / 7.8);
            double series1 = temperature;
            // Call the handler
            handler.onData(currentTime / 1000.0, series0, series1);

            // Sleep until next walk
            if ((nextTime += interval) <= currentTime)
				nextTime = currentTime + interval;
			
			try { Thread.sleep((int)(nextTime - currentTime)); }
			catch (InterruptedException e) {};
        }
    }
}
