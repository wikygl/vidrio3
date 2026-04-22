package C0;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class e {

    /* renamed from: j  reason: collision with root package name */
    public static final e f314j;

    /* renamed from: k  reason: collision with root package name */
    public static final e f315k;

    /* renamed from: l  reason: collision with root package name */
    public static final e f316l;

    /* renamed from: m  reason: collision with root package name */
    public static final /* synthetic */ e[] f317m;
    /* JADX INFO: Fake field, exist only in values array */
    e EF4;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v1, types: [java.lang.Enum, C0.e] */
    /* JADX WARN: Type inference failed for: r6v1, types: [java.lang.Enum, C0.e] */
    /* JADX WARN: Type inference failed for: r7v1, types: [java.lang.Enum, C0.e] */
    static {
        Enum r4 = new Enum("REPLACE", 0);
        ?? r5 = new Enum("KEEP", 1);
        f314j = r5;
        ?? r6 = new Enum("APPEND", 2);
        f315k = r6;
        ?? r7 = new Enum("APPEND_OR_REPLACE", 3);
        f316l = r7;
        f317m = new e[]{r4, r5, r6, r7};
    }

    public e() {
        throw null;
    }

    public static e valueOf(String str) {
        return (e) Enum.valueOf(e.class, str);
    }

    public static e[] values() {
        return (e[]) f317m.clone();
    }
}
