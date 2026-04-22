package v3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import u3.q;
import u3.r;
import u3.s;
import u3.t;
import u3.u;
import u3.v;
import u3.w;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class c implements z3.b<Object>, b {

    /* renamed from: b  reason: collision with root package name */
    public static final Map<Class<? extends l3.a<?>>, Integer> f6306b;

    /* renamed from: a  reason: collision with root package name */
    public final Class<?> f6307a;

    static {
        int i4 = 0;
        List asList = Arrays.asList(u3.a.class, u3.l.class, u3.p.class, q.class, r.class, s.class, t.class, u.class, v.class, w.class, u3.b.class, u3.c.class, u3.d.class, u3.e.class, u3.f.class, u3.g.class, u3.h.class, u3.i.class, u3.j.class, u3.k.class, u3.m.class, u3.n.class, u3.o.class);
        h.d(asList, "asList(this)");
        List list = asList;
        ArrayList arrayList = new ArrayList(m3.c.o(list));
        for (Object obj : list) {
            int i5 = i4 + 1;
            if (i4 >= 0) {
                arrayList.add(new l3.b((Class) obj, Integer.valueOf(i4)));
                i4 = i5;
            } else {
                throw new ArithmeticException("Index overflow has happened.");
            }
        }
        f6306b = m3.i.h(arrayList);
        HashMap hashMap = new HashMap();
        hashMap.put("boolean", "kotlin.Boolean");
        hashMap.put("char", "kotlin.Char");
        hashMap.put("byte", "kotlin.Byte");
        hashMap.put("short", "kotlin.Short");
        hashMap.put("int", "kotlin.Int");
        hashMap.put("float", "kotlin.Float");
        hashMap.put("long", "kotlin.Long");
        hashMap.put("double", "kotlin.Double");
        HashMap hashMap2 = new HashMap();
        hashMap2.put("java.lang.Boolean", "kotlin.Boolean");
        hashMap2.put("java.lang.Character", "kotlin.Char");
        hashMap2.put("java.lang.Byte", "kotlin.Byte");
        hashMap2.put("java.lang.Short", "kotlin.Short");
        hashMap2.put("java.lang.Integer", "kotlin.Int");
        hashMap2.put("java.lang.Float", "kotlin.Float");
        hashMap2.put("java.lang.Long", "kotlin.Long");
        hashMap2.put("java.lang.Double", "kotlin.Double");
        HashMap hashMap3 = new HashMap();
        hashMap3.put("java.lang.Object", "kotlin.Any");
        hashMap3.put("java.lang.String", "kotlin.String");
        hashMap3.put("java.lang.CharSequence", "kotlin.CharSequence");
        hashMap3.put("java.lang.Throwable", "kotlin.Throwable");
        hashMap3.put("java.lang.Cloneable", "kotlin.Cloneable");
        hashMap3.put("java.lang.Number", "kotlin.Number");
        hashMap3.put("java.lang.Comparable", "kotlin.Comparable");
        hashMap3.put("java.lang.Enum", "kotlin.Enum");
        hashMap3.put("java.lang.annotation.Annotation", "kotlin.Annotation");
        hashMap3.put("java.lang.Iterable", "kotlin.collections.Iterable");
        hashMap3.put("java.util.Iterator", "kotlin.collections.Iterator");
        hashMap3.put("java.util.Collection", "kotlin.collections.Collection");
        hashMap3.put("java.util.List", "kotlin.collections.List");
        hashMap3.put("java.util.Set", "kotlin.collections.Set");
        hashMap3.put("java.util.ListIterator", "kotlin.collections.ListIterator");
        hashMap3.put("java.util.Map", "kotlin.collections.Map");
        hashMap3.put("java.util.Map$Entry", "kotlin.collections.Map.Entry");
        hashMap3.put("kotlin.jvm.internal.StringCompanionObject", "kotlin.String.Companion");
        hashMap3.put("kotlin.jvm.internal.EnumCompanionObject", "kotlin.Enum.Companion");
        hashMap3.putAll(hashMap);
        hashMap3.putAll(hashMap2);
        Collection<String> values = hashMap.values();
        h.d(values, "primitiveFqNames.values");
        for (String str : values) {
            StringBuilder sb = new StringBuilder("kotlin.jvm.internal.");
            h.d(str, "kotlinName");
            sb.append(B3.h.d(str));
            sb.append("CompanionObject");
            hashMap3.put(sb.toString(), str.concat(".Companion"));
        }
        for (Map.Entry<Class<? extends l3.a<?>>, Integer> entry : f6306b.entrySet()) {
            int intValue = entry.getValue().intValue();
            String name = entry.getKey().getName();
            hashMap3.put(name, "kotlin.Function" + intValue);
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap(m3.i.g(hashMap3.size()));
        for (Map.Entry entry2 : hashMap3.entrySet()) {
            linkedHashMap.put(entry2.getKey(), B3.h.d((String) entry2.getValue()));
        }
    }

    public c(Class<?> cls) {
        h.e(cls, "jClass");
        this.f6307a = cls;
    }

    @Override // v3.b
    public final Class<?> a() {
        return this.f6307a;
    }

    public final boolean equals(Object obj) {
        if ((obj instanceof c) && B3.a.a(this).equals(B3.a.a((z3.b) obj))) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return B3.a.a(this).hashCode();
    }

    public final String toString() {
        return this.f6307a.toString() + " (Kotlin reflection is not available)";
    }
}
