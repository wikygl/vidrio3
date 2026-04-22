package D1;

/* renamed from: D1.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0180c {

    /* renamed from: a  reason: collision with root package name */
    public boolean f671a;

    /* renamed from: b  reason: collision with root package name */
    public float f672b;

    public final synchronized float a() {
        if (e()) {
            return this.f672b;
        }
        return 1.0f;
    }

    public final synchronized void b(boolean z4) {
        this.f671a = z4;
    }

    public final synchronized void c(float f) {
        this.f672b = f;
    }

    public final synchronized boolean d() {
        return this.f671a;
    }

    public final synchronized boolean e() {
        if (this.f672b >= 0.0f) {
            return true;
        }
        return false;
    }
}
