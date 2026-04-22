package x1;

import T1.f;
import W1.C0324l;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.gms.common.util.VisibleForTesting;
import e2.C0408a;
import e2.d;
import e2.e;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/* renamed from: x1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0855a {

    /* renamed from: a  reason: collision with root package name */
    public T1.a f6461a;

    /* renamed from: b  reason: collision with root package name */
    public e f6462b;

    /* renamed from: c  reason: collision with root package name */
    public boolean f6463c;

    /* renamed from: d  reason: collision with root package name */
    public final Object f6464d = new Object();

    /* renamed from: e  reason: collision with root package name */
    public C0857c f6465e;
    public final Context f;

    /* renamed from: g  reason: collision with root package name */
    public final long f6466g;

    /* renamed from: x1.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class C0081a {

        /* renamed from: a  reason: collision with root package name */
        public final String f6467a;

        /* renamed from: b  reason: collision with root package name */
        public final boolean f6468b;

        @Deprecated
        public C0081a(String str, boolean z4) {
            this.f6467a = str;
            this.f6468b = z4;
        }

        public final String toString() {
            String str = this.f6467a;
            StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 7);
            sb.append("{");
            sb.append(str);
            sb.append("}");
            sb.append(this.f6468b);
            return sb.toString();
        }
    }

    @VisibleForTesting
    public C0855a(Context context, long j4, boolean z4) {
        Context applicationContext;
        C0324l.d(context);
        if (z4 && (applicationContext = context.getApplicationContext()) != null) {
            context = applicationContext;
        }
        this.f = context;
        this.f6463c = false;
        this.f6466g = j4;
    }

    public static C0081a a(Context context) {
        C0855a c0855a = new C0855a(context, -1L, true);
        try {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            c0855a.d(false);
            C0081a f = c0855a.f();
            e(f, SystemClock.elapsedRealtime() - elapsedRealtime, null);
            return f;
        } finally {
        }
    }

    public static boolean b(Context context) {
        boolean i4;
        C0855a c0855a = new C0855a(context, -1L, false);
        try {
            c0855a.d(false);
            C0324l.c("Calling this from your main thread can lead to deadlock");
            synchronized (c0855a) {
                if (!c0855a.f6463c) {
                    synchronized (c0855a.f6464d) {
                        C0857c c0857c = c0855a.f6465e;
                        if (c0857c == null || !c0857c.f6473m) {
                            throw new IOException("AdvertisingIdClient is not connected.");
                        }
                    }
                    try {
                        c0855a.d(false);
                        if (!c0855a.f6463c) {
                            throw new IOException("AdvertisingIdClient cannot reconnect.");
                        }
                    } catch (Exception e4) {
                        throw new IOException("AdvertisingIdClient cannot reconnect.", e4);
                    }
                }
                C0324l.d(c0855a.f6461a);
                C0324l.d(c0855a.f6462b);
                try {
                    i4 = c0855a.f6462b.i();
                } catch (RemoteException e5) {
                    Log.i("AdvertisingIdClient", "GMS remote exception ", e5);
                    throw new IOException("Remote exception");
                }
            }
            c0855a.g();
            return i4;
        } finally {
            c0855a.c();
        }
    }

    @VisibleForTesting
    public static void e(C0081a c0081a, long j4, Throwable th) {
        if (Math.random() <= 0.0d) {
            HashMap hashMap = new HashMap();
            String str = "1";
            hashMap.put("app_context", "1");
            if (c0081a != null) {
                if (true != c0081a.f6468b) {
                    str = "0";
                }
                hashMap.put("limit_ad_tracking", str);
                String str2 = c0081a.f6467a;
                if (str2 != null) {
                    hashMap.put("ad_id_size", Integer.toString(str2.length()));
                }
            }
            if (th != null) {
                hashMap.put("error", th.getClass().getName());
            }
            hashMap.put("tag", "AdvertisingIdClient");
            hashMap.put("time_spent", Long.toString(j4));
            new C0856b(hashMap).start();
        }
    }

    public final void c() {
        C0324l.c("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            try {
                if (this.f != null && this.f6461a != null) {
                    if (this.f6463c) {
                        Z1.a.a().b(this.f, this.f6461a);
                    }
                    this.f6463c = false;
                    this.f6462b = null;
                    this.f6461a = null;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v5, types: [e2.e] */
    /* JADX WARN: Type inference failed for: r1v7 */
    @VisibleForTesting
    public final void d(boolean z4) {
        e eVar;
        C0324l.c("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            try {
                if (this.f6463c) {
                    c();
                }
                Context context = this.f;
                try {
                    context.getPackageManager().getPackageInfo("com.android.vending", 0);
                    int c4 = f.f2354b.c(context, 12451000);
                    if (c4 != 0 && c4 != 2) {
                        throw new IOException("Google Play services not available");
                    }
                    T1.a aVar = new T1.a();
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    if (Z1.a.a().c(context, context.getClass().getName(), intent, aVar, 1, null)) {
                        this.f6461a = aVar;
                        try {
                            IBinder a4 = aVar.a(TimeUnit.MILLISECONDS);
                            int i4 = d.f3358j;
                            IInterface queryLocalInterface = a4.queryLocalInterface("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                            if (queryLocalInterface instanceof e) {
                                eVar = (e) queryLocalInterface;
                            } else {
                                eVar = new C0408a(a4);
                            }
                            this.f6462b = eVar;
                            this.f6463c = true;
                            if (z4) {
                                g();
                            }
                        } catch (InterruptedException unused) {
                            throw new IOException("Interrupted exception");
                        } catch (Throwable th) {
                            throw new IOException(th);
                        }
                    } else {
                        throw new IOException("Connection failure");
                    }
                } catch (PackageManager.NameNotFoundException unused2) {
                    throw new Exception();
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public final C0081a f() {
        C0081a c0081a;
        C0324l.c("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            try {
                if (!this.f6463c) {
                    synchronized (this.f6464d) {
                        C0857c c0857c = this.f6465e;
                        if (c0857c == null || !c0857c.f6473m) {
                            throw new IOException("AdvertisingIdClient is not connected.");
                        }
                    }
                    try {
                        d(false);
                        if (!this.f6463c) {
                            throw new IOException("AdvertisingIdClient cannot reconnect.");
                        }
                    } catch (Exception e4) {
                        throw new IOException("AdvertisingIdClient cannot reconnect.", e4);
                    }
                }
                C0324l.d(this.f6461a);
                C0324l.d(this.f6462b);
                try {
                    c0081a = new C0081a(this.f6462b.e(), this.f6462b.b());
                } catch (RemoteException e5) {
                    Log.i("AdvertisingIdClient", "GMS remote exception ", e5);
                    throw new IOException("Remote exception");
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        g();
        return c0081a;
    }

    public final void finalize() {
        c();
        super.finalize();
    }

    public final void g() {
        synchronized (this.f6464d) {
            C0857c c0857c = this.f6465e;
            if (c0857c != null) {
                c0857c.f6472l.countDown();
                try {
                    this.f6465e.join();
                } catch (InterruptedException unused) {
                }
            }
            long j4 = this.f6466g;
            if (j4 > 0) {
                this.f6465e = new C0857c(this, j4);
            }
        }
    }
}
