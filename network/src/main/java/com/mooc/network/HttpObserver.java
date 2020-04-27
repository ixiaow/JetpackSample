package com.mooc.network;

import androidx.lifecycle.Observer;

public abstract class HttpObserver<T> extends ApiResponseToken<T> implements Observer<T> {
}
