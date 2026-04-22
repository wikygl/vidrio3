package Z;

import android.util.Log;
import androidx.fragment.app.k;
import java.util.LinkedHashMap;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class b {

    /* renamed from: a  reason: collision with root package name */
    public static final C0035b f2831a = C0035b.f2836a;

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class a {

        /* renamed from: j  reason: collision with root package name */
        public static final a f2832j;

        /* renamed from: k  reason: collision with root package name */
        public static final a f2833k;

        /* renamed from: l  reason: collision with root package name */
        public static final a f2834l;

        /* renamed from: m  reason: collision with root package name */
        public static final /* synthetic */ a[] f2835m;
        /* JADX INFO: Fake field, exist only in values array */
        a EF8;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r10v1, types: [Z.b$a, java.lang.Enum] */
        /* JADX WARN: Type inference failed for: r11v1, types: [Z.b$a, java.lang.Enum] */
        /* JADX WARN: Type inference failed for: r15v1, types: [Z.b$a, java.lang.Enum] */
        static {
            Enum r8 = new Enum("PENALTY_LOG", 0);
            Enum r9 = new Enum("PENALTY_DEATH", 1);
            ?? r10 = new Enum("DETECT_FRAGMENT_REUSE", 2);
            f2832j = r10;
            ?? r11 = new Enum("DETECT_FRAGMENT_TAG_USAGE", 3);
            f2833k = r11;
            Enum r12 = new Enum("DETECT_RETAIN_INSTANCE_USAGE", 4);
            Enum r13 = new Enum("DETECT_SET_USER_VISIBLE_HINT", 5);
            Enum r14 = new Enum("DETECT_TARGET_FRAGMENT_USAGE", 6);
            ?? r15 = new Enum("DETECT_WRONG_FRAGMENT_CONTAINER", 7);
            f2834l = r15;
            f2835m = new a[]{r8, r9, r10, r11, r12, r13, r14, r15};
        }

        public a() {
            throw null;
        }

        public static a valueOf(String str) {
            return (a) Enum.valueOf(a.class, str);
        }

        public static a[] values() {
            return (a[]) f2835m.clone();
        }
    }

    /* renamed from: Z.b$b  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class C0035b {

        /* renamed from: a  reason: collision with root package name */
        public static final C0035b f2836a;

        /* JADX WARN: Type inference failed for: r0v0, types: [Z.b$b, java.lang.Object] */
        static {
            ?? obj = new Object();
            new LinkedHashMap();
            f2836a = obj;
        }
    }

    public static C0035b a(k kVar) {
        while (kVar != null) {
            if (kVar.o()) {
                kVar.j();
            }
            kVar = kVar.D;
        }
        return f2831a;
    }

    public static void b(d dVar) {
        if (Log.isLoggable("FragmentManager", 3)) {
            Log.d("FragmentManager", "StrictMode violation in ".concat(dVar.f2838j.getClass().getName()), dVar);
        }
    }

    public static final void c(k kVar, String str) {
        h.e(kVar, "fragment");
        h.e(str, "previousFragmentId");
        b(new d(kVar, "Attempting to reuse fragment " + kVar + " with previous ID " + str));
        a(kVar).getClass();
        a aVar = a.f2832j;
        if (aVar instanceof Void) {
            Void r32 = (Void) aVar;
        }
    }
}
