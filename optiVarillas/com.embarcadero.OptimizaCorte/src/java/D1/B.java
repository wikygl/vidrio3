package D1;

import com.google.android.gms.internal.ads.J5;
import com.google.android.gms.internal.ads.N5;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class B implements J5 {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ String f625j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ D f626k;

    public B(String str, D d4) {
        this.f625j = str;
        this.f626k = d4;
    }

    public final void d(N5 n5) {
        String obj = n5.toString();
        E1.m.g("Failed to load URL: " + this.f625j + "\n" + obj);
        this.f626k.b((Object) null);
    }
}
