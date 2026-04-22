package V1;

import U1.c;
import W1.AbstractC0319g;
import W1.C0322j;
import W1.C0323k;
import W1.C0324l;
import W1.C0325m;
import W1.C0326n;
import W1.C0327o;
import W1.C0328p;
import W1.C0336y;
import android.app.ActivityManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import b2.C0355a;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.Status;
import com.google.errorprone.annotations.ResultIgnorabilityUnspecified;
import h2.C0441d;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import r.C0775d;
import r.i;

/* renamed from: V1.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0298d implements Handler.Callback {

    /* renamed from: A  reason: collision with root package name */
    public static C0298d f2571A;

    /* renamed from: x  reason: collision with root package name */
    public static final Status f2572x = new Status(4, "Sign-out occurred while this API call was in progress.", (PendingIntent) null, (T1.b) null);

    /* renamed from: y  reason: collision with root package name */
    public static final Status f2573y = new Status(4, "The user must be signed in to make this API call.", (PendingIntent) null, (T1.b) null);

    /* renamed from: z  reason: collision with root package name */
    public static final Object f2574z = new Object();

    /* renamed from: j  reason: collision with root package name */
    public long f2575j;

    /* renamed from: k  reason: collision with root package name */
    public boolean f2576k;

    /* renamed from: l  reason: collision with root package name */
    public C0327o f2577l;

    /* renamed from: m  reason: collision with root package name */
    public Y1.c f2578m;

    /* renamed from: n  reason: collision with root package name */
    public final Context f2579n;

    /* renamed from: o  reason: collision with root package name */
    public final T1.e f2580o;

    /* renamed from: p  reason: collision with root package name */
    public final C0336y f2581p;

    /* renamed from: q  reason: collision with root package name */
    public final AtomicInteger f2582q;

    /* renamed from: r  reason: collision with root package name */
    public final AtomicInteger f2583r;

    /* renamed from: s  reason: collision with root package name */
    public final ConcurrentHashMap f2584s;

    /* renamed from: t  reason: collision with root package name */
    public final C0775d f2585t;

    /* renamed from: u  reason: collision with root package name */
    public final C0775d f2586u;

    /* renamed from: v  reason: collision with root package name */
    public final g2.g f2587v;

    /* renamed from: w  reason: collision with root package name */
    public volatile boolean f2588w;

    /* JADX WARN: Type inference failed for: r2v5, types: [android.os.Handler, g2.g] */
    public C0298d(Context context, Looper looper) {
        T1.e eVar = T1.e.f2352d;
        this.f2575j = 10000L;
        this.f2576k = false;
        boolean z4 = true;
        this.f2582q = new AtomicInteger(1);
        this.f2583r = new AtomicInteger(0);
        this.f2584s = new ConcurrentHashMap(5, 0.75f, 1);
        this.f2585t = new C0775d();
        this.f2586u = new C0775d();
        this.f2588w = true;
        this.f2579n = context;
        ?? handler = new Handler(looper, this);
        Looper.getMainLooper();
        this.f2587v = handler;
        this.f2580o = eVar;
        this.f2581p = new C0336y();
        PackageManager packageManager = context.getPackageManager();
        if (a2.d.f2866e == null) {
            a2.d.f2866e = Boolean.valueOf((a2.g.a() && packageManager.hasSystemFeature("android.hardware.type.automotive")) ? false : false);
        }
        if (a2.d.f2866e.booleanValue()) {
            this.f2588w = false;
        }
        handler.sendMessage(handler.obtainMessage(6));
    }

    public static Status c(C0295a c0295a, T1.b bVar) {
        String str = c0295a.f2563b.f2375b;
        String valueOf = String.valueOf(bVar);
        return new Status(17, "API: " + str + " is not available on this device. Connection failed with: " + valueOf, bVar.f2343l, bVar);
    }

    @ResultIgnorabilityUnspecified
    public static C0298d e(Context context) {
        C0298d c0298d;
        synchronized (f2574z) {
            try {
                if (f2571A == null) {
                    Looper looper = AbstractC0319g.b().getLooper();
                    Context applicationContext = context.getApplicationContext();
                    Object obj = T1.e.f2351c;
                    f2571A = new C0298d(applicationContext, looper);
                }
                c0298d = f2571A;
            } catch (Throwable th) {
                throw th;
            }
        }
        return c0298d;
    }

    public final boolean a() {
        if (this.f2576k) {
            return false;
        }
        C0326n c0326n = C0325m.a().f2758a;
        if (c0326n != null && !c0326n.f2760k) {
            return false;
        }
        int i4 = this.f2581p.f2776a.get(203400000, -1);
        if (i4 != -1 && i4 != 0) {
            return false;
        }
        return true;
    }

    @ResultIgnorabilityUnspecified
    public final boolean b(T1.b bVar, int i4) {
        boolean z4;
        T1.e eVar = this.f2580o;
        eVar.getClass();
        Context context = this.f2579n;
        if (C0355a.c(context)) {
            return false;
        }
        int i5 = bVar.f2342k;
        PendingIntent pendingIntent = bVar.f2343l;
        if (i5 != 0 && pendingIntent != null) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (!z4) {
            pendingIntent = null;
            Intent b4 = eVar.b(i5, context, null);
            if (b4 != null) {
                pendingIntent = PendingIntent.getActivity(context, 0, b4, C0441d.f3586a | 134217728);
            }
        }
        if (pendingIntent == null) {
            return false;
        }
        int i6 = GoogleApiActivity.k;
        Intent intent = new Intent(context, GoogleApiActivity.class);
        intent.putExtra("pending_intent", pendingIntent);
        intent.putExtra("failing_client_id", i4);
        intent.putExtra("notify_manager", true);
        eVar.g(context, i5, PendingIntent.getActivity(context, 0, intent, g2.f.f3501a | 134217728));
        return true;
    }

    @ResultIgnorabilityUnspecified
    public final u d(U1.c cVar) {
        ConcurrentHashMap concurrentHashMap = this.f2584s;
        C0295a c0295a = cVar.f2382e;
        u uVar = (u) concurrentHashMap.get(c0295a);
        if (uVar == null) {
            uVar = new u(this, cVar);
            concurrentHashMap.put(c0295a, uVar);
        }
        if (uVar.f2605k.o()) {
            this.f2586u.add(c0295a);
        }
        uVar.k();
        return uVar;
    }

    public final void f(T1.b bVar, int i4) {
        if (!b(bVar, i4)) {
            g2.g gVar = this.f2587v;
            gVar.sendMessage(gVar.obtainMessage(5, i4, 0, bVar));
        }
    }

    /* JADX WARN: Type inference failed for: r2v18, types: [java.lang.Object, V1.k$a] */
    /* JADX WARN: Type inference failed for: r2v19, types: [Y1.c, U1.c] */
    /* JADX WARN: Type inference failed for: r2v33, types: [java.lang.Object, V1.k$a] */
    /* JADX WARN: Type inference failed for: r2v34, types: [Y1.c, U1.c] */
    /* JADX WARN: Type inference failed for: r3v13, types: [java.lang.Object, V1.k$a] */
    /* JADX WARN: Type inference failed for: r3v14, types: [Y1.c, U1.c] */
    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message message) {
        u uVar;
        Status status;
        T1.d[] g4;
        int i4 = message.what;
        long j4 = 300000;
        switch (i4) {
            case 1:
                if (true == ((Boolean) message.obj).booleanValue()) {
                    j4 = 10000;
                }
                this.f2575j = j4;
                this.f2587v.removeMessages(12);
                for (C0295a c0295a : this.f2584s.keySet()) {
                    g2.g gVar = this.f2587v;
                    gVar.sendMessageDelayed(gVar.obtainMessage(12, c0295a), this.f2575j);
                }
                break;
            case 2:
                ((M) message.obj).getClass();
                throw null;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                for (u uVar2 : this.f2584s.values()) {
                    C0324l.a(uVar2.f2616v.f2587v);
                    uVar2.f2614t = null;
                    uVar2.k();
                }
                break;
            case 4:
            case 8:
            case 13:
                D d4 = (D) message.obj;
                u uVar3 = (u) this.f2584s.get(d4.f2541c.f2382e);
                if (uVar3 == null) {
                    uVar3 = d(d4.f2541c);
                }
                if (uVar3.f2605k.o() && this.f2583r.get() != d4.f2540b) {
                    d4.f2539a.a(f2572x);
                    uVar3.o();
                    break;
                } else {
                    uVar3.l((A) d4.f2539a);
                    break;
                }
            case 5:
                int i5 = message.arg1;
                T1.b bVar = (T1.b) message.obj;
                Iterator it = this.f2584s.values().iterator();
                while (true) {
                    if (it.hasNext()) {
                        uVar = (u) it.next();
                        if (uVar.f2610p == i5) {
                        }
                    } else {
                        uVar = null;
                    }
                }
                if (uVar != null) {
                    if (bVar.f2342k == 13) {
                        T1.e eVar = this.f2580o;
                        int i6 = bVar.f2342k;
                        eVar.getClass();
                        AtomicBoolean atomicBoolean = T1.i.f2356a;
                        uVar.b(new Status(17, "Error resolution was canceled by the user, original error message: " + T1.b.h(i6) + ": " + bVar.f2344m, (PendingIntent) null, (T1.b) null));
                        break;
                    } else {
                        uVar.b(c(uVar.f2606l, bVar));
                        break;
                    }
                } else {
                    Log.wtf("GoogleApiManager", I.h.b(i5, "Could not find API instance ", " while trying to fail enqueued calls."), new Exception());
                    break;
                }
            case 6:
                if (this.f2579n.getApplicationContext() instanceof Application) {
                    Application application = (Application) this.f2579n.getApplicationContext();
                    ComponentCallbacks2C0296b componentCallbacks2C0296b = ComponentCallbacks2C0296b.f2566n;
                    synchronized (componentCallbacks2C0296b) {
                        try {
                            if (!componentCallbacks2C0296b.f2570m) {
                                application.registerActivityLifecycleCallbacks(componentCallbacks2C0296b);
                                application.registerComponentCallbacks(componentCallbacks2C0296b);
                                componentCallbacks2C0296b.f2570m = true;
                            }
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                    componentCallbacks2C0296b.a(new r(this));
                    AtomicBoolean atomicBoolean2 = componentCallbacks2C0296b.f2568k;
                    boolean z4 = atomicBoolean2.get();
                    AtomicBoolean atomicBoolean3 = componentCallbacks2C0296b.f2567j;
                    if (!z4) {
                        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
                        ActivityManager.getMyMemoryState(runningAppProcessInfo);
                        if (!atomicBoolean2.getAndSet(true) && runningAppProcessInfo.importance > 100) {
                            atomicBoolean3.set(true);
                        }
                    }
                    if (!atomicBoolean3.get()) {
                        this.f2575j = 300000L;
                        break;
                    }
                }
                break;
            case 7:
                d((U1.c) message.obj);
                break;
            case 9:
                if (this.f2584s.containsKey(message.obj)) {
                    u uVar4 = (u) this.f2584s.get(message.obj);
                    C0324l.a(uVar4.f2616v.f2587v);
                    if (uVar4.f2612r) {
                        uVar4.k();
                        break;
                    }
                }
                break;
            case 10:
                Iterator it2 = this.f2586u.iterator();
                while (true) {
                    i.a aVar = (i.a) it2;
                    if (aVar.hasNext()) {
                        u uVar5 = (u) this.f2584s.remove((C0295a) aVar.next());
                        if (uVar5 != null) {
                            uVar5.o();
                        }
                    } else {
                        this.f2586u.clear();
                        break;
                    }
                }
            case 11:
                if (this.f2584s.containsKey(message.obj)) {
                    u uVar6 = (u) this.f2584s.get(message.obj);
                    C0298d c0298d = uVar6.f2616v;
                    C0324l.a(c0298d.f2587v);
                    boolean z5 = uVar6.f2612r;
                    if (z5) {
                        if (z5) {
                            C0298d c0298d2 = uVar6.f2616v;
                            g2.g gVar2 = c0298d2.f2587v;
                            C0295a c0295a2 = uVar6.f2606l;
                            gVar2.removeMessages(11, c0295a2);
                            c0298d2.f2587v.removeMessages(9, c0295a2);
                            uVar6.f2612r = false;
                        }
                        if (c0298d.f2580o.c(c0298d.f2579n, T1.f.f2353a) == 18) {
                            status = new Status(21, "Connection timed out waiting for Google Play services update to complete.", (PendingIntent) null, (T1.b) null);
                        } else {
                            status = new Status(22, "API failed to connect while resuming due to an unknown error.", (PendingIntent) null, (T1.b) null);
                        }
                        uVar6.b(status);
                        uVar6.f2605k.c("Timing out connection while resuming.");
                        break;
                    }
                }
                break;
            case 12:
                if (this.f2584s.containsKey(message.obj)) {
                    ((u) this.f2584s.get(message.obj)).j(true);
                    break;
                }
                break;
            case 14:
                ((C0309o) message.obj).getClass();
                if (!this.f2584s.containsKey(null)) {
                    throw null;
                }
                ((u) this.f2584s.get(null)).j(false);
                throw null;
            case 15:
                v vVar = (v) message.obj;
                if (this.f2584s.containsKey(vVar.f2617a)) {
                    u uVar7 = (u) this.f2584s.get(vVar.f2617a);
                    if (uVar7.f2613s.contains(vVar) && !uVar7.f2612r) {
                        if (!uVar7.f2605k.a()) {
                            uVar7.k();
                            break;
                        } else {
                            uVar7.d();
                            break;
                        }
                    }
                }
                break;
            case 16:
                v vVar2 = (v) message.obj;
                if (this.f2584s.containsKey(vVar2.f2617a)) {
                    u uVar8 = (u) this.f2584s.get(vVar2.f2617a);
                    if (uVar8.f2613s.remove(vVar2)) {
                        C0298d c0298d3 = uVar8.f2616v;
                        c0298d3.f2587v.removeMessages(15, vVar2);
                        c0298d3.f2587v.removeMessages(16, vVar2);
                        T1.d dVar = vVar2.f2618b;
                        LinkedList<L> linkedList = uVar8.f2604j;
                        ArrayList arrayList = new ArrayList(linkedList.size());
                        for (L l2 : linkedList) {
                            if ((l2 instanceof A) && (g4 = ((A) l2).g(uVar8)) != null) {
                                int length = g4.length;
                                int i7 = 0;
                                while (true) {
                                    if (i7 >= length) {
                                        break;
                                    } else if (C0323k.a(g4[i7], dVar)) {
                                        if (i7 >= 0) {
                                            arrayList.add(l2);
                                        }
                                    } else {
                                        i7++;
                                    }
                                }
                            }
                        }
                        int size = arrayList.size();
                        for (int i8 = 0; i8 < size; i8++) {
                            L l4 = (L) arrayList.get(i8);
                            linkedList.remove(l4);
                            l4.b(new U1.j(dVar));
                        }
                        break;
                    }
                }
                break;
            case 17:
                C0327o c0327o = this.f2577l;
                if (c0327o != null) {
                    if (c0327o.f2764j > 0 || a()) {
                        if (this.f2578m == null) {
                            this.f2578m = new U1.c(this.f2579n, Y1.c.f2829i, C0328p.f2766c, c.a.f2385b);
                        }
                        Y1.c cVar = this.f2578m;
                        cVar.getClass();
                        ?? obj = new Object();
                        T1.d[] dVarArr = {g2.e.f3499a};
                        obj.f2592a = new C1.A(2, c0327o);
                        cVar.c(2, new H(obj, dVarArr, false, 0));
                    }
                    this.f2577l = null;
                    break;
                }
                break;
            case 18:
                C c4 = (C) message.obj;
                if (c4.f2537c == 0) {
                    C0327o c0327o2 = new C0327o(c4.f2536b, Arrays.asList(c4.f2535a));
                    if (this.f2578m == null) {
                        this.f2578m = new U1.c(this.f2579n, Y1.c.f2829i, C0328p.f2766c, c.a.f2385b);
                    }
                    Y1.c cVar2 = this.f2578m;
                    cVar2.getClass();
                    ?? obj2 = new Object();
                    T1.d[] dVarArr2 = {g2.e.f3499a};
                    obj2.f2592a = new C1.A(2, c0327o2);
                    cVar2.c(2, new H(obj2, dVarArr2, false, 0));
                    break;
                } else {
                    C0327o c0327o3 = this.f2577l;
                    if (c0327o3 != null) {
                        List list = c0327o3.f2765k;
                        if (c0327o3.f2764j == c4.f2536b && (list == null || list.size() < c4.f2538d)) {
                            C0327o c0327o4 = this.f2577l;
                            C0322j c0322j = c4.f2535a;
                            if (c0327o4.f2765k == null) {
                                c0327o4.f2765k = new ArrayList();
                            }
                            c0327o4.f2765k.add(c0322j);
                        } else {
                            this.f2587v.removeMessages(17);
                            C0327o c0327o5 = this.f2577l;
                            if (c0327o5 != null) {
                                if (c0327o5.f2764j > 0 || a()) {
                                    if (this.f2578m == null) {
                                        this.f2578m = new U1.c(this.f2579n, Y1.c.f2829i, C0328p.f2766c, c.a.f2385b);
                                    }
                                    Y1.c cVar3 = this.f2578m;
                                    cVar3.getClass();
                                    ?? obj3 = new Object();
                                    T1.d[] dVarArr3 = {g2.e.f3499a};
                                    obj3.f2592a = new C1.A(2, c0327o5);
                                    cVar3.c(2, new H(obj3, dVarArr3, false, 0));
                                }
                                this.f2577l = null;
                            }
                        }
                    }
                    if (this.f2577l == null) {
                        ArrayList arrayList2 = new ArrayList();
                        arrayList2.add(c4.f2535a);
                        this.f2577l = new C0327o(c4.f2536b, arrayList2);
                        g2.g gVar3 = this.f2587v;
                        gVar3.sendMessageDelayed(gVar3.obtainMessage(17), c4.f2537c);
                        break;
                    }
                }
                break;
            case 19:
                this.f2576k = false;
                break;
            default:
                Log.w("GoogleApiManager", "Unknown message id: " + i4);
                return false;
        }
        return true;
    }
}
