package C0;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import java.util.Collections;
import java.util.List;

@SuppressLint({"AddedAbstractMethod"})
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public abstract class p {
    public void a(k kVar) {
        List singletonList = Collections.singletonList(kVar);
        D0.k kVar2 = (D0.k) this;
        if (!singletonList.isEmpty()) {
            D0.f fVar = new D0.f(kVar2, singletonList);
            if (!fVar.f578h) {
                ((O0.b) kVar2.f588d).a(new M0.e(fVar));
                return;
            }
            i.c().f(D0.f.f571i, C.b.b("Already enqueued work ids (", TextUtils.join(", ", fVar.f576e), ")"), new Throwable[0]);
            return;
        }
        throw new IllegalArgumentException("enqueue needs at least one WorkRequest.");
    }
}
