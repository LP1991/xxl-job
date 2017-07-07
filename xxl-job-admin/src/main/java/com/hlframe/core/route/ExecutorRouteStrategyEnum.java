package com.hlframe.core.route;

import com.hlframe.core.route.strategy.*;

/**
 * Created by xuxueli on 17/3/10.
 */
public enum ExecutorRouteStrategyEnum {

    FIRST("第一个", new ExecutorRouteFirst()),
    LAST("最后一个", new ExecutorRouteLast()),
    ROUND("轮询", new ExecutorRouteRound()),
    RANDOM("随机", new ExecutorRouteRandom()),
    CONSISTENT_HASH("一致性HASH", new ExecutorRouteConsistentHash()),
    LEAST_FREQUENTLY_USED("最不经常使用", new ExecutorRouteLFU()),
    LEAST_RECENTLY_USED("最近最久未使用", new ExecutorRouteLRU()),
    FAILOVER("故障转移", null);

    ExecutorRouteStrategyEnum(String title, ExecutorRouter router) {
        this.title = title;
        this.router = router;
    }

    private String title;
    private ExecutorRouter router;

    public String getTitle() {
        return title;
    }
    public ExecutorRouter getRouter() {
        return router;
    }

    public static ExecutorRouteStrategyEnum match(String name, ExecutorRouteStrategyEnum defaultItem){
        if (name != null) {
            for (ExecutorRouteStrategyEnum item: ExecutorRouteStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }

    public static ExecutorRouteStrategyEnum match(int name){
        switch (name){
            case 1:
                return ExecutorRouteStrategyEnum.FIRST;
            case 2:
                return ExecutorRouteStrategyEnum.LAST;
            case 3:
                return ExecutorRouteStrategyEnum.ROUND;
            case 4:
                return ExecutorRouteStrategyEnum.RANDOM;
            case 5:
                return ExecutorRouteStrategyEnum.CONSISTENT_HASH;
            case 6:
                return ExecutorRouteStrategyEnum.LEAST_FREQUENTLY_USED;
            case 7:
                return ExecutorRouteStrategyEnum.LEAST_RECENTLY_USED;
            case 8:
                return ExecutorRouteStrategyEnum.FAILOVER;
                default:
                    return ExecutorRouteStrategyEnum.FAILOVER;
        }
    }

}
