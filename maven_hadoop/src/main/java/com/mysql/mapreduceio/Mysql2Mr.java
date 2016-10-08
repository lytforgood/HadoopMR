package com.mysql.mapreduceio;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.db.DBConfiguration;
import org.apache.hadoop.mapred.lib.db.DBInputFormat;
import org.apache.hadoop.mapred.lib.db.DBOutputFormat;
import org.apache.hadoop.mapred.lib.db.DBWritable;
 
/**
 * Function: 测试 mr 与 mysql 的数据交互，此测试用例将一个表中的数据复制到另一张表中
 *           实际当中，可能只需要从 mysql 读，或者写到 mysql 中。
 * date: 2013-7-29 上午2:34:04 <br/>
 * 旧API 0.19
 */
public class Mysql2Mr {
    // DROP TABLE IF EXISTS `hadoop`.`studentinfo`;
    // CREATE TABLE studentinfo (
    // id INTEGER NOT NULL PRIMARY KEY,
    // name VARCHAR(32) NOT NULL);
 
    public static class StudentinfoRecord implements Writable, DBWritable {
        int id;
        String name;
 
        public StudentinfoRecord() {
 
        }
 
        public void readFields(DataInput in) throws IOException {
            this.id = in.readInt();
            this.name = Text.readString(in);
        }
 
        public String toString() {
            return new String(this.id + " " + this.name);
        }
 
        @Override
        public void write(PreparedStatement stmt) throws SQLException {
            stmt.setInt(1, this.id);
            stmt.setString(2, this.name);
        }
 
        @Override
        public void readFields(ResultSet result) throws SQLException {
            this.id = result.getInt(1);
            this.name = result.getString(2);
        }
 
        @Override
        public void write(DataOutput out) throws IOException {
            out.writeInt(this.id);
            Text.writeString(out, this.name);
        }
    }
 
    // 记住此处是静态内部类，要不然你自己实现无参构造器，或者等着抛异常：
    // Caused by: java.lang.NoSuchMethodException: DBInputMapper.<init>()
    // http://stackoverflow.com/questions/7154125/custom-mapreduce-input-format-cant-find-constructor
    // 网上脑残式的转帖，没见到一个写对的。。。
    public static class DBInputMapper extends MapReduceBase implements
            Mapper<LongWritable, StudentinfoRecord, LongWritable, Text> {
        public void map(LongWritable key, StudentinfoRecord value,
                OutputCollector<LongWritable, Text> collector, Reporter reporter) throws IOException {
            collector.collect(new LongWritable(value.id), new Text(value.toString()));
        }
    }
 
    public static class MyReducer extends MapReduceBase implements
            Reducer<LongWritable, Text, StudentinfoRecord, Text> {
        @Override
        public void reduce(LongWritable key, Iterator<Text> values,
                OutputCollector<StudentinfoRecord, Text> output, Reporter reporter) throws IOException {
            String[] splits = values.next().toString().split(" ");
            StudentinfoRecord r = new StudentinfoRecord();
            r.id = Integer.parseInt(splits[0]);
            r.name = splits[1];
            output.collect(r, new Text(r.name));
        }
    }
 
    public static void main(String[] args) throws IOException {
        JobConf conf = new JobConf(Mysql2Mr.class);
//        DistributedCache.addFileToClassPath(new Path("/tmp/mysql-connector-java-5.0.8-bin.jar"), conf);
 
        conf.setMapOutputKeyClass(LongWritable.class);
        conf.setMapOutputValueClass(Text.class);
        conf.setOutputKeyClass(LongWritable.class);
        conf.setOutputValueClass(Text.class);
 
        conf.setOutputFormat(DBOutputFormat.class);
        conf.setInputFormat(DBInputFormat.class);
        // // mysql to hdfs
        // conf.setReducerClass(IdentityReducer.class);
        // Path outPath = new Path("/tmp/1");
        // FileSystem.get(conf).delete(outPath, true);
        // FileOutputFormat.setOutputPath(conf, outPath);
 
        DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/hivetest",
                "root", "root");
        String[] fields = { "id", "name" };
        // 从 t 表读数据
        DBInputFormat.setInput(conf, StudentinfoRecord.class, "t", null, "id", fields);
        // mapreduce 将数据输出到 t2 表
        DBOutputFormat.setOutput(conf, "t2", "id", "name");
        // conf.setMapperClass(org.apache.hadoop.mapred.lib.IdentityMapper.class);
        conf.setMapperClass(DBInputMapper.class);
        conf.setReducerClass(MyReducer.class);
 
        JobClient.runJob(conf);
    }
}
