package TestDemo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.csvreader.CsvReader;

import OMP.Demodulator;
import Wireless.UShapeSequence;

public class TestDemoForUShapeSequenceFromCsv {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String srcCSV="H:\\�ĵ�\\Eclipse_WorkSpace\\WirelessDetection\\src\\Wireless\\2.csv";
				
		CsvReader reader=new CsvReader(srcCSV, ',',Charset.forName("UTF-8"));
		reader.readHeaders();  
		ArrayList<String> csvFileList=new ArrayList<String>();
        // ���ж������ͷ������  
        while (reader.readRecord()) {  
           // System.out.println(reader.getRawRecord());   
            csvFileList.add(reader.getRawRecord());   
        }  
        reader.close();  
          UShapeSequence uss=new UShapeSequence();
        // ������ȡ��CSV�ļ�  
          DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS");  
        for (int row = 0; row < csvFileList.size(); row++) {  
            // ȡ�õ�row�е�0�е�����  
            String cell = csvFileList.get(row);  
        
            String[] contents=cell.split(",");
            double WL=Double.parseDouble(contents[1]);
            uss.add(WL);
            if (uss.isUpdatedWl) {
            	System.out.println(sdf2.format(System.currentTimeMillis()));
            	
				double temperature;
				temperature=Demodulator.getTemperature(WL, 10.0, 20, 1538.742);
            	System.out.println("�¶���"+temperature);
            	System.out.println();
			}
            
        }  
		

	}

}
