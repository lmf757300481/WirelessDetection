package FBG;

import java.io.IOException;
import java.util.HashMap;
import com.csvreader.CsvReader;

public class FBGSensor {
	
	

	public HashMap<Double,Double> standard=new HashMap<Double,Double>();
	
	
	public FBGSensor(String fileName) {
		
		
		super();
		// TODO Auto-generated constructor stub
		this.read(fileName);
		
	}
	
	
	public FBGSensor() {
		super();
		// TODO Auto-generated constructor stub
		read();
	}
	
	public  void read(String fileName){

        String filePath =fileName ;//"标定.csv";//fileName ;//"XXX.csv";
        if(filePath==null  || filePath.length()==0) filePath="标定.csv";

        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath);

            // 读表头
            csvReader.readHeaders();
            while (csvReader.readRecord()){
                // 读一整行
                System.out.println(csvReader.getRawRecord());
               String line= csvReader.getRawRecord();
               if(line!=null&&line.length()!=0)
               {
            	   String lineStrArray []=line.split(",");  
            	   Double temperaturePoint = Double.parseDouble(lineStrArray[0]);
            	   Double wavelengthPoint = Double.parseDouble(lineStrArray[1]);
            	   this.standard.put(temperaturePoint,wavelengthPoint);
               }
                // 读这行的某一列
               // System.out.println(csvReader.get("Temperature"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
        
        public  void read(){

            String filePath = "标定.csv";//fileName ;//"XXX.csv";
            if(filePath==null  || filePath.length()==0) filePath="标定.csv";

            try {
                // 创建CSV读对象
                CsvReader csvReader = new CsvReader(filePath);

                // 读表头
                csvReader.readHeaders();
                while (csvReader.readRecord()){
                    // 读一整行
                //    System.out.println(csvReader.getRawRecord());
                   String line= csvReader.getRawRecord();
                   if(line!=null&&line.length()!=0)
                   {
                	   String lineStrArray []=line.split(",");  
                	   Double temperaturePoint = Double.parseDouble(lineStrArray[0]);
                	   Double wavelengthPoint = Double.parseDouble(lineStrArray[1]);
                	   this.standard.put(temperaturePoint,wavelengthPoint);
                   }
                    // 读这行的某一列
                   // System.out.println(csvReader.get("Temperature"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
         
    }

}
