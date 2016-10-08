package com.advanced.mergesmallfile;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/*
 * 实现一个定制的RecordReader，这六个方法均为继承的RecordReader要求的虚函数。
 实现的RecordReader，为自定义的InputFormat服务
 */
public class WholeFileRecordReader extends
		RecordReader<NullWritable, BytesWritable> {

	private FileSplit fileSplit;
	private Configuration conf;
	private BytesWritable value = new BytesWritable();
	private boolean processed = false;

	@Override
	public void close() throws IOException {
		// do nothing
	}

	@Override
	public NullWritable getCurrentKey() throws IOException,
			InterruptedException {
		return NullWritable.get();
	}

	@Override
	public BytesWritable getCurrentValue() throws IOException,
			InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return processed ? 1.0f : 0.0f;
	}

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
		this.fileSplit = (FileSplit) split;
		this.conf = context.getConfiguration();
	}

	// process表示记录是否已经被处理过
	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (!processed) {
			byte[] contents = new byte[(int) fileSplit.getLength()];
			Path file = fileSplit.getPath();
			FileSystem fs = file.getFileSystem(conf);
			FSDataInputStream in = null;
			try {
				in = fs.open(file);
				// 将file文件中
				// 的内容放入contents数组中。使用了IOUtils实用类的readFully方法，将in流中得内容放入
				// contents字节数组中。
				IOUtils.readFully(in, contents, 0, contents.length);
				// BytesWritable是一个可用做key或value的字节序列，而ByteWritable是单个字节。
				// 将value的内容设置为contents的值
				value.set(contents, 0, contents.length);
			} finally {
				IOUtils.closeStream(in);
			}
			processed = true;
			return true;
		}
		return false;
	}
}
