package j$.util.stream;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* renamed from: j$.util.stream.f3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class EnumC0545f3 {
    public static final EnumC0545f3 DOUBLE_VALUE;
    public static final EnumC0545f3 INT_VALUE;
    public static final EnumC0545f3 LONG_VALUE;
    public static final EnumC0545f3 REFERENCE;

    /* renamed from: a  reason: collision with root package name */
    private static final /* synthetic */ EnumC0545f3[] f4508a;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v0, types: [java.lang.Enum, j$.util.stream.f3] */
    /* JADX WARN: Type inference failed for: r5v1, types: [java.lang.Enum, j$.util.stream.f3] */
    /* JADX WARN: Type inference failed for: r6v1, types: [java.lang.Enum, j$.util.stream.f3] */
    /* JADX WARN: Type inference failed for: r7v1, types: [java.lang.Enum, j$.util.stream.f3] */
    static {
        ?? r4 = new Enum("REFERENCE", 0);
        REFERENCE = r4;
        ?? r5 = new Enum("INT_VALUE", 1);
        INT_VALUE = r5;
        ?? r6 = new Enum("LONG_VALUE", 2);
        LONG_VALUE = r6;
        ?? r7 = new Enum("DOUBLE_VALUE", 3);
        DOUBLE_VALUE = r7;
        f4508a = new EnumC0545f3[]{r4, r5, r6, r7};
    }

    public static EnumC0545f3 valueOf(String str) {
        return (EnumC0545f3) Enum.valueOf(EnumC0545f3.class, str);
    }

    public static EnumC0545f3[] values() {
        return (EnumC0545f3[]) f4508a.clone();
    }
}
