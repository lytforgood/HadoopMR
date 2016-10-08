package com.bs.bus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DayForWeek {
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
	@SuppressWarnings("deprecation")
	public  static  int  dayForWeek2(String pTime) throws  Throwable {  
	      
	    SimpleDateFormat format = new  SimpleDateFormat("yyyy-MM-dd" );  
	      
	    Date tmpDate = format.parse(pTime);  
	      
	    Calendar cal = new  GregorianCalendar();  
	      
	    cal.set(tmpDate.getYear(), tmpDate.getMonth(), tmpDate.getDay());  
//	    cal.setTime(tmpDate);  
	    return  cal.get(Calendar.DAY_OF_WEEK);  
	}  
	public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }
	public static String getyymmhh(String day) {
		SimpleDateFormat format = new  SimpleDateFormat("yyyy/MM/dd" );  
		Date tmpDate = null;
		try {
			tmpDate = format.parse(day);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String Time = sdf.format(tmpDate); 
        return Time;
    }
	/**
	* 获取当前日期是星期几
	*
	* @param dt
	* @return 当前日期是星期几
	*/
	public static String getWeekOfDate2(Date dt) {
	String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	Calendar cal = Calendar.getInstance();
	cal.setTime(dt);

	int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
	if (w < 0)
	w = 0;

	return weekDays[w];
	}
	public static void main(String[] args) {
		Date date=new Date();
		//今天是几号
		@SuppressWarnings("deprecation")
		int day=date.getDay();
		System.out.println("Today is :"+day+"号");
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		//今天是这个星期的第几天
		int week=c.get(Calendar.DAY_OF_WEEK);
		System.out.println("week:"+week);
		//当前月的最后一天是几号
		int lastday=c.getActualMaximum(Calendar.DAY_OF_MONTH);
		System.out.println("这个月最后一天是:"+lastday+"号"); 
	}
	
}
