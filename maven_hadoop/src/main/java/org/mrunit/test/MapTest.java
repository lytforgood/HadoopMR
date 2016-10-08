package org.mrunit.test;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Test;


public class MapTest {
	MapDriver<LongWritable, Text, Text, IntWritable> mapDriver;

	@Before
	public void init() {
		WordCountMapper mapper = new WordCountMapper();
		mapDriver = MapDriver.newMapDriver(mapper);
	}

	@Test
	public void test() throws IOException {
		String line = "a	is	a";
		mapDriver.withInput(new LongWritable(), new Text(line));
		mapDriver.withOutput(new Text("a"), new IntWritable(1))
				.withOutput(new Text("is"), new IntWritable(1))
				.withOutput(new Text("a"), new IntWritable(1));
		mapDriver.runTest();
	}

}
