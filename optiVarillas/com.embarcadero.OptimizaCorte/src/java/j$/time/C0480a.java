package j$.time;

import java.io.ObjectInputStream;
import java.io.Serializable;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.time.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0480a extends AbstractC0481b implements Serializable {

    /* renamed from: b  reason: collision with root package name */
    static final C0480a f3852b;
    private static final long serialVersionUID = 6740630888130243051L;

    /* renamed from: a  reason: collision with root package name */
    private final A f3853a;

    static {
        System.currentTimeMillis();
        f3852b = new C0480a(B.f3838e);
    }

    C0480a(A a4) {
        this.f3853a = a4;
    }

    private void readObject(ObjectInputStream objectInputStream) {
        objectInputStream.defaultReadObject();
    }

    @Override // j$.time.AbstractC0481b
    public final long a() {
        return System.currentTimeMillis();
    }

    public final boolean equals(Object obj) {
        if (obj instanceof C0480a) {
            return this.f3853a.equals(((C0480a) obj).f3853a);
        }
        return false;
    }

    public final int hashCode() {
        return this.f3853a.hashCode() + 1;
    }

    public final String toString() {
        return "SystemClock[" + this.f3853a + "]";
    }
}
