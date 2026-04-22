package D3;

import C3.K;
import C3.Y;
import C3.g0;
import F3.q;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.CancellationException;
import n3.f;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class c extends d {
    private volatile c _immediate;

    /* renamed from: l  reason: collision with root package name */
    public final Handler f797l;

    /* renamed from: m  reason: collision with root package name */
    public final String f798m;

    /* renamed from: n  reason: collision with root package name */
    public final boolean f799n;

    /* renamed from: o  reason: collision with root package name */
    public final c f800o;

    public c(Handler handler, String str, boolean z4) {
        this.f797l = handler;
        this.f798m = str;
        this.f799n = z4;
        this._immediate = z4 ? this : null;
        c cVar = this._immediate;
        if (cVar == null) {
            cVar = new c(handler, str, true);
            this._immediate = cVar;
        }
        this.f800o = cVar;
    }

    @Override // C3.AbstractC0171v
    public final void F(f fVar, Runnable runnable) {
        if (!this.f797l.post(runnable)) {
            CancellationException cancellationException = new CancellationException("The task was rejected, the handler underlying the dispatcher '" + this + "' was closed");
            Y y4 = (Y) fVar.E(Y.b.f450j);
            if (y4 != null) {
                y4.w(cancellationException);
            }
            K.f432b.F(fVar, runnable);
        }
    }

    @Override // C3.AbstractC0171v
    public final boolean G() {
        if (this.f799n && h.a(Looper.myLooper(), this.f797l.getLooper())) {
            return false;
        }
        return true;
    }

    @Override // C3.g0
    public final g0 H() {
        return this.f800o;
    }

    public final boolean equals(Object obj) {
        if ((obj instanceof c) && ((c) obj).f797l == this.f797l) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return System.identityHashCode(this.f797l);
    }

    @Override // C3.g0, C3.AbstractC0171v
    public final String toString() {
        g0 g0Var;
        String str;
        G3.c cVar = K.f431a;
        g0 g0Var2 = q.f944a;
        if (this == g0Var2) {
            str = "Dispatchers.Main";
        } else {
            try {
                g0Var = g0Var2.H();
            } catch (UnsupportedOperationException unused) {
                g0Var = null;
            }
            if (this == g0Var) {
                str = "Dispatchers.Main.immediate";
            } else {
                str = null;
            }
        }
        if (str == null) {
            String str2 = this.f798m;
            if (str2 == null) {
                str2 = this.f797l.toString();
            }
            if (this.f799n) {
                return I.h.c(str2, ".immediate");
            }
            return str2;
        }
        return str;
    }

    public c(Handler handler) {
        this(handler, null, false);
    }
}
