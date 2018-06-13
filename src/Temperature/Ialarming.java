package Temperature;

public interface Ialarming {
	
	boolean isExceedThreshold(double  temperatureThreshhold);//ÊÇ·ñ³¬ÓÚãĞÖµ
	boolean isRisingTooFast(double speedThreshhold);
	

}
