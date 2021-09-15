package com.ekspeace.buddyapp_v2.Interface;


import com.ekspeace.buddyapp_v2.Model.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlots);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();
}
