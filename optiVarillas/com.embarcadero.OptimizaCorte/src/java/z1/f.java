package z1;

import A1.C0124p;
import A1.r;
import D1.E;
import D1.t0;
import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.gms.internal.ads.Eb;
import com.google.android.gms.internal.ads.GJ;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.YI;
import com.google.android.gms.internal.ads.fn;
import com.google.android.gms.internal.ads.m7;
import com.google.android.gms.internal.ads.o7;
import com.google.android.gms.internal.ads.q7;
import com.google.android.gms.internal.ads.vb;
import com.google.android.gms.internal.ads.w8;
import com.google.android.gms.internal.ads.xk;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class f implements Runnable, o7 {

    /* renamed from: m  reason: collision with root package name */
    public boolean f6536m;

    /* renamed from: n  reason: collision with root package name */
    public final boolean f6537n;

    /* renamed from: o  reason: collision with root package name */
    public final boolean f6538o;

    /* renamed from: p  reason: collision with root package name */
    public final ExecutorService f6539p;

    /* renamed from: q  reason: collision with root package name */
    public final YI f6540q;

    /* renamed from: r  reason: collision with root package name */
    public Context f6541r;

    /* renamed from: s  reason: collision with root package name */
    public final Context f6542s;

    /* renamed from: t  reason: collision with root package name */
    public E1.a f6543t;

    /* renamed from: u  reason: collision with root package name */
    public final E1.a f6544u;

    /* renamed from: v  reason: collision with root package name */
    public final boolean f6545v;

    /* renamed from: x  reason: collision with root package name */
    public int f6547x;

    /* renamed from: j  reason: collision with root package name */
    public final Vector f6533j = new Vector();

    /* renamed from: k  reason: collision with root package name */
    public final AtomicReference f6534k = new AtomicReference();

    /* renamed from: l  reason: collision with root package name */
    public final AtomicReference f6535l = new AtomicReference();

    /* renamed from: w  reason: collision with root package name */
    public final CountDownLatch f6546w = new CountDownLatch(1);

    public f(Context context, E1.a aVar) {
        this.f6541r = context;
        this.f6542s = context;
        this.f6543t = aVar;
        this.f6544u = aVar;
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        this.f6539p = newCachedThreadPool;
        vb vbVar = Gb.Z1;
        r rVar = r.f168d;
        boolean booleanValue = ((Boolean) rVar.f171c.a(vbVar)).booleanValue();
        this.f6545v = booleanValue;
        this.f6540q = YI.a(context, newCachedThreadPool, booleanValue);
        vb vbVar2 = Gb.W1;
        Eb eb = rVar.f171c;
        this.f6537n = ((Boolean) eb.a(vbVar2)).booleanValue();
        this.f6538o = ((Boolean) eb.a(Gb.a2)).booleanValue();
        if (((Boolean) eb.a(Gb.Y1)).booleanValue()) {
            this.f6547x = 2;
        } else {
            this.f6547x = 1;
        }
        if (!((Boolean) eb.a(Gb.X2)).booleanValue()) {
            this.f6536m = i();
        }
        if (((Boolean) eb.a(Gb.R2)).booleanValue()) {
            xk.a.execute(this);
            return;
        }
        E1.f fVar = C0124p.f.f161a;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            xk.a.execute(this);
        } else {
            run();
        }
    }

    public final void a(MotionEvent motionEvent) {
        o7 k4 = k();
        if (k4 != null) {
            l();
            k4.a(motionEvent);
            return;
        }
        this.f6533j.add(new Object[]{motionEvent});
    }

    public final void b(View view) {
        o7 k4 = k();
        if (k4 != null) {
            k4.b(view);
        }
    }

    public final void c(StackTraceElement[] stackTraceElementArr) {
        o7 k4;
        if (j() && (k4 = k()) != null) {
            k4.c(stackTraceElementArr);
        }
    }

    public final String d(Context context, View view, Activity activity) {
        vb vbVar = Gb.t9;
        r rVar = r.f168d;
        boolean booleanValue = ((Boolean) rVar.f171c.a(vbVar)).booleanValue();
        Eb eb = rVar.f171c;
        if (booleanValue) {
            if (j()) {
                o7 k4 = k();
                if (((Boolean) eb.a(Gb.u9)).booleanValue()) {
                    t0 t0Var = p.f6575A.f6578c;
                    t0.h(view, 2);
                }
                if (k4 != null) {
                    return k4.d(context, view, activity);
                }
                return "";
            }
            return "";
        }
        o7 k5 = k();
        if (((Boolean) eb.a(Gb.u9)).booleanValue()) {
            t0 t0Var2 = p.f6575A.f6578c;
            t0.h(view, 2);
        }
        if (k5 != null) {
            return k5.d(context, view, activity);
        }
        return "";
    }

    public final String e(Context context) {
        o7 k4;
        if (j() && (k4 = k()) != null) {
            l();
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                context = applicationContext;
            }
            return k4.e(context);
        }
        return "";
    }

    public final void f(int i4, int i5, int i6) {
        o7 k4 = k();
        if (k4 != null) {
            l();
            k4.f(i4, i5, i6);
            return;
        }
        this.f6533j.add(new Object[]{Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6)});
    }

    public final String g(Context context, String str, View view) {
        return h(context, str, view, null);
    }

    public final String h(Context context, String str, View view, Activity activity) {
        if (j()) {
            o7 k4 = k();
            if (((Boolean) r.f168d.f171c.a(Gb.u9)).booleanValue()) {
                t0 t0Var = p.f6575A.f6578c;
                t0.h(view, 4);
            }
            if (k4 != null) {
                l();
                Context applicationContext = context.getApplicationContext();
                if (applicationContext != null) {
                    context = applicationContext;
                }
                return k4.h(context, str, view, activity);
            }
            return "";
        }
        return "";
    }

    public final boolean i() {
        Context context = this.f6541r;
        E e4 = new E(this);
        GJ gj = new GJ(context, fn.o(context, this.f6540q), e4, ((Boolean) r.f168d.f171c.a(Gb.X1)).booleanValue());
        long currentTimeMillis = System.currentTimeMillis();
        synchronized (GJ.f) {
            try {
                w8 f = gj.f(1);
                if (f == null) {
                    gj.e(4025, currentTimeMillis);
                } else {
                    File c4 = gj.c(f.M());
                    if (!new File(c4, "pcam.jar").exists()) {
                        gj.e(4026, currentTimeMillis);
                    } else if (!new File(c4, "pcbc").exists()) {
                        gj.e(4027, currentTimeMillis);
                    } else {
                        gj.e(5019, currentTimeMillis);
                        return true;
                    }
                }
                return false;
            } finally {
            }
        }
    }

    public final boolean j() {
        try {
            this.f6546w.await();
            return true;
        } catch (InterruptedException e4) {
            E1.m.h("Interrupted during GADSignals creation.", e4);
            return false;
        }
    }

    public final o7 k() {
        int i4;
        if (this.f6537n && !this.f6536m) {
            i4 = 1;
        } else {
            i4 = this.f6547x;
        }
        if (i4 == 2) {
            return (o7) this.f6535l.get();
        }
        return (o7) this.f6534k.get();
    }

    public final void l() {
        Vector vector = this.f6533j;
        o7 k4 = k();
        if (!vector.isEmpty() && k4 != null) {
            Iterator it = vector.iterator();
            while (it.hasNext()) {
                Object[] objArr = (Object[]) it.next();
                int length = objArr.length;
                if (length == 1) {
                    k4.a((MotionEvent) objArr[0]);
                } else if (length == 3) {
                    k4.f(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue());
                }
            }
            vector.clear();
        }
    }

    public final void m(boolean z4) {
        String str = this.f6543t.f844j;
        Context context = this.f6541r;
        Context applicationContext = context.getApplicationContext();
        if (applicationContext != null) {
            context = applicationContext;
        }
        q7.t(context, z4);
        this.f6534k.set(new q7(context, str, z4));
    }

    @Override // java.lang.Runnable
    public final void run() {
        int i4;
        boolean z4;
        try {
            vb vbVar = Gb.X2;
            r rVar = r.f168d;
            if (((Boolean) rVar.f171c.a(vbVar)).booleanValue()) {
                this.f6536m = i();
            }
            boolean z5 = this.f6543t.f847m;
            final boolean z6 = false;
            if (!((Boolean) rVar.f171c.a(Gb.M0)).booleanValue() && z5) {
                z6 = true;
            }
            if (this.f6537n && !this.f6536m) {
                i4 = 1;
            } else {
                i4 = this.f6547x;
            }
            if (i4 == 1) {
                m(z6);
                if (this.f6547x == 2) {
                    this.f6539p.execute(new Runnable() { // from class: z1.e
                        @Override // java.lang.Runnable
                        public final void run() {
                            boolean z7 = z6;
                            f fVar = f.this;
                            fVar.getClass();
                            long currentTimeMillis = System.currentTimeMillis();
                            try {
                                String str = fVar.f6544u.f844j;
                                Context context = fVar.f6542s;
                                Context applicationContext = context.getApplicationContext();
                                if (applicationContext != null) {
                                    context = applicationContext;
                                }
                                m7.i(context, str, z7, fVar.f6545v).m();
                            } catch (NullPointerException e4) {
                                fVar.f6540q.b(2027, System.currentTimeMillis() - currentTimeMillis, e4);
                            }
                        }
                    });
                }
            } else {
                long currentTimeMillis = System.currentTimeMillis();
                try {
                    String str = this.f6543t.f844j;
                    Context context = this.f6541r;
                    Context applicationContext = context.getApplicationContext();
                    if (applicationContext != null) {
                        context = applicationContext;
                    }
                    m7 i5 = m7.i(context, str, z6, this.f6545v);
                    this.f6535l.set(i5);
                    if (this.f6538o) {
                        synchronized (i5) {
                            z4 = i5.z;
                        }
                        if (!z4) {
                            this.f6547x = 1;
                            m(z6);
                        }
                    }
                } catch (NullPointerException e4) {
                    this.f6547x = 1;
                    m(z6);
                    this.f6540q.b(2031, System.currentTimeMillis() - currentTimeMillis, e4);
                }
            }
            this.f6546w.countDown();
            this.f6541r = null;
            this.f6543t = null;
        } catch (Throwable th) {
            this.f6546w.countDown();
            this.f6541r = null;
            this.f6543t = null;
            throw th;
        }
    }
}
