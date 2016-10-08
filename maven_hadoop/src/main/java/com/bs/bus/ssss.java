package com.bs.bus;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ssss {

	
	public static void main(String[] args) {
//		for (int i = 1; i <= 7; i++) {
//			System.out.println(i);
//		}
		String a="2015/9/09";
		String time=DayForWeek.getyymmhh(a);
		System.out.println(time);
//		int day=0;
//		try {
//			day=dayForWeek(a);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(day);
//		System.out.println(a.substring(a.length()-2, a.length()));
//		System.out.println(a.substring(0, a.length()-2));
//		System.out.println(a.length());
//		int time=Integer.parseInt(a.substring(a.length()-2, a.length()));
//		if (time<6||time>21) {
//			System.out.println("flase");
//		}else {
//			System.out.println("yes");
//		}
	}
	public  static  int  dayForWeek(String pTime) throws  Exception {  
		SimpleDateFormat format = new  SimpleDateFormat("yyyyMMdd" );  
		 Calendar c = Calendar.getInstance();  
		 c.setTime(format.parse(pTime));  
		 int  dayForWeek = 0 ;  
		 if (c.get(Calendar.DAY_OF_WEEK) == 1 ){  
		  dayForWeek = 7 ;  
		 }else {  
		  dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1 ;  
		 }  
		 return  dayForWeek;  
		}  
}
