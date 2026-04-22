package D1;

import android.content.Context;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.I5;
import com.google.android.gms.internal.ads.X5;
import com.google.android.gms.internal.ads.c1;
import com.google.android.gms.internal.ads.r5;
import com.google.android.gms.internal.ads.zk;
import java.util.HashMap;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class G {

    /* renamed from: a  reason: collision with root package name */
    public static I5 f636a;

    /* renamed from: b  reason: collision with root package name */
    public static final Object f637b = new Object();

    public G(Context context) {
        I5 i5;
        context = context.getApplicationContext() != null ? context.getApplicationContext() : context;
        synchronized (f637b) {
            try {
                if (f636a == null) {
                    Gb.a(context);
                    if (((Boolean) A1.r.f168d.f171c.a(Gb.S3)).booleanValue()) {
                        i5 = C0199t.i(context);
                    } else {
                        i5 = new I5(new X5(new Q0.p(context.getApplicationContext())), new c1());
                        i5.c();
                    }
                    f636a = i5;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX WARN: Type inference failed for: r8v0, types: [com.google.android.gms.internal.ads.zk, D1.D] */
    public static D a(int i4, String str, HashMap hashMap, byte[] bArr) {
        ?? zkVar = new zk();
        B b4 = new B(str, zkVar);
        E1.l lVar = new E1.l();
        C c4 = new C(i4, str, zkVar, b4, bArr, hashMap, lVar);
        if (E1.l.c()) {
            try {
                Map d4 = c4.d();
                if (bArr == null) {
                    bArr = null;
                }
                if (E1.l.c()) {
                    lVar.d("onNetworkRequest", new E1.g(str, "GET", d4, bArr));
                }
            } catch (r5 e4) {
                E1.m.g(e4.getMessage());
            }
        }
        f636a.a(c4);
        return zkVar;
    }
}
