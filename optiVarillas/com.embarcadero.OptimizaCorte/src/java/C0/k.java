package C0;

import C0.q;
import androidx.work.ListenableWorker;
import androidx.work.OverwritingInputMerger;
import java.util.HashSet;
import java.util.UUID;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class k extends q {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class a extends q.a<a, k> {
        public a(Class<? extends ListenableWorker> cls) {
            this.f349c = new HashSet();
            this.f347a = UUID.randomUUID();
            this.f348b = new L0.p(this.f347a.toString(), cls.getName());
            this.f349c.add(cls.getName());
            this.f348b.f1453d = OverwritingInputMerger.class.getName();
        }
    }
}
