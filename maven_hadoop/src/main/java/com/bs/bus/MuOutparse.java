package com.bs.bus;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MuOutparse extends Configured implements Tool {

	public static enum Counter {
		MU_ERR
	}

	public static class BusMap extends Mapper<LongWritable, Text, Text, Text> {
		private Text mykey = new Text();// 星期几
		private Text myvalue = new Text();// 星期几

		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] array = value.toString().split(",");
			mykey.set(array[0]);
			myvalue.set(array[1] + "," + array[2] + "," + array[3] + ","
					+ array[4]);
			context.write(mykey, myvalue);
		};
	}

	public static class BusReduce extends Reducer<Text, Text, Text, Text> {
		private MultipleOutputs<Text, Text> mos;

		protected void setup(Context context) throws IOException,
				InterruptedException {
			mos = new MultipleOutputs<Text, Text>(context);

		}

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			// 方法一，数组转换成list调用contains函数判断是否存在
			// String stringArray="1,2,3,4,5,6,7";
			// String[] array = stringArray.toString().split(",");
			// List<String> tempList = Arrays.asList(array);
			// if(tempList.contains(key.toString()))
			// {
			// for (Text t : values)
			// mos.write(key.toString(), key, t);
			// } else {
			// context.getCounter(Counter.MU_ERR).increment(1L);
			// }
			// 方法二，数组遍历判断是否存在
			for (int i = 1; i <= 7; i++) {
				if (String.valueOf(i).equals(key.toString())) {
					for (Text t : values)
						mos.write(key.toString(), key, t);
				}
			}
		};

		protected void cleanup(Context context) throws IOException,
				InterruptedException {

			mos.close();

		}
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
		// job.setNumReduceTasks(1);
		// 3.8 set sort comparator class
		// job.setSortComparatorClass(LongWritable.Comparator.class);
		// 3.9 set group comparator class
		// job.setGroupingComparatorClass(LongWritable.Comparator.class);
		// 3.10 set combiner class
		// job.setCombinerClass(null);
		// 3.11 set reducer class
		job.setReducerClass(BusReduce.class);
		// 3.12 set output format
		MultipleOutputs.addNamedOutput(job, "1", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "2", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "3", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "4", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "5", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "6", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "7", TextOutputFormat.class,
				Text.class, Text.class);
		// job.setOutputFormatClass(TextOutputFormat.class);
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
				"hdfs://hadoop-master.dragon.org:9000/bs/output04/part-r-00000",
				// "hdfs://hadoop-00:9000/home910/liyuting/output/" };
				"hdfs://hadoop-master.dragon.org:9000/bs/output06/" };
		// run mapreduce
		int status = ToolRunner.run(new MuOutparse(), args);
		// 5 exit
		System.exit(status);
	}
}
