package t1;

import A1.B1;
import A1.C0111j;
import A1.C0120n;
import A1.C0124p;
import A1.D;
import A1.G;
import A1.K0;
import W1.C0324l;
import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.bg;
import com.google.android.gms.internal.ads.pc;

/* renamed from: t1.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0801c {

    /* renamed from: a  reason: collision with root package name */
    public final B1 f5782a;

    /* renamed from: b  reason: collision with root package name */
    public final Context f5783b;

    /* renamed from: c  reason: collision with root package name */
    public final D f5784c;

    /* renamed from: t1.c$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public final Context f5785a;

        /* renamed from: b  reason: collision with root package name */
        public final G f5786b;

        public a(Context context, String str) {
            C0324l.e(context, "context cannot be null");
            C0120n c0120n = C0124p.f.f162b;
            bg bgVar = new bg();
            c0120n.getClass();
            this.f5785a = context;
            this.f5786b = (G) new C0111j(c0120n, context, str, bgVar).d(context, false);
        }
    }

    public C0801c(Context context, D d4) {
        B1 b12 = B1.f8a;
        this.f5783b = context;
        this.f5784c = d4;
        this.f5782a = b12;
    }

    public final void a(C0802d c0802d) {
        K0 k02 = c0802d.f5787a;
        Context context = this.f5783b;
        Gb.a(context);
        if (((Boolean) pc.c.e()).booleanValue()) {
            if (((Boolean) A1.r.f168d.f171c.a(Gb.T9)).booleanValue()) {
                E1.c.f852b.execute(new B.h(this, 9, k02));
                return;
            }
        }
        try {
            D d4 = this.f5784c;
            this.f5782a.getClass();
            d4.h3(B1.a(context, k02));
        } catch (RemoteException e4) {
            E1.m.e("Failed to load ad.", e4);
        }
    }
}
