package f1;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class d {

    /* renamed from: j  reason: collision with root package name */
    public static final d f3393j;

    /* renamed from: k  reason: collision with root package name */
    public static final d f3394k;

    /* renamed from: l  reason: collision with root package name */
    public static final d f3395l;

    /* renamed from: m  reason: collision with root package name */
    public static final /* synthetic */ d[] f3396m;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0, types: [java.lang.Enum, f1.d] */
    /* JADX WARN: Type inference failed for: r4v1, types: [java.lang.Enum, f1.d] */
    /* JADX WARN: Type inference failed for: r5v1, types: [java.lang.Enum, f1.d] */
    static {
        ?? r32 = new Enum("DEFAULT", 0);
        f3393j = r32;
        ?? r4 = new Enum("VERY_LOW", 1);
        f3394k = r4;
        ?? r5 = new Enum("HIGHEST", 2);
        f3395l = r5;
        f3396m = new d[]{r32, r4, r5};
    }

    public d() {
        throw null;
    }

    public static d valueOf(String str) {
        return (d) Enum.valueOf(d.class, str);
    }

    public static d[] values() {
        return (d[]) f3396m.clone();
    }
}
