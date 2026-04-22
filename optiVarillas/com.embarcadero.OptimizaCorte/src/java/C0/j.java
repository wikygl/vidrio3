package C0;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class j {

    /* renamed from: j  reason: collision with root package name */
    public static final j f324j;

    /* renamed from: k  reason: collision with root package name */
    public static final j f325k;

    /* renamed from: l  reason: collision with root package name */
    public static final j f326l;

    /* renamed from: m  reason: collision with root package name */
    public static final j f327m;

    /* renamed from: n  reason: collision with root package name */
    public static final j f328n;

    /* renamed from: o  reason: collision with root package name */
    public static final j f329o;

    /* renamed from: p  reason: collision with root package name */
    public static final /* synthetic */ j[] f330p;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v1, types: [java.lang.Enum, C0.j] */
    /* JADX WARN: Type inference failed for: r11v1, types: [java.lang.Enum, C0.j] */
    /* JADX WARN: Type inference failed for: r6v0, types: [java.lang.Enum, C0.j] */
    /* JADX WARN: Type inference failed for: r7v1, types: [java.lang.Enum, C0.j] */
    /* JADX WARN: Type inference failed for: r8v1, types: [java.lang.Enum, C0.j] */
    /* JADX WARN: Type inference failed for: r9v1, types: [java.lang.Enum, C0.j] */
    static {
        ?? r6 = new Enum("NOT_REQUIRED", 0);
        f324j = r6;
        ?? r7 = new Enum("CONNECTED", 1);
        f325k = r7;
        ?? r8 = new Enum("UNMETERED", 2);
        f326l = r8;
        ?? r9 = new Enum("NOT_ROAMING", 3);
        f327m = r9;
        ?? r10 = new Enum("METERED", 4);
        f328n = r10;
        ?? r11 = new Enum("TEMPORARILY_UNMETERED", 5);
        f329o = r11;
        f330p = new j[]{r6, r7, r8, r9, r10, r11};
    }

    public j() {
        throw null;
    }

    public static j valueOf(String str) {
        return (j) Enum.valueOf(j.class, str);
    }

    public static j[] values() {
        return (j[]) f330p.clone();
    }
}
