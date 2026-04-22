package i3;

import j$.util.Objects;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class a<T> {

    /* renamed from: a  reason: collision with root package name */
    public final Class<? super T> f3831a;

    /* renamed from: b  reason: collision with root package name */
    public final Type f3832b;

    /* renamed from: c  reason: collision with root package name */
    public final int f3833c;

    public a() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            if (parameterizedType.getRawType() == a.class) {
                Type a4 = com.google.gson.internal.a.a(parameterizedType.getActualTypeArguments()[0]);
                this.f3832b = a4;
                this.f3831a = com.google.gson.internal.a.e(a4);
                this.f3833c = a4.hashCode();
                return;
            }
        } else if (genericSuperclass == a.class) {
            throw new IllegalStateException("TypeToken must be created with a type argument: new TypeToken<...>() {}; When using code shrinkers (ProGuard, R8, ...) make sure that generic signatures are preserved.");
        }
        throw new IllegalStateException("Must only create direct subclasses of TypeToken");
    }

    public final boolean equals(Object obj) {
        if (obj instanceof a) {
            if (com.google.gson.internal.a.c(this.f3832b, ((a) obj).f3832b)) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return this.f3833c;
    }

    public final String toString() {
        return com.google.gson.internal.a.g(this.f3832b);
    }

    public a(Type type) {
        Objects.requireNonNull(type);
        Type a4 = com.google.gson.internal.a.a(type);
        this.f3832b = a4;
        this.f3831a = com.google.gson.internal.a.e(a4);
        this.f3833c = a4.hashCode();
    }
}
