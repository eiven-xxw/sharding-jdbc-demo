package com.shardingjdbc.demo.sharding;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.SingleKeyDatabaseShardingAlgorithm;
import com.google.common.collect.Range;

import java.util.Collection;
import java.util.LinkedHashSet;

public class ModuloDatabaseShardingAlgorithm implements SingleKeyDatabaseShardingAlgorithm<Integer> {
	@Override
	public String doEqualSharding(Collection<String> availableTargetNames, ShardingValue<Integer> shardingValue) {
        //return availableTargetNames.isEmpty() ? null : availableTargetNames.iterator().next();
	    for (String each : availableTargetNames) {
            if (each.endsWith(shardingValue.getValue() % 2 + "")) {
                return each;
            }
        }
        throw new IllegalArgumentException();
	}

    public static void main(String[] args) {
        System.out.println(2%2);
        System.out.println("sharding_jdbc1".endsWith(9%2+""));
    }
	@Override
	public Collection<String> doInSharding(Collection<String> availableTargetNames,
			ShardingValue<Integer> shardingValue) {
		Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        for (Integer value : shardingValue.getValues()) {
            for (String targetName : availableTargetNames) {
                if (targetName.endsWith(value % 2 + "")) {
                    result.add(targetName);
                }
            }
        }
        return result;
	}

	@Override
	public Collection<String> doBetweenSharding(Collection<String> availableTargetNames,
			ShardingValue<Integer> shardingValue) {
		Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        Range<Integer> range = (Range<Integer>) shardingValue.getValueRange();
        for (Integer i = range.lowerEndpoint(); i <= range.upperEndpoint(); i++) {
            for (String each : availableTargetNames) {
                if (each.endsWith(i % 2 + "")) {
                    result.add(each);
                }
            }
        }
        return result;
	}


}
