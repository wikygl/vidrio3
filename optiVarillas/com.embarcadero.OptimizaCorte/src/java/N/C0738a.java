package n;

import java.util.HashMap;
import n.C0739b;

/* renamed from: n.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0738a<K, V> extends C0739b<K, V> {

    /* renamed from: n  reason: collision with root package name */
    public final HashMap<K, C0739b.c<K, V>> f5350n = new HashMap<>();

    @Override // n.C0739b
    public final C0739b.c<K, V> j(K k4) {
        return this.f5350n.get(k4);
    }

    @Override // n.C0739b
    public final V k(K k4) {
        V v4 = (V) super.k(k4);
        this.f5350n.remove(k4);
        return v4;
    }

    public final V l(K k4, V v4) {
        C0739b.c<K, V> j4 = j(k4);
        if (j4 != null) {
            return j4.f5356k;
        }
        HashMap<K, C0739b.c<K, V>> hashMap = this.f5350n;
        C0739b.c<K, V> cVar = new C0739b.c<>(k4, v4);
        this.f5354m++;
        C0739b.c<K, V> cVar2 = this.f5352k;
        if (cVar2 == null) {
            this.f5351j = cVar;
            this.f5352k = cVar;
        } else {
            cVar2.f5357l = cVar;
            cVar.f5358m = cVar2;
            this.f5352k = cVar;
        }
        hashMap.put(k4, cVar);
        return null;
    }
}
