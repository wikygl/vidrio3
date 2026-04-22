package C3;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class A {

    /* renamed from: j  reason: collision with root package name */
    public static final A f424j;

    /* renamed from: k  reason: collision with root package name */
    public static final /* synthetic */ A[] f425k;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v0, types: [java.lang.Enum, C3.A] */
    static {
        ?? r4 = new Enum("DEFAULT", 0);
        f424j = r4;
        f425k = new A[]{r4, new Enum("LAZY", 1), new Enum("ATOMIC", 2), new Enum("UNDISPATCHED", 3)};
    }

    public A() {
        throw null;
    }

    public static A valueOf(String str) {
        return (A) Enum.valueOf(A.class, str);
    }

    public static A[] values() {
        return (A[]) f425k.clone();
    }
}
