package j$.util.concurrent;

import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class k implements Map.Entry {

    /* renamed from: a  reason: collision with root package name */
    final Object f4161a;

    /* renamed from: b  reason: collision with root package name */
    Object f4162b;

    /* renamed from: c  reason: collision with root package name */
    final ConcurrentHashMap f4163c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public k(Object obj, Object obj2, ConcurrentHashMap concurrentHashMap) {
        this.f4161a = obj;
        this.f4162b = obj2;
        this.f4163c = concurrentHashMap;
    }

    @Override // java.util.Map.Entry
    public final boolean equals(Object obj) {
        Map.Entry entry;
        Object key;
        Object value;
        Object obj2;
        Object obj3;
        return (obj instanceof Map.Entry) && (key = (entry = (Map.Entry) obj).getKey()) != null && (value = entry.getValue()) != null && (key == (obj2 = this.f4161a) || key.equals(obj2)) && (value == (obj3 = this.f4162b) || value.equals(obj3));
    }

    @Override // java.util.Map.Entry
    public final Object getKey() {
        return this.f4161a;
    }

    @Override // java.util.Map.Entry
    public final Object getValue() {
        return this.f4162b;
    }

    @Override // java.util.Map.Entry
    public final int hashCode() {
        return this.f4161a.hashCode() ^ this.f4162b.hashCode();
    }

    @Override // java.util.Map.Entry
    public final Object setValue(Object obj) {
        obj.getClass();
        Object obj2 = this.f4162b;
        this.f4162b = obj;
        this.f4163c.put(this.f4161a, obj);
        return obj2;
    }

    public final String toString() {
        return u.b(this.f4161a, this.f4162b);
    }
}
