package A0;

import B0.a;
import B0.p;
import B0.s;
import C3.G;
import com.google.gson.internal.i;
import e0.C0405a;
import e0.C0406b;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import s.C0789b;
import s.C0791d;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class c implements i {

    /* renamed from: j  reason: collision with root package name */
    public static c f6j;

    public static C0791d a(G g4) {
        C0789b c0789b = new C0789b();
        C0791d<T> c0791d = new C0791d<>(c0789b);
        c0789b.f5750b = c0791d;
        c0789b.f5749a = C0405a.class;
        try {
            g4.J(new C0406b(c0789b, g4));
            c0789b.f5749a = "Deferred.asListenableFuture";
        } catch (Exception e4) {
            c0791d.f5754k.i(e4);
        }
        return c0791d;
    }

    public static String b(int i4, int i5, String str) {
        if (i4 < 0) {
            return H.a.g("%s (%s) must not be negative", str, Integer.valueOf(i4));
        }
        if (i5 >= 0) {
            return H.a.g("%s (%s) must not be greater than size (%s)", str, Integer.valueOf(i4), Integer.valueOf(i5));
        }
        throw new IllegalArgumentException(C0405a.c("negative size: ", i5));
    }

    public static void c(int i4, int i5) {
        String g4;
        if (i4 >= 0 && i4 < i5) {
            return;
        }
        if (i4 >= 0) {
            if (i5 >= 0) {
                g4 = H.a.g("%s (%s) must be less than size (%s)", "index", Integer.valueOf(i4), Integer.valueOf(i5));
            } else {
                throw new IllegalArgumentException(C0405a.c("negative size: ", i5));
            }
        } else {
            g4 = H.a.g("%s (%s) must not be negative", "index", Integer.valueOf(i4));
        }
        throw new IndexOutOfBoundsException(g4);
    }

    public static void d(int i4, int i5, int i6) {
        String b4;
        if (i4 >= 0 && i5 >= i4 && i5 <= i6) {
            return;
        }
        if (i4 >= 0 && i4 <= i6) {
            if (i5 >= 0 && i5 <= i6) {
                b4 = H.a.g("end index (%s) must not be less than start index (%s)", Integer.valueOf(i5), Integer.valueOf(i4));
            } else {
                b4 = b(i5, i6, "end index");
            }
        } else {
            b4 = b(i4, i6, "start index");
        }
        throw new IndexOutOfBoundsException(b4);
    }

    public static boolean e(String str) {
        a.d dVar = s.f289a;
        Set<p> unmodifiableSet = Collections.unmodifiableSet(B0.a.f283c);
        HashSet hashSet = new HashSet();
        for (p pVar : unmodifiableSet) {
            if (pVar.a().equals(str)) {
                hashSet.add(pVar);
            }
        }
        if (!hashSet.isEmpty()) {
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                if (((p) it.next()).b()) {
                    return true;
                }
            }
            return false;
        }
        throw new RuntimeException("Unknown feature ".concat(str));
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x0065  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00a5  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00ad  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x00eb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final long f(java.lang.String r23, long r24, long r26, long r28) {
        /*
            Method dump skipped, instructions count: 296
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: A0.c.f(java.lang.String, long, long, long):long");
    }

    public static int g(String str, int i4, int i5, int i6, int i7) {
        if ((i7 & 4) != 0) {
            i5 = 1;
        }
        if ((i7 & 8) != 0) {
            i6 = Integer.MAX_VALUE;
        }
        return (int) f(str, i4, i5, i6);
    }

    public Object k() {
        return new ConcurrentSkipListMap();
    }
}
