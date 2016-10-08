package org.dragon.hadoop.hdfs;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

/**
 * HDFS工具类
 * 
 * @author Administrator
 * 
 */
public class HDFSUtils {
	//查看FsShell命令源码
	org.apache.hadoop.fs.FsShell test=null;
	
	public static FileSystem getFileSystem() {
		
		// 声明FileSystem
		FileSystem hdfs = null;
		try {
			// 获取配置文件信息(加载配置XML)
			Configuration conf = new Configuration();
			// 获取文件系统
			// FileSystem fs = FileSystem.get(URI.create(""), conf);
			hdfs = FileSystem.get(conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hdfs;

	}

}
