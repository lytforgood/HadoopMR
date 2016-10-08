package foo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sss {
	
	public static void main(String[] args) {
		String ssString=" 210.44.128.153	01/01/2014--3	02/01/2014--1	";
//		String[] ss=ssString.split("\t");
		
//		String pattern = "--";
//		Pattern r = Pattern.compile(pattern);
//        if (ss.length==3) {
//			
//		
//		for (int i = 1; i < ss.length; i++) {
//			
//			String[] ssss=ss[i].split("--");
//			for (String string : ssss) {
//				System.out.println(string);
//			}
//		}
		StringBuffer sb=new StringBuffer();
		String[] ss = ssString.split("\t");
		// int sum=0;
		if (ss.length==3) {
		for (int i = 1; i < ss.length; i++) {
			
			String[] ssss=ss[i].split("--");
			String aString=ssss[0]+"\t"+ssss[1];
			sb.append("\t"+aString);
		}
		System.out.println(sb.toString().substring(1,sb.toString().length()));
		System.out.println("");
////			Matcher m = r.matcher(ss[i]);
//			if (m.find()) {
//				System.out.println(ss[i].substring(m.end() - 11, m.end()));
//				System.out.println(ss[i].substring(m.end() + 1,ss[i].length()));
//			}
		}
		
//		int sum=0;
//		for (int i = 1; i < ss.length; i++) {
//			sum+=Integer.valueOf(ss[i].substring(12, ss[i].length()));
//		}
//		System.out.println(ss[1].substring(0,12)+"次数"+ new Long(ss.length-1)+"次数"+sum);
	
}
}
