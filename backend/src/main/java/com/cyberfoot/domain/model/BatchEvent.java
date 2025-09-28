package com.cyberfoot.domain.model;

public class BatchEvent {
    public enum Type {
        ROUND_START, MATCH_START, MATCH_EVENT, MATCH_END, ROUND_END, DONE
    }
    private int seq;
    private Type type;
    private Object data;

    public BatchEvent(int seq, Type type, Object data) {
        this.seq = seq;
        this.type = type;
        this.data = data;
    }
    public int getSeq() { return seq; }
    public Type getType() { return type; }
    public Object getData() { return data; }
    public void setSeq(int seq) { this.seq = seq; }
    public void setType(Type type) { this.type = type; }
    public void setData(Object data) { this.data = data; }
}
