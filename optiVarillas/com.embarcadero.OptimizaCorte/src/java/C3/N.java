package C3;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class N extends AbstractC0171v {

    /* renamed from: l  reason: collision with root package name */
    public long f434l;

    /* renamed from: m  reason: collision with root package name */
    public boolean f435m;

    /* renamed from: n  reason: collision with root package name */
    public m3.a<J<?>> f436n;

    public final void H() {
        long j4 = this.f434l - 4294967296L;
        this.f434l = j4;
        if (j4 <= 0 && this.f435m) {
            shutdown();
        }
    }

    public final void I(boolean z4) {
        long j4;
        long j5 = this.f434l;
        if (z4) {
            j4 = 4294967296L;
        } else {
            j4 = 1;
        }
        this.f434l = j4 + j5;
        if (!z4) {
            this.f435m = true;
        }
    }

    public final boolean J() {
        J<?> o4;
        m3.a<J<?>> aVar = this.f436n;
        if (aVar == null) {
            return false;
        }
        if (aVar.isEmpty()) {
            o4 = null;
        } else {
            o4 = aVar.o();
        }
        J<?> j4 = o4;
        if (j4 == null) {
            return false;
        }
        j4.run();
        return true;
    }

    public void shutdown() {
    }
}
