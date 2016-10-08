package org.dragon.hadoop.hdfs;

import java.io.InputStream;
import java.net.URL;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;
import org.junit.Test;

/**
 * 
 * HDFS API URL 方式操作
 * 
 * @author Administrator
 * 
 */
public class HDFSUrlTest {
	// 让JAVA程序识别HDFS 的URL
	static {
		URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
	}

	// 查看文件内容
	@Test
	public void testRead() throws Exception {
		InputStream in = null;
		// 文件路径
		String fileUrl = "hdfs://hadoop-master.dragon.org:9000/opt/data/dir/0.125.log";
		try {
			// 获取文件输入流
			in = new URL(fileUrl).openStream();
			// 将文件内容读取出来，打印控制台4096是复制缓冲区的大小false是复制结束后是否关闭数据流
			IOUtils.copyBytes(in, System.out, 4096, false);

		} finally {
			IOUtils.closeStream(in);
		}
	}

}
