package com.bs.bus;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class dayparse extends Configured implements Tool {

	public static enum Counter {
		DAY_PARSER_ERR
	}

	public static class BusMap extends Mapper<LongWritable, Text, Text, Text> {
		private Text mykey = new Text();// 星期几
		private Text myvalue = new Text();// 星期几


		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] array = value.toString().split(",");
			int day = 0;
			try {
				day = DayForWeek.dayForWeek(array[1]);
			} catch (Exception e) {
				context.getCounter(Counter.DAY_PARSER_ERR).increment(1L);
				e.printStackTrace();
			}
			mykey.set(String.valueOf(day)+","+array[0]+","+array[2]+","+array[1]);
			myvalue.set(array[3]);
			context.write(mykey, myvalue);

		};
	}


	@Override
	public int run(String[] args) throws Exception {
		// 1 conf
		Configuration conf = new Configuration();
		conf.set("mapred.textoutputformat.separator", ",");// key value分隔符
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
		 job.setNumReduceTasks(1);
		// 3.8 set sort comparator class
		// job.setSortComparatorClass(LongWritable.Comparator.class);
		// 3.9 set group comparator class
		// job.setGroupingComparatorClass(LongWritable.Comparator.class);
		// 3.10 set combiner class
		// job.setCombinerClass(null);
		// 3.11 set reducer class
//		job.setReducerClass(BusReduce.class);
		// 3.12 set output format

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
		args = new String[] {
				"hdfs://hadoop-master.dragon.org:9000/bs/output02/part-r-00000",
				// "hdfs://hadoop-00:9000/home910/liyuting/output/" };
				"hdfs://hadoop-master.dragon.org:9000/bs/output04/" };
		// run mapreduce
		int status = ToolRunner.run(new dayparse(), args);
		// 5 exit
		System.exit(status);
	}
}
