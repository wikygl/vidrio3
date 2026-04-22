package j$.util.stream;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class F3 {
    public static final F3 MAYBE_MORE;
    public static final F3 NO_MORE;
    public static final F3 UNLIMITED;

    /* renamed from: a  reason: collision with root package name */
    private static final /* synthetic */ F3[] f4279a;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0, types: [java.lang.Enum, j$.util.stream.F3] */
    /* JADX WARN: Type inference failed for: r4v1, types: [java.lang.Enum, j$.util.stream.F3] */
    /* JADX WARN: Type inference failed for: r5v1, types: [java.lang.Enum, j$.util.stream.F3] */
    static {
        ?? r32 = new Enum("NO_MORE", 0);
        NO_MORE = r32;
        ?? r4 = new Enum("MAYBE_MORE", 1);
        MAYBE_MORE = r4;
        ?? r5 = new Enum("UNLIMITED", 2);
        UNLIMITED = r5;
        f4279a = new F3[]{r32, r4, r5};
    }

    public static F3 valueOf(String str) {
        return (F3) Enum.valueOf(F3.class, str);
    }

    public static F3[] values() {
        return (F3[]) f4279a.clone();
    }
}
