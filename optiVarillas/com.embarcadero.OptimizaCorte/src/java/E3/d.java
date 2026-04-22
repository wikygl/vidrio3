package e3;

/* JADX WARN: Method from annotation default annotation not found: intEncoding */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public @interface d {

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class a {

        /* renamed from: j  reason: collision with root package name */
        public static final a f3361j;

        /* renamed from: k  reason: collision with root package name */
        public static final /* synthetic */ a[] f3362k;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r3v0, types: [java.lang.Enum, e3.d$a] */
        static {
            ?? r32 = new Enum("DEFAULT", 0);
            f3361j = r32;
            f3362k = new a[]{r32, new Enum("SIGNED", 1), new Enum("FIXED", 2)};
        }

        public a() {
            throw null;
        }

        public static a valueOf(String str) {
            return (a) Enum.valueOf(a.class, str);
        }

        public static a[] values() {
            return (a[]) f3362k.clone();
        }
    }
}
