package e3;

import e3.d;
import java.lang.annotation.Annotation;

/* renamed from: e3.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0409a implements d {

    /* renamed from: a  reason: collision with root package name */
    public final int f3359a;

    public C0409a(int i4) {
        this.f3359a = i4;
    }

    @Override // java.lang.annotation.Annotation
    public final Class<? extends Annotation> annotationType() {
        return d.class;
    }

    @Override // java.lang.annotation.Annotation
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d)) {
            return false;
        }
        C0409a c0409a = (C0409a) ((d) obj);
        if (this.f3359a == c0409a.f3359a) {
            Object obj2 = d.a.f3361j;
            c0409a.getClass();
            if (obj2.equals(obj2)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.lang.annotation.Annotation
    public final int hashCode() {
        return (14552422 ^ this.f3359a) + (d.a.f3361j.hashCode() ^ 2041407134);
    }

    @Override // java.lang.annotation.Annotation
    public final String toString() {
        return "@com.google.firebase.encoders.proto.Protobuf(tag=" + this.f3359a + "intEncoding=" + d.a.f3361j + ')';
    }
}
