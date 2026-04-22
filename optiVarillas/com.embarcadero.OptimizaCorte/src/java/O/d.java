package o;

import android.net.Uri;
import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class d implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f5395j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Uri f5396k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ boolean f5397l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ Bundle f5398m;

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ e f5399n;

    public d(e eVar, int i4, Uri uri, boolean z4, Bundle bundle) {
        this.f5399n = eVar;
        this.f5395j = i4;
        this.f5396k = uri;
        this.f5397l = z4;
        this.f5398m = bundle;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f5399n.f5401k.f(this.f5395j, this.f5396k, this.f5397l, this.f5398m);
    }
}
