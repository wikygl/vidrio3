package j$.time.format;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class v {
    public static final v ALWAYS;
    public static final v EXCEEDS_PAD;
    public static final v NEVER;
    public static final v NORMAL;
    public static final v NOT_NEGATIVE;

    /* renamed from: a  reason: collision with root package name */
    private static final /* synthetic */ v[] f3964a;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v0, types: [j$.time.format.v, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r6v1, types: [j$.time.format.v, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r7v1, types: [j$.time.format.v, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r8v1, types: [j$.time.format.v, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r9v1, types: [j$.time.format.v, java.lang.Enum] */
    static {
        ?? r5 = new Enum("NORMAL", 0);
        NORMAL = r5;
        ?? r6 = new Enum("ALWAYS", 1);
        ALWAYS = r6;
        ?? r7 = new Enum("NEVER", 2);
        NEVER = r7;
        ?? r8 = new Enum("NOT_NEGATIVE", 3);
        NOT_NEGATIVE = r8;
        ?? r9 = new Enum("EXCEEDS_PAD", 4);
        EXCEEDS_PAD = r9;
        f3964a = new v[]{r5, r6, r7, r8, r9};
    }

    public static v valueOf(String str) {
        return (v) Enum.valueOf(v.class, str);
    }

    public static v[] values() {
        return (v[]) f3964a.clone();
    }
}
