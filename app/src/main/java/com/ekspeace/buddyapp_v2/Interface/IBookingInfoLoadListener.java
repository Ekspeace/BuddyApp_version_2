package com.ekspeace.buddyapp_v2.Interface;
import com.ekspeace.buddyapp_v2.Model.BookingInformation;

import java.util.List;

public interface IBookingInfoLoadListener {
    void onBookingInfoLoadSuccess(List<BookingInformation> bookingInformation);
    void onBookingInfoLoadFailed(String message);
}
