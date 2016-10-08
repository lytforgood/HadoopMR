package com.mapreduce.monitor;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobStatus;

public class JobMonitor {
	public static void main(String[] args) {
		Configuration conf = new Configuration();   
        JobClient client;   
        try {   
            conf.addResource("classpath:core-site.xml");
            conf.addResource("classpath:hdfs-site.xml");
            conf.addResource("classpath:mapred-site.xml");
            client = new JobClient(new JobConf(conf)); 
            
            JobStatus[] s=client.getAllJobs();
            for (JobStatus jobStatus : s) {
            	 System.out.println("任务ID"+jobStatus.getJobID());
                System.out.println("map进程"+jobStatus.mapProgress());
                System.out.println("reduce进程"+jobStatus.reduceProgress());
                System.out.println("是否完成"+jobStatus.isJobComplete());
               

			}
//            System.out.println(client.getQueues()[0].getQueueName());   
//            System.out.println(client.getJobsFromQueue(client.getQueues()[0].getQueueName()).length);   
        } catch (IOException e) {   
            throw new RuntimeException(e);   
        }  
	}

}
