package com.flu.concurrent.stastic;


import com.alibaba.fastjson.JSONObject;
import com.flu.concurrent.web.WebStaInfoUpdateListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统计数据中心
 * Created by float.lu on 7/9/16.
 */
public class StaInfoCenter {

    private static StaInfoCenter staInfoCenter = new StaInfoCenter();

    private StaInfoUpdateListener staInfoUpdateListener = new WebStaInfoUpdateListener();

    private ConfigurationBean configurationBean = new ConfigurationBean();
    /**
     * 线程池的统计信息
     */
    private Map<String, List<StaItem>> poolEffInfoList = new ConcurrentHashMap<String, List<StaItem>>();

    private StaInfoCenter() {}

    public static StaInfoCenter mySelf() {
        return staInfoCenter;
    }

    public void addEffNode(String poolName, Node node) {
        List<StaItem> staItems = poolEffInfoList.get(poolName);
        if (staItems == null) {
            staItems = new ArrayList<StaItem>();
            StaItem staItem = new StaItem();
            staItem.setType("area");
            staItem.setName("效率");
            staItem.setInnerType("EFF");
            staItem.setData(new FixSizeLinkedList(configurationBean.getCacheCount()));
            staItems.add(staItem);
            poolEffInfoList.put(poolName, staItems);
        }
        StaItem staItem = staItems.get(0);
        List<Object> nodeVal = makeData(node);
        staItem.getData().add(nodeVal);
        refresh();
    }

    private List<Object> makeData(Node node) {
        List<Object> nodeVal = new ArrayList<Object>();
        nodeVal.add(node.getDate());
        nodeVal.add(node.getValue());
        return nodeVal;
    }

    private void refresh() {
        staInfoUpdateListener.updated(this);
    }


    public Map<String, List<StaItem>> getSeries() {
        return poolEffInfoList;
    }


    public Map<String, List<StaItem>> getSeriesFrom(long date) {
        Map<String, List<StaItem>> stringListMap = new HashMap<String, List<StaItem>>();
        for (Map.Entry<String, List<StaItem>> entry : poolEffInfoList.entrySet()) {
            if (stringListMap.get(entry.getKey()) == null) {
                stringListMap.put(entry.getKey(), new ArrayList<StaItem>());
            }
            List<StaItem> staItems = entry.getValue();
            List<StaItem> staItemLists = new ArrayList<StaItem>();
            for (StaItem staItem : staItems) {
                boolean found = false;
                StaItem tmpStaItem = new StaItem();
                tmpStaItem.setInnerType(staItem.getInnerType());
                tmpStaItem.setName(staItem.getName());
                tmpStaItem.setType(staItem.getType());
                tmpStaItem.setData(new FixSizeLinkedList(configurationBean.getCacheCount()));
                Iterator iterator = new ArrayList(staItem.getData()).iterator();
                while (iterator.hasNext()) {
                    List<Object> node = (List)iterator.next();
                    if ((node.get(0)).equals(date)) {
                        found = true;
                        continue;
                    }
                    if (found) {
                        tmpStaItem.getData().add(node);
                    }
                }
                staItemLists.add(tmpStaItem);
            }
            stringListMap.put(entry.getKey(), staItemLists);
        }
        return stringListMap;
    }

    public ConfigurationBean getConfiguration() {
        return configurationBean;
    }

    public JSONObject getCfgForWeb() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("poolKeys", new ArrayList<String>(poolEffInfoList.keySet()));
        jsonObject.put("interval",configurationBean.getInterval());
        jsonObject.put("autoRefresh", configurationBean.isAutoRefresh());
        return jsonObject;
    }

    private static class FixSizeLinkedList extends LinkedList {
        private long fixSize;

        public FixSizeLinkedList(long fixSize) {
            this.fixSize = fixSize;
        }

        @Override
        public boolean add(Object o) {
            if (size() >= fixSize) {
                removeFirst();
            }
            return super.add(o);
        }

    }
}
