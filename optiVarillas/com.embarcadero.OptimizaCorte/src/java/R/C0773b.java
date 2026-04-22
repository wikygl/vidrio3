package r;

import j$.util.Map;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import r.i;

/* renamed from: r.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0773b<K, V> extends j<K, V> implements Map<K, V>, j$.util.Map {

    /* renamed from: q  reason: collision with root package name */
    public C0772a f5636q;

    @Override // java.util.Map, j$.util.Map, java.util.concurrent.ConcurrentMap
    public final /* synthetic */ Object compute(Object obj, BiFunction biFunction) {
        return Map.CC.$default$compute(this, obj, biFunction);
    }

    @Override // java.util.Map, j$.util.Map, java.util.concurrent.ConcurrentMap
    public final /* synthetic */ Object computeIfAbsent(Object obj, Function function) {
        return Map.CC.$default$computeIfAbsent(this, obj, function);
    }

    @Override // java.util.Map, j$.util.Map, java.util.concurrent.ConcurrentMap
    public final /* synthetic */ Object computeIfPresent(Object obj, BiFunction biFunction) {
        return Map.CC.$default$computeIfPresent(this, obj, biFunction);
    }

    @Override // java.util.Map
    public final Set<Map.Entry<K, V>> entrySet() {
        if (this.f5636q == null) {
            this.f5636q = new C0772a(this);
        }
        C0772a c0772a = this.f5636q;
        if (c0772a.f5664a == null) {
            c0772a.f5664a = new i.b();
        }
        return c0772a.f5664a;
    }

    @Override // java.util.Map, j$.util.Map, java.util.concurrent.ConcurrentMap
    public final /* synthetic */ void forEach(BiConsumer biConsumer) {
        Map.CC.$default$forEach(this, biConsumer);
    }

    @Override // java.util.Map
    public final Set<K> keySet() {
        if (this.f5636q == null) {
            this.f5636q = new C0772a(this);
        }
        C0772a c0772a = this.f5636q;
        if (c0772a.f5665b == null) {
            c0772a.f5665b = new i.c();
        }
        return c0772a.f5665b;
    }

    @Override // java.util.Map, j$.util.Map, java.util.concurrent.ConcurrentMap
    public final /* synthetic */ Object merge(Object obj, Object obj2, BiFunction biFunction) {
        return Map.CC.$default$merge(this, obj, obj2, biFunction);
    }

    @Override // java.util.Map
    public final void putAll(java.util.Map<? extends K, ? extends V> map) {
        b(map.size() + this.f5685l);
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override // java.util.Map, j$.util.Map, java.util.concurrent.ConcurrentMap
    public final /* synthetic */ void replaceAll(BiFunction biFunction) {
        Map.CC.$default$replaceAll(this, biFunction);
    }

    @Override // java.util.Map
    public final Collection<V> values() {
        if (this.f5636q == null) {
            this.f5636q = new C0772a(this);
        }
        C0772a c0772a = this.f5636q;
        if (c0772a.f5666c == null) {
            c0772a.f5666c = new i.e();
        }
        return c0772a.f5666c;
    }
}
