package org.mrunit.test;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WordCountReduce extends
Reducer<Text, IntWritable, Text, IntWritable>{
	private IntWritable result = new IntWritable();


	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Context context) throws IOException, InterruptedException {
		
		// 用于累加
		int sum = 0;
		// 循环遍历 Interable
		for (IntWritable value : values) {
			// 累加
			sum += value.get();
		}
		// 设置总次数
		result.set(sum);
		
		
		context.write(key, result);
	}

}
