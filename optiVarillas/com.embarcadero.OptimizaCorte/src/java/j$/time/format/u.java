package j$.time.format;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class u {
    public static final u LENIENT;
    public static final u SMART;
    public static final u STRICT;

    /* renamed from: a  reason: collision with root package name */
    private static final /* synthetic */ u[] f3963a;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0, types: [java.lang.Enum, j$.time.format.u] */
    /* JADX WARN: Type inference failed for: r4v1, types: [java.lang.Enum, j$.time.format.u] */
    /* JADX WARN: Type inference failed for: r5v1, types: [java.lang.Enum, j$.time.format.u] */
    static {
        ?? r32 = new Enum("STRICT", 0);
        STRICT = r32;
        ?? r4 = new Enum("SMART", 1);
        SMART = r4;
        ?? r5 = new Enum("LENIENT", 2);
        LENIENT = r5;
        f3963a = new u[]{r32, r4, r5};
    }

    public static u valueOf(String str) {
        return (u) Enum.valueOf(u.class, str);
    }

    public static u[] values() {
        return (u[]) f3963a.clone();
    }
}
