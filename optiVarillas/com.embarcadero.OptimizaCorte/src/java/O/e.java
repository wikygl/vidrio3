package o;

import a.InterfaceC0338a;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.internal.ads.Zb;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class e extends InterfaceC0338a.AbstractBinderC0036a {

    /* renamed from: j  reason: collision with root package name */
    public final Handler f5400j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ C0745a f5401k;

    public e(Zb zb) {
        this.f5401k = zb;
        attachInterface(this, "android.support.customtabs.ICustomTabsCallback");
        this.f5400j = new Handler(Looper.getMainLooper());
    }
}
