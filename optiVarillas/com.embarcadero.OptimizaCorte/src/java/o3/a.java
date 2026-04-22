package o3;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class a {

    /* renamed from: j  reason: collision with root package name */
    public static final a f5500j;

    /* renamed from: k  reason: collision with root package name */
    public static final /* synthetic */ a[] f5501k;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0, types: [java.lang.Enum, o3.a] */
    static {
        ?? r32 = new Enum("COROUTINE_SUSPENDED", 0);
        f5500j = r32;
        f5501k = new a[]{r32, new Enum("UNDECIDED", 1), new Enum("RESUMED", 2)};
    }

    public a() {
        throw null;
    }

    public static a valueOf(String str) {
        return (a) Enum.valueOf(a.class, str);
    }

    public static a[] values() {
        return (a[]) f5501k.clone();
    }
}
