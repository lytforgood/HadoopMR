package com.advanced.join;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author zengzhaozheng
 *
 * 用途说明：
 * reudce side join中的left outer join
 * 左连接，两个文件分别代表2个表,连接字段table1的id字段和table2的cityID字段
 * table1(左表):tb_dim_city(id int,name string,orderid int,city_code,is_show)
 * tb_dim_city.dat文件内容,分隔符为"|"：
 * id     name  orderid  city_code  is_show
 * 0       其他        9999     9999         0
 * 1       长春        1        901          1
 * 2       吉林        2        902          1
 * 3       四平        3        903          1
 * 4       松原        4        904          1
 * 5       通化        5        905          1
 * 6       辽源        6        906          1
 * 7       白城        7        907          1
 * 8       白山        8        908          1
 * 9       延吉        9        909          1
 * -------------------------风骚的分割线-------------------------------
 * table2(右表)：tb_user_profiles(userID int,userName string,network string,double flow,cityID int)
 * tb_user_profiles.dat文件内容,分隔符为"|"：
 * userID   network     flow    cityID
 * 1           2G       123      1
 * 2           3G       333      2
 * 3           3G       555      1
 * 4           2G       777      3
 * 5           3G       666      4
 * -------------------------风骚的分割线-------------------------------
 * joinKey.dat内容：
 * city_code
 * 1
 * 2
 * 3
 * 4
 * -------------------------风骚的分割线-------------------------------
 *  结果：
 *  1   长春  1   901 1   1   2G  123
 *  1   长春  1   901 1   3   3G  555
 *  2   吉林  2   902 1   2   3G  333
 *  3   四平  3   903 1   4   2G  777
 *  4   松原  4   904 1   5   3G  666
 */
