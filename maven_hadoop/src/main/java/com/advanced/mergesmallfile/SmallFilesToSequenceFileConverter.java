package com.advanced.mergesmallfile;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/*
 * 将小文件打包成SequenceFile：以文件名为key内容为value的SequenceFile文件
 * 参考CombineFileInputFormat
 * 
 */
public class SmallFilesToSequenceFileConverter extends Configured implements Tool{
	 
    //静态内部类，作为mapper
    static class SequenceFileMapper extends Mapper<NullWritable, BytesWritable, Text, BytesWritable>
    {
        private Text filenameKey;
         
        //setup在task开始前调用，这里主要是初始化filenamekey
        @Override
        protected void setup(Context context)
        {
            InputSplit split = context.getInputSplit();
            Path path = ((FileSplit) split).getPath();
            filenameKey = new Text(path.toString());
        }
        @Override
        public void map(NullWritable key, BytesWritable value, Context context)
                throws IOException, InterruptedException{
            context.write(filenameKey, value);
        }
    }
 
    public int run(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = new Job(conf);
        job.setJobName("SmallFilesToSequenceFileConverter");
         
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
         
        //再次理解此处设置的输入输出格式。。。它表示的是一种对文件划分，索引的方法
        job.setInputFormatClass(WholeFileInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
         
        //此处的设置是最终输出的key/value，一定要注意！
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(BytesWritable.class);
         
        job.setMapperClass(SequenceFileMapper.class);
         
        return job.waitForCompletion(true) ? 0 : 1;
    }
     
    public static void main(String [] args) throws Exception
    {
		args = new String[] {
		"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/input/",
		"hdfs://hadoop-master.dragon.org:9000/opt/data/wc/outputmerge/"};
        int exitCode = ToolRunner.run(new SmallFilesToSequenceFileConverter(), args);
        System.exit(exitCode);
    }
}
