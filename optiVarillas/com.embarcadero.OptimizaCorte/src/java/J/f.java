package J;

import J.j;
import android.content.Context;
import java.util.concurrent.Callable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class f implements Callable<j.a> {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ String f1163a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f1164b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ e f1165c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ int f1166d;

    public f(String str, Context context, e eVar, int i4) {
        this.f1163a = str;
        this.f1164b = context;
        this.f1165c = eVar;
        this.f1166d = i4;
    }

    @Override // java.util.concurrent.Callable
    public final j.a call() {
        return j.a(this.f1163a, this.f1164b, this.f1165c, this.f1166d);
    }
}
