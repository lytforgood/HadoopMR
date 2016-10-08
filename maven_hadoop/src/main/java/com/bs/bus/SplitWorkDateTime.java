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

public class SplitWorkDateTime extends Configured implements Tool {

	public static enum Counter {
		MU_ERR
	}

	public static class BusMap extends Mapper<LongWritable, Text, Text, Text> {
		private Text mykey = new Text();//
		private Text myvalue = new Text();//

		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] array = value.toString().split(",");
			String keyString = array[0] + "," + array[2] + "," + array[4] + ","
					+ array[7];
			String valueString = array[1] + "," + array[3] + "," + array[5]
					+ "," + array[6];
			mykey.set(keyString);
			myvalue.set(valueString);
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
			String[] array = key.toString().split(",");
			String times="06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21";
			String[] array1 = times.toString().split(",");
			for (String time : array1) {
				String keyString = "线路10," + time + ",多云/多云,1";
				String keyString2 = "线路15," + time + ",多云/多云,1";
				if (array[0].equals("线路10") && key.toString().equals(keyString)) {
					for (Text t : values)
						mos.write("10x"+time, key, t);
				}
				if (array[0].equals("线路15") && key.toString().equals(keyString2)) {
					for (Text t : values)
						mos.write("15x"+time, key, t);
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
		Configuration conf = new Configuration();
		conf.set("mapred.textoutputformat.separator", ",");// key value分隔符
		Job job = this.parseInputAndOutput(this, conf, args);
		job.setInputFormatClass(TextInputFormat.class);
		job.setMapperClass(BusMap.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setReducerClass(BusReduce.class);
		MultipleOutputs.addNamedOutput(job, "10x06", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x07", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x08", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x09", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x10", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x11", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x12", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x13", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x14", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x15", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x16", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x17", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x18", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x19", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x20", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "10x21", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x06", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x07", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x08", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x09", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x10", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x11", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x12", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x13", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x14", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x15", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x16", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x17", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x18", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x19", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x20", TextOutputFormat.class,
				Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "15x21", TextOutputFormat.class,
				Text.class, Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		boolean isSuccess = job.waitForCompletion(true);
		return isSuccess ? 0 : 1;
	}

	public Job parseInputAndOutput(Tool tool, Configuration conf, String[] args)
			throws Exception {
		if (args.length != 2) {
			System.err.printf("Usage:%s [genneric options]<input><output>\n",
					tool.getClass().getSimpleName());
			ToolRunner.printGenericCommandUsage(System.err);
			return null;
		}
		Job job = new Job(conf, tool.getClass().getSimpleName());
		job.setJarByClass(tool.getClass());
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		return job;
	}

	public static void main(String[] args) throws Exception {
		args = new String[] {
				"hdfs://hadoop-master.dragon.org:9000/bs/input/dd2.txt",
				"hdfs://hadoop-master.dragon.org:9000/bs/can/dd_time_1/" };
		int status = ToolRunner.run(new SplitWorkDateTime(), args);
		System.exit(status);
	}
}
