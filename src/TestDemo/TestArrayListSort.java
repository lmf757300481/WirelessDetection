package TestDemo;

import java.util.ArrayList;

public class TestArrayListSort {

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Double> list =new ArrayList<Double>();
		list.add(1.0);
		list.add(3.0);
		list.add(4.0);
		list.add(2.0);
		list.sort(null);
		
		for (int i = 0; i < list.size(); i++) {
			            System.out.println(list.get(i));
			        }
	}

}
