package com.rlxw.count;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class BusCount4 extends Configured implements Tool {

	public static enum Counter {
		PARSER_ERR
	}

	public static class BusMap extends
			Mapper<LongWritable, Text, Text, Text> {
		private Text mykey = new Text();// 线路名称+交易时间+刷卡终端ID
		private Text myval = new Text();// 线路名称+交易时间+刷卡终端ID
		private final static IntWritable one = new IntWritable(1);

		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] ss=value.toString().split(" ");
//			int sum=0;
//			for (int i = 1; i < ss.length; i++) {
//				sum+=Integer.valueOf(ss[i].substring(12, ss[i].length()));
//			}
			if (ss.length<3) {
				mykey.set(ss[0]);
//				mykey.set(bus.getLine_name() +","+ bus.getDeal_time()+","
//						+ bus.getTerminal_id());
				myval.set(ss[1]);
				context.write(mykey, myval);
			}
				
		};
	}

	public static class BusReduce extends
			Reducer<Text, Text, Text, Text> {
		private Text val = new Text();
		protected void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			// 用于累加
			int sum = 0;
			// 循环遍历 Interable
			for (Text value : values) {
				// 累加
				sum += Integer.parseInt(value.toString());
				
			}
			val.set(String.valueOf(sum));
			context.write(key, val);
		};
	}

	@Override
	public int run(String[] args) throws Exception {
		// 1 conf
		Configuration conf = new Configuration();
//		conf.set("mapred.textoutputformat.separator", ",");//key value分隔符
		// 2 create job
		// Job job = new Job(conf, ModuleMapReduce.class.getSimpleName());
		Job job = this.parseInputAndOutput(this, conf, args);
		// 3 set job
		// 3.1 set run jar class
		// job.setJarByClass(ModuleReducer.class);
		// 3.2 set intputformat
		job.setInputFormatClass(TextInputFormat.class);
		// 3.3 set input path
		// FileInputFormat.addInputPath(job, new Path(args[0]));
		// 3.4 set mapper
		job.setMapperClass(BusMap.class);
		// 3.5 set map output key/value class
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		// 3.6 set partitioner class
		// job.setPartitionerClass(HashPartitioner.class);
		// 3.7 set reduce number
//		 job.setNumReduceTasks(0);
		// 3.8 set sort comparator class
		// job.setSortComparatorClass(LongWritable.Comparator.class);
		// 3.9 set group comparator class
		// job.setGroupingComparatorClass(LongWritable.Comparator.class);
		// 3.10 set combiner class
		// job.setCombinerClass(null);
		// 3.11 set reducer class
		job.setReducerClass(BusReduce.class);
		// 3.12 set output format
		
		job.setOutputFormatClass(TextOutputFormat.class);
		// 3.13 job output key/value class
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		// 3.14 set job output path
		// FileOutputFormat.setOutputPath(job, new Path(args[1]));
		// 4 submit job
		boolean isSuccess = job.waitForCompletion(true);
		// 5 exit
		// System.exit(isSuccess ? 0 : 1);
		return isSuccess ? 0 : 1;
	}

	public Job parseInputAndOutput(Tool tool, Configuration conf, String[] args)
			throws Exception {
		// validate
		if (args.length != 2) {
			System.err.printf("Usage:%s [genneric options]<input><output>\n",
					tool.getClass().getSimpleName());
			ToolRunner.printGenericCommandUsage(System.err);
			return null;
		}
		// 2 create job
		Job job = new Job(conf, tool.getClass().getSimpleName());
		// 3.1 set run jar class
		job.setJarByClass(tool.getClass());
		// 3.3 set input path
		FileInputFormat.addInputPath(job, new Path(args[0]));
		// 3.14 set job output path
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		return job;
	}

	public static void main(String[] args) throws Exception {

//		String[] ssStrings={"nucmail"};
//		for (String string : ssStrings) {
//			args = new String[] {
//					"hdfs://hadoop-master.dragon.org:9000/bs/input/"+string+".txt",
//					// "hdfs://hadoop-00:9000/home910/liyuting/output/" };
//					"hdfs://hadoop-master.dragon.org:9000/bs/out1/"+string };
//			// run mapreduce
//			int status = ToolRunner.run(new BusCount4(), args);
//		}
		// 5 exit
		args = new String[] {
				"hdfs://hadoop-master.dragon.org:9000/bs/input/dw.txt",
				// "hdfs://hadoop-00:9000/home910/liyuting/output/" };
				"hdfs://hadoop-master.dragon.org:9000/bs/out1/xx"};
		// run mapreduce
		int status = ToolRunner.run(new BusCount4(), args);
		System.exit(status);
	}
}
