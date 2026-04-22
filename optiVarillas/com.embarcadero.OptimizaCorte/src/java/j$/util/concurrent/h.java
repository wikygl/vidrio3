package j$.util.concurrent;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class h extends AbstractC0500a implements Iterator, Enumeration {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ int f4158k;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ h(l[] lVarArr, int i4, int i5, ConcurrentHashMap concurrentHashMap, int i6) {
        super(lVarArr, i4, i5, concurrentHashMap);
        this.f4158k = i6;
    }

    @Override // java.util.Iterator
    public final Object next() {
        switch (this.f4158k) {
            case 0:
                l lVar = this.f4173b;
                if (lVar != null) {
                    this.f4153j = lVar;
                    a();
                    return lVar.f4165b;
                }
                throw new NoSuchElementException();
            default:
                l lVar2 = this.f4173b;
                if (lVar2 != null) {
                    Object obj = lVar2.f4166c;
                    this.f4153j = lVar2;
                    a();
                    return obj;
                }
                throw new NoSuchElementException();
        }
    }

    @Override // java.util.Enumeration
    public final Object nextElement() {
        switch (this.f4158k) {
            case 0:
                return next();
            default:
                return next();
        }
    }
}
