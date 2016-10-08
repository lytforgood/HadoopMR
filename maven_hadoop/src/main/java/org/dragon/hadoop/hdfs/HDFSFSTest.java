package org.dragon.hadoop.hdfs;

import java.io.IOException;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.io.IOUtils;
import org.junit.Test;

/**
 * 通过FileSystem API 操作HDFS
 * 
 * @author Administrator
 * 
 */
public class HDFSFSTest {
	// 读取文件系统
	/**
	 * @throws Exception
	 */
	@Test
	public void testRead() throws Exception {
		// 获取文件系统
		FileSystem hdfs = HDFSUtils.getFileSystem();
		// Configuration conf = new Configuration();
		// FileSystem hdfs = FileSystem.get(conf);
		// 文件名称
		Path path = new Path("/opt/data/dir/touch3.data");
		// 打开输入流--------open()
		FSDataInputStream inStream = hdfs.open(path);
		// 读取文件内容到控制台显示-------read()4096是复制缓冲区的大小false是复制结束后是否关闭数据流
		IOUtils.copyBytes(inStream, System.out, 4096, false);
		// 关闭流--------------close()
		IOUtils.closeStream(inStream);

	}

	// 查看目录
	@Test
	public void testList() throws Exception {

		FileSystem hdfs = HDFSUtils.getFileSystem();
		// 目录
		Path path = new Path("/opt/data");
		FileStatus[] fileStatus = hdfs.listStatus(path);
		for (FileStatus fs : fileStatus) {
			Path p = fs.getPath();
			String info = fs.isDir() ? "目录" : "文件";
			System.out.println(info + ":" + p);
		}
	}

	// 创建目录
	@Test
	public void testDirectory() throws IOException {
		FileSystem hdfs = HDFSUtils.getFileSystem();
		Path path = new Path("/opt/data/dir");
		boolean isSuccess = hdfs.mkdirs(path);
		String info = isSuccess ? "成功" : "失败";
		System.out.println("创建目录" + path + info);
	}

	// 上传本地文件-- put copyFromLocal
	@Test
	public void testput() throws IOException {
		FileSystem hdfs = HDFSUtils.getFileSystem();
		// 本地文件（目录+名称）
		Path srcPath = new Path("d:/jdk-7u79-linux-x64.tar.gz");

		// HDFS 文件上传路径
		Path dstPath = new Path("/opt/data/dir/");
		// 文件上传
		hdfs.copyFromLocalFile(srcPath, dstPath);

	}

	// 创建HDFS并写入内存
	@Test
	public void testCreate() throws IOException {
		FileSystem hdfs = HDFSUtils.getFileSystem();

		// HDFS 文件路径
		Path path = new Path("/opt/data/dir/touch3.data");
		// 创建文件，并获取输出流
		FSDataOutputStream fsDataOutputStream = hdfs.create(path);
		// 通过输出流写入数据
		fsDataOutputStream.writeUTF("Hello Hadoop!");
		// 通过输出流写入数据--中文数据
		fsDataOutputStream.write("你好".getBytes());
		IOUtils.closeStream(fsDataOutputStream);
	}

	// 对HDFS上文件进行重命名
	@Test
	public void testRename() throws Exception {
		FileSystem hdfs = HDFSUtils.getFileSystem();

		// HDFS 文件路径
		Path srcpath = new Path("/opt/data/dir/touch2.data");
		Path dstpath = new Path("/opt/data/dir/rename.data");

		boolean flag = hdfs.rename(srcpath, dstpath);
		System.out.println(flag);
	}

	// 删除文件
	@Test
	public void testDelete() throws Exception {
		FileSystem hdfs = HDFSUtils.getFileSystem();

		// HDFS 文件路径
		Path path = new Path("/opt/data/dir/touch3.data");

		boolean flag = hdfs.deleteOnExit(path);
		System.out.println(flag);
	}

	// 删除目录
	@Test
	public void testDeleteDir() throws Exception {
		FileSystem hdfs = HDFSUtils.getFileSystem();

		// HDFS 文件路径
		Path path = new Path("/opt/data/dir/");

		boolean flag = hdfs.delete(path, true);
		System.out.println(flag);
	}

	// 查找某个文件在HDFS集群的位置
	@Test
	public void testLocation() throws Exception {
		FileSystem hdfs = HDFSUtils.getFileSystem();

		// HDFS 文件路径
		Path path = new Path("/opt/data/dir/jdk-7u79-linux-x64.tar.gz");
		FileStatus fileStatus = hdfs.getFileStatus(path);
		BlockLocation[] blockLocations = hdfs.getFileBlockLocations(fileStatus,
				0, fileStatus.getLen());
		for (BlockLocation blockLocation : blockLocations) {
			String[] hosts = blockLocation.getHosts();
			for (String host : hosts) {
				System.out.println(host + "该块主机名");
			}
		}
	}

	// 获取HDFS集群上所有节点名称信息
	@Test
	public void testCluster() throws Exception {
		FileSystem hdfs = HDFSUtils.getFileSystem();
		//分布式系统
		DistributedFileSystem distributedFileSystem=(DistributedFileSystem) hdfs;
		//节点信息
		DatanodeInfo[] datanodeInfos=distributedFileSystem.getDataNodeStats();
		for (DatanodeInfo datanodeInfo : datanodeInfos) {
			String hostname=datanodeInfo.getHostName();
			System.out.println(hostname);
		}
	}
}

