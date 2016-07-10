package com.flu.concurrent.stastic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;


/**
 * Created by float.lu on 7/9/16.
 */
@Data
public class StaItem {
    private String type;
    private String name;
    private List<List<Object>> data;
    @JSONField(serialize = false)
    private String innerType;
}
