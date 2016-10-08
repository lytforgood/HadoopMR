package com.bs.bus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.bs.bus.BusCount.Counter;
/**
 * @author zengzhaozheng
 *
 * 用途说明：
 * Map side join中的left outer join
 * 左连接，两个文件分别代表2个表,连接字段table1的id字段和table2的cityID字段
 * table1(左表):tb_dim_city(id int,name string,orderid int,city_code,is_show)，
 * 假设tb_dim_city文件记录数很少，tb_dim_city.dat文件内容,分隔符为"|"：
 *2014/8/1,晴/雷阵雨,36℃/26℃,无持续风向≤3级/无持续风向≤3级
 * -------------------------风骚的分割线-------------------------------
 * table2(右表)：tb_user_profiles(userID int,userName string,network string,double flow,cityID int)
 * tb_user_profiles.dat文件内容,分隔符为"|"：
广州,线路10,4589bb610f9be53a43a7bc26bb40e44d,8ce79e0b647053f191d20c5552eb49f0,广州,2014082016,普通卡
 * -------------------------风骚的分割线-------------------------------
 *  结果：
广州,线路10,4589bb610f9be53a43a7bc26bb40e44d,8ce79e0b647053f191d20c5552eb49f0,广州,2014082016,普通卡,晴/雷阵雨,36℃/26℃,无持续风向≤3级/无持续风向≤3级
 */
public class MapSideJoinMain extends Configured implements Tool{
    public static class LeftOutJoinMapper extends Mapper<LongWritable, Text, Text, Text> {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
        private HashMap<String,String> city_info = new HashMap<String, String>();
        private Text outPutKey = new Text();
        private Text outPutValue = new Text();
        private String city_secondPart = null;
        /**
         * 此方法在每个task开始之前执行，这里主要用作从DistributedCache
         * 中取到tb_dim_city文件，并将里边记录取出放到内存中。
         */
        @Override
        protected void setup(Context context)
                throws IOException, InterruptedException {
            BufferedReader br = null;
            //获得当前作业的DistributedCache相关文件
            Path[] distributePaths = DistributedCache.getLocalCacheFiles(context.getConfiguration());
            String cityInfo = null;
            for(Path p : distributePaths){
                if(p.toString().endsWith("gd_weather_report.txt")){
                    //读缓存文件，并放到mem中
                    br = new BufferedReader(new FileReader(p.toString()));
                    while(null!=(cityInfo=br.readLine())){
                        String[] cityPart = cityInfo.split(",");
                        if(cityPart.length ==4){
                        	String timeString=DayForWeek.getyymmhh(cityPart[0]);
                            city_info.put(timeString, cityPart[1]+","+cityPart[2]+","+cityPart[3]);
                        }
                    }
                }
            }
        }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
        /**
         * Map端的实现相当简单，直接判断tb_user_profiles.dat中的
         * cityID是否存在我的map中就ok了，这样就可以实现Map Join了
         */
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            //排掉空行
            if(value == null || value.toString().equals("")){
                return;
            }
            Bus bus = Bus.parser(value.toString());
			if (bus.isValid()) {
				//判断链接字段是否在map中存在
				city_secondPart = city_info.get(bus.getDeal_yymmdd());
	            if(city_secondPart != null){
	            	outPutKey.set(value);
	            	outPutValue.set(city_secondPart);
	                context.write(outPutKey, outPutValue);
	            }
			} else {
				context.getCounter(Counter.PARSER_ERR).increment(1L);
			}
        }
    }
    @Override
    public int run(String[] args) throws Exception {
            Configuration conf=getConf(); //获得配置文件对象
            conf.set("mapred.textoutputformat.separator", ",");
            DistributedCache.addCacheFile(new Path(args[2]).toUri(), conf);//为该job添加缓存文件
            Job job=new Job(conf,"MapJoinMR");
            job.setNumReduceTasks(0);
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
            FileInputFormat.addInputPath(job, new Path(args[0])); //设置map输入文件路径
            FileOutputFormat.setOutputPath(job, new Path(args[1])); //设置reduce输出文件路径
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
            job.setJarByClass(MapSideJoinMain.class);
            job.setMapperClass(LeftOutJoinMapper.class);
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
            job.setInputFormatClass(TextInputFormat.class); //设置文件输入格式
            job.setOutputFormatClass(TextOutputFormat.class);//使用默认的output格式
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
            //设置map的输出key和value类型
            job.setMapOutputKeyClass(Text.class);
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
            //设置reduce的输出key和value类型
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
            job.waitForCompletion(true);
            return job.isSuccessful()?0:1;
    }
    public static void main(String[] args) throws IOException,
            ClassNotFoundException, InterruptedException {
//    	args = new String[] {需要集群运行
//				"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/inputjion/",
//				"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/outputjion/",
//				"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/inputjion/tb_dim_city.dat"};
    	try {
            int returnCode =  ToolRunner.run(new MapSideJoinMain(),args);
            System.exit(returnCode);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
}