public class SemiJoin extends Configured implements Tool{
    private static final Logger logger = LoggerFactory.getLogger(SemiJoin.class);
    public static class SemiJoinMapper extends Mapper<Object, Text, Text, CombineValues> {
        private CombineValues combineValues = new CombineValues();
        private HashSet<String> joinKeySet = new HashSet<String>();
        private Text flag = new Text();
        private Text joinKey = new Text();
        private Text secondPart = new Text();
        /**
         * 将参加join的key从DistributedCache取出放到内存中，以便在map端将要参加join的key过滤出来。b
         */
        @Override
        protected void setup(Context context)
                throws IOException, InterruptedException {
            BufferedReader br = null;
            //获得当前作业的DistributedCache相关文件
            Path[] distributePaths = DistributedCache.getLocalCacheFiles(context.getConfiguration());
            String joinKeyStr = null;
            for(Path p : distributePaths){
            	System.out.println(p.toString());
                if(p.toString().endsWith("joinKey.dat")){
                    //读缓存文件，并放到mem中
                    br = new BufferedReader(new FileReader(p.toString()));
                    while(null!=(joinKeyStr=br.readLine())){
                        joinKeySet.add(joinKeyStr);
                    }
                }
            }
        }
        @Override
        protected void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            //获得文件输入路径
            String pathName = ((FileSplit) context.getInputSplit()).getPath().toString();
            //数据来自tb_dim_city.dat文件,标志即为"0"
            if(pathName.endsWith("tb_dim_city.dat")){
                String[] valueItems = value.toString().split("\t");
                //过滤格式错误的记录
                if(valueItems.length != 5){
                    return;
                }
                //过滤掉不需要参加join的记录
                if(joinKeySet.contains(valueItems[0])){
                    flag.set("0");
                    joinKey.set(valueItems[0]);
                    secondPart.set(valueItems[1]+"\t"+valueItems[2]+"\t"+valueItems[3]+"\t"+valueItems[4]);
                    combineValues.setFlag(flag);
                    combineValues.setJoinKey(joinKey);
                    combineValues.setSecondPart(secondPart);
                    context.write(combineValues.getJoinKey(), combineValues);
                }else{
                    return ;
                }
            }//数据来自于tb_user_profiles.dat，标志即为"1"
            else if(pathName.endsWith("tb_user_profiles.dat")){
                String[] valueItems = value.toString().split("\t");
                //过滤格式错误的记录
                if(valueItems.length != 4){
                    return;
                }
                //过滤掉不需要参加join的记录
                if(joinKeySet.contains(valueItems[3])){
                    flag.set("1");
                    joinKey.set(valueItems[3]);
                    secondPart.set(valueItems[0]+"\t"+valueItems[1]+"\t"+valueItems[2]);
                    combineValues.setFlag(flag);
                    combineValues.setJoinKey(joinKey);
                    combineValues.setSecondPart(secondPart);
                    context.write(combineValues.getJoinKey(), combineValues);
                }else{
                    return ;
                }
            }
        }
    }
    public static class SemiJoinReducer extends Reducer<Text, CombineValues, Text, Text> {
        //存储一个分组中的左表信息
        private ArrayList<Text> leftTable = new ArrayList<Text>();
        //存储一个分组中的右表信息
        private ArrayList<Text> rightTable = new ArrayList<Text>();
        private Text secondPar = null;
        private Text output = new Text();
        /**
         * 一个分组调用一次reduce函数
         */
        @Override
        protected void reduce(Text key, Iterable<CombineValues> value, Context context)
                throws IOException, InterruptedException {
            leftTable.clear();
            rightTable.clear();
            /**
             * 将分组中的元素按照文件分别进行存放
             * 这种方法要注意的问题：
             * 如果一个分组内的元素太多的话，可能会导致在reduce阶段出现OOM，
             * 在处理分布式问题之前最好先了解数据的分布情况，根据不同的分布采取最
             * 适当的处理方法，这样可以有效的防止导致OOM和数据过度倾斜问题。
             */
            for(CombineValues cv : value){
                secondPar = new Text(cv.getSecondPart().toString());
                //左表tb_dim_city
                if("0".equals(cv.getFlag().toString().trim())){
                    leftTable.add(secondPar);
                }
                //右表tb_user_profiles
                else if("1".equals(cv.getFlag().toString().trim())){
                    rightTable.add(secondPar);
                }
            }
            logger.info("tb_dim_city:"+leftTable.toString());
            logger.info("tb_user_profiles:"+rightTable.toString());
            for(Text leftPart : leftTable){
                for(Text rightPart : rightTable){
                    output.set(leftPart+ "\t" + rightPart);
                    context.write(key, output);
                }
            }
        }
    }
    @Override
    public int run(String[] args) throws Exception {
            Configuration conf=getConf(); //获得配置文件对象
            DistributedCache.addCacheFile(new Path(args[2]).toUri(), conf);
                                                                                                                                                                                                                                           
            Job job=new Job(conf,"LeftOutJoinMR");
            job.setJarByClass(SemiJoin.class);
                                                                                                                                                                                                                                           
            FileInputFormat.addInputPath(job, new Path(args[0])); //设置map输入文件路径
            FileOutputFormat.setOutputPath(job, new Path(args[1])); //设置reduce输出文件路径
                                                                                                                                                                                                                                                                                                                                                                                
            job.setMapperClass(SemiJoinMapper.class);
            job.setReducerClass(SemiJoinReducer.class);
                                                                                                                                                                                                                                          
            job.setInputFormatClass(TextInputFormat.class); //设置文件输入格式
            job.setOutputFormatClass(TextOutputFormat.class);//使用默认的output格式
                                                                                                                                                                                                                                           
            //设置map的输出key和value类型
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(CombineValues.class);
                                                                                                                                                                                                                                           
            //设置reduce的输出key和value类型
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
            job.waitForCompletion(true);
            return job.isSuccessful()?0:1;
    }
    public static void main(String[] args) throws IOException,
            ClassNotFoundException, InterruptedException {
    	
    	try {
//    		args = new String[] {需要集群运行
//    				"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/inputjion/",
//    				"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/outputjion/",
//    				"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/inputjion/joinKey.dat"};
            int returnCode =  ToolRunner.run(new SemiJoin(),args);
            System.exit(returnCode);
        } catch (Exception e) {
//            logger.error(e.getMessage());
        	e.printStackTrace();
        }
    }
}
