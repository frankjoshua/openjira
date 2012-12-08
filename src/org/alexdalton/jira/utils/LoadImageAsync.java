/*******************************************************************************
 * Copyright 2012 Alexandre d'Alton
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.alexdalton.jira.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.alexdalton.jira.JiraApp;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class LoadImageAsync {
    private final static String LOGTAG = "LoadImageAsync";
    static final int THREAD_COUNT = 2;
    public static ImageDownloader imageLoader;
    private static ConcurrentHashMap<String, LoadBitmapListener> callbacks = new ConcurrentHashMap<String, LoadBitmapListener>();
    private static ConcurrentHashMap<String, WeakReference<Bitmap>> cachedBitmaps = new ConcurrentHashMap<String, WeakReference<Bitmap>>();
    private static ConcurrentHashMap<ImageView, String> viewUrl = new ConcurrentHashMap<ImageView, String>();


    public static class ImageDownloader {
        private final ExecutorService pool;
        Context context;
        String cookie;

        class CustomThreadFactory implements ThreadFactory {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                return t;
            }
        }

        public ImageDownloader(Context ctx) {
            pool = Executors.newFixedThreadPool(THREAD_COUNT, new CustomThreadFactory());
            context = ctx;
        }

        public void loadImage(final String tag, final String id, final String url) {
            pool.submit(new Runnable() {
                public void run() {
                    String fileName = tag + "_" + id + ".png";
                    FileOutputStream fos = null;
                    try {
                        File fDir = context.getFileStreamPath(tag);
                        String sDir = fDir.getAbsolutePath();
                        File file = new File(sDir + "/" + id + ".png");
                        if (file != null) {
                            fos = new FileOutputStream(file);
                            // Log.i(LOGTAG, "Getting image id " + tag + "_" +
                            // id + " from : " + url);
                            HttpClient httpClient = null;
                            if (url.startsWith("https"))
                                httpClient = new ConnectionClient(null, 443);
                            else
                                httpClient = new ConnectionClient(null);
                            Log.i(LOGTAG, "Url: " + url);
                            HttpGet get = new HttpGet(url);
                            get.addHeader("Cookie", cookie);
                            HttpResponse resp;
                            int read;
                            resp = httpClient.execute(get);
                            InputStream is = resp.getEntity().getContent();
                            byte[] buf = new byte[4096];
                            while ((read = is.read(buf)) != -1) {
                                fos.write(buf, 0, read);
                            }
                            fos.close();
                            LoadBitmapListener cb = callbacks.remove(fileName);
                            if (cb != null) {
                                FileInputStream fis = new FileInputStream(file);
                                Bitmap bm = BitmapFactory.decodeStream(fis);
                                cb.bitmapLoaded(bm, null);
                            }
                        }
                    } catch (Throwable e) {
                        Log.v(LOGTAG, "Got an error loading: " + id + "url: " + url + " exception: " + e);
                        e.printStackTrace();
                        context.deleteFile(fileName);
                    }
                }
            });
        }

        public void loadImageHttp(final String url, final ImageView v, final LoadBitmapListener cb, final Activity activity) {
            viewUrl.put(v, url);
            pool.submit(new Runnable() {
                public void run() {
                    try {
                        HttpClient httpClient = null;
                        if (url.startsWith("https"))
                            httpClient = new ConnectionClient(null, 443);
                        else
                            httpClient = new ConnectionClient(null);
                        HttpGet get = new HttpGet(url);
                        get.addHeader("Cookie", cookie);
                        HttpResponse resp;
                        int read;
                        resp = httpClient.execute(get);
                        int errCode = resp.getStatusLine().getStatusCode();
                        if (errCode != 200) {
                            viewUrl.remove(v);
                            return;
                        }
                        InputStream is = resp.getEntity().getContent();
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        // Log.v(LOGTAG, " url= " + url + " status = " + errCode
                        // + " len = "
                        // + resp.getEntity().getContentLength() + " is= " + is
                        // + " bm = " + bm);
                        WeakReference<Bitmap> ref = new WeakReference<Bitmap>(bm);
                        cachedBitmaps.put(url, ref);
                        saveImageToFile(url, bm);
                        String purl = viewUrl.remove(v);
                        if (purl != null && purl.equals(url) && cb != null) {
                            if (activity != null) {
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        cb.bitmapLoaded(bm, v);
                                    }
                                });
                            } else {
                                cb.bitmapLoaded(bm, v);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        Log.v(LOGTAG, "Got an error loading url: " + url + " for view: " + v + " exception: " + e);
                    }
                }
            });
        }

        public void post(Runnable runnable) {
            pool.submit(runnable);
        }

        public Bitmap getFromHttp(String url) {
            try {
                Bitmap bm = null;
                HttpClient httpClient = null;
                if (url.startsWith("https"))
                    httpClient = new ConnectionClient(null, 443);
                else
                    httpClient = new ConnectionClient(null);
                HttpGet get = new HttpGet(url);
                HttpResponse resp = httpClient.execute(get);
                InputStream is = resp.getEntity().getContent();
                bm = BitmapFactory.decodeStream(is);
                is.close();
                return bm;
            } catch (Exception e) {
                return null;
            }
        }

        public void getBitmapAsync(final String url, final LoadBitmapListener cb) {
            pool.submit(new Runnable() {
                public void run() {
                    Log.v(LOGTAG, "get " + url);
                    Bitmap b = getFromHttp(url);
                    Log.v(LOGTAG, "got " + b);
                    if (cb != null)
                        cb.bitmapLoaded(b, null);
                }
            });

        }
    }

    public static boolean imageExists(Context ctx, String tag, String uuid) {
        // TODO Auto-generated method stub
        String fileName = tag + "_" + uuid + ".png";
        FileInputStream fis = null;

        File fDir = ctx.getFileStreamPath(tag);
        String sDir = fDir.getAbsolutePath();
        File file = new File(sDir + "/" + uuid + ".png");

        try {
            fis = new FileInputStream(file);
            fis.close();
        } catch (FileNotFoundException e1) {
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public static void loadImage(final Context ctx, final String tag, final String id, final String url) {
        if (imageLoader == null) {
            imageLoader = new ImageDownloader(JiraApp.get());
        }
        imageLoader.loadImage(tag, id, url);
    }

    public static Bitmap getFromHttp(String url) {
        if (imageLoader == null) {
            imageLoader = new ImageDownloader(JiraApp.get());
        }
        return imageLoader.getFromHttp(url);
    }

    public interface LoadBitmapListener {
        public void bitmapLoaded(Bitmap b, View v);
    }

    public static void getFromHttpAsync(final String url, final LoadBitmapListener cb) {
        if (imageLoader == null) {
            imageLoader = new ImageDownloader(JiraApp.get());
        }

        imageLoader.getBitmapAsync(url, cb);
    }

    public static void cancelCallback(String filename) {
        callbacks.remove(filename);
    }

    public static void addCallback(String filename, LoadBitmapListener callback) {
        callbacks.put(filename, callback);
    }

    public static void saveImageToFile(String url, Bitmap bm) {
        try {
            File root = Environment.getExternalStorageDirectory();
            if (root.canWrite()) {
                File dir = new File(root, "openjiracache");
                dir.mkdir();
                File file = new File(dir, url.replace("/", "_").replace(":", "-").replace("?", "_").replace("=", "-"));

                FileOutputStream os = new FileOutputStream(file);
                bm.compress(CompressFormat.PNG, 5, os);
                // Log.v(LOGTAG, "Saved bitmap in " + file.getAbsolutePath());
            }
        } catch (Throwable e) {
            Log.e(LOGTAG, "Could not write file " + e.getMessage());
        }
    }

    public static Bitmap getImageFromFile(String url) {
        try {
            File root = Environment.getExternalStorageDirectory();
            if (root.canWrite()) {
                File dir = new File(root, "openjiracache");
                dir.mkdir();
                File file = new File(dir, url.replace("/", "_").replace(":", "-").replace("?", "_").replace("=", "-"));
                if (file.exists() && file.length() > 0) {
                    FileInputStream is = new FileInputStream(file);
                    Bitmap bm = BitmapFactory.decodeStream(is);
                    // Log.v(LOGTAG, "Loaded file from " +
                    // file.getAbsolutePath());
                    WeakReference<Bitmap> ref = new WeakReference<Bitmap>(bm);
                    cachedBitmaps.put(url, ref);
                    return bm;
                }
            }
        } catch (Throwable e) {
            Log.e(LOGTAG, "Could not read file " + e.getMessage());
        }
        return null;
    }


    public static void setImageViewAsync(String url, ImageView v, int defaultImage, LoadBitmapListener callback, Activity activity) {
        WeakReference<Bitmap> ref = cachedBitmaps.get(url);
        // Log.v(LOGTAG, "Cache size: " + cachedBitmaps.size());
        Bitmap bitmap = (ref != null) ? ref.get() : null;
        if (bitmap != null) {
            callback.bitmapLoaded(bitmap, v);
            return;
        }
        {
            // Log.v("Utils.setImageViewAsync", "Will load " + url);
            v.setImageResource(defaultImage);
            bitmap = getImageFromFile(url);

            if (bitmap != null) {
                callback.bitmapLoaded(bitmap, v);
            } else {
                if (imageLoader == null) {
                    imageLoader = new ImageDownloader(JiraApp.get());
                }
                if (url != null)
                    imageLoader.loadImageHttp(url, v, callback, activity);
            }
        }
    }

    public static FileInputStream openStream(Context context, String tag, String uuid) throws Exception {
        File fDir = context.getFileStreamPath(tag);
        String sDir = fDir.getAbsolutePath();
        File file = new File(sDir + "/" + uuid + ".png");
        FileInputStream is = new FileInputStream(file);
        return is;
    }

    public static void postRunnable(Runnable r) {
        if (imageLoader == null) {
            imageLoader = new ImageDownloader(JiraApp.get());
        }
        imageLoader.post(r);
    }

}
