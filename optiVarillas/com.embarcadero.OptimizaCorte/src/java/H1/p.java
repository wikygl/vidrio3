package h1;

import com.google.auto.value.AutoValue;

@AutoValue
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class p {

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class a {

        /* renamed from: j  reason: collision with root package name */
        public static final a f3576j;

        /* renamed from: k  reason: collision with root package name */
        public static final /* synthetic */ a[] f3577k;
        /* JADX INFO: Fake field, exist only in values array */
        a EF2;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r3v1, types: [java.lang.Enum, h1.p$a] */
        static {
            Enum r22 = new Enum("UNKNOWN", 0);
            ?? r32 = new Enum("ANDROID_FIREBASE", 1);
            f3576j = r32;
            f3577k = new a[]{r22, r32};
        }

        public a() {
            throw null;
        }

        public static a valueOf(String str) {
            return (a) Enum.valueOf(a.class, str);
        }

        public static a[] values() {
            return (a[]) f3577k.clone();
        }
    }

    public abstract AbstractC0434a a();

    public abstract a b();
}
