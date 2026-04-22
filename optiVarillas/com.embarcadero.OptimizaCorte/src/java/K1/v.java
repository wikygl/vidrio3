package K1;

import android.content.Context;
import com.google.android.gms.internal.ads.CA;
import com.google.android.gms.internal.ads.Tv;
import com.google.android.gms.internal.ads.ft;
import com.google.android.gms.internal.ads.mB;
import com.google.android.gms.internal.ads.qX;
import com.google.android.gms.internal.ads.tA;
import com.google.android.gms.internal.ads.vX;
import com.google.android.gms.internal.ads.zX;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class v implements qX {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f1411a;

    /* renamed from: b  reason: collision with root package name */
    public final zX f1412b;

    /* renamed from: c  reason: collision with root package name */
    public final zX f1413c;

    /* renamed from: d  reason: collision with root package name */
    public final zX f1414d;

    public /* synthetic */ v(zX zXVar, vX vXVar, zX zXVar2, int i4) {
        this.f1411a = i4;
        this.f1412b = zXVar;
        this.f1413c = vXVar;
        this.f1414d = zXVar2;
    }

    public final /* synthetic */ Object c() {
        switch (this.f1411a) {
            case 0:
                return new u((Tv) this.f1412b.c(), (t) this.f1413c.c(), (String) this.f1414d.c());
            case 1:
                boolean booleanValue = this.f1412b.a().booleanValue();
                CA a4 = this.f1413c.a();
                mB a5 = this.f1414d.a();
                if (!booleanValue) {
                    return a5;
                }
                return a4;
            default:
                return new tA((Context) this.f1412b.c(), (ft) this.f1413c.c(), this.f1414d.a());
        }
    }
}
