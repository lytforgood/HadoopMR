package org.mrunit.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;


public class ReducerTest {
	ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;
	MapReduceDriver<LongWritable, Text, Text, IntWritable, Text, IntWritable> mapReduceDriver;

	@Before
	public void init() {
		WordCountMapper mapper = new WordCountMapper();
		WordCountReduce reducer = new WordCountReduce();
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
		mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
	}

	@Test
	public void test() throws IOException {
		List<IntWritable> values = new ArrayList<IntWritable>();
		values.add(new IntWritable(2));
		values.add(new IntWritable(3));
		reduceDriver.withInput(new Text("taobao"), values);
		reduceDriver.withOutput(new Text("taobao"), new IntWritable(5));
		reduceDriver.runTest();
	}

	@Test
	public void testMapReduce() throws IOException {
		mapReduceDriver.withInput(new LongWritable(), new Text("a	is	a"));
		List<IntWritable> values = new ArrayList<IntWritable>();
		values.add(new IntWritable(1));
		values.add(new IntWritable(1));
		mapReduceDriver.withOutput(new Text("a"), new IntWritable(2))
						.withOutput(new Text("is"), new IntWritable(1));
		mapReduceDriver.runTest();
	}
}
