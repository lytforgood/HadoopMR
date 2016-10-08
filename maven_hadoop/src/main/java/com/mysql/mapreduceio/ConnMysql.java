package com.mysql.mapreduceio;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*
 * 新API
 */
public class ConnMysql {

	// static {
	// conf.addResource(new Path("F:/lxw-hadoop/hdfs-site.xml"));
	// conf.addResource(new Path("F:/lxw-hadoop/mapred-site.xml"));
	// conf.addResource(new Path("F:/lxw-hadoop/core-site.xml"));
	// conf.set("mapred.job.tracker", "10.133.103.21:50021");
	// }

	public static class TblsRecord implements Writable, DBWritable {
		String tbl_name;
		String tbl_type;

		public TblsRecord() {

		}

		@Override
		public void write(PreparedStatement statement) throws SQLException {
			// TODO Auto-generated method stub
			statement.setString(1, this.tbl_name);
			statement.setString(2, this.tbl_type);
		}

		@Override
		public void readFields(ResultSet resultSet) throws SQLException {
			// TODO Auto-generated method stub
			this.tbl_name = resultSet.getString(1);
			this.tbl_type = resultSet.getString(2);
		}

		@Override
		public void write(DataOutput out) throws IOException {
			// TODO Auto-generated method stub
			Text.writeString(out, this.tbl_name);
			Text.writeString(out, this.tbl_type);
		}

		@Override
		public void readFields(DataInput in) throws IOException {
			// TODO Auto-generated method stub
			this.tbl_name = Text.readString(in);
			this.tbl_type = Text.readString(in);
		}

		public String toString() {
			return new String(this.tbl_name + " " + this.tbl_type);
		}

	}

	public static class ConnMysqlMapper extends
			Mapper<LongWritable, TblsRecord, Text, Text> {
		public void map(LongWritable key, TblsRecord values, Context context)
				throws IOException, InterruptedException {
			context.write(new Text(values.tbl_name), new Text(values.tbl_type));
		}
	}

	public static class ConnMysqlReducer extends
			Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for (Iterator<Text> itr = values.iterator(); itr.hasNext();) {
				context.write(key, itr.next());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Path output = new Path("/bs/output11/");
		FileSystem fs = FileSystem.get(URI.create(output.toString()), conf);
		if (fs.exists(output)) {
			fs.delete(output, true);
		}

		// //mysql的jdbc驱动分发到各个节点缓存，防止jdbc找不到错误
		// DistributedCache.addFileToClassPath(new Path(
		// "hdfs://hd022-test.nh.sdo.com/user/liuxiaowen/mysql-connector-java-5.1.13-bin.jar"),
		// conf);

		DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver",
				"jdbc:mysql://127.0.0.1:3306/hivetest", "root", "root");

		Job job = new Job(conf, "test mysql connection");
		job.setJarByClass(ConnMysql.class);

		job.setMapperClass(ConnMysqlMapper.class);
		job.setReducerClass(ConnMysqlReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setInputFormatClass(DBInputFormat.class);
		FileOutputFormat.setOutputPath(job, output);

		// 列名
		String[] fields = { "TBL_NAME", "TBL_TYPE" };
		// 六个参数分别为：
		// 1.Job;2.Class<? extends DBWritable>
		// 3.表名;4.where条件
		// 5.order by语句;6.列名
		DBInputFormat.setInput(job, TblsRecord.class, "mapreducetable",
				"TBL_NAME like 'a%'", "TBL_NAME", fields);

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
