package D1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.google.android.gms.internal.ads.Em;
import com.google.android.gms.internal.ads.Fu;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.az;
import com.google.android.gms.internal.ads.bx;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.mH;
import com.google.android.gms.internal.ads.yG;
import com.google.android.gms.internal.ads.yk;
import r1.InterfaceC0782a;
import r1.b;
import r1.c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class o0 implements Em, mH, k3.a {

    /* renamed from: j  reason: collision with root package name */
    public final Object f756j;

    /* renamed from: k  reason: collision with root package name */
    public final Object f757k;

    /* renamed from: l  reason: collision with root package name */
    public final Object f758l;

    public /* synthetic */ o0(Object obj, Object obj2, Object obj3) {
        this.f756j = obj;
        this.f757k = obj2;
        this.f758l = obj3;
    }

    public Object d(Object obj) {
        az azVar = (az) this.f756j;
        azVar.getClass();
        azVar.k.execute(new C1.z((SQLiteDatabase) obj, (String) this.f758l, (E1.q) this.f757k, 2));
        return null;
    }

    @Override // k3.a
    public Object get() {
        return new j1.i((Context) ((k3.a) this.f756j).get(), (InterfaceC0782a) ((k3.a) this.f757k).get(), (InterfaceC0782a) ((k3.a) this.f758l).get());
    }

    public void h(String str, int i4, String str2, boolean z4) {
        Fu fu = (Fu) this.f756j;
        fu.getClass();
        boolean booleanValue = ((Boolean) A1.r.f168d.f171c.a(Gb.x3)).booleanValue();
        yG yGVar = fu.a;
        em emVar = (em) this.f757k;
        yk ykVar = (yk) this.f758l;
        if (booleanValue) {
            if (z4) {
                if (yGVar.a != null && emVar.q() != null) {
                    emVar.q().E4(yGVar.a);
                }
                ykVar.d();
                return;
            }
            ykVar.c(new bx("Native Video WebView failed to load. Error code: " + i4 + ", Description: " + str + ", Failing URL: " + str2, 1));
            return;
        }
        if (yGVar.a != null && emVar.q() != null) {
            emVar.q().E4(yGVar.a);
        }
        ykVar.d();
    }

    public o0(i2.Y y4) {
        r1.b bVar = b.a.f5707a;
        r1.c cVar = c.a.f5708a;
        this.f756j = y4;
        this.f757k = bVar;
        this.f758l = cVar;
    }
}
