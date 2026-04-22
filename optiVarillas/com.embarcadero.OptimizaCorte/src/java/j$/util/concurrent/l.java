package j$.util.concurrent;

import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class l implements Map.Entry {

    /* renamed from: a  reason: collision with root package name */
    final int f4164a;

    /* renamed from: b  reason: collision with root package name */
    final Object f4165b;

    /* renamed from: c  reason: collision with root package name */
    volatile Object f4166c;

    /* renamed from: d  reason: collision with root package name */
    volatile l f4167d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public l(int i4, Object obj, Object obj2) {
        this.f4164a = i4;
        this.f4165b = obj;
        this.f4166c = obj2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public l(int i4, Object obj, Object obj2, l lVar) {
        this(i4, obj, obj2);
        this.f4167d = lVar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public l a(Object obj, int i4) {
        Object obj2;
        if (obj != null) {
            l lVar = this;
            do {
                if (lVar.f4164a == i4 && ((obj2 = lVar.f4165b) == obj || (obj2 != null && obj.equals(obj2)))) {
                    return lVar;
                }
                lVar = lVar.f4167d;
            } while (lVar != null);
            return null;
        }
        return null;
    }

    @Override // java.util.Map.Entry
    public final boolean equals(Object obj) {
        Map.Entry entry;
        Object key;
        Object value;
        Object obj2;
        Object obj3;
        return (obj instanceof Map.Entry) && (key = (entry = (Map.Entry) obj).getKey()) != null && (value = entry.getValue()) != null && (key == (obj2 = this.f4165b) || key.equals(obj2)) && (value == (obj3 = this.f4166c) || value.equals(obj3));
    }

    @Override // java.util.Map.Entry
    public final Object getKey() {
        return this.f4165b;
    }

    @Override // java.util.Map.Entry
    public final Object getValue() {
        return this.f4166c;
    }

    @Override // java.util.Map.Entry
    public final int hashCode() {
        return this.f4165b.hashCode() ^ this.f4166c.hashCode();
    }

    @Override // java.util.Map.Entry
    public final Object setValue(Object obj) {
        throw new UnsupportedOperationException();
    }

    public final String toString() {
        return u.b(this.f4165b, this.f4166c);
    }
}
