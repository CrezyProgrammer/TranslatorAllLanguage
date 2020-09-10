package com.allword.translation;

public class Dictionary {
    String source , result;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Dictionary(String source, String result) {
        this.source = source;
        this.result = result;
    }

    public Dictionary() {
    }

    public boolean isEmpty() {
        if(source==null)return  true;
        else return false;
    }
}
