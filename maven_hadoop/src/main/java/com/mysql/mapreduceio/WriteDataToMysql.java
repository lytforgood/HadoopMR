package com.mysql.mapreduceio;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;


/**
 * 将mapreduce的结果数据写入mysql中
 * @author asheng
 */
public class WriteDataToMysql {
/**
     * 重写DBWritable
     * @author asheng
     * TblsWritable需要向mysql中写入数据
     */
    public static class TblsWritable implements Writable, DBWritable
    {  
            String tbl_name;  
            String tbl_type;  
            public TblsWritable()
            {  
            
            }  
            public TblsWritable(String tbl_name,String tab_type)
            {  
            this.tbl_name = tbl_name;
            this.tbl_type = tab_type;
            }  
            @Override  
            public void write(PreparedStatement statement) throws SQLException
            {
                    statement.setString(1, this.tbl_name);  
                    statement.setString(2, this.tbl_type);  
            }  
            @Override  
            public void readFields(ResultSet resultSet) throws SQLException
            {  
                    this.tbl_name = resultSet.getString(1);  
                    this.tbl_type = resultSet.getString(2);  
            }  
            @Override  
            public void write(DataOutput out) throws IOException
            {  
                    out.writeUTF(this.tbl_name);
                    out.writeUTF(this.tbl_type);
            }  
            @Override  
            public void readFields(DataInput in) throws IOException
            {  
                    this.tbl_name = in.readUTF();  
                    this.tbl_type = in.readUTF();  
            }  
            public String toString()
            {  
                return new String(this.tbl_name + " " + this.tbl_type);  
            }  
    }
    public static class ConnMysqlMapper extends Mapper<LongWritable,Text,Text,Text>
    //TblsRecord是自定义的类型，也就是上面重写的DBWritable类
   {  
        public void map(LongWritable key,Text value,Context context)throws IOException,InterruptedException
        {  
        //<首字母偏移量,该行内容>接收进来，然后处理value，将abc和x作为map的输出
        //key对于本程序没有太大的意义，没有使用
        String name = value.toString().split(" ")[0];
        String type = value.toString().split(" ")[1];
                context.write(new Text(name),new Text(type));  
        }  
   }  
    public static class ConnMysqlReducer extends Reducer<Text,Text,TblsWritable,TblsWritable>
    {  
        public void reduce(Text key,Iterable<Text> values,Context context)throws IOException,
                                                                                                                InterruptedException
        {  
        //接收到的key value对即为要输入数据库的字段，所以在reduce中：
        //wirte的第一个参数，类型是自定义类型TblsWritable，利用key和value将其组合成TblsWritable，
       //然后等待写入数据库
        //wirte的第二个参数，wirte的第一个参数已经涵盖了要输出的类型，所以第二个类型没有用，设为null
        for(Iterator<Text> itr = values.iterator();itr.hasNext();)
                 {  
                     context.write(new TblsWritable(key.toString(),itr.next().toString()),null);
                 }  
        }  
    }  
    public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException
    {
    	args = new String[] {
				"hdfs://hadoop-master.dragon.org:9000/bs/data/cc.txt",
		 };
        Configuration conf = new Configuration();

        DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver","jdbc:mysql://127.0.0.1:3306/hivetest","root", "root");    
        Job job = new Job(conf,"test mysql connection");  
        job.setJarByClass(WriteDataToMysql.class);  
          
        job.setMapperClass(ConnMysqlMapper.class);  
        job.setReducerClass(ConnMysqlReducer.class);  
          
        job.setOutputKeyClass(Text.class);  
        job.setOutputValueClass(Text.class);
        
        job.setInputFormatClass(TextInputFormat.class);  
        job.setOutputFormatClass(DBOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        //设置输出的表名，字段的顺序	(字段名称不区分大小写)
        DBOutputFormat.setOutput(job, "mapreducetable", "TBL_NAME","TBL_TYPE");
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
