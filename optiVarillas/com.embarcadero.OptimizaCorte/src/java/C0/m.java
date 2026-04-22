package C0;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class m {

    /* renamed from: j  reason: collision with root package name */
    public static final m f334j;

    /* renamed from: k  reason: collision with root package name */
    public static final m f335k;

    /* renamed from: l  reason: collision with root package name */
    public static final /* synthetic */ m[] f336l;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [C0.m, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r3v1, types: [C0.m, java.lang.Enum] */
    static {
        ?? r22 = new Enum("RUN_AS_NON_EXPEDITED_WORK_REQUEST", 0);
        f334j = r22;
        ?? r32 = new Enum("DROP_WORK_REQUEST", 1);
        f335k = r32;
        f336l = new m[]{r22, r32};
    }

    public m() {
        throw null;
    }

    public static m valueOf(String str) {
        return (m) Enum.valueOf(m.class, str);
    }

    public static m[] values() {
        return (m[]) f336l.clone();
    }
}
