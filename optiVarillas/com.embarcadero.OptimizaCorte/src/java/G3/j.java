package G3;

import C3.C;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class j extends h {

    /* renamed from: l  reason: collision with root package name */
    public final Runnable f998l;

    public j(Runnable runnable, long j4, i iVar) {
        super(j4, iVar);
        this.f998l = runnable;
    }

    @Override // java.lang.Runnable
    public final void run() {
        try {
            this.f998l.run();
        } finally {
            this.f996k.getClass();
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("Task[");
        Runnable runnable = this.f998l;
        sb.append(runnable.getClass().getSimpleName());
        sb.append('@');
        sb.append(C.c(runnable));
        sb.append(", ");
        sb.append(this.f995j);
        sb.append(", ");
        sb.append(this.f996k);
        sb.append(']');
        return sb.toString();
    }
}
