package com.flu.concurrent.stastic;

import lombok.Data;


/**
 * Created by float.lu on 7/11/16.
 */
@Data
public class ConfigurationBean {
    /**
     * 统计时间间隔,单位毫秒
     */
    private long interval;
    /**
     * 内存缓存次数
     */
    private long cacheCount;
}
