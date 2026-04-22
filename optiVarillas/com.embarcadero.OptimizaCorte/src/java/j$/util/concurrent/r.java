package j$.util.concurrent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class r extends l {

    /* renamed from: e  reason: collision with root package name */
    r f4183e;
    r f;

    /* renamed from: g  reason: collision with root package name */
    r f4184g;

    /* renamed from: h  reason: collision with root package name */
    r f4185h;

    /* renamed from: i  reason: collision with root package name */
    boolean f4186i;

    /* JADX INFO: Access modifiers changed from: package-private */
    public r(int i4, Object obj, Object obj2, l lVar, r rVar) {
        super(i4, obj, obj2, lVar);
        this.f4183e = rVar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.concurrent.l
    public final l a(Object obj, int i4) {
        return b(i4, obj, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final r b(int i4, Object obj, Class cls) {
        if (obj != null) {
            r rVar = this;
            do {
                r rVar2 = rVar.f;
                r rVar3 = rVar.f4184g;
                int i5 = rVar.f4164a;
                if (i5 <= i4) {
                    if (i5 >= i4) {
                        Object obj2 = rVar.f4165b;
                        if (obj2 == obj || (obj2 != null && obj.equals(obj2))) {
                            return rVar;
                        }
                        if (rVar2 != null) {
                            if (rVar3 != null) {
                                if (cls != null || (cls = ConcurrentHashMap.c(obj)) != null) {
                                    int i6 = ConcurrentHashMap.f4132g;
                                    int compareTo = (obj2 == null || obj2.getClass() != cls) ? 0 : ((Comparable) obj).compareTo(obj2);
                                    if (compareTo != 0) {
                                        if (compareTo >= 0) {
                                            rVar2 = rVar3;
                                        }
                                    }
                                }
                                r b4 = rVar3.b(i4, obj, cls);
                                if (b4 != null) {
                                    return b4;
                                }
                            }
                        }
                    }
                    rVar = rVar3;
                    continue;
                }
                rVar = rVar2;
                continue;
            } while (rVar != null);
            return null;
        }
        return null;
    }
}
