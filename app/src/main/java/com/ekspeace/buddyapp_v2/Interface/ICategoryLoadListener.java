package com.ekspeace.buddyapp_v2.Interface;


import com.ekspeace.buddyapp_v2.Model.Category;

import java.util.List;

public interface ICategoryLoadListener {
    void onCategoryLoadSuccess(List<Category> categoryList, List<String> stringList);
    void onCategoryLoadFailed(String message);
}
