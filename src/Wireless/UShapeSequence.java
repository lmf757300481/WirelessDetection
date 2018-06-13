package Wireless;

import java.awt.Toolkit;
import java.util.ArrayList;

public class UShapeSequence {

	int length; // length of UShapeSequence U�����еĳ���
	ArrayList<Double> wlSequence = null;// new ArrayList<Double>(); // ���ڴ洢�����еĲ���
	ArrayList<Double> lastWlSequence = null;
	boolean isComing; // ��ͷ�Ƿ�����
	boolean lastIsComing; // ���ֵ�״̬��ͷ�Ƿ�����

	double targetWl; // Ŀ�����Ĳ���
	public boolean isUpdatedWl; // ���ֵ�״̬��ͷ�Ƿ�����

	public UShapeSequence() { // ���ھ������������
		super();
		this.length = 0;
		this.wlSequence = new ArrayList<Double>();
		this.lastWlSequence = new ArrayList<Double>();
		this.isComing = false;
		this.lastIsComing = false;
		this.targetWl = 0.0;
		this.isUpdatedWl = false;
	}

	public UShapeSequence(int length, ArrayList<Double> wlSequence,ArrayList<Double> lastWlSequence, boolean isComing, boolean lastIsComing,
			double targetWl, boolean isUpdatedWl) {
		super();
		this.length = length;
		this.wlSequence = wlSequence;
		this.lastWlSequence=lastWlSequence;
		this.isComing = isComing;
		this.lastIsComing = lastIsComing;
		this.targetWl = targetWl;
		this.isUpdatedWl = isUpdatedWl;

	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isComing() {
		return isComing;
	}

	public void setComing(double recievedWl) {
		this.lastIsComing = this.isComing;
		this.isComing = checkIsComing(recievedWl);
	}

	public double getTargetWl() {
		return targetWl;
	}

	public void setTargetWl(double targetWl) {
		this.targetWl = targetWl;
	}

	
	public ArrayList<Double> getLastWlSequence() {
		return lastWlSequence;
	}

	public void setLastWlSequence(ArrayList<Double> lastWlSequence) {
		this.lastWlSequence = lastWlSequence;
	}

	public void add(double recievedWl) {
		this.setComing(recievedWl); // ÿ����һ����������� lastIsComing&&isComing ��������

		// �˴��������
		if ((!lastIsComing) && (!isComing)) { //ȫ������0
			wlSequence.clear();
			length = 0;
			isUpdatedWl = false;
		} else if ((!lastIsComing) && (isComing)) { //����U�͵ĵ�һ����
			wlSequence.add(recievedWl);
			length++;
			isUpdatedWl = false;
		} else if ((lastIsComing) && (isComing)) { //����U�ͺ󣬼�����ӣ�ֻ�����һ����ǰ
			wlSequence.add(recievedWl);
			length++;
			isUpdatedWl = false;

		} else if ((lastIsComing) && (!isComing)) {  //����U�ͺ����һ����
			// System.out.println("U�͵ĳ���"+length);
			if (length == 0) {
			//	System.out.println("U�͵ĳ���" + 0);
				wlSequence.clear();
				length = 0;
				isUpdatedWl = true;
			} else if (length == 1) {
			//	System.out.println("U�͵ĳ���" + 1);
				targetWl = wlSequence.get(0);
				lastWlSequence.clear();
				lastWlSequence.addAll(wlSequence);
				wlSequence.clear();
				length = 0;
				isUpdatedWl = true;
				Toolkit.getDefaultToolkit().beep();
			} else {
			//	System.out.println("U�͵ĳ���" + length);
				targetWl = wlSequence.get(length / (int) 2);// ,Ŀǰ��ȡ�м��ֵ���򵥵�
				lastWlSequence.clear();
				lastWlSequence.addAll(wlSequence);
				wlSequence.clear();
				length = 0;
				isUpdatedWl = true;
				Toolkit.getDefaultToolkit().beep();
			}

		}

	}

	public static boolean checkIsComing(double recievedWl) { // Ŀǰ�򵥵���Ϊ�в���������������������뿪
		if (recievedWl == 0) {
			return false;
		} else if (recievedWl >= 1535 && recievedWl <= 1565) {
			// System.out.println("�ε�һ��");
			// Toolkit.getDefaultToolkit().beep();
			return true;

		} else {
			return false;
		}
	}

}
