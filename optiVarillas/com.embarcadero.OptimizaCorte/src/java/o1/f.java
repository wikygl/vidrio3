package o1;

import com.google.auto.value.AutoValue;
import java.util.Map;
import java.util.Set;
import r1.InterfaceC0782a;

@AutoValue
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class f {

    @AutoValue
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static abstract class a {
        public abstract long a();

        public abstract Set<b> b();

        public abstract long c();
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class b {

        /* renamed from: j  reason: collision with root package name */
        public static final b f5437j;

        /* renamed from: k  reason: collision with root package name */
        public static final b f5438k;

        /* renamed from: l  reason: collision with root package name */
        public static final b f5439l;

        /* renamed from: m  reason: collision with root package name */
        public static final /* synthetic */ b[] f5440m;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r3v0, types: [java.lang.Enum, o1.f$b] */
        /* JADX WARN: Type inference failed for: r4v1, types: [java.lang.Enum, o1.f$b] */
        /* JADX WARN: Type inference failed for: r5v1, types: [java.lang.Enum, o1.f$b] */
        static {
            ?? r32 = new Enum("NETWORK_UNMETERED", 0);
            f5437j = r32;
            ?? r4 = new Enum("DEVICE_IDLE", 1);
            f5438k = r4;
            ?? r5 = new Enum("DEVICE_CHARGING", 2);
            f5439l = r5;
            f5440m = new b[]{r32, r4, r5};
        }

        public b() {
            throw null;
        }

        public static b valueOf(String str) {
            return (b) Enum.valueOf(b.class, str);
        }

        public static b[] values() {
            return (b[]) f5440m.clone();
        }
    }

    public abstract InterfaceC0782a a();

    public final long b(f1.d dVar, long j4, int i4) {
        long j5;
        long a4 = j4 - a().a();
        a aVar = c().get(dVar);
        long a5 = aVar.a();
        int i5 = i4 - 1;
        if (a5 > 1) {
            j5 = a5;
        } else {
            j5 = 2;
        }
        return Math.min(Math.max((long) (Math.pow(3.0d, i5) * a5 * Math.max(1.0d, Math.log(10000.0d) / Math.log(j5 * i5))), a4), aVar.c());
    }

    public abstract Map<f1.d, a> c();
}
