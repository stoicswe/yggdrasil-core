package org.yggdrasil.node.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NodeConnectionHashMap<String, NodeConnection> implements Map<String, NodeConnection> {

    private Map<String, NodeConnection> map;
    private final int sizeLimit;

    public NodeConnectionHashMap(int sizeLimit) {
        map = new HashMap<>();
        this.sizeLimit = sizeLimit;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public NodeConnection get(Object key) {
        return map.get(key);
    }

    @Override
    public NodeConnection put(String key, NodeConnection value) {
        if(map.size() != sizeLimit) {
            return map.put(key, value);
        } else {
            throw new IndexOutOfBoundsException("Node connection pool is full.");
        }
    }

    @Override
    public NodeConnection remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends NodeConnection> m) {
        //does nothing.
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<NodeConnection> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, NodeConnection>> entrySet() {
        return map.entrySet();
    }

}
