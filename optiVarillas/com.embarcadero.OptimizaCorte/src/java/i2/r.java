package i2;

import android.os.Handler;
import android.webkit.WebView;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class r extends WebView {

    /* renamed from: m  reason: collision with root package name */
    public static final /* synthetic */ int f3796m = 0;

    /* renamed from: j  reason: collision with root package name */
    public final Handler f3797j;

    /* renamed from: k  reason: collision with root package name */
    public final C0475v f3798k;

    /* renamed from: l  reason: collision with root package name */
    public boolean f3799l;

    public r(C0473t c0473t, Handler handler, C0475v c0475v) {
        super(c0473t);
        this.f3799l = false;
        this.f3797j = handler;
        this.f3798k = c0475v;
    }

    public final void a(String str, String str2) {
        this.f3797j.post(new E0.a(this, str + "(" + str2 + ");", 8, false));
    }
}
