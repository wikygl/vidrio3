package B;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class i {

    /* renamed from: a  reason: collision with root package name */
    public static final Class<?> f226a;

    /* renamed from: b  reason: collision with root package name */
    public static final Field f227b;

    /* renamed from: c  reason: collision with root package name */
    public static final Field f228c;

    /* renamed from: d  reason: collision with root package name */
    public static final Method f229d;

    /* renamed from: e  reason: collision with root package name */
    public static final Method f230e;
    public static final Method f;

    /* renamed from: g  reason: collision with root package name */
    public static final Handler f231g = new Handler(Looper.getMainLooper());

    /* JADX WARN: Can't wrap try/catch for region: R(21:1|(2:2|3)|4|5|6|7|8|9|10|(12:33|34|13|(6:29|30|16|(3:24|25|26)|20|21)|15|16|(1:18)|24|25|26|20|21)|12|13|(0)|15|16|(0)|24|25|26|20|21) */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0074  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x005b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    static {
        /*
            r0 = 3
            java.lang.Class<android.os.IBinder> r1 = android.os.IBinder.class
            r2 = 2
            r3 = 0
            r4 = 1
            java.lang.Class<android.app.Activity> r5 = android.app.Activity.class
            android.os.Handler r6 = new android.os.Handler
            android.os.Looper r7 = android.os.Looper.getMainLooper()
            r6.<init>(r7)
            B.i.f231g = r6
            r6 = 0
            java.lang.String r7 = "android.app.ActivityThread"
            java.lang.Class r7 = java.lang.Class.forName(r7)     // Catch: java.lang.Throwable -> L1b
            goto L1c
        L1b:
            r7 = r6
        L1c:
            B.i.f226a = r7
            java.lang.String r7 = "mMainThread"
            java.lang.reflect.Field r7 = r5.getDeclaredField(r7)     // Catch: java.lang.Throwable -> L28
            r7.setAccessible(r4)     // Catch: java.lang.Throwable -> L28
            goto L29
        L28:
            r7 = r6
        L29:
            B.i.f227b = r7
            java.lang.String r7 = "mToken"
            java.lang.reflect.Field r5 = r5.getDeclaredField(r7)     // Catch: java.lang.Throwable -> L35
            r5.setAccessible(r4)     // Catch: java.lang.Throwable -> L35
            goto L36
        L35:
            r5 = r6
        L36:
            B.i.f228c = r5
            java.lang.Class<?> r5 = B.i.f226a
            java.lang.String r7 = "performStopActivity"
            if (r5 != 0) goto L40
        L3e:
            r5 = r6
            goto L53
        L40:
            java.lang.Class[] r8 = new java.lang.Class[r0]     // Catch: java.lang.Throwable -> L3e
            r8[r3] = r1     // Catch: java.lang.Throwable -> L3e
            java.lang.Class r9 = java.lang.Boolean.TYPE     // Catch: java.lang.Throwable -> L3e
            r8[r4] = r9     // Catch: java.lang.Throwable -> L3e
            java.lang.Class<java.lang.String> r9 = java.lang.String.class
            r8[r2] = r9     // Catch: java.lang.Throwable -> L3e
            java.lang.reflect.Method r5 = r5.getDeclaredMethod(r7, r8)     // Catch: java.lang.Throwable -> L3e
            r5.setAccessible(r4)     // Catch: java.lang.Throwable -> L3e
        L53:
            B.i.f229d = r5
            java.lang.Class<?> r5 = B.i.f226a
            if (r5 != 0) goto L5b
        L59:
            r5 = r6
            goto L6a
        L5b:
            java.lang.Class[] r8 = new java.lang.Class[r2]     // Catch: java.lang.Throwable -> L59
            r8[r3] = r1     // Catch: java.lang.Throwable -> L59
            java.lang.Class r9 = java.lang.Boolean.TYPE     // Catch: java.lang.Throwable -> L59
            r8[r4] = r9     // Catch: java.lang.Throwable -> L59
            java.lang.reflect.Method r5 = r5.getDeclaredMethod(r7, r8)     // Catch: java.lang.Throwable -> L59
            r5.setAccessible(r4)     // Catch: java.lang.Throwable -> L59
        L6a:
            B.i.f230e = r5
            java.lang.Class<?> r5 = B.i.f226a
            int r7 = android.os.Build.VERSION.SDK_INT
            r8 = 26
            if (r7 == r8) goto L78
            r8 = 27
            if (r7 != r8) goto La9
        L78:
            if (r5 != 0) goto L7b
            goto La9
        L7b:
            java.lang.String r7 = "requestRelaunchActivity"
            r8 = 9
            java.lang.Class[] r8 = new java.lang.Class[r8]     // Catch: java.lang.Throwable -> La9
            r8[r3] = r1     // Catch: java.lang.Throwable -> La9
            java.lang.Class<java.util.List> r1 = java.util.List.class
            r8[r4] = r1     // Catch: java.lang.Throwable -> La9
            r8[r2] = r1     // Catch: java.lang.Throwable -> La9
            java.lang.Class r1 = java.lang.Integer.TYPE     // Catch: java.lang.Throwable -> La9
            r8[r0] = r1     // Catch: java.lang.Throwable -> La9
            java.lang.Class r0 = java.lang.Boolean.TYPE     // Catch: java.lang.Throwable -> La9
            r1 = 4
            r8[r1] = r0     // Catch: java.lang.Throwable -> La9
            java.lang.Class<android.content.res.Configuration> r1 = android.content.res.Configuration.class
            r2 = 5
            r8[r2] = r1     // Catch: java.lang.Throwable -> La9
            r2 = 6
            r8[r2] = r1     // Catch: java.lang.Throwable -> La9
            r1 = 7
            r8[r1] = r0     // Catch: java.lang.Throwable -> La9
            r1 = 8
            r8[r1] = r0     // Catch: java.lang.Throwable -> La9
            java.lang.reflect.Method r0 = r5.getDeclaredMethod(r7, r8)     // Catch: java.lang.Throwable -> La9
            r0.setAccessible(r4)     // Catch: java.lang.Throwable -> La9
            r6 = r0
        La9:
            B.i.f = r6
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: B.i.<clinit>():void");
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static final class a implements Application.ActivityLifecycleCallbacks {

        /* renamed from: j  reason: collision with root package name */
        public Object f232j;

        /* renamed from: k  reason: collision with root package name */
        public Activity f233k;

        /* renamed from: l  reason: collision with root package name */
        public final int f234l;

        /* renamed from: m  reason: collision with root package name */
        public boolean f235m = false;

        /* renamed from: n  reason: collision with root package name */
        public boolean f236n = false;

        /* renamed from: o  reason: collision with root package name */
        public boolean f237o = false;

        public a(Activity activity) {
            this.f233k = activity;
            this.f234l = activity.hashCode();
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public final void onActivityDestroyed(Activity activity) {
            if (this.f233k == activity) {
                this.f233k = null;
                this.f236n = true;
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public final void onActivityPaused(Activity activity) {
            if (this.f236n && !this.f237o && !this.f235m) {
                Object obj = this.f232j;
                try {
                    Object obj2 = i.f228c.get(activity);
                    if (obj2 == obj && activity.hashCode() == this.f234l) {
                        i.f231g.postAtFrontOfQueue(new h(i.f227b.get(activity), 0, obj2));
                        this.f237o = true;
                        this.f232j = null;
                    }
                } catch (Throwable th) {
                    Log.e("ActivityRecreator", "Exception while fetching field values", th);
                }
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public final void onActivityStarted(Activity activity) {
            if (this.f233k == activity) {
                this.f235m = true;
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public final void onActivityResumed(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public final void onActivityStopped(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public final void onActivityCreated(Activity activity, Bundle bundle) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }
    }
}
