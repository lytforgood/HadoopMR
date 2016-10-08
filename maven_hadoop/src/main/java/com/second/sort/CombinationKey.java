package com.second.sort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * 自定义组合键
 * 
 */
public class CombinationKey implements WritableComparable<CombinationKey>{
    //private static final Logger logger = LoggerFactory.getLogger(CombinationKey.class);
    private Text firstKey;
    private IntWritable secondKey;
    public CombinationKey() {
        this.firstKey = new Text();
        this.secondKey = new IntWritable();
    }
    public Text getFirstKey() {
        return this.firstKey;
    }
    public void setFirstKey(Text firstKey) {
        this.firstKey = firstKey;
    }
    public IntWritable getSecondKey() {
        return this.secondKey;
    }
    public void setSecondKey(IntWritable secondKey) {
        this.secondKey = secondKey;
    }
    public void readFields(DataInput dateInput) throws IOException {
        // TODO Auto-generated method stub
        this.firstKey.readFields(dateInput);
        this.secondKey.readFields(dateInput);
    }
    public void write(DataOutput outPut) throws IOException {
        this.firstKey.write(outPut);
        this.secondKey.write(outPut);
    }
    /**
    * 自定义比较策略
    * 注意：该比较策略用于mapreduce的第一次默认排序，也就是发生在map阶段的sort小阶段，
    * 发生地点为环形缓冲区(可以通过io.sort.mb进行大小调整)
    */
    public int compareTo(CombinationKey combinationKey) {
        //("-------CombinationKey flag-------");
        return this.getFirstKey().compareTo(combinationKey.getFirstKey());
    }
}
