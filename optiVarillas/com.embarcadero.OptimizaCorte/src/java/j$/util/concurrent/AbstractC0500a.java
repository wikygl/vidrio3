package j$.util.concurrent;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.concurrent.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class AbstractC0500a extends p {

    /* renamed from: i  reason: collision with root package name */
    final ConcurrentHashMap f4152i;

    /* renamed from: j  reason: collision with root package name */
    l f4153j;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractC0500a(l[] lVarArr, int i4, int i5, ConcurrentHashMap concurrentHashMap) {
        super(lVarArr, i4, 0, i5);
        this.f4152i = concurrentHashMap;
        a();
    }

    public final boolean hasMoreElements() {
        return this.f4173b != null;
    }

    public final boolean hasNext() {
        return this.f4173b != null;
    }

    public final void remove() {
        l lVar = this.f4153j;
        if (lVar == null) {
            throw new IllegalStateException();
        }
        this.f4153j = null;
        this.f4152i.g(lVar.f4165b, null, null);
    }
}
