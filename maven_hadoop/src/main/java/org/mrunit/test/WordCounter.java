package org.mrunit.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class WordCounter {
	public static void main(String[] args) throws Exception {
//		args =new String[]{
//				"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/topkeyinput/",
//				"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/minDriveroutput2/"};
		//conf
		args =new String[]{
				"F:/data/input","F:/data/"
		};
		Configuration conf	=new Configuration();
		//create job
		Job job = new Job(conf,WordCounter.class.getSimpleName());
		//set job
		job.setJarByClass(WordCounter.class);
		//set input/output path
		job.setInputFormatClass(TextInputFormat.class);
		job.setMapperClass(WordCountMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setReducerClass(WordCountReduce.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		//submin job
		boolean isSuccess=job.waitForCompletion(true);
		//exit
		System.exit(isSuccess ? 0 : 1);
	}
}
