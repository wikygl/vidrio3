package U1;

import C3.C;
import U1.a;
import U1.a.c;
import V1.C0295a;
import V1.C0298d;
import W1.C0315c;
import W1.C0324l;
import android.accounts.Account;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.util.Collection;
import java.util.Collections;
import r.C0775d;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public abstract class c<O extends a.c> {

    /* renamed from: a  reason: collision with root package name */
    public final Context f2378a;

    /* renamed from: b  reason: collision with root package name */
    public final String f2379b;

    /* renamed from: c  reason: collision with root package name */
    public final U1.a f2380c;

    /* renamed from: d  reason: collision with root package name */
    public final a.c f2381d;

    /* renamed from: e  reason: collision with root package name */
    public final C0295a f2382e;
    public final int f;

    /* renamed from: g  reason: collision with root package name */
    public final C f2383g;

    /* renamed from: h  reason: collision with root package name */
    public final C0298d f2384h;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a {

        /* renamed from: b  reason: collision with root package name */
        public static final a f2385b = new a(new Object(), Looper.getMainLooper());

        /* renamed from: a  reason: collision with root package name */
        public final C f2386a;

        public a(C c4, Looper looper) {
            this.f2386a = c4;
        }
    }

    public c(Context context, U1.a<O> aVar, O o4, a aVar2) {
        String str;
        C0324l.e(context, "Null context is not permitted.");
        C0324l.e(aVar, "Api must not be null.");
        C0324l.e(aVar2, "Settings must not be null; use Settings.DEFAULT_SETTINGS instead.");
        Context applicationContext = context.getApplicationContext();
        C0324l.e(applicationContext, "The provided context did not have an application context.");
        this.f2378a = applicationContext;
        if (Build.VERSION.SDK_INT >= 30) {
            str = context.getAttributionTag();
        } else {
            str = null;
        }
        this.f2379b = str;
        this.f2380c = aVar;
        this.f2381d = o4;
        this.f2382e = new C0295a(aVar, o4, str);
        C0298d e4 = C0298d.e(applicationContext);
        this.f2384h = e4;
        this.f = e4.f2582q.getAndIncrement();
        this.f2383g = aVar2.f2386a;
        g2.g gVar = e4.f2587v;
        gVar.sendMessage(gVar.obtainMessage(7, this));
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [W1.c$a, java.lang.Object] */
    public final C0315c.a b() {
        Collection emptySet;
        GoogleSignInAccount b4;
        ?? obj = new Object();
        a.c cVar = this.f2381d;
        boolean z4 = cVar instanceof a.c.b;
        Account account = null;
        if (z4 && (b4 = ((a.c.b) cVar).b()) != null) {
            String str = b4.m;
            if (str != null) {
                account = new Account(str, "com.google");
            }
        } else if (cVar instanceof a.c.InterfaceC0027a) {
            account = ((a.c.InterfaceC0027a) cVar).a();
        }
        obj.f2713a = account;
        if (z4) {
            GoogleSignInAccount b5 = ((a.c.b) cVar).b();
            if (b5 == null) {
                emptySet = Collections.emptySet();
            } else {
                emptySet = b5.h();
            }
        } else {
            emptySet = Collections.emptySet();
        }
        if (obj.f2714b == null) {
            obj.f2714b = new C0775d();
        }
        obj.f2714b.addAll(emptySet);
        Context context = this.f2378a;
        obj.f2716d = context.getClass().getName();
        obj.f2715c = context.getPackageName();
        return obj;
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x0079  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final p2.q c(int r18, V1.H r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r19
            p2.g r2 = new p2.g
            r2.<init>()
            V1.d r11 = r0.f2384h
            r11.getClass()
            int r5 = r1.f2591c
            g2.g r12 = r11.f2587v
            p2.q r13 = r2.f5552a
            if (r5 == 0) goto L84
            boolean r3 = r11.a()
            if (r3 != 0) goto L1d
            goto L59
        L1d:
            W1.m r3 = W1.C0325m.a()
            W1.n r3 = r3.f2758a
            V1.a r6 = r0.f2382e
            r4 = 1
            if (r3 == 0) goto L5b
            boolean r7 = r3.f2760k
            if (r7 == 0) goto L59
            j$.util.concurrent.ConcurrentHashMap r7 = r11.f2584s
            java.lang.Object r7 = r7.get(r6)
            V1.u r7 = (V1.u) r7
            if (r7 == 0) goto L56
            U1.a$e r8 = r7.f2605k
            boolean r9 = r8 instanceof W1.AbstractC0314b
            if (r9 == 0) goto L59
            W1.b r8 = (W1.AbstractC0314b) r8
            W1.Q r9 = r8.f2703v
            if (r9 == 0) goto L56
            boolean r9 = r8.g()
            if (r9 != 0) goto L56
            W1.d r3 = V1.B.a(r7, r8, r5)
            if (r3 == 0) goto L59
            int r8 = r7.f2615u
            int r8 = r8 + r4
            r7.f2615u = r8
            boolean r4 = r3.f2719l
            goto L5b
        L56:
            boolean r4 = r3.f2761l
            goto L5b
        L59:
            r3 = 0
            goto L77
        L5b:
            V1.B r14 = new V1.B
            r7 = 0
            if (r4 == 0) goto L66
            long r9 = java.lang.System.currentTimeMillis()
            goto L67
        L66:
            r9 = r7
        L67:
            if (r4 == 0) goto L6f
            long r3 = android.os.SystemClock.elapsedRealtime()
            r15 = r3
            goto L70
        L6f:
            r15 = r7
        L70:
            r3 = r14
            r4 = r11
            r7 = r9
            r9 = r15
            r3.<init>(r4, r5, r6, r7, r9)
        L77:
            if (r3 == 0) goto L84
            r12.getClass()
            V1.q r4 = new V1.q
            r4.<init>()
            r13.b(r4, r3)
        L84:
            V1.J r3 = new V1.J
            C3.C r4 = r0.f2383g
            r5 = r18
            r3.<init>(r5, r1, r2, r4)
            java.util.concurrent.atomic.AtomicInteger r1 = r11.f2583r
            V1.D r2 = new V1.D
            int r1 = r1.get()
            r2.<init>(r3, r1, r0)
            r1 = 4
            android.os.Message r1 = r12.obtainMessage(r1, r2)
            r12.sendMessage(r1)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: U1.c.c(int, V1.H):p2.q");
    }
}
