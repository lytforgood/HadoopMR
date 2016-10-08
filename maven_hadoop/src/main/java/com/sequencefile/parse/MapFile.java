package com.sequencefile.parse;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.util.ReflectionUtils;

/*
 * MapFile文件的读写
 */
public class MapFile {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// // TODO Auto-generated method stub
		Configuration conf = new Configuration();
		Path path = new Path("/test/MapFile.map");
		String uri = path.toString();
		FileSystem fs = FileSystem.get(conf);
		// Writer内部类用于文件的写操作
		Text mkey = new Text();
		Text mvalue = new Text();
		org.apache.hadoop.io.MapFile.Writer writer = new org.apache.hadoop.io.MapFile.Writer(
				conf, fs, uri, mkey.getClass(), mvalue.getClass());
		// 通过writer向文档中写入记录
		writer.append(new Text("key"), new Text("value"));

		IOUtils.closeStream(writer);// 关闭write流
		// 通过reader从文档中读取记录
		org.apache.hadoop.io.MapFile.Reader reader = new org.apache.hadoop.io.MapFile.Reader(fs, uri, conf);
		WritableComparable<?> key = (WritableComparable<?>) ReflectionUtils.newInstance(
				reader.getKeyClass(), conf);
		Writable value = (Writable) ReflectionUtils.newInstance(
				reader.getValueClass(), conf);
		// long position =reader.getPosition();//下一个记录的开始位置
		while (reader.next(key, value)) {
			System.out.println(value.toString() + " belongs to cluster "
					+ key.toString());
		}
		IOUtils.closeStream(reader);// 关闭read流

	}

}
