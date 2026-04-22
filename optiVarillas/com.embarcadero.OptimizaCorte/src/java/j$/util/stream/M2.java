package j$.util.stream;

import j$.util.Collection;
import j$.util.List;
import j$.util.Objects;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class M2 extends E2 {

    /* renamed from: d  reason: collision with root package name */
    private ArrayList f4333d;

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f4333d.add(obj);
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public final void k() {
        ArrayList arrayList = this.f4333d;
        boolean z4 = arrayList instanceof List;
        Comparator comparator = this.f4272b;
        if (z4) {
            ((List) arrayList).sort(comparator);
        } else {
            List.CC.$default$sort(arrayList, comparator);
        }
        InterfaceC0599q2 interfaceC0599q2 = this.f4545a;
        interfaceC0599q2.l(this.f4333d.size());
        if (this.f4273c) {
            Iterator it = this.f4333d.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (interfaceC0599q2.n()) {
                    break;
                }
                interfaceC0599q2.accept((InterfaceC0599q2) next);
            }
        } else {
            ArrayList arrayList2 = this.f4333d;
            Objects.requireNonNull(interfaceC0599q2);
            Collection.EL.a(arrayList2, new C0516a(interfaceC0599q2, 1));
        }
        interfaceC0599q2.k();
        this.f4333d = null;
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4333d = j4 >= 0 ? new ArrayList((int) j4) : new ArrayList();
    }
}
