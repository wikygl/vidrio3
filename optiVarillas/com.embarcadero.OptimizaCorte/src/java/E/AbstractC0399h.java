package e;

import android.app.LocaleManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import e.AbstractC0399h;
import e.v;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.Executor;
import r.C0775d;

/* renamed from: e.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class AbstractC0399h {

    /* renamed from: j  reason: collision with root package name */
    public static final c f3182j = new c(new Object());

    /* renamed from: k  reason: collision with root package name */
    public static final int f3183k = -100;

    /* renamed from: l  reason: collision with root package name */
    public static I.g f3184l = null;

    /* renamed from: m  reason: collision with root package name */
    public static I.g f3185m = null;

    /* renamed from: n  reason: collision with root package name */
    public static Boolean f3186n = null;

    /* renamed from: o  reason: collision with root package name */
    public static boolean f3187o = false;

    /* renamed from: p  reason: collision with root package name */
    public static final C0775d<WeakReference<AbstractC0399h>> f3188p = new C0775d<>();

    /* renamed from: q  reason: collision with root package name */
    public static final Object f3189q = new Object();

    /* renamed from: r  reason: collision with root package name */
    public static final Object f3190r = new Object();

    /* renamed from: e.h$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a {
        public static LocaleList a(String str) {
            return LocaleList.forLanguageTags(str);
        }
    }

    /* renamed from: e.h$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class b {
        public static LocaleList a(Object obj) {
            return ((LocaleManager) obj).getApplicationLocales();
        }

        public static void b(Object obj, LocaleList localeList) {
            ((LocaleManager) obj).setApplicationLocales(localeList);
        }
    }

    /* renamed from: e.h$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class c implements Executor {

        /* renamed from: j  reason: collision with root package name */
        public final Object f3191j = new Object();

        /* renamed from: k  reason: collision with root package name */
        public final ArrayDeque f3192k = new ArrayDeque();

        /* renamed from: l  reason: collision with root package name */
        public final Executor f3193l;

        /* renamed from: m  reason: collision with root package name */
        public Runnable f3194m;

        public c(d dVar) {
            this.f3193l = dVar;
        }

        public final void a() {
            synchronized (this.f3191j) {
                try {
                    Runnable runnable = (Runnable) this.f3192k.poll();
                    this.f3194m = runnable;
                    if (runnable != null) {
                        this.f3193l.execute(runnable);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // java.util.concurrent.Executor
        public final void execute(final Runnable runnable) {
            synchronized (this.f3191j) {
                try {
                    this.f3192k.add(new Runnable() { // from class: e.i
                        @Override // java.lang.Runnable
                        public final void run() {
                            Runnable runnable2 = runnable;
                            AbstractC0399h.c cVar = AbstractC0399h.c.this;
                            cVar.getClass();
                            try {
                                runnable2.run();
                            } finally {
                                cVar.a();
                            }
                        }
                    });
                    if (this.f3194m == null) {
                        a();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    /* renamed from: e.h$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class d implements Executor {
        @Override // java.util.concurrent.Executor
        public final void execute(Runnable runnable) {
            new Thread(runnable).start();
        }
    }

    public static boolean k(Context context) {
        int i4;
        if (f3186n == null) {
            try {
                int i5 = v.f3302j;
                if (Build.VERSION.SDK_INT >= 24) {
                    i4 = v.a.a() | 128;
                } else {
                    i4 = 640;
                }
                Bundle bundle = context.getPackageManager().getServiceInfo(new ComponentName(context, v.class), i4).metaData;
                if (bundle != null) {
                    f3186n = Boolean.valueOf(bundle.getBoolean("autoStoreLocales"));
                }
            } catch (PackageManager.NameNotFoundException unused) {
                Log.d("AppCompatDelegate", "Checking for metadata for AppLocalesMetadataHolderService : Service not found");
                f3186n = Boolean.FALSE;
            }
        }
        return f3186n.booleanValue();
    }

    public static void r(AbstractC0399h abstractC0399h) {
        synchronized (f3189q) {
            try {
                Iterator<WeakReference<AbstractC0399h>> it = f3188p.iterator();
                while (it.hasNext()) {
                    AbstractC0399h abstractC0399h2 = it.next().get();
                    if (abstractC0399h2 == abstractC0399h || abstractC0399h2 == null) {
                        it.remove();
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public abstract void c(View view, ViewGroup.LayoutParams layoutParams);

    public abstract <T extends View> T e(int i4);

    public Context f() {
        return null;
    }

    public int g() {
        return -100;
    }

    public abstract MenuInflater h();

    public abstract void i();

    public abstract void j();

    public abstract void l();

    public abstract void m();

    public abstract void n();

    public abstract void o();

    public abstract void p();

    public abstract void q();

    public abstract boolean s(int i4);

    public abstract void t(int i4);

    public abstract void u(View view);

    public abstract void v(View view, ViewGroup.LayoutParams layoutParams);

    public abstract void w(CharSequence charSequence);

    public Context d(Context context) {
        return context;
    }
}
