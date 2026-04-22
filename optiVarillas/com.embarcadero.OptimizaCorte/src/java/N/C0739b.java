package n;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/* renamed from: n.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class C0739b<K, V> implements Iterable<Map.Entry<K, V>> {

    /* renamed from: j  reason: collision with root package name */
    public c<K, V> f5351j;

    /* renamed from: k  reason: collision with root package name */
    public c<K, V> f5352k;

    /* renamed from: l  reason: collision with root package name */
    public final WeakHashMap<f<K, V>, Boolean> f5353l = new WeakHashMap<>();

    /* renamed from: m  reason: collision with root package name */
    public int f5354m = 0;

    /* renamed from: n.b$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class a<K, V> extends e<K, V> {
        @Override // n.C0739b.e
        public final c<K, V> b(c<K, V> cVar) {
            return cVar.f5358m;
        }

        @Override // n.C0739b.e
        public final c<K, V> c(c<K, V> cVar) {
            return cVar.f5357l;
        }
    }

    /* renamed from: n.b$b  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class C0059b<K, V> extends e<K, V> {
        @Override // n.C0739b.e
        public final c<K, V> b(c<K, V> cVar) {
            return cVar.f5357l;
        }

        @Override // n.C0739b.e
        public final c<K, V> c(c<K, V> cVar) {
            return cVar.f5358m;
        }
    }

    /* renamed from: n.b$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class c<K, V> implements Map.Entry<K, V> {

        /* renamed from: j  reason: collision with root package name */
        public final K f5355j;

        /* renamed from: k  reason: collision with root package name */
        public final V f5356k;

        /* renamed from: l  reason: collision with root package name */
        public c<K, V> f5357l;

        /* renamed from: m  reason: collision with root package name */
        public c<K, V> f5358m;

        public c(K k4, V v4) {
            this.f5355j = k4;
            this.f5356k = v4;
        }

        @Override // java.util.Map.Entry
        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof c)) {
                return false;
            }
            c cVar = (c) obj;
            if (this.f5355j.equals(cVar.f5355j) && this.f5356k.equals(cVar.f5356k)) {
                return true;
            }
            return false;
        }

        @Override // java.util.Map.Entry
        public final K getKey() {
            return this.f5355j;
        }

        @Override // java.util.Map.Entry
        public final V getValue() {
            return this.f5356k;
        }

        @Override // java.util.Map.Entry
        public final int hashCode() {
            return this.f5355j.hashCode() ^ this.f5356k.hashCode();
        }

        @Override // java.util.Map.Entry
        public final V setValue(V v4) {
            throw new UnsupportedOperationException("An entry modification is not supported");
        }

        public final String toString() {
            return this.f5355j + "=" + this.f5356k;
        }
    }

    /* renamed from: n.b$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class d extends f<K, V> implements Iterator<Map.Entry<K, V>> {

        /* renamed from: j  reason: collision with root package name */
        public c<K, V> f5359j;

        /* renamed from: k  reason: collision with root package name */
        public boolean f5360k = true;

        public d() {
        }

        @Override // n.C0739b.f
        public final void a(c<K, V> cVar) {
            boolean z4;
            c<K, V> cVar2 = this.f5359j;
            if (cVar == cVar2) {
                c<K, V> cVar3 = cVar2.f5358m;
                this.f5359j = cVar3;
                if (cVar3 == null) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                this.f5360k = z4;
            }
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            if (this.f5360k) {
                if (C0739b.this.f5351j == null) {
                    return false;
                }
                return true;
            }
            c<K, V> cVar = this.f5359j;
            if (cVar == null || cVar.f5357l == null) {
                return false;
            }
            return true;
        }

        @Override // java.util.Iterator
        public final Object next() {
            c<K, V> cVar;
            if (this.f5360k) {
                this.f5360k = false;
                this.f5359j = C0739b.this.f5351j;
            } else {
                c<K, V> cVar2 = this.f5359j;
                if (cVar2 != null) {
                    cVar = cVar2.f5357l;
                } else {
                    cVar = null;
                }
                this.f5359j = cVar;
            }
            return this.f5359j;
        }
    }

    /* renamed from: n.b$e */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static abstract class e<K, V> extends f<K, V> implements Iterator<Map.Entry<K, V>> {

        /* renamed from: j  reason: collision with root package name */
        public c<K, V> f5362j;

        /* renamed from: k  reason: collision with root package name */
        public c<K, V> f5363k;

        public e(c<K, V> cVar, c<K, V> cVar2) {
            this.f5362j = cVar2;
            this.f5363k = cVar;
        }

        @Override // n.C0739b.f
        public final void a(c<K, V> cVar) {
            c<K, V> cVar2 = null;
            if (this.f5362j == cVar && cVar == this.f5363k) {
                this.f5363k = null;
                this.f5362j = null;
            }
            c<K, V> cVar3 = this.f5362j;
            if (cVar3 == cVar) {
                this.f5362j = b(cVar3);
            }
            c<K, V> cVar4 = this.f5363k;
            if (cVar4 == cVar) {
                c<K, V> cVar5 = this.f5362j;
                if (cVar4 != cVar5 && cVar5 != null) {
                    cVar2 = c(cVar4);
                }
                this.f5363k = cVar2;
            }
        }

        public abstract c<K, V> b(c<K, V> cVar);

        public abstract c<K, V> c(c<K, V> cVar);

        @Override // java.util.Iterator
        public final boolean hasNext() {
            if (this.f5363k != null) {
                return true;
            }
            return false;
        }

        @Override // java.util.Iterator
        public final Object next() {
            c<K, V> cVar;
            c<K, V> cVar2 = this.f5363k;
            c<K, V> cVar3 = this.f5362j;
            if (cVar2 != cVar3 && cVar3 != null) {
                cVar = c(cVar2);
            } else {
                cVar = null;
            }
            this.f5363k = cVar;
            return cVar2;
        }
    }

    /* renamed from: n.b$f */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static abstract class f<K, V> {
        public abstract void a(c<K, V> cVar);
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0048, code lost:
        if (r3.hasNext() != false) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0050, code lost:
        if (((n.C0739b.e) r7).hasNext() != false) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0053, code lost:
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:?, code lost:
        return true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean equals(java.lang.Object r7) {
        /*
            r6 = this;
            r0 = 1
            if (r7 != r6) goto L4
            return r0
        L4:
            boolean r1 = r7 instanceof n.C0739b
            r2 = 0
            if (r1 != 0) goto La
            return r2
        La:
            n.b r7 = (n.C0739b) r7
            int r1 = r6.f5354m
            int r3 = r7.f5354m
            if (r1 == r3) goto L13
            return r2
        L13:
            java.util.Iterator r1 = r6.iterator()
            java.util.Iterator r7 = r7.iterator()
        L1b:
            r3 = r1
            n.b$e r3 = (n.C0739b.e) r3
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L44
            r4 = r7
            n.b$e r4 = (n.C0739b.e) r4
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L44
            java.lang.Object r3 = r3.next()
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3
            java.lang.Object r4 = r4.next()
            if (r3 != 0) goto L3b
            if (r4 != 0) goto L43
        L3b:
            if (r3 == 0) goto L1b
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L1b
        L43:
            return r2
        L44:
            boolean r1 = r3.hasNext()
            if (r1 != 0) goto L53
            n.b$e r7 = (n.C0739b.e) r7
            boolean r7 = r7.hasNext()
            if (r7 != 0) goto L53
            goto L54
        L53:
            r0 = 0
        L54:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: n.C0739b.equals(java.lang.Object):boolean");
    }

    public final int hashCode() {
        Iterator<Map.Entry<K, V>> it = iterator();
        int i4 = 0;
        while (true) {
            e eVar = (e) it;
            if (eVar.hasNext()) {
                i4 += ((Map.Entry) eVar.next()).hashCode();
            } else {
                return i4;
            }
        }
    }

    @Override // java.lang.Iterable
    public final Iterator<Map.Entry<K, V>> iterator() {
        e eVar = new e(this.f5351j, this.f5352k);
        this.f5353l.put(eVar, Boolean.FALSE);
        return eVar;
    }

    public c<K, V> j(K k4) {
        c<K, V> cVar = this.f5351j;
        while (cVar != null && !cVar.f5355j.equals(k4)) {
            cVar = cVar.f5357l;
        }
        return cVar;
    }

    public V k(K k4) {
        c<K, V> j4 = j(k4);
        if (j4 == null) {
            return null;
        }
        this.f5354m--;
        WeakHashMap<f<K, V>, Boolean> weakHashMap = this.f5353l;
        if (!weakHashMap.isEmpty()) {
            for (f<K, V> fVar : weakHashMap.keySet()) {
                fVar.a(j4);
            }
        }
        c<K, V> cVar = j4.f5358m;
        if (cVar != null) {
            cVar.f5357l = j4.f5357l;
        } else {
            this.f5351j = j4.f5357l;
        }
        c<K, V> cVar2 = j4.f5357l;
        if (cVar2 != null) {
            cVar2.f5358m = cVar;
        } else {
            this.f5352k = cVar;
        }
        j4.f5357l = null;
        j4.f5358m = null;
        return j4.f5356k;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("[");
        Iterator<Map.Entry<K, V>> it = iterator();
        while (true) {
            e eVar = (e) it;
            if (eVar.hasNext()) {
                sb.append(((Map.Entry) eVar.next()).toString());
                if (eVar.hasNext()) {
                    sb.append(", ");
                }
            } else {
                sb.append("]");
                return sb.toString();
            }
        }
    }
}
