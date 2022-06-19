package com.istiaksaif.highlymotavated.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.istiaksaif.highlymotavated.Model.SliderImageModel;
import com.istiaksaif.highlymotavated.R;

import java.util.List;

public class SliderAdapterimage extends PagerAdapter {
    private List<SliderImageModel> sliderList;
    private Context mContext;
    LayoutInflater layoutInflater;


    public SliderAdapterimage(Context mContext, List<SliderImageModel> sliderList) {
        this.mContext = mContext;
        this.sliderList = sliderList;
//        layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount(){
        return sliderList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view== object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.slider,container,false);
        ImageView imageView = (ImageView) view.findViewById(R.id.productimage);
        Glide.with(container.getContext()).load(sliderList.get(position).getImageUrl())
                .placeholder(R.drawable.dropdown).into(imageView);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView((View) object);
    }
}
