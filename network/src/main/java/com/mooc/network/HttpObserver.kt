package com.mooc.network

import androidx.lifecycle.Observer

abstract class HttpObserver<T> : ApiResponseToken<T>(), Observer<T>