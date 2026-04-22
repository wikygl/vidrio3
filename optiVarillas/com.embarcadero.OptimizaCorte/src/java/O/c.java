package o;

import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class c implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ String f5392j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Bundle f5393k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ e f5394l;

    public c(e eVar, String str, Bundle bundle) {
        this.f5394l = eVar;
        this.f5392j = str;
        this.f5393k = bundle;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f5394l.f5401k.a(this.f5392j, this.f5393k);
    }
}
