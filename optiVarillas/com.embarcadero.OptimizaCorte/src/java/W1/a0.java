package W1;

import android.util.SparseBooleanArray;
import com.google.android.gms.internal.ads.P1;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a0 {

    /* renamed from: a  reason: collision with root package name */
    public boolean f2680a;

    /* renamed from: b  reason: collision with root package name */
    public final Object f2681b;

    public a0(String str, boolean z4) {
        this.f2681b = str;
        this.f2680a = z4;
    }

    public void a(int i4) {
        com.google.android.gms.internal.ads.T.v(!this.f2680a);
        ((SparseBooleanArray) this.f2681b).append(i4, true);
    }

    public P1 b() {
        com.google.android.gms.internal.ads.T.v(!this.f2680a);
        this.f2680a = true;
        return new P1((SparseBooleanArray) this.f2681b);
    }

    public a0() {
        this.f2681b = new SparseBooleanArray();
    }
}
