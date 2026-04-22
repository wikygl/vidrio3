package b3;

import java.util.Collections;
import java.util.Map;

/* renamed from: b3.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0358c {

    /* renamed from: a  reason: collision with root package name */
    public final String f2927a;

    /* renamed from: b  reason: collision with root package name */
    public final Map<Class<?>, Object> f2928b;

    public C0358c(String str, Map<Class<?>, Object> map) {
        this.f2927a = str;
        this.f2928b = map;
    }

    public static C0358c a(String str) {
        return new C0358c(str, Collections.emptyMap());
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0358c)) {
            return false;
        }
        C0358c c0358c = (C0358c) obj;
        if (this.f2927a.equals(c0358c.f2927a) && this.f2928b.equals(c0358c.f2928b)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return this.f2928b.hashCode() + (this.f2927a.hashCode() * 31);
    }

    public final String toString() {
        return "FieldDescriptor{name=" + this.f2927a + ", properties=" + this.f2928b.values() + "}";
    }
}
