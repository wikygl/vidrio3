package j$.util;

import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class Spliterators {

    /* renamed from: a  reason: collision with root package name */
    private static final Spliterator f4095a = new Object();

    /* renamed from: b  reason: collision with root package name */
    private static final J f4096b = new Object();

    /* renamed from: c  reason: collision with root package name */
    private static final M f4097c = new Object();

    /* renamed from: d  reason: collision with root package name */
    private static final G f4098d = new Object();

    private static void a(int i4, int i5, int i6) {
        if (i5 <= i6) {
            if (i5 < 0) {
                throw new ArrayIndexOutOfBoundsException(i5);
            }
            if (i6 > i4) {
                throw new ArrayIndexOutOfBoundsException(i6);
            }
            return;
        }
        throw new ArrayIndexOutOfBoundsException("origin(" + i5 + ") > fence(" + i6 + ")");
    }

    public static G b() {
        return f4098d;
    }

    public static J c() {
        return f4096b;
    }

    public static M d() {
        return f4097c;
    }

    public static Spliterator e() {
        return f4095a;
    }

    public static InterfaceC0515s f(G g4) {
        Objects.requireNonNull(g4);
        return new V(g4);
    }

    public static InterfaceC0643w g(J j4) {
        Objects.requireNonNull(j4);
        return new T(j4);
    }

    public static A h(M m4) {
        Objects.requireNonNull(m4);
        return new U(m4);
    }

    public static Iterator i(Spliterator spliterator) {
        Objects.requireNonNull(spliterator);
        return new S(spliterator);
    }

    public static G j(double[] dArr, int i4, int i5) {
        a(((double[]) Objects.requireNonNull(dArr)).length, i4, i5);
        return new X(dArr, i4, i5, 1040);
    }

    public static J k(int[] iArr, int i4, int i5) {
        a(((int[]) Objects.requireNonNull(iArr)).length, i4, i5);
        return new c0(iArr, i4, i5, 1040);
    }

    public static M l(long[] jArr, int i4, int i5) {
        a(((long[]) Objects.requireNonNull(jArr)).length, i4, i5);
        return new e0(jArr, i4, i5, 1040);
    }

    public static Spliterator m(Object[] objArr, int i4, int i5) {
        a(((Object[]) Objects.requireNonNull(objArr)).length, i4, i5);
        return new W(objArr, i4, i5, 1040);
    }

    public static <T> Spliterator<T> spliterator(java.util.Collection<? extends T> collection, int i4) {
        return new d0((java.util.Collection) Objects.requireNonNull(collection), i4);
    }
}
