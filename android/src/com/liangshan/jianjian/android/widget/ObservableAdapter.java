/**
 * Copyright 2011
 */

package com.liangshan.jianjian.android.widget;


/**
 * Interface that our adapters can implement to release any observers they
 * may have registered with remote resources manager. Most of the adapters
 * register an observer in their constructor, but there is was no appropriate
 * place to release them. Parent activities can call this method in their
 * onPause(isFinishing()) block to properly release the observers.
 * 
 * If the observers are not released, it will cause a memory leak.
 * 
 */
public interface ObservableAdapter {
    public void removeObserver();
}
