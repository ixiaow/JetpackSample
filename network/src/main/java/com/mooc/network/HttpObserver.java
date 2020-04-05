package com.mooc.network;

import androidx.lifecycle.Observer;

import com.mooc.network.http.TypeToken;

public abstract class HttpObserver<T> extends TypeToken<T> implements Observer<T> {

}
