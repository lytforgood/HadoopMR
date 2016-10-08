package com.second.sort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;
/**
 * 自定义分区
 * @author Administrator
 *
 */
public class DefinedPartition extends Partitioner<CombinationKey,IntWritable>{
   // private static final Logger logger = LoggerFactory.getLogger(DefinedPartition.class);
    /**
    *  数据输入来源：map输出
    * @author zengzhaozheng
    * @param key map输出键值
    * @param value map输出value值
    * @param numPartitions 分区总数，即reduce task个数
    */
    @Override
    public int getPartition(CombinationKey key, IntWritable value,int numPartitions) {
        //("--------enter DefinedPartition flag--------");
        /**
        * 注意：这里采用默认的hash分区实现方法
        * 根据组合键的第一个值作为分区
        * 这里需要说明一下，如果不自定义分区的话，mapreduce框架会根据默认的hash分区方法，
        * 将整个组合将相等的分到一个分区中，这样的话显然不是我们要的效果
        */
       // ("--------out DefinedPartition flag--------");
        return (key.getFirstKey().hashCode()&Integer.MAX_VALUE)%numPartitions;
    }
}
