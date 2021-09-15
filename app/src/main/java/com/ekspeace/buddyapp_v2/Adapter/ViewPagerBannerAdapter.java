package com.ekspeace.buddyapp_v2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ekspeace.buddyapp_v2.Model.Banner;
import com.ekspeace.buddyapp_v2.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ViewPagerBannerAdapter extends PagerAdapter {
    private List<Banner> banners;
    private Context context;

    public ViewPagerBannerAdapter(List<Banner> banners, Context context) {
        this.banners = banners;
        this.context = context;
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view.equals(object);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_view_banner,
                container,
                false
        );

        ImageView bannerThumb = view.findViewById(R.id.banner_image);

        String strBannerThumb = banners.get(position).getImage();
        Picasso.get().load(strBannerThumb).into(bannerThumb);

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
