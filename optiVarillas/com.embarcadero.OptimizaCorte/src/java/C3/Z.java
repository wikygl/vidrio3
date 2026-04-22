package C3;

import java.util.concurrent.CancellationException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class Z extends CancellationException {

    /* renamed from: j  reason: collision with root package name */
    public final transient Y f451j;

    public Z(String str, Throwable th, Y y4) {
        super(str);
        this.f451j = y4;
        if (th != null) {
            initCause(th);
        }
    }

    public final boolean equals(Object obj) {
        if (obj != this) {
            if (obj instanceof Z) {
                Z z4 = (Z) obj;
                if (!v3.h.a(z4.getMessage(), getMessage()) || !v3.h.a(z4.f451j, this.f451j) || !v3.h.a(z4.getCause(), getCause())) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // java.lang.Throwable
    public final Throwable fillInStackTrace() {
        setStackTrace(new StackTraceElement[0]);
        return this;
    }

    public final int hashCode() {
        int i4;
        String message = getMessage();
        v3.h.b(message);
        int hashCode = (this.f451j.hashCode() + (message.hashCode() * 31)) * 31;
        Throwable cause = getCause();
        if (cause != null) {
            i4 = cause.hashCode();
        } else {
            i4 = 0;
        }
        return hashCode + i4;
    }

    @Override // java.lang.Throwable
    public final String toString() {
        return super.toString() + "; job=" + this.f451j;
    }
}
