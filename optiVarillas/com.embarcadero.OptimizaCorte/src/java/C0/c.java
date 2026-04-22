package C0;

import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class c {

    /* renamed from: i  reason: collision with root package name */
    public static final c f303i;

    /* renamed from: b  reason: collision with root package name */
    public boolean f305b;

    /* renamed from: c  reason: collision with root package name */
    public boolean f306c;

    /* renamed from: d  reason: collision with root package name */
    public boolean f307d;

    /* renamed from: e  reason: collision with root package name */
    public boolean f308e;

    /* renamed from: a  reason: collision with root package name */
    public j f304a = j.f324j;
    public long f = -1;

    /* renamed from: g  reason: collision with root package name */
    public long f309g = -1;

    /* renamed from: h  reason: collision with root package name */
    public d f310h = new d();

    /* JADX WARN: Type inference failed for: r2v0, types: [java.lang.Object, C0.c] */
    static {
        j jVar = j.f324j;
        d dVar = new d();
        ?? obj = new Object();
        obj.f304a = jVar;
        obj.f = -1L;
        obj.f309g = -1L;
        obj.f310h = new d();
        obj.f305b = false;
        int i4 = Build.VERSION.SDK_INT;
        obj.f306c = false;
        obj.f304a = jVar;
        obj.f307d = false;
        obj.f308e = false;
        if (i4 >= 24) {
            obj.f310h = dVar;
            obj.f = -1L;
            obj.f309g = -1L;
        }
        f303i = obj;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || c.class != obj.getClass()) {
            return false;
        }
        c cVar = (c) obj;
        if (this.f305b != cVar.f305b || this.f306c != cVar.f306c || this.f307d != cVar.f307d || this.f308e != cVar.f308e || this.f != cVar.f || this.f309g != cVar.f309g || this.f304a != cVar.f304a) {
            return false;
        }
        return this.f310h.equals(cVar.f310h);
    }

    public final int hashCode() {
        long j4 = this.f;
        long j5 = this.f309g;
        return this.f310h.f311a.hashCode() + (((((((((((((this.f304a.hashCode() * 31) + (this.f305b ? 1 : 0)) * 31) + (this.f306c ? 1 : 0)) * 31) + (this.f307d ? 1 : 0)) * 31) + (this.f308e ? 1 : 0)) * 31) + ((int) (j4 ^ (j4 >>> 32)))) * 31) + ((int) (j5 ^ (j5 >>> 32)))) * 31);
    }
}
