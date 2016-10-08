package com.advanced.compression;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * 对最终输出的文件进行压缩 FileOutputFormat.setCompressOutput(job, true);
 * FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
 */
public class Compress {
	// Mapper区域
	/**
	 * KEYIN, VALUEIN, KEYOUT, VALUEOUT; 输入KEY类型, 输入VALUE类型 ,输出key类型,输出value类型
	 * WordCount程序map类
	 * 
	 */
	static class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			Counter counter = context.getCounter("MAP_REDUCE_COUNTER",
					"MAP_INPUT_KEYVALUE");
			counter.increment(1L);
			// 获取每行数据的之
			String lineValue = value.toString();
			// 进行分割 \t\n\r\f 空格,制表符\t,换行\n,回车符\r,换页\f
			StringTokenizer stringTokenizer = new StringTokenizer(lineValue);
			// 遍历
			while (stringTokenizer.hasMoreElements()) {
				// 获取每个值
				String wordValue = stringTokenizer.nextToken();
				// 设置map输出的key值
				word.set(wordValue);
				// 上下文输出map的key和value
				context.write(word, one);
			}
		}
	}

	// Reduce 区域
	/**
	 * KEYIN, VALUEIN, KEYOUT, VALUEOUT; WordCount程序reduce类
	 * 
	 */
	static class MyReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {

		private IntWritable result = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {

			context.getCounter("MAP_REDUCE_COUNTER", "REDUCE_INPUT_KEYVALUE")
					.increment(1L);

			// 用于累加
			int sum = 0;
			// 循环遍历 Interable
			for (IntWritable value : values) {
				// 累加
				sum += value.get();
			}
			// 设置总次数
			result.set(sum);

			context.getCounter("MAP_REDUCE_COUNTER", "REDUCE_OUTPUT_KEYVALUE")
					.increment(1L);

			context.write(key, result);
		}

	}

	// Client 区域
	public static void main(String[] args) throws Exception {
		args = new String[] {
				"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/input/wendu.data",
				"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/outputcompress/" };
		// 获取配置信息
		Configuration configuration = new Configuration();

		// 优化代码去掉警告WARN mapred.JobClient: Use GenericOptionsParser for parsing
		// the arguments. Applications should implement Tool for the same
		String[] otherArgs = new GenericOptionsParser(configuration, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage:wordcount <in><out>");
			System.exit(2);
		}
		// 创建Job 设置配置和Job
		Job job = new Job(configuration, "wc");
		// 1:设置Job运行的类
		job.setJarByClass(Compress.class);
		// 2：设置Mapper和Reduce类
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);

		// 3：设置输入文件的目录和输出文件的目录
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

		// 对最终输出的文件进行压缩
		FileOutputFormat.setCompressOutput(job, true);
		FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
		// 4：设置输出结果的key和value的类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		// 5:提交Job,等待运行结果，并在客户端显示运行信息
		boolean isSuccess = job.waitForCompletion(true);
		// 6:结束程序
		System.exit(isSuccess ? 0 : 1);
	}
}
