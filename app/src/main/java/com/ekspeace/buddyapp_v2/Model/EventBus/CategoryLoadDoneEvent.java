package com.ekspeace.buddyapp_v2.Model.EventBus;

import com.ekspeace.buddyapp_v2.Model.Category;

import java.util.List;

public class CategoryLoadDoneEvent {
    private boolean isCategoryLoadDone;

    public CategoryLoadDoneEvent(boolean isCategoryLoadDone) {
        this.isCategoryLoadDone = isCategoryLoadDone;
    }

    public boolean isCategoryLoadDone() {
        return isCategoryLoadDone;
    }

    public void setCategoryLoadDone(boolean isCategoryLoadDone) {
        this.isCategoryLoadDone = isCategoryLoadDone;
    }
}

