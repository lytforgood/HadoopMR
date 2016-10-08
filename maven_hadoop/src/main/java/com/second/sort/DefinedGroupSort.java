package com.second.sort;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * 自定义分组策略
 * 将组合将中第一个值相同的分在一组
 * @author zengzhaozheng
 */
public class DefinedGroupSort extends WritableComparator{
   // private static final Logger logger = LoggerFactory.getLogger(DefinedGroupSort.class);
    public DefinedGroupSort() {
        super(CombinationKey.class,true);
    }
    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        //("-------enter DefinedGroupSort flag-------");
        CombinationKey ck1 = (CombinationKey)a;
        CombinationKey ck2 = (CombinationKey)b;
        //("-------Grouping result:"+ck1.getFirstKey().
         //       compareTo(ck2.getFirstKey())+"-------");
       // ("-------out DefinedGroupSort flag-------");
        return ck1.getFirstKey().compareTo(ck2.getFirstKey());
    }
}
