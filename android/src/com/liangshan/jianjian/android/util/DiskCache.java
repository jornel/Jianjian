/**
 * Copyright 2011 Joe Chen
 */

package com.liangshan.jianjian.android.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Joe Chen
 */
public interface DiskCache {

    public boolean exists(String key);

    public File getFile(String key);

    public InputStream getInputStream(String key) throws IOException;

    public void store(String key, InputStream is);

    public void invalidate(String key);

    public void cleanup();

    public void clear();

}
