package com.advanced.join;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
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
 * Map side join中的left outer join
 * 左连接，两个文件分别代表2个表,连接字段table1的id字段和table2的cityID字段
 * table1(左表):tb_dim_city(id int,name string,orderid int,city_code,is_show)，
 * 假设tb_dim_city文件记录数很少，tb_dim_city.dat文件内容,分隔符为"|"：
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
 *  结果：
 *  1   长春  1   901 1   1   2G  123
 *  1   长春  1   901 1   3   3G  555
 *  2   吉林  2   902 1   2   3G  333
 *  3   四平  3   903 1   4   2G  777
 *  4   松原  4   904 1   5   3G  666
 */
public class MapSideJoinMain extends Configured implements Tool{
    private static final Logger logger = LoggerFactory.getLogger(MapSideJoinMain.class);
    public static class LeftOutJoinMapper extends Mapper<Object, Text, Text, Text> {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
        private HashMap<String,String> city_info = new HashMap<String, String>();
        private Text outPutKey = new Text();
        private Text outPutValue = new Text();
        private String mapInputStr = null;
        private String mapInputSpit[] = null;
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
                if(p.toString().endsWith("tb_dim_city.dat")){
                    //读缓存文件，并放到mem中
                    br = new BufferedReader(new FileReader(p.toString()));
                    while(null!=(cityInfo=br.readLine())){
                        String[] cityPart = cityInfo.split("\t",5);
                        if(cityPart.length ==5){
                            city_info.put(cityPart[0], cityPart[1]+"\t"+cityPart[2]+"\t"+cityPart[3]+"\t"+cityPart[4]);
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
        protected void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            //排掉空行
            if(value == null || value.toString().equals("")){
                return;
            }
            mapInputStr = value.toString();
            mapInputSpit = mapInputStr.split("\t",4);
            //过滤非法记录
            if(mapInputSpit.length != 4){
                return;
            }
            //判断链接字段是否在map中存在
            city_secondPart = city_info.get(mapInputSpit[3]);
            if(city_secondPart != null){
                this.outPutKey.set(mapInputSpit[3]);
                this.outPutValue.set(city_secondPart+"\t"+mapInputSpit[0]+"\t"+mapInputSpit[1]+"\t"+mapInputSpit[2]);
                context.write(outPutKey, outPutValue);
            }
        }
    }
    @Override
    public int run(String[] args) throws Exception {
            Configuration conf=getConf(); //获得配置文件对象
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
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }
    }
}
