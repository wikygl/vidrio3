package C0;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class a {

    /* renamed from: j  reason: collision with root package name */
    public static final a f298j;

    /* renamed from: k  reason: collision with root package name */
    public static final a f299k;

    /* renamed from: l  reason: collision with root package name */
    public static final /* synthetic */ a[] f300l;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [java.lang.Enum, C0.a] */
    /* JADX WARN: Type inference failed for: r3v1, types: [java.lang.Enum, C0.a] */
    static {
        ?? r22 = new Enum("EXPONENTIAL", 0);
        f298j = r22;
        ?? r32 = new Enum("LINEAR", 1);
        f299k = r32;
        f300l = new a[]{r22, r32};
    }

    public a() {
        throw null;
    }

    public static a valueOf(String str) {
        return (a) Enum.valueOf(a.class, str);
    }

    public static a[] values() {
        return (a[]) f300l.clone();
    }
}
