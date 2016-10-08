package org.dragon.hadoop.hdfs;

import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
/**
 * 简化命令行方式运行作业，用来解释常用的hadoop命令行选项
 * 讲解Tool用法
 * @author Administrator
 *
 */
public class ConfigurationPrinter extends Configured implements Tool{
	static {
	    Configuration.addDefaultResource("core-site.xml");
	    Configuration.addDefaultResource("hdfs-site.xml");
	  }
	  @Override
	  public int run(String[] args) throws Exception {
	    Configuration conf = getConf();
	    for (Entry<String, String> entry: conf) {
	      System.out.printf("%s=%s\n", entry.getKey(), entry.getValue());
	    }
	    return 0;
	  }
	 
	  public static void main(String[] args) throws Exception {
	    int exitCode = ToolRunner.run(new ConfigurationPrinter(), args);
	    System.exit(exitCode);
	  }
}
