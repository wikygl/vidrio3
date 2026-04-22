package o;

import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class b implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f5389j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Bundle f5390k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ e f5391l;

    public b(e eVar, int i4, Bundle bundle) {
        this.f5391l = eVar;
        this.f5389j = i4;
        this.f5390k = bundle;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f5391l.f5401k.d(this.f5389j, this.f5390k);
    }
}
