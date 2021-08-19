package com.applutions.t2y.data.response.filter;

import java.util.ArrayList;

public class FilterResponse {
    Boolean success;
    String message;
    FilterObj filtersObj;

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public FilterObj getFiltersObj() {
        return filtersObj;
    }
}

