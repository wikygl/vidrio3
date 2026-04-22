package J;

import J.j;
import android.content.Context;
import java.util.concurrent.Callable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class h implements Callable<j.a> {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ String f1168a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f1169b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ e f1170c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ int f1171d;

    public h(String str, Context context, e eVar, int i4) {
        this.f1168a = str;
        this.f1169b = context;
        this.f1170c = eVar;
        this.f1171d = i4;
    }

    @Override // java.util.concurrent.Callable
    public final j.a call() {
        try {
            return j.a(this.f1168a, this.f1169b, this.f1170c, this.f1171d);
        } catch (Throwable unused) {
            return new j.a(-3);
        }
    }
}
