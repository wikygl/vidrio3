package C1;

import A1.RunnableC0098e1;
import S0.L0;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.internal.ads.F5;
import com.google.android.gms.internal.ads.HY;
import com.google.android.gms.internal.ads.J5;
import com.google.android.gms.internal.ads.K5;
import com.google.android.gms.internal.ads.N5;
import com.google.android.gms.internal.ads.YL;
import com.google.android.gms.internal.ads.az;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.sm;
import com.google.android.gms.internal.ads.v10;
import i2.C0459e;
import i2.C0460f;
import i2.C0462h;
import i2.C0467m;
import i2.C0468n;
import i2.C0469o;
import i2.C0470p;
import i2.C0472s;
import i2.C0476w;
import i2.G;
import i2.L;
import i2.X;
import i2.Y;
import i2.Z;
import i2.f0;
import i2.i0;
import j$.util.Objects;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class z implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f416j;

    /* renamed from: k  reason: collision with root package name */
    public final Object f417k;

    /* renamed from: l  reason: collision with root package name */
    public final Object f418l;

    /* renamed from: m  reason: collision with root package name */
    public final Object f419m;

    public /* synthetic */ z(Object obj, Object obj2, Object obj3, int i4) {
        this.f416j = i4;
        this.f417k = obj;
        this.f418l = obj2;
        this.f419m = obj3;
    }

    @Override // java.lang.Runnable
    public final void run() {
        J5 j5;
        final JSONObject jSONObject;
        switch (this.f416j) {
            case 0:
                em emVar = ((B) this.f417k).f355c;
                if (emVar != null) {
                    emVar.b((String) this.f418l, (Map) this.f419m);
                    return;
                }
                return;
            case 1:
                ((F5) this.f417k).o();
                K5 k5 = (K5) this.f418l;
                N5 n5 = k5.c;
                if (n5 == null) {
                    ((F5) this.f417k).f(k5.a);
                } else {
                    F5 f5 = (F5) this.f417k;
                    synchronized (f5.n) {
                        j5 = f5.o;
                    }
                    j5.d(n5);
                }
                if (((K5) this.f418l).d) {
                    ((F5) this.f417k).e("intermediate-response");
                } else {
                    ((F5) this.f417k).g("done");
                }
                Runnable runnable = (Runnable) this.f419m;
                if (runnable != null) {
                    runnable.run();
                    return;
                }
                return;
            case 2:
                ContentValues contentValues = new ContentValues();
                contentValues.put("event_state", (Integer) 1);
                SQLiteDatabase sQLiteDatabase = (SQLiteDatabase) this.f417k;
                sQLiteDatabase.update("offline_buffered_pings", contentValues, "gws_query_id = ?", new String[]{(String) this.f418l});
                az.d(sQLiteDatabase, (E1.q) this.f419m);
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                HY hy = (HY) this.f417k;
                hy.getClass();
                hy.c.v(((YL) this.f418l).v(), (v10) this.f419m);
                return;
            case 4:
                String str = (String) this.f418l;
                if (TextUtils.isEmpty(str)) {
                    Log.d("UserMessagingPlatform", "Error on action: empty action name");
                    return;
                }
                final String lowerCase = str.toLowerCase();
                String str2 = (String) this.f417k;
                if (TextUtils.isEmpty(str2)) {
                    jSONObject = new JSONObject();
                } else {
                    try {
                        jSONObject = new JSONObject(str2);
                    } catch (JSONException unused) {
                        Log.d("UserMessagingPlatform", "Action[" + lowerCase + "]: failed to parse args: " + str2);
                        return;
                    }
                }
                String obj = jSONObject.toString();
                Log.d("UserMessagingPlatform", "Action[" + lowerCase + "]: " + obj);
                int i4 = 0;
                while (true) {
                    L[] lArr = (L[]) this.f419m;
                    if (i4 < lArr.length) {
                        final L l2 = lArr[i4];
                        FutureTask futureTask = new FutureTask(new Callable() { // from class: i2.k
                            @Override // java.util.concurrent.Callable
                            public final Object call() {
                                return Boolean.valueOf(L.this.b(lowerCase, jSONObject));
                            }
                        });
                        l2.a().execute(futureTask);
                        try {
                        } catch (InterruptedException e4) {
                            Log.d("UserMessagingPlatform", "Thread interrupted for Action[" + lowerCase + "]: ", e4);
                        } catch (ExecutionException e5) {
                            Log.d("UserMessagingPlatform", C.b.b("Failed to run Action[", lowerCase, "]: "), e5.getCause());
                        }
                        if (!((Boolean) futureTask.get()).booleanValue()) {
                            i4++;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            default:
                f0 f0Var = (f0) this.f417k;
                f0Var.getClass();
                L0 l0 = (L0) this.f418l;
                Objects.requireNonNull(l0);
                f0Var.f3736b.post(new RunnableC0098e1(13, l0));
                if (((i0) this.f419m).f3763b != 2) {
                    C0469o c0469o = f0Var.f3739e;
                    C0470p c0470p = (C0470p) c0469o.f3791c.get();
                    if (c0470p == null) {
                        Log.e("UserMessagingPlatform", "Failed to load and cache a form due to null consent form resources.");
                        return;
                    }
                    C0460f c0460f = (C0460f) c0469o.f3789a.a();
                    c0460f.getClass();
                    C0459e c0459e = c0460f.f3734a;
                    X b4 = X.b(new B0.v(c0459e.f3723c));
                    Y y4 = new Y(c0470p);
                    A a4 = new A();
                    Y y5 = c0459e.f3723c;
                    X x4 = c0459e.f3726g;
                    C0462h c0462h = c0459e.f3727h;
                    X x5 = c0459e.f3724d;
                    X b5 = X.b(new C0468n(y5, c0459e.f3725e, b4, x5, y4, new C0472s(b4, new C0476w(y5, b4, x4, c0462h, a4, x5))));
                    if (((Z) a4.f352k) == null) {
                        a4.f352k = b5;
                        C0467m c0467m = (C0467m) a4.a();
                        c0467m.f3782l = true;
                        G.f3679a.post(new sm(c0469o, 6, c0467m));
                        return;
                    }
                    throw new IllegalStateException();
                }
                return;
        }
    }

    public /* synthetic */ z(String str, String str2, L[] lArr) {
        this.f416j = 4;
        this.f418l = str;
        this.f417k = str2;
        this.f419m = lArr;
    }
}
