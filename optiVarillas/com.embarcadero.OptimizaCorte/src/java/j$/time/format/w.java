package j$.time.format;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class w {
    public static final w FULL;
    public static final w FULL_STANDALONE;
    public static final w NARROW;
    public static final w NARROW_STANDALONE;
    public static final w SHORT;
    public static final w SHORT_STANDALONE;

    /* renamed from: a  reason: collision with root package name */
    private static final /* synthetic */ w[] f3965a;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v1, types: [j$.time.format.w, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r11v1, types: [j$.time.format.w, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r6v0, types: [j$.time.format.w, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r7v1, types: [j$.time.format.w, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r8v1, types: [j$.time.format.w, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r9v1, types: [j$.time.format.w, java.lang.Enum] */
    static {
        ?? r6 = new Enum("FULL", 0);
        FULL = r6;
        ?? r7 = new Enum("FULL_STANDALONE", 1);
        FULL_STANDALONE = r7;
        ?? r8 = new Enum("SHORT", 2);
        SHORT = r8;
        ?? r9 = new Enum("SHORT_STANDALONE", 3);
        SHORT_STANDALONE = r9;
        ?? r10 = new Enum("NARROW", 4);
        NARROW = r10;
        ?? r11 = new Enum("NARROW_STANDALONE", 5);
        NARROW_STANDALONE = r11;
        f3965a = new w[]{r6, r7, r8, r9, r10, r11};
    }

    public static w valueOf(String str) {
        return (w) Enum.valueOf(w.class, str);
    }

    public static w[] values() {
        return (w[]) f3965a.clone();
    }
}
