package j1;

import android.content.Context;
import r1.InterfaceC0782a;

/* renamed from: j1.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0665c extends h {

    /* renamed from: a  reason: collision with root package name */
    public final Context f4744a;

    /* renamed from: b  reason: collision with root package name */
    public final InterfaceC0782a f4745b;

    /* renamed from: c  reason: collision with root package name */
    public final InterfaceC0782a f4746c;

    /* renamed from: d  reason: collision with root package name */
    public final String f4747d;

    public C0665c(Context context, InterfaceC0782a interfaceC0782a, InterfaceC0782a interfaceC0782a2, String str) {
        if (context != null) {
            this.f4744a = context;
            if (interfaceC0782a != null) {
                this.f4745b = interfaceC0782a;
                if (interfaceC0782a2 != null) {
                    this.f4746c = interfaceC0782a2;
                    if (str != null) {
                        this.f4747d = str;
                        return;
                    }
                    throw new NullPointerException("Null backendName");
                }
                throw new NullPointerException("Null monotonicClock");
            }
            throw new NullPointerException("Null wallClock");
        }
        throw new NullPointerException("Null applicationContext");
    }

    @Override // j1.h
    public final Context a() {
        return this.f4744a;
    }

    @Override // j1.h
    public final String b() {
        return this.f4747d;
    }

    @Override // j1.h
    public final InterfaceC0782a c() {
        return this.f4746c;
    }

    @Override // j1.h
    public final InterfaceC0782a d() {
        return this.f4745b;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof h)) {
            return false;
        }
        h hVar = (h) obj;
        if (this.f4744a.equals(hVar.a()) && this.f4745b.equals(hVar.d()) && this.f4746c.equals(hVar.c()) && this.f4747d.equals(hVar.b())) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return ((((((this.f4744a.hashCode() ^ 1000003) * 1000003) ^ this.f4745b.hashCode()) * 1000003) ^ this.f4746c.hashCode()) * 1000003) ^ this.f4747d.hashCode();
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("CreationContext{applicationContext=");
        sb.append(this.f4744a);
        sb.append(", wallClock=");
        sb.append(this.f4745b);
        sb.append(", monotonicClock=");
        sb.append(this.f4746c);
        sb.append(", backendName=");
        return C.b.c(sb, this.f4747d, "}");
    }
}
