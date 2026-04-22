package b1;

import j$.util.Objects;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class e {

    /* renamed from: a  reason: collision with root package name */
    public final String f2912a;

    /* renamed from: b  reason: collision with root package name */
    public final String f2913b;

    /* renamed from: c  reason: collision with root package name */
    public final String f2914c;

    /* renamed from: d  reason: collision with root package name */
    public final double f2915d;

    /* renamed from: e  reason: collision with root package name */
    public int f2916e;
    public int f;

    /* renamed from: g  reason: collision with root package name */
    public int f2917g;

    /* renamed from: h  reason: collision with root package name */
    public f f2918h;

    public e(String str, String str2, String str3, double d4) {
        this.f2916e = 0;
        this.f = 0;
        this.f2917g = 0;
        this.f2918h = new f("");
        this.f2912a = str;
        this.f2913b = str2;
        this.f2914c = str3;
        this.f2915d = d4;
    }

    public final String a() {
        return this.f2912a;
    }

    public final String b() {
        return this.f2914c;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || e.class != obj.getClass()) {
            return false;
        }
        e eVar = (e) obj;
        if (Double.compare(eVar.f2915d, this.f2915d) == 0 && Objects.equals(this.f2912a, eVar.f2912a) && Objects.equals(this.f2913b, eVar.f2913b) && Objects.equals(this.f2914c, eVar.f2914c)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(this.f2912a, this.f2913b, this.f2914c, Double.valueOf(this.f2915d));
    }

    public final String toString() {
        int i4 = this.f2916e;
        int i5 = this.f;
        int i6 = this.f2917g;
        f fVar = this.f2918h;
        return "Optimizacion (long_barra=" + this.f2912a + ", linea_optimizada=" + this.f2913b + ", retal=" + this.f2914c + ", total_usado=" + this.f2915d + ", numBars=" + i4 + ", tColor=" + i5 + ", cortadas=" + i6 + ", VIEW_TYPE=0, lineaSpan=" + ((Object) fVar) + ")";
    }

    public e(String str, String str2, String str3, double d4, f fVar) {
        this.f2916e = 0;
        this.f = 0;
        this.f2917g = 0;
        new f("");
        this.f2912a = str;
        this.f2913b = str2;
        this.f2914c = str3;
        this.f2915d = d4;
        this.f2918h = fVar;
    }
}
