package com.sequencefile.parse;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
/*
 * sequencefile转换为MapFile为MapFile创建索引(调用fix方法)
 */
public class SequenceFileToMapFile {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// // TODO Auto-generated method stub
		Configuration conf = new Configuration();
		Path path = new Path("/test/seqFile2.seq");
		FileSystem fs = FileSystem.get(conf);
		// Writer内部类用于文件的写操作,假设Key和Value都为Text类型
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path,
				Text.class, Text.class);
		// 通过writer向文档中写入记录
		writer.append(new Text("key"), new Text("value"));

		IOUtils.closeStream(writer);// 关闭write流
		// 通过reader从文档中读取记录
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path(
				"/opt/output/part-m-00000"), conf);
		Class keyClass=reader.getKeyClass();
		Class valClass=reader.getValueClass();
		reader.close();
		//调用fix()建索引
		long entries=MapFile.fix(fs, path, keyClass, valClass, false, conf);
		System.out.println("索引"+entries);
	}

}
