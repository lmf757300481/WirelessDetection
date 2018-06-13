package Wireless;

import java.awt.Toolkit;
import java.util.ArrayList;

public class UShapeSequence {

	int length; // length of UShapeSequence U型序列的长度
	ArrayList<Double> wlSequence = null;// new ArrayList<Double>(); // 用于存储序列中的波长
	ArrayList<Double> lastWlSequence = null;
	boolean isComing; // 镜头是否来到
	boolean lastIsComing; // 上轮的状态镜头是否来到

	double targetWl; // 目标中心波长
	public boolean isUpdatedWl; // 上轮的状态镜头是否来到

	public UShapeSequence() { // 现在就用这个构造器
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
		this.setComing(recievedWl); // 每操作一次这个函数， lastIsComing&&isComing 都被更新

		// 此处四种情况
		if ((!lastIsComing) && (!isComing)) { //全部都是0
			wlSequence.clear();
			length = 0;
			isUpdatedWl = false;
		} else if ((!lastIsComing) && (isComing)) { //进入U型的第一个点
			wlSequence.add(recievedWl);
			length++;
			isUpdatedWl = false;
		} else if ((lastIsComing) && (isComing)) { //进入U型后，继续添加，只到最后一个点前
			wlSequence.add(recievedWl);
			length++;
			isUpdatedWl = false;

		} else if ((lastIsComing) && (!isComing)) {  //进入U型后，最后一个点
			// System.out.println("U型的长度"+length);
			if (length == 0) {
			//	System.out.println("U型的长度" + 0);
				wlSequence.clear();
				length = 0;
				isUpdatedWl = true;
			} else if (length == 1) {
			//	System.out.println("U型的长度" + 1);
				targetWl = wlSequence.get(0);
				lastWlSequence.clear();
				lastWlSequence.addAll(wlSequence);
				wlSequence.clear();
				length = 0;
				isUpdatedWl = true;
				Toolkit.getDefaultToolkit().beep();
			} else {
			//	System.out.println("U型的长度" + length);
				targetWl = wlSequence.get(length / (int) 2);// ,目前先取中间的值，简单的
				lastWlSequence.clear();
				lastWlSequence.addAll(wlSequence);
				wlSequence.clear();
				length = 0;
				isUpdatedWl = true;
				Toolkit.getDefaultToolkit().beep();
			}

		}

	}

	public static boolean checkIsComing(double recievedWl) { // 目前简单的认为有波长就是来到，否则就是离开
		if (recievedWl == 0) {
			return false;
		} else if (recievedWl >= 1535 && recievedWl <= 1565) {
			// System.out.println("滴滴一下");
			// Toolkit.getDefaultToolkit().beep();
			return true;

		} else {
			return false;
		}
	}

}
