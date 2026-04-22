package i2;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* renamed from: i2.x  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class EnumC0477x {

    /* renamed from: j  reason: collision with root package name */
    public static final EnumC0477x f3817j;

    /* renamed from: k  reason: collision with root package name */
    public static final /* synthetic */ EnumC0477x[] f3818k;
    /* JADX INFO: Fake field, exist only in values array */
    EnumC0477x EF5;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r9v1, types: [i2.x, java.lang.Enum] */
    static {
        Enum r5 = new Enum("DEBUG_PARAM_UNKNOWN", 0);
        Enum r6 = new Enum("ALWAYS_SHOW", 1);
        Enum r7 = new Enum("GEO_OVERRIDE_EEA", 2);
        Enum r8 = new Enum("GEO_OVERRIDE_NON_EEA", 3);
        ?? r9 = new Enum("PREVIEWING_DEBUG_MESSAGES", 4);
        f3817j = r9;
        f3818k = new EnumC0477x[]{r5, r6, r7, r8, r9};
    }

    public static EnumC0477x[] values() {
        return (EnumC0477x[]) f3818k.clone();
    }
}
