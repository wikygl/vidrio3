package z1;

import android.content.Context;
import com.google.android.gms.internal.ads.q7;
import com.google.android.gms.internal.ads.s7;
import java.util.concurrent.Callable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class l implements Callable {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ o f6559a;

    public l(o oVar) {
        this.f6559a = oVar;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        o oVar = this.f6559a;
        String str = oVar.f6566j.f844j;
        Context context = oVar.f6569m;
        q7.t(context, false);
        return new s7(new q7(context, str, false));
    }
}
