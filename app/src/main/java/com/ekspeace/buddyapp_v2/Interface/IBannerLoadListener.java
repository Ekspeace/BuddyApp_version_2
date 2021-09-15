package com.ekspeace.buddyapp_v2.Interface;

import com.ekspeace.buddyapp_v2.Model.Banner;

import java.util.List;


public interface IBannerLoadListener {
    void onBannerLoadSuccess(List<Banner> banners);
    void onBannerLoadFailed(String message);
}
