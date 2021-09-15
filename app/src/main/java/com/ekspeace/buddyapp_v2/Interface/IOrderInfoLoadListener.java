package com.ekspeace.buddyapp_v2.Interface;

import com.ekspeace.buddyapp_v2.Model.PickInformation;

import java.util.List;

public interface IOrderInfoLoadListener {
    void onOrderInfoLoadSuccess(List<PickInformation> pickInformation);
    void onOrderInfoLoadFailed(String message);
}
