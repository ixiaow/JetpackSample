package com.mooc.network

import android.annotation.SuppressLint
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class SimpleX509TrustManager : X509TrustManager {
    @SuppressLint("TrustAllX509TrustManager")
    @Throws(CertificateException::class)
    override fun checkClientTrusted(
        chain: Array<X509Certificate>,
        authType: String
    ) {
    }

    @SuppressLint("TrustAllX509TrustManager")
    @Throws(CertificateException::class)
    override fun checkServerTrusted(
        chain: Array<X509Certificate>,
        authType: String
    ) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

}