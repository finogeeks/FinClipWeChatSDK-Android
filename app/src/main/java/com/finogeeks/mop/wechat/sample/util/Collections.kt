package com.finogeeks.mop.wechat.sample.util

import android.util.ArrayMap
import android.util.SparseArray

@Suppress("NOTHING_TO_INLINE")
inline operator fun <K, V> ArrayMap<K, V>.set(k: K, v: V) = put(k, v)

@Suppress("NOTHING_TO_INLINE")
inline operator fun <E> SparseArray<E>.set(k: Int, v: E?) = put(k, v)
