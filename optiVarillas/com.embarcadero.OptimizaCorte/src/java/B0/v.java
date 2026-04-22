package B0;

import android.app.Application;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import i2.Z;
import j$.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;
import org.chromium.support_lib_boundary.StaticsBoundaryInterface;
import org.chromium.support_lib_boundary.WebViewProviderBoundaryInterface;
import org.chromium.support_lib_boundary.WebViewProviderFactoryBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class v implements u, com.google.gson.internal.i, Z {

    /* renamed from: j  reason: collision with root package name */
    public Object f293j;

    public /* synthetic */ v(Object obj) {
        this.f293j = obj;
    }

    @Override // i2.Z
    public Object a() {
        return new ContextWrapper((Application) ((Z) this.f293j).a());
    }

    @Override // B0.u
    public String[] b() {
        return ((WebViewProviderFactoryBoundaryInterface) this.f293j).getSupportedFeatures();
    }

    @Override // B0.u
    public WebViewProviderBoundaryInterface createWebView(WebView webView) {
        return (WebViewProviderBoundaryInterface) H3.a.a(WebViewProviderBoundaryInterface.class, ((WebViewProviderFactoryBoundaryInterface) this.f293j).createWebView(webView));
    }

    @Override // B0.u
    public StaticsBoundaryInterface getStatics() {
        return (StaticsBoundaryInterface) H3.a.a(StaticsBoundaryInterface.class, ((WebViewProviderFactoryBoundaryInterface) this.f293j).getStatics());
    }

    public Object k() {
        Class cls = (Class) this.f293j;
        try {
            return com.google.gson.internal.o.a.a(cls);
        } catch (Exception e4) {
            throw new RuntimeException("Unable to create instance of " + cls + ". Registering an InstanceCreator or a TypeAdapter for this type, or adding a no-args constructor may fix this problem.", e4);
        }
    }

    public v(int i4) {
        Handler handler;
        Handler handler2;
        switch (i4) {
            case 2:
                this.f293j = new ConcurrentHashMap();
                new AtomicInteger(0);
                return;
            default:
                Looper mainLooper = Looper.getMainLooper();
                if (Build.VERSION.SDK_INT >= 28) {
                    handler2 = I.f.a(mainLooper);
                } else {
                    try {
                        handler = (Handler) Handler.class.getDeclaredConstructor(Looper.class, Handler.Callback.class, Boolean.TYPE).newInstance(mainLooper, null, Boolean.TRUE);
                    } catch (IllegalAccessException e4) {
                        e = e4;
                        Log.w("HandlerCompat", "Unable to invoke Handler(Looper, Callback, boolean) constructor", e);
                        handler = new Handler(mainLooper);
                        handler2 = handler;
                        this.f293j = handler2;
                        return;
                    } catch (InstantiationException e5) {
                        e = e5;
                        Log.w("HandlerCompat", "Unable to invoke Handler(Looper, Callback, boolean) constructor", e);
                        handler = new Handler(mainLooper);
                        handler2 = handler;
                        this.f293j = handler2;
                        return;
                    } catch (NoSuchMethodException e6) {
                        e = e6;
                        Log.w("HandlerCompat", "Unable to invoke Handler(Looper, Callback, boolean) constructor", e);
                        handler = new Handler(mainLooper);
                        handler2 = handler;
                        this.f293j = handler2;
                        return;
                    } catch (InvocationTargetException e7) {
                        Throwable cause = e7.getCause();
                        if (!(cause instanceof RuntimeException)) {
                            if (cause instanceof Error) {
                                throw ((Error) cause);
                            }
                            throw new RuntimeException(cause);
                        }
                        throw ((RuntimeException) cause);
                    }
                    handler2 = handler;
                }
                this.f293j = handler2;
                return;
        }
    }
}
