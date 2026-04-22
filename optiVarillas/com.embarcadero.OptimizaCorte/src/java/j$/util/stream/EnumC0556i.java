package j$.util.stream;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* renamed from: j$.util.stream.i  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class EnumC0556i {
    public static final EnumC0556i CONCURRENT;
    public static final EnumC0556i IDENTITY_FINISH;
    public static final EnumC0556i UNORDERED;

    /* renamed from: a  reason: collision with root package name */
    private static final /* synthetic */ EnumC0556i[] f4525a;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0, types: [j$.util.stream.i, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r4v1, types: [j$.util.stream.i, java.lang.Enum] */
    /* JADX WARN: Type inference failed for: r5v1, types: [j$.util.stream.i, java.lang.Enum] */
    static {
        ?? r32 = new Enum("CONCURRENT", 0);
        CONCURRENT = r32;
        ?? r4 = new Enum("UNORDERED", 1);
        UNORDERED = r4;
        ?? r5 = new Enum("IDENTITY_FINISH", 2);
        IDENTITY_FINISH = r5;
        f4525a = new EnumC0556i[]{r32, r4, r5};
    }

    public static EnumC0556i valueOf(String str) {
        return (EnumC0556i) Enum.valueOf(EnumC0556i.class, str);
    }

    public static EnumC0556i[] values() {
        return (EnumC0556i[]) f4525a.clone();
    }
}
