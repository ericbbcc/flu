package com.flu.concurrent.stastic;

import lombok.Data;


/**
 * Created by float.lu on 7/11/16.
 */
@Data
public class ConfigurationBean {
    /**
     * 统计时间间隔,单位毫秒,默认一秒
     */
    private long interval = 1000;
    /**
     * 内存缓存次数,默认600
     */
    private long cacheCount = 600;
    /**
     * 是否自动刷新,默认为不自动刷新
     */
    private boolean autoRefresh = false;
}
