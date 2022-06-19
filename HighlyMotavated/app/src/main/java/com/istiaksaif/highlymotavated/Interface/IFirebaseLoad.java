package com.istiaksaif.highlymotavated.Interface;

import com.istiaksaif.highlymotavated.Model.SliderImageModel;

import java.util.List;

public interface IFirebaseLoad {
    void onFirebaseLoadSuccess(List<SliderImageModel> sliderList);
    void onFirebaseLoadFailed(String message);
}
