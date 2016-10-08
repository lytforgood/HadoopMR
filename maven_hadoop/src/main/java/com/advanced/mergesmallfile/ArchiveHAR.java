package com.advanced.mergesmallfile;

/**
 * 对于小文件问题，Hadoop本身也提供了几个解决方案，分别为：Hadoop Archive，Sequence
 * file和CombineFileInputFormat。
 * 
 * 
 */
public class ArchiveHAR {

	/**
	 * Hadoop Archive或者HAR，是一个高效地将小文件放入HDFS块中的文件存档工具，它能够将多个小文件打包成一个HAR文件，
	 * 这样在减少namenode内存使用的同时，仍然允许对文件进行透明的访问。
	 * 
	 * 对某个目录/foo/bar下的所有小文件存档成/outputdir/ zoo.har：
	 * 
	 * hadoop archive -archiveName zoo.har -p /foo/bar /outputdir
	 * 
	 * 当然，也可以指定HAR的大小(使用-Dhar.block.size)。
	 * 
	 * HAR是在Hadoop file system之上的一个文件系统，因此所有fs
	 * shell命令对HAR文件均可用，只不过是文件路径格式不一样，HAR的访问路径可以是以下两种格式：
	 * 
	 * har://scheme-hostname:port/archivepath/fileinarchive
	 * 
	 * har:///archivepath/fileinarchive(本节点)
	 * 
	 * 可以这样查看HAR文件存档中的文件：
	 * 
	 * hadoop dfs -ls har:///user/zoo/foo.har
	 * 
	 * 输出：
	 * 
	 * har:///user/zoo/foo.har/hadoop/dir1
	 * 
	 * har:///user/zoo/foo.har/hadoop/dir2
	 * 
	 * 使用HAR时需要两点，第一，对小文件进行存档后，原文件并不会自动被删除，需要用户自己删除；第二，
	 * 创建HAR文件的过程实际上是在运行一个mapreduce作业，因而需要有一个hadoop集群运行此命令。
	 * 
	 * 此外，HAR还有一些缺陷：第一，一旦创建，Archives便不可改变。要增加或移除里面的文件，必须重新创建归档文件。第二，
	 * 要归档的文件名中不能有空格，否则会抛出异常，可以将空格用其他符号替换(使用-Dhar.space.replacement.enable=true
	 * 和-Dhar.space.replacement参数)。
	 * 
	 * 用法：hadoop archive -archiveName NAME <src>* <dest> 命令选项： -archiveName NAME
	 * 要创建的档案的名字。 src 源文件系统的路径名。 dest 保存档案文件的目标目录。
	 */
}
