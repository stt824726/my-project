package com.stt.workflow.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 工艺流责任链的管理器实现，单例模式
 * @author: shaott
 * @create: 2024-05-11 11:22
 * @Version 1.0
 **/
public enum DefaultCraftEngineManager implements CraftEngineManager {

    //职贡链管理器对象
    INSTANCE;
    //责任链编号，执行时创建的责任链会存入map中
    private final ConcurrentHashMap<String, HandlerChain> chainMap = new ConcurrentHashMap<>();

    @Override
    public void addHandlerChain(HandlerChain chain) {
        this.chainMap.putIfAbsent(chain.no(), chain);
    }

    @Override
    public HandlerChain getHandlerChain(String chainNo) {
        return this.chainMap.get(chainNo);
    }

    @Override
    public Map<String, HandlerChain> getHandlerChains() {
        return this.chainMap;
    }

    @Override
    public void remove(String chainNo) {
        this.chainMap.remove(chainNo);
    }

    @Override
    public void stop(String chainNo) {
        HandlerChain chain = this.chainMap.remove(chainNo);
        if (chain != null) {
            chain.stop();
        }
    }

}
