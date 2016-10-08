package com.bs.bus;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 
 * 获取某段时间之间所有时间 \n是换行，英文是New line，表示使光标到行首 \r是回车，英文是Carriage return，表示使光标下移一格
 * \r\n表示回车换行
 *
 * @author Administrator
 * 
 */
public class FindDates {

	public static void main(String[] args) throws Exception {
		Calendar cal = Calendar.getInstance();
		String start = "20150101";
		String end = "20150107";// 2014-02-05
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// yyyy-MM-dd
		Date dBegin = sdf.parse(start);
		Date dEnd = sdf.parse(end);
		List<Date> lDate = findDates(dBegin, dEnd);
		// File file = new File("E:\\bs\\time.txt");
		// PrintStream ps = new PrintStream(new FileOutputStream(file));
		FileWriter writer = new FileWriter("E:\\bs\\time3.txt");
		;
		for (Date date : lDate) {
			for (int i = 6; i <= 21; i++) {
				// System.out.println(sdf.format(date) + "-"
				// + String.format("%02d", i));
				writer.write("线路10"+","+sdf.format(date) + ","+String.format("%02d", i));
				writer.write("\r\n");
				// ps.append(sdf.format(date) + String.format("%02d", i) +
				// "\r\n");// 在已有的基础上添加字符串
			}
			for (int i = 6; i <= 21; i++) {
				// System.out.println(sdf.format(date) + "-"
				// + String.format("%02d", i));
				writer.write("线路15"+","+sdf.format(date) + ","+String.format("%02d", i));
				writer.write("\r\n");
				// ps.append(sdf.format(date) + String.format("%02d", i) +
				// "\r\n");// 在已有的基础上添加字符串
			}
		}
		writer.flush();
		writer.close();
	}

	public static List<Date> findDates(Date dBegin, Date dEnd) {
		List lDate = new ArrayList();
		lDate.add(dBegin);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())) {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			lDate.add(calBegin.getTime());
		}
		return lDate;
	}
}
