package I;

import C3.C0155e;
import android.os.OutcomeReceiver;
import java.lang.Throwable;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class e<R, E extends Throwable> extends AtomicBoolean implements OutcomeReceiver<R, E> {

    /* renamed from: j  reason: collision with root package name */
    public final n3.d<R> f1133j;

    public e(C0155e c0155e) {
        super(false);
        this.f1133j = c0155e;
    }

    @Override // android.os.OutcomeReceiver
    public final void onError(E e4) {
        if (compareAndSet(false, true)) {
            this.f1133j.j(B2.a.a(e4));
        }
    }

    @Override // android.os.OutcomeReceiver
    public final void onResult(R r4) {
        if (compareAndSet(false, true)) {
            this.f1133j.j(r4);
        }
    }

    @Override // java.util.concurrent.atomic.AtomicBoolean
    public final String toString() {
        return "ContinuationOutcomeReceiver(outcomeReceived = " + get() + ')';
    }
}
