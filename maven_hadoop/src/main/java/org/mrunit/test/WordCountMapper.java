package org.mrunit.test;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WordCountMapper extends
		Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable one = new IntWritable(1);
	private Text word = new Text();

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
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
