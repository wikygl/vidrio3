package p2;

import W1.C0324l;

/* renamed from: p2.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0758g<TResult> {

    /* renamed from: a  reason: collision with root package name */
    public final q f5552a = new q();

    public final void a(Exception exc) {
        q qVar = this.f5552a;
        qVar.getClass();
        C0324l.e(exc, "Exception must not be null");
        synchronized (qVar.f5572a) {
            try {
                if (!qVar.f5574c) {
                    qVar.f5574c = true;
                    qVar.f = exc;
                    qVar.f5573b.d(qVar);
                }
            } finally {
            }
        }
    }
}
