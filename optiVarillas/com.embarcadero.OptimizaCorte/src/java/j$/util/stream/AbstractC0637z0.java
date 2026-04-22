package j$.util.stream;

import j$.util.C0503f;
import j$.util.Objects;
import j$.util.Spliterator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.stream.Collector;

/* renamed from: j$.util.stream.z0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public abstract /* synthetic */ class AbstractC0637z0 implements K3 {

    /* renamed from: a  reason: collision with root package name */
    private static final C0538e1 f4631a = new Object();

    /* renamed from: b  reason: collision with root package name */
    private static final H0 f4632b = new Object();

    /* renamed from: c  reason: collision with root package name */
    private static final J0 f4633c = new Object();

    /* renamed from: d  reason: collision with root package name */
    private static final F0 f4634d = new Object();

    /* renamed from: e  reason: collision with root package name */
    private static final int[] f4635e = new int[0];
    private static final long[] f = new long[0];

    /* renamed from: g  reason: collision with root package name */
    private static final double[] f4636g = new double[0];

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractC0637z0(EnumC0545f3 enumC0545f3) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long A(long j4, long j5, long j6) {
        if (j4 >= 0) {
            return Math.max(-1L, Math.min(j4 - j5, j6));
        }
        return -1L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long B(long j4, long j5) {
        long j6 = j5 >= 0 ? j4 + j5 : Long.MAX_VALUE;
        if (j6 >= 0) {
            return j6;
        }
        return Long.MAX_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Spliterator C(EnumC0545f3 enumC0545f3, Spliterator spliterator, long j4, long j5) {
        long j6 = j5 >= 0 ? j4 + j5 : Long.MAX_VALUE;
        long j7 = j6 >= 0 ? j6 : Long.MAX_VALUE;
        int i4 = AbstractC0639z2.f4637a[enumC0545f3.ordinal()];
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 != 3) {
                    if (i4 == 4) {
                        return new x3((j$.util.G) spliterator, j4, j7);
                    }
                    throw new IllegalStateException("Unknown shape " + enumC0545f3);
                }
                return new x3((j$.util.M) spliterator, j4, j7);
            }
            return new x3((j$.util.J) spliterator, j4, j7);
        }
        return new y3(spliterator, j4, j7);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v1, types: [j$.util.stream.a3, j$.util.stream.D0] */
    /* JADX WARN: Type inference failed for: r0v3, types: [j$.util.stream.O0, j$.util.stream.D0] */
    public static D0 D(long j4, IntFunction intFunction) {
        return (j4 < 0 || j4 >= 2147483639) ? new C0520a3() : new O0(j4, intFunction);
    }

    /* JADX WARN: Type inference failed for: r3v0, types: [java.util.function.LongFunction, j$.util.stream.R0, java.lang.Object] */
    public static L0 E(AbstractC0521b abstractC0521b, Spliterator spliterator, boolean z4, IntFunction intFunction) {
        long C4 = abstractC0521b.C(spliterator);
        if (C4 < 0 || !spliterator.hasCharacteristics(16384)) {
            ?? obj = new Object();
            obj.f4371a = intFunction;
            L0 l0 = (L0) new Q0(abstractC0521b, spliterator, obj, new C0571l(16), 3).invoke();
            return z4 ? N(l0, intFunction) : l0;
        } else if (C4 < 2147483639) {
            Object[] objArr = (Object[]) intFunction.apply((int) C4);
            new C0626w1(spliterator, abstractC0521b, objArr).invoke();
            return new O0(objArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static F0 F(AbstractC0521b abstractC0521b, Spliterator spliterator, boolean z4) {
        long C4 = abstractC0521b.C(spliterator);
        if (C4 < 0 || !spliterator.hasCharacteristics(16384)) {
            F0 f02 = (F0) new Q0(abstractC0521b, spliterator, new C0571l(10), new C0571l(11), 0).invoke();
            return z4 ? O(f02) : f02;
        } else if (C4 < 2147483639) {
            double[] dArr = new double[(int) C4];
            new C0612t1(spliterator, abstractC0521b, dArr).invoke();
            return new Y0(dArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static H0 G(AbstractC0521b abstractC0521b, Spliterator spliterator, boolean z4) {
        long C4 = abstractC0521b.C(spliterator);
        if (C4 < 0 || !spliterator.hasCharacteristics(16384)) {
            H0 h02 = (H0) new Q0(abstractC0521b, spliterator, new C0571l(12), new C0571l(13), 1).invoke();
            return z4 ? P(h02) : h02;
        } else if (C4 < 2147483639) {
            int[] iArr = new int[(int) C4];
            new C0617u1(spliterator, abstractC0521b, iArr).invoke();
            return new C0553h1(iArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static J0 H(AbstractC0521b abstractC0521b, Spliterator spliterator, boolean z4) {
        long C4 = abstractC0521b.C(spliterator);
        if (C4 < 0 || !spliterator.hasCharacteristics(16384)) {
            J0 j02 = (J0) new Q0(abstractC0521b, spliterator, new C0571l(14), new C0571l(15), 2).invoke();
            return z4 ? Q(j02) : j02;
        } else if (C4 < 2147483639) {
            long[] jArr = new long[(int) C4];
            new C0622v1(spliterator, abstractC0521b, jArr).invoke();
            return new C0598q1(jArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static N0 I(EnumC0545f3 enumC0545f3, L0 l0, L0 l02) {
        int i4 = M0.f4328a[enumC0545f3.ordinal()];
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 != 3) {
                    if (i4 == 4) {
                        return new N0((F0) l0, (F0) l02);
                    }
                    throw new IllegalStateException("Unknown shape " + enumC0545f3);
                }
                return new N0((J0) l0, (J0) l02);
            }
            return new N0((H0) l0, (H0) l02);
        }
        return new N0(l0, l02);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v1, types: [j$.util.stream.Z2, j$.util.stream.A0] */
    /* JADX WARN: Type inference failed for: r0v3, types: [j$.util.stream.Y0, j$.util.stream.A0] */
    public static A0 J(long j4) {
        return (j4 < 0 || j4 >= 2147483639) ? new Z2() : new Y0(j4);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.stream.F, j$.util.stream.b] */
    public static F K(j$.util.G g4) {
        return new AbstractC0521b(g4, EnumC0540e3.m(g4), false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AbstractC0543f1 L(EnumC0545f3 enumC0545f3) {
        L0 l0;
        int i4 = M0.f4328a[enumC0545f3.ordinal()];
        if (i4 != 1) {
            if (i4 == 2) {
                l0 = f4632b;
            } else if (i4 == 3) {
                l0 = f4633c;
            } else if (i4 != 4) {
                throw new IllegalStateException("Unknown shape " + enumC0545f3);
            } else {
                l0 = f4634d;
            }
            return (AbstractC0543f1) l0;
        }
        return f4631a;
    }

    private static int M(long j4) {
        return (j4 != -1 ? EnumC0540e3.f4496u : 0) | EnumC0540e3.f4495t;
    }

    public static L0 N(L0 l0, IntFunction intFunction) {
        if (l0.q() > 0) {
            long count = l0.count();
            if (count < 2147483639) {
                Object[] objArr = (Object[]) intFunction.apply((int) count);
                new A1(l0, objArr, 1).invoke();
                return new O0(objArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return l0;
    }

    public static F0 O(F0 f02) {
        if (f02.q() > 0) {
            long count = f02.count();
            if (count < 2147483639) {
                double[] dArr = new double[(int) count];
                new A1(f02, dArr, 0).invoke();
                return new Y0(dArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return f02;
    }

    public static H0 P(H0 h02) {
        if (h02.q() > 0) {
            long count = h02.count();
            if (count < 2147483639) {
                int[] iArr = new int[(int) count];
                new A1(h02, iArr, 0).invoke();
                return new C0553h1(iArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return h02;
    }

    public static J0 Q(J0 j02) {
        if (j02.q() > 0) {
            long count = j02.count();
            if (count < 2147483639) {
                long[] jArr = new long[(int) count];
                new A1(j02, jArr, 0).invoke();
                return new C0598q1(jArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return j02;
    }

    public static Set R(Set set) {
        if (set == null || set.isEmpty()) {
            return set;
        }
        HashSet hashSet = new HashSet();
        Object next = set.iterator().next();
        if (next instanceof EnumC0556i) {
            Iterator it = set.iterator();
            while (it.hasNext()) {
                try {
                    EnumC0556i enumC0556i = (EnumC0556i) it.next();
                    hashSet.add(enumC0556i == null ? null : enumC0556i == EnumC0556i.CONCURRENT ? Collector.Characteristics.CONCURRENT : enumC0556i == EnumC0556i.UNORDERED ? Collector.Characteristics.UNORDERED : Collector.Characteristics.IDENTITY_FINISH);
                } catch (ClassCastException e4) {
                    C0503f.a("java.util.stream.Collector.Characteristics", e4);
                    throw null;
                }
            }
            return hashSet;
        } else if (!(next instanceof Collector.Characteristics)) {
            C0503f.a("java.util.stream.Collector.Characteristics", next.getClass());
            throw null;
        } else {
            Iterator it2 = set.iterator();
            while (it2.hasNext()) {
                try {
                    Collector.Characteristics characteristics = (Collector.Characteristics) it2.next();
                    hashSet.add(characteristics == null ? null : characteristics == Collector.Characteristics.CONCURRENT ? EnumC0556i.CONCURRENT : characteristics == Collector.Characteristics.UNORDERED ? EnumC0556i.UNORDERED : EnumC0556i.IDENTITY_FINISH);
                } catch (ClassCastException e5) {
                    C0503f.a("java.util.stream.Collector.Characteristics", e5);
                    throw null;
                }
            }
            return hashSet;
        }
    }

    public static C0516a S(Function function) {
        C0516a c0516a = new C0516a(8);
        c0516a.f4446b = function;
        return c0516a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v1, types: [j$.util.stream.Z2, j$.util.stream.B0] */
    /* JADX WARN: Type inference failed for: r0v3, types: [j$.util.stream.h1, j$.util.stream.B0] */
    public static B0 T(long j4) {
        return (j4 < 0 || j4 >= 2147483639) ? new Z2() : new C0553h1(j4);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.stream.IntStream, j$.util.stream.b] */
    public static IntStream U(j$.util.J j4) {
        return new AbstractC0521b(j4, EnumC0540e3.m(j4), false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v1, types: [j$.util.stream.Z2, j$.util.stream.C0] */
    /* JADX WARN: Type inference failed for: r0v3, types: [j$.util.stream.q1, j$.util.stream.C0] */
    public static C0 V(long j4) {
        return (j4 < 0 || j4 >= 2147483639) ? new Z2() : new C0598q1(j4);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.stream.o0, j$.util.stream.b] */
    public static InterfaceC0587o0 W(j$.util.M m4) {
        return new AbstractC0521b(m4, EnumC0540e3.m(m4), false);
    }

    public static F X(AbstractC0521b abstractC0521b, long j4, long j5) {
        if (j4 >= 0) {
            return new C0635y2(abstractC0521b, M(j5), j4, j5);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j4);
    }

    public static C0629x0 Y(EnumC0625w0 enumC0625w0) {
        Objects.requireNonNull(null);
        Objects.requireNonNull(enumC0625w0);
        return new C0629x0(EnumC0545f3.DOUBLE_VALUE, enumC0625w0, new C0592p0(enumC0625w0, 2));
    }

    public static IntStream Z(AbstractC0521b abstractC0521b, long j4, long j5) {
        if (j4 >= 0) {
            return new C0618u2(abstractC0521b, M(j5), j4, j5);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j4);
    }

    public static void a() {
        throw new IllegalStateException("called wrong accept method");
    }

    public static C0629x0 a0(EnumC0625w0 enumC0625w0) {
        Objects.requireNonNull(null);
        Objects.requireNonNull(enumC0625w0);
        return new C0629x0(EnumC0545f3.INT_VALUE, enumC0625w0, new C0592p0(enumC0625w0, 1));
    }

    public static InterfaceC0587o0 b0(AbstractC0521b abstractC0521b, long j4, long j5) {
        if (j4 >= 0) {
            return new C0627w2(abstractC0521b, M(j5), j4, j5);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j4);
    }

    public static C0629x0 c0(EnumC0625w0 enumC0625w0) {
        Objects.requireNonNull(null);
        Objects.requireNonNull(enumC0625w0);
        return new C0629x0(EnumC0545f3.LONG_VALUE, enumC0625w0, new C0592p0(enumC0625w0, 0));
    }

    public static C0629x0 d0(EnumC0625w0 enumC0625w0, Predicate predicate) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(enumC0625w0);
        return new C0629x0(EnumC0545f3.REFERENCE, enumC0625w0, new C0597q0(0, enumC0625w0, predicate));
    }

    public static void e(InterfaceC0584n2 interfaceC0584n2, Double d4) {
        if (N3.f4343a) {
            N3.a(interfaceC0584n2.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
            throw null;
        } else {
            interfaceC0584n2.accept(d4.doubleValue());
        }
    }

    public static Stream e0(AbstractC0521b abstractC0521b, long j4, long j5) {
        if (j4 >= 0) {
            return new C0608s2(abstractC0521b, M(j5), j4, j5);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j4);
    }

    public static void g(InterfaceC0589o2 interfaceC0589o2, Integer num) {
        if (N3.f4343a) {
            N3.a(interfaceC0589o2.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
            throw null;
        } else {
            interfaceC0589o2.accept(num.intValue());
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.stream.Stream, j$.util.stream.b] */
    public static Stream g0(Spliterator spliterator, boolean z4) {
        Objects.requireNonNull(spliterator);
        return new AbstractC0521b(spliterator, EnumC0540e3.m(spliterator), z4);
    }

    public static void i(InterfaceC0594p2 interfaceC0594p2, Long l2) {
        if (N3.f4343a) {
            N3.a(interfaceC0594p2.getClass(), "{0} calling Sink.OfLong.accept(Long)");
            throw null;
        } else {
            interfaceC0594p2.accept(l2.longValue());
        }
    }

    public static void k() {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void l() {
        throw new IllegalStateException("called wrong accept method");
    }

    public static Object[] m(K0 k02, IntFunction intFunction) {
        if (N3.f4343a) {
            N3.a(k02.getClass(), "{0} calling Node.OfPrimitive.asArray");
            throw null;
        } else if (k02.count() < 2147483639) {
            Object[] objArr = (Object[]) intFunction.apply((int) k02.count());
            k02.i(objArr, 0);
            return objArr;
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static void n(F0 f02, Double[] dArr, int i4) {
        if (N3.f4343a) {
            N3.a(f02.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
            throw null;
        }
        double[] dArr2 = (double[]) f02.e();
        for (int i5 = 0; i5 < dArr2.length; i5++) {
            dArr[i4 + i5] = Double.valueOf(dArr2[i5]);
        }
    }

    public static void o(H0 h02, Integer[] numArr, int i4) {
        if (N3.f4343a) {
            N3.a(h02.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
            throw null;
        }
        int[] iArr = (int[]) h02.e();
        for (int i5 = 0; i5 < iArr.length; i5++) {
            numArr[i4 + i5] = Integer.valueOf(iArr[i5]);
        }
    }

    public static void p(J0 j02, Long[] lArr, int i4) {
        if (N3.f4343a) {
            N3.a(j02.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
            throw null;
        }
        long[] jArr = (long[]) j02.e();
        for (int i5 = 0; i5 < jArr.length; i5++) {
            lArr[i4 + i5] = Long.valueOf(jArr[i5]);
        }
    }

    public static void q(F0 f02, Consumer consumer) {
        if (consumer instanceof DoubleConsumer) {
            f02.f((DoubleConsumer) consumer);
        } else if (N3.f4343a) {
            N3.a(f02.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        } else {
            ((j$.util.G) f02.spliterator()).forEachRemaining(consumer);
        }
    }

    public static void r(H0 h02, Consumer consumer) {
        if (consumer instanceof IntConsumer) {
            h02.f((IntConsumer) consumer);
        } else if (N3.f4343a) {
            N3.a(h02.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
            throw null;
        } else {
            ((j$.util.J) h02.spliterator()).forEachRemaining(consumer);
        }
    }

    public static void s(J0 j02, Consumer consumer) {
        if (consumer instanceof LongConsumer) {
            j02.f((LongConsumer) consumer);
        } else if (N3.f4343a) {
            N3.a(j02.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        } else {
            ((j$.util.M) j02.spliterator()).forEachRemaining(consumer);
        }
    }

    public static F0 t(F0 f02, long j4, long j5) {
        if (j4 == 0 && j5 == f02.count()) {
            return f02;
        }
        long j6 = j5 - j4;
        j$.util.G g4 = (j$.util.G) f02.spliterator();
        A0 J3 = J(j6);
        J3.l(j6);
        for (int i4 = 0; i4 < j4 && g4.tryAdvance((DoubleConsumer) new E0(0)); i4++) {
        }
        if (j5 == f02.count()) {
            g4.forEachRemaining((DoubleConsumer) J3);
        } else {
            for (int i5 = 0; i5 < j6 && g4.tryAdvance((DoubleConsumer) J3); i5++) {
            }
        }
        J3.k();
        return J3.a();
    }

    public static H0 u(H0 h02, long j4, long j5) {
        if (j4 == 0 && j5 == h02.count()) {
            return h02;
        }
        long j6 = j5 - j4;
        j$.util.J j7 = (j$.util.J) h02.spliterator();
        B0 T3 = T(j6);
        T3.l(j6);
        for (int i4 = 0; i4 < j4 && j7.tryAdvance((IntConsumer) new G0(0)); i4++) {
        }
        if (j5 == h02.count()) {
            j7.forEachRemaining((IntConsumer) T3);
        } else {
            for (int i5 = 0; i5 < j6 && j7.tryAdvance((IntConsumer) T3); i5++) {
            }
        }
        T3.k();
        return T3.a();
    }

    public static J0 v(J0 j02, long j4, long j5) {
        if (j4 == 0 && j5 == j02.count()) {
            return j02;
        }
        long j6 = j5 - j4;
        j$.util.M m4 = (j$.util.M) j02.spliterator();
        C0 V3 = V(j6);
        V3.l(j6);
        for (int i4 = 0; i4 < j4 && m4.tryAdvance((LongConsumer) new I0(0)); i4++) {
        }
        if (j5 == j02.count()) {
            m4.forEachRemaining((LongConsumer) V3);
        } else {
            for (int i5 = 0; i5 < j6 && m4.tryAdvance((LongConsumer) V3); i5++) {
            }
        }
        V3.k();
        return V3.a();
    }

    public static L0 w(L0 l0, long j4, long j5, IntFunction intFunction) {
        if (j4 == 0 && j5 == l0.count()) {
            return l0;
        }
        Spliterator spliterator = l0.spliterator();
        long j6 = j5 - j4;
        D0 D4 = D(j6, intFunction);
        D4.l(j6);
        for (int i4 = 0; i4 < j4 && spliterator.tryAdvance(new C0537e0(2)); i4++) {
        }
        if (j5 == l0.count()) {
            spliterator.forEachRemaining(D4);
        } else {
            for (int i5 = 0; i5 < j6 && spliterator.tryAdvance(D4); i5++) {
            }
        }
        D4.k();
        return D4.a();
    }

    @Override // j$.util.stream.K3
    public Object b(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        V1 f02 = f0();
        abstractC0521b.R(spliterator, f02);
        return f02.get();
    }

    @Override // j$.util.stream.K3
    public Object c(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        return ((V1) new C0529c2(this, abstractC0521b, spliterator).invoke()).get();
    }

    @Override // j$.util.stream.K3
    public /* synthetic */ int d() {
        return 0;
    }

    public abstract V1 f0();
}
