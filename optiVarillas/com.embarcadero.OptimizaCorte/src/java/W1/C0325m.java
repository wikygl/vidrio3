package W1;

/* renamed from: W1.m  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0325m {

    /* renamed from: b  reason: collision with root package name */
    public static C0325m f2756b;

    /* renamed from: c  reason: collision with root package name */
    public static final C0326n f2757c = new C0326n(0, false, false, 0, 0);

    /* renamed from: a  reason: collision with root package name */
    public C0326n f2758a;

    /* JADX WARN: Type inference failed for: r1v3, types: [W1.m, java.lang.Object] */
    public static synchronized C0325m a() {
        C0325m c0325m;
        synchronized (C0325m.class) {
            try {
                if (f2756b == null) {
                    f2756b = new Object();
                }
                c0325m = f2756b;
            } catch (Throwable th) {
                throw th;
            }
        }
        return c0325m;
    }
}
