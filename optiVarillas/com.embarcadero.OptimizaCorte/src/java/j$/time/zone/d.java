package j$.time.zone;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class d {
    public static final d STANDARD;
    public static final d UTC;
    public static final d WALL;

    /* renamed from: a  reason: collision with root package name */
    private static final /* synthetic */ d[] f4053a;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0, types: [java.lang.Enum, j$.time.zone.d] */
    /* JADX WARN: Type inference failed for: r4v1, types: [java.lang.Enum, j$.time.zone.d] */
    /* JADX WARN: Type inference failed for: r5v1, types: [java.lang.Enum, j$.time.zone.d] */
    static {
        ?? r32 = new Enum("UTC", 0);
        UTC = r32;
        ?? r4 = new Enum("WALL", 1);
        WALL = r4;
        ?? r5 = new Enum("STANDARD", 2);
        STANDARD = r5;
        f4053a = new d[]{r32, r4, r5};
    }

    public static d valueOf(String str) {
        return (d) Enum.valueOf(d.class, str);
    }

    public static d[] values() {
        return (d[]) f4053a.clone();
    }
}
