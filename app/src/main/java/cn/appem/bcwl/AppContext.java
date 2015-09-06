package cn.appem.bcwl;

import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.easemob.chatuidemo.DemoApplication;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.wewe.android.BaseApplication;
import com.wewe.android.util.PathUtil;

import cn.appem.bcwl.util.UserPref;

public class AppContext extends DemoApplication {
    private static AppContext _context;

    @Override
    public void onCreate() {
        super.onCreate();
        _context = this;
        initImageLoader(this);
        initMapSdk();

//        FeedbackPush.getInstance(this).init(false);

        PathUtil.getInstance().initDirs("bcwl", getApplicationContext());
    }

    void initMapSdk() {
        try {
            if (!isEmulator(this))
                SDKInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AppContext getInstance() {
        return _context;
    }

    public static boolean isLogin() {
        return UserPref.getInstance(_context).isLogined();
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(PathUtil.getInstance().getImagePath()))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        L.writeDebugLogs(false);
        L.writeLogs(false);
    }
}
