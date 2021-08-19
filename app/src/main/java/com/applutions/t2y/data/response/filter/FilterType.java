package com.applutions.t2y.data.response.filter;

public class FilterType {
    String name;
    String code;
    public int checked = 0;

    public FilterType(String name, String code, int checked) {
        this.name = name;
        this.code = code;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void resetChecked(){
        checked = 0;
    }
}
