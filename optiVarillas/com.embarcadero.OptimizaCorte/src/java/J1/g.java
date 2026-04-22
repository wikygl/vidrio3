package j1;

import com.google.auto.value.AutoValue;

@AutoValue
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class g {

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class a {

        /* renamed from: j  reason: collision with root package name */
        public static final a f4748j;

        /* renamed from: k  reason: collision with root package name */
        public static final a f4749k;

        /* renamed from: l  reason: collision with root package name */
        public static final a f4750l;

        /* renamed from: m  reason: collision with root package name */
        public static final a f4751m;

        /* renamed from: n  reason: collision with root package name */
        public static final /* synthetic */ a[] f4752n;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r4v0, types: [java.lang.Enum, j1.g$a] */
        /* JADX WARN: Type inference failed for: r5v1, types: [java.lang.Enum, j1.g$a] */
        /* JADX WARN: Type inference failed for: r6v1, types: [java.lang.Enum, j1.g$a] */
        /* JADX WARN: Type inference failed for: r7v1, types: [java.lang.Enum, j1.g$a] */
        static {
            ?? r4 = new Enum("OK", 0);
            f4748j = r4;
            ?? r5 = new Enum("TRANSIENT_ERROR", 1);
            f4749k = r5;
            ?? r6 = new Enum("FATAL_ERROR", 2);
            f4750l = r6;
            ?? r7 = new Enum("INVALID_PAYLOAD", 3);
            f4751m = r7;
            f4752n = new a[]{r4, r5, r6, r7};
        }

        public a() {
            throw null;
        }

        public static a valueOf(String str) {
            return (a) Enum.valueOf(a.class, str);
        }

        public static a[] values() {
            return (a[]) f4752n.clone();
        }
    }

    public abstract long a();

    public abstract a b();
}
