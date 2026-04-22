package j$.time.zone;

import j$.time.B;
import j$.time.Duration;
import j$.time.chrono.AbstractC0491i;
import j$.time.k;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class b implements Comparable, Serializable {
    private static final long serialVersionUID = -6946044323557704546L;

    /* renamed from: a  reason: collision with root package name */
    private final long f4048a;

    /* renamed from: b  reason: collision with root package name */
    private final k f4049b;

    /* renamed from: c  reason: collision with root package name */
    private final B f4050c;

    /* renamed from: d  reason: collision with root package name */
    private final B f4051d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public b(long j4, B b4, B b5) {
        this.f4048a = j4;
        this.f4049b = k.M(j4, 0, b4);
        this.f4050c = b4;
        this.f4051d = b5;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public b(k kVar, B b4, B b5) {
        kVar.getClass();
        this.f4048a = AbstractC0491i.n(kVar, b4);
        this.f4049b = kVar;
        this.f4050c = b4;
        this.f4051d = b5;
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new a((byte) 2, this);
    }

    public final long C() {
        return this.f4048a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void D(DataOutput dataOutput) {
        a.c(this.f4048a, dataOutput);
        a.d(this.f4050c, dataOutput);
        a.d(this.f4051d, dataOutput);
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        return Long.compare(this.f4048a, ((b) obj).f4048a);
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof b) {
            b bVar = (b) obj;
            return this.f4048a == bVar.f4048a && this.f4050c.equals(bVar.f4050c) && this.f4051d.equals(bVar.f4051d);
        }
        return false;
    }

    public final int hashCode() {
        return (this.f4049b.hashCode() ^ this.f4050c.hashCode()) ^ Integer.rotateLeft(this.f4051d.hashCode(), 16);
    }

    public final k j() {
        return this.f4049b.O(this.f4051d.J() - this.f4050c.J());
    }

    public final k l() {
        return this.f4049b;
    }

    public final Duration m() {
        return Duration.r(this.f4051d.J() - this.f4050c.J());
    }

    public final B r() {
        return this.f4051d;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("Transition[");
        sb.append(z() ? "Gap" : "Overlap");
        sb.append(" at ");
        sb.append(this.f4049b);
        sb.append(this.f4050c);
        sb.append(" to ");
        sb.append(this.f4051d);
        sb.append(']');
        return sb.toString();
    }

    public final B u() {
        return this.f4050c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List v() {
        return z() ? Collections.emptyList() : j$.com.android.tools.r8.a.k(new Object[]{this.f4050c, this.f4051d});
    }

    public final boolean z() {
        return this.f4051d.J() > this.f4050c.J();
    }
}
