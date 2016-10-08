/**
 * 
 */
package com.mapreduce.monitor;

//import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 工具类
 * @author fansy
 * @date 2015-6-8
 */
public class Utils {

	// hadoop 常量
	
	//
	public static String baseServicePacakges="com.fz.service.*";
	
//	private static Map<String,String> HADOOPCONSTANTS=new HashMap<String,String>();
	private static ResourceBundle resb = null;
	private static PrintWriter  writer=null;
	
	public static final int GETDRAWPICRECORDS_EVERYFILE=500;// 画决策图时每个文件提取的记录数
	
	
	private static String[] userdata_attributes=new String[]{"Id","Reputation","CreationDate","DisplayName",
			"EmailHash","LastAccessDate","Location","Age","AboutMe","Views","UpVotes","DownVotes"};
	public static String[] useful_attributes=new String[]{"reputations","upVotes","downVotes","views"};
	private static String userdata_elementName="row";
	private static String userdata_xmlPath="ask_ubuntu_users.xml";
	
	private static int counter=0;// 在任务运行时返回递增的点字符串
	
	/**
	 * 初始化登录表数据
	 */
//	static{// 这种方式不行
//		dBService.insertLoginUser();
//		System.out.println(new java.util.Date()+"：初始化登录表完成！");
//	}
	
	
	
	/**
	 * 向PrintWriter中输入数据
	 * @param info
	 */
	

	/**
	 * @param flag
	 */

	/**
	 * 根据类名获得实体类
	 * @param tableName
	 * @param json
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings("unchecked")
	/**
	 *  获得表的实体类的全路径
	 * @param tableName
	 * @return
	 */
	public static String getEntityPackages(String tableName){
		return "com.fz.model."+tableName;
	}
	/**
	 * 获得根路径
	 * @return
	 */

	
	/**
	 * 获得根路径下面的subPath路径
	 * @param subPath
	 * @return
	 */
	/**
	 * 把传入的xml文件夹的.xml文件解析为字符串数组
	 * @param xmlFolder
	 * @return
	 */
	/**
	
	/**
	 * 把xml转为字符串数组
	 * @param xmlPath
	 * @return
	 */

	/**
	 * 获得递增点字符串
	 * @return
	 */
	public static String getDotState(String pre){
		counter++;
		StringBuffer buff =new StringBuffer();
		for(int i=0;i<counter;i++){
			buff.append(".");
		}
		if(counter>=7){
			counter=0;
		}
		return pre+buff.toString();
	}

	/**
	 * 获取一行中的某个属性的值
	 * @param line
	 *  <row Id="-1" Reputation="9" CreationDate="2010-07-28T16:38:27.683" 
	 *  DisplayName="Community" EmailHash="a007be5a61f6aa8f3e85ae2fc18dd66e" 
	 *  LastAccessDate="2010-07-28T16:38:27.683" Location="on the server farm" 
	 *  AboutMe="&lt;p&gt;Hi, I'm not really a person.&lt;/p&gt;&#xD;&#xA;&lt;p&gt;
	 *  I'm a background process that helps keep this site clean!&lt;/p&gt;&#xD;&#xA;&lt;p&gt;
	 *  I do things like&lt;/p&gt;&#xD;&#xA;&lt;ul&gt;&#xD;&#xA;&lt;li&gt;
	 *  Randomly poke old unanswered questions every hour so they get some attention&lt;
	 *  /li&gt;&#xD;&#xA;&lt;li&gt;Own community questions and answers so nobody gets
	 *   unnecessary reputation from them&lt;/li&gt;&#xD;&#xA;&lt;li&gt;
	 *  Own downvotes on spam/evil posts that get permanently deleted&#xD;
	 *  &#xA;&lt;/ul&gt;" Views="0" UpVotes="142" DownVotes="119" />
	 * @param attr
	 * @return
	 */
	public static String getAttrValInLine(String line,String attr) {
		String tmpAttr = " "+attr+"=\"";
		int start = line.indexOf(tmpAttr);
		if(start==-1){
			return null;
		}
		start+=tmpAttr.length();
		int end = line.indexOf("\"",start);
		return line.substring(start, end);
	}

	/**
	 * 定制解析
	 * @param datFolder
	 * @return
	 */
	public static List<String[]> parseDatFolder2StrArr(String datFolder) {
		File folder = new File(datFolder);
		List<String[]> list = new ArrayList<String[]>();
		File[] files=null;
		try{
			files= folder.listFiles();
			for(File f:files){
				list.addAll(parseDat2StrArr(f.getAbsolutePath()));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 定制解析单个dat文件
	 * @param datFile
	 * @return
	 * @throws IOException 
	 */
	private static List<String[]> parseDat2StrArr(String datFile) throws IOException{
		List<String[]> list = new ArrayList<String[]>();

		 FileReader reader = new FileReader(datFile);
         BufferedReader br = new BufferedReader(reader);
        
         String line = null;
         String[] arr= null;
         while((line = br.readLine()) != null) {
        	 arr=new String[userdata_attributes.length];
        	 for(int i=1;i<userdata_attributes.length;i++){
        		 arr[i]=Utils.getAttrValInLine(line, userdata_attributes[i]);
        	 }
        	 list.add(arr);
         }
        
         br.close();
         reader.close();
		return list;
	}
	/**
	 * 简单日志
	 * @param msg
	 */
	public static void simpleLog(String msg){
		System.out.println(new java.util.Date()+":"+msg);
	}

	/**
	 * 获取给定数组两两之间的距离(欧式距离:HUtils.getDistance() )，每个行为一个向量
	 * 并把这些距离按照大小从小到大排序
	 * @param vectors
	 * @return
	 */

	/**
	 * 获得input的数据，每行作为一个字符串，全部数据放入list中
	 * @param input
	 * @return
	 * @throws IOException 
	 */
	/**
	 * double 或者float或者int转为百分数
	 * @param dou
	 * @param dotNum
	 * @return
	 */
	public static String obejct2Percent(Object dou,int dotNum){
		double dd= (Double)dou;
		//百分数格式化
		NumberFormat fmt = NumberFormat.getPercentInstance();
		fmt.setMaximumFractionDigits(dotNum);//最多dotNum位百分小数
		return fmt.format(dd);
	}
	
}
