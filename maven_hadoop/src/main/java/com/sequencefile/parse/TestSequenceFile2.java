package com.sequencefile.parse;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;
/*
 *  sequencefile读写2(自动判别keyvalue类型)
 */
public class TestSequenceFile2 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
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
		Writable key=(Writable) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
		Writable value=(Writable) ReflectionUtils.newInstance(reader.getValueClass(), conf);
//		long position =reader.getPosition();//下一个记录的开始位置
		while (reader.next(key, value)) {
			System.out.println(value.toString() + " belongs to cluster "
					+ key.toString());
		}
		IOUtils.closeStream(reader);// 关闭read流

	}

}
