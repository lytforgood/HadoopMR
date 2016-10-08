package com.mapreduce.monitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobStatus;
import org.mortbay.util.ajax.JSON;


public class CloudAction {
	public static void main(String[] args) {
		 try {
			monitor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void monitor() throws IOException{
    	Map<String ,Object> jsonMap = new HashMap<String,Object>();
    	List<CurrentJobInfo> currJobList =null;
    	try{
    		Configuration conf = new Configuration();  
    		conf.addResource("classpath:core-site.xml");
            conf.addResource("classpath:hdfs-site.xml");
            conf.addResource("classpath:mapred-site.xml");
		
    		JobClient jobClient =new JobClient(new JobConf(conf)); 
    			
    		JobStatus[] jss = jobClient.getAllJobs();
    		List<CurrentJobInfo> jsList = new ArrayList<CurrentJobInfo>();
    		jsList.clear();
    		for (JobStatus js : jss) {
    			if (js.getStartTime() < System.currentTimeMillis()) {
    				jsList.add(new CurrentJobInfo(jobClient.getJob(
    						js.getJobID()), js.getStartTime(), js.getRunState()));
    			}
    		}
    		Collections.sort(jsList);
//    		CurrentJobInfo cInfo=new CurrentJobInfo();//获取最新的JOb
//    		cInfo=jsList.get(jsList.size()-1);
//    		jsList.clear();
//    		jsList.add(cInfo);
//			System.out.println(jsList.get(0).getJobId());
    		currJobList= jsList;
    		
			
    		jsonMap.put("rows", currJobList);// 放入数据
    		jsonMap.put("total", 1);
    		// 任务完成的标识是获取的任务个数必须等于jobNum，同时最后一个job完成
    		// true 所有任务完成
    		// false 任务正在运行
    		// error 某一个任务运行失败，则不再监控
    		if(currJobList.size()==0){
    			jsonMap.put("finished", "false");
//    			return ;
    		}
    		if(currJobList.size()>=1){// 如果返回的list有JOBNUM个，那么才可能完成任务
    			if("success".equals(hasFinished(currJobList.get(currJobList.size()-1)))){
    				jsonMap.put("finished", "true");
    				// 运行完成，初始化时间点
    				System.currentTimeMillis();
    			}else if("running".equals(hasFinished(currJobList.get(currJobList.size()-1)))){
    				jsonMap.put("finished", "false");
    			}else{// fail 或者kill则设置为error
    				jsonMap.put("finished", "error");
    				System.currentTimeMillis();
    			}
    		}else if(currJobList.size()>0){
    			if("fail".equals(hasFinished(currJobList.get(currJobList.size()-1)))||
    					"kill".equals(hasFinished(currJobList.get(currJobList.size()-1)))){
    				jsonMap.put("finished", "error");
    				System.currentTimeMillis();
    			}else{
    				jsonMap.put("finished", "false");
    			}
        	}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    		jsonMap.put("finished", "error");
    		System.currentTimeMillis();
    	}
    	List<CurrentJobInfo> list = (List<CurrentJobInfo>) jsonMap.get("rows");
		if (list.size() == 1) {
			return;
		}
		for (int i = list.size(); i < 1; i++) {
			list.add(new CurrentJobInfo());
		}
    // 如果jsonMap.get("rows").size()!=HUtils.JOBNUM,则添加
    	System.out.println(new java.util.Date()+":"+JSON.toString(jsonMap));
    	
    	
    	return ;
    }
	public static String hasFinished(CurrentJobInfo currentJobInfo) {

		if (currentJobInfo != null) {
			if ("SUCCEEDED".equals(currentJobInfo.getRunState())) {
				return "success";
			}
			if ("FAILED".equals(currentJobInfo.getRunState())) {
				return "fail";
			}
			if ("KILLED".equals(currentJobInfo.getRunState())) {
				return "kill";
			}
		}

		return "running";
	}
}
