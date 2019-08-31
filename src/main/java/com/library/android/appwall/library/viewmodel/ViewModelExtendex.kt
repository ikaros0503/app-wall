package com.library.android.appwall.library.viewmodel

import android.arch.lifecycle.ViewModel
import android.support.annotation.MainThread
import java.io.Closeable
import java.io.IOException

open class ExtendedViewModel : ViewModel() {

    private val bagOfTags: HashMap<String, Any> = hashMapOf()
    @Volatile
    private var mCleared = false

    @MainThread
    internal fun clear() {
        mCleared = true
        // Since clear() is final, this method is still called on mock objects
        // and in those cases, mBagOfTags is null. It'll always be empty though
        // because setTagIfAbsent and getTag are not final so we can skip
        // clearing it
        if (bagOfTags != null) {
            synchronized(bagOfTags) {
                for (value in bagOfTags.values) {
                    // see comment for the similar call in setTagIfAbsent
                    closeWithRuntimeException(value)
                }
            }
        }
        onCleared()
    }

    /**
     * Sets a tag associated with this viewmodel and a key.
     * If the given `newValue` is [Closeable],
     * it will be closed once [.clear].
     *
     *
     * If a value was already set for the given key, this calls do nothing and
     * returns currently associated value, the given `newValue` would be ignored
     *
     *
     * If the ViewModel was already cleared then close() would be called on the returned object if
     * it implements [Closeable]. The same object may receive multiple close calls, so method
     * should be idempotent.
     */
    internal fun <T> setTagIfAbsent(key: String, newValue: T): T {
        val previous: T?
        synchronized(bagOfTags) {
            previous = bagOfTags[key] as T?
            if (previous == null) {
                bagOfTags.put(key, newValue as Any)
            }
        }
        val result = previous ?: newValue
        if (mCleared) {
            // It is possible that we'll call close() multiple times on the same object, but
            // Closeable interface requires close method to be idempotent:
            // "if the stream is already closed then invoking this method has no effect." (c)
            closeWithRuntimeException(result as Any)
        }
        return result
    }

    /**
     * Returns the tag associated with this viewmodel and the specified key.
     */
    internal fun <T> getTag(key: String): T {
        synchronized(bagOfTags) {
            return bagOfTags[key] as T
        }
    }

    private fun closeWithRuntimeException(obj: Any) {
        if (obj is Closeable) {
            try {
                obj.close()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

        }
    }

}