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
日期补零
 */
public class MapJoin extends Configured implements Tool{
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
                if(p.toString().endsWith("qq")){
                    //读缓存文件，并放到mem中
                    br = new BufferedReader(new FileReader(p.toString()));
                    while(null!=(cityInfo=br.readLine())){
                    	String[] cityPart = cityInfo.split(",");
                            city_info.put(cityPart[0]+cityPart[1], cityPart[2]);
                        
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
            String[] array = value.toString().split(",");
			
				//判断链接字段是否在map中存在
				city_secondPart = city_info.get(array[0]+array[1]);
	            if(city_secondPart != null){
	            	outPutKey.set(array[0]+","+array[1]);
	            	outPutValue.set(city_secondPart);
	                context.write(outPutKey, outPutValue);
	            }else {
	            	outPutKey.set(array[0]+","+array[1]);
	            	outPutValue.set("0");
	            	context.write(outPutKey, outPutValue);
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
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
            job.setJarByClass(MapJoin.class);
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
            int returnCode =  ToolRunner.run(new MapJoin(),args);
            System.exit(returnCode);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
}
