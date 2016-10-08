package com.bs.bus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

public class ReducerTest {
	MapDriver<LongWritable, Text, Text, IntWritable> mapDriver;
	ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;
	MapReduceDriver<LongWritable, Text, Text, IntWritable, Text, IntWritable> mapReduceDriver;

	@Before
	public void init() {
		BusCount.BusMap mapper = new BusCount.BusMap();
		BusCount.BusReduce reducer = new BusCount.BusReduce();
		mapDriver = MapDriver.newMapDriver(mapper);
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
		mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
	}

	@Test
	public void maptest() throws IOException {
		String line = "广州,线路10,4589bb610f9be53a43a7bc26bb40e44d,8ce79e0b647053f191d20c5552eb49f0,广州,2014082016,普通卡";
		mapDriver.withInput(new LongWritable(), new Text(line));
		mapDriver.withOutput(new Text(
				"线路1020140820164589bb610f9be53a43a7bc26bb40e44d"),
				new IntWritable(1));
		mapDriver.runTest();
	}

	@Test
	public void reducetest() throws IOException {
		List<IntWritable> values = new ArrayList<IntWritable>();
		values.add(new IntWritable(2));
		values.add(new IntWritable(3));
		reduceDriver.withInput(new Text(
				"线路1020140820164589bb610f9be53a43a7bc26bb40e44d"), values);
		reduceDriver.withOutput(new Text(
				"线路1020140820164589bb610f9be53a43a7bc26bb40e44d"),
				new IntWritable(5));
		reduceDriver.runTest();
	}

	@Test
	public void testMapReduce() throws IOException {
		mapReduceDriver
				.withInput(
						new LongWritable(),
						new Text(
								"广州,线路10,4589bb610f9be53a43a7bc26bb40e44d,8ce79e0b647053f191d20c5552eb49f0,广州,2014082016,普通卡"));
		mapReduceDriver.withOutput(new Text(
				"线路1020140820164589bb610f9be53a43a7bc26bb40e44d"),
				new IntWritable(1));
		mapReduceDriver.runTest();
	}
}
