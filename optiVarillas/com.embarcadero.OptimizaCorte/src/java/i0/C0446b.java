package i0;

import j$.util.Objects;
import java.util.HashSet;
import java.util.List;

/* renamed from: i0.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0446b {

    /* renamed from: a  reason: collision with root package name */
    public final List<C0447c> f3595a;

    public C0446b(List<C0447c> list) {
        v3.h.e(list, "topics");
        this.f3595a = list;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0446b)) {
            return false;
        }
        List<C0447c> list = this.f3595a;
        C0446b c0446b = (C0446b) obj;
        if (list.size() != c0446b.f3595a.size()) {
            return false;
        }
        return new HashSet(list).equals(new HashSet(c0446b.f3595a));
    }

    public final int hashCode() {
        return Objects.hash(this.f3595a);
    }

    public final String toString() {
        return "Topics=" + this.f3595a;
    }
}
