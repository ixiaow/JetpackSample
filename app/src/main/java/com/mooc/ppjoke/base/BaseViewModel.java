package com.mooc.ppjoke.base;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel {
    protected LifecycleOwner viewLifecycleOwner;

    public void setOwner(LifecycleOwner owner) {
        this.viewLifecycleOwner = owner;
    }
}
