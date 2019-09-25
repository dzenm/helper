package com.dzenm.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Integer> mPosition;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");

        mPosition = new MediatorLiveData<>();
        mPosition.setValue(1);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<Integer> getmPosition() {
        return mPosition;
    }
}