package j$.com.android.tools.r8;

import j$.util.Objects;
import j$.util.concurrent.t;
import j$.util.function.b;
import j$.util.function.d;
import j$.util.function.e;
import j$.util.function.f;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import sun.misc.Unsafe;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract /* synthetic */ class a {
    public static f a(Predicate predicate, Predicate predicate2) {
        Objects.requireNonNull(predicate2);
        return new f(predicate, predicate2, 0);
    }

    public static t b(BiConsumer biConsumer, BiConsumer biConsumer2) {
        Objects.requireNonNull(biConsumer2);
        return new t(1, biConsumer, biConsumer2);
    }

    public static t c(BiFunction biFunction, Function function) {
        Objects.requireNonNull(function);
        return new t(biFunction, function);
    }

    public static t d(Consumer consumer, Consumer consumer2) {
        Objects.requireNonNull(consumer2);
        return new t(3, consumer, consumer2);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.function.b] */
    public static b e(final DoubleConsumer doubleConsumer, final DoubleConsumer doubleConsumer2) {
        Objects.requireNonNull(doubleConsumer2);
        return new DoubleConsumer() { // from class: j$.util.function.b
            @Override // java.util.function.DoubleConsumer
            public final void accept(double d4) {
                doubleConsumer.accept(d4);
                doubleConsumer2.accept(d4);
            }

            @Override // java.util.function.DoubleConsumer
            public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer3) {
                return j$.com.android.tools.r8.a.e(this, doubleConsumer3);
            }
        };
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.function.d] */
    public static d f(final IntConsumer intConsumer, final IntConsumer intConsumer2) {
        Objects.requireNonNull(intConsumer2);
        return new IntConsumer() { // from class: j$.util.function.d
            @Override // java.util.function.IntConsumer
            public final void accept(int i4) {
                intConsumer.accept(i4);
                intConsumer2.accept(i4);
            }

            @Override // java.util.function.IntConsumer
            public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer3) {
                return j$.com.android.tools.r8.a.f(this, intConsumer3);
            }
        };
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.function.e] */
    public static e g(final LongConsumer longConsumer, final LongConsumer longConsumer2) {
        Objects.requireNonNull(longConsumer2);
        return new LongConsumer() { // from class: j$.util.function.e
            @Override // java.util.function.LongConsumer
            public final void accept(long j4) {
                longConsumer.accept(j4);
                longConsumer2.accept(j4);
            }

            @Override // java.util.function.LongConsumer
            public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer3) {
                return j$.com.android.tools.r8.a.g(this, longConsumer3);
            }
        };
    }

    public static f h(Predicate predicate, Predicate predicate2) {
        Objects.requireNonNull(predicate2);
        return new f(predicate, predicate2, 1);
    }

    public static /* synthetic */ long i(long j4, long j5) {
        long j6 = j4 + j5;
        if (((j5 ^ j4) < 0) || ((j4 ^ j6) >= 0)) {
            return j6;
        }
        throw new ArithmeticException();
    }

    public static /* synthetic */ AbstractMap.SimpleImmutableEntry j(String str, String str2) {
        return new AbstractMap.SimpleImmutableEntry(Objects.requireNonNull(str), Objects.requireNonNull(str2));
    }

    public static /* synthetic */ List k(Object[] objArr) {
        ArrayList arrayList = new ArrayList(objArr.length);
        for (Object obj : objArr) {
            arrayList.add(Objects.requireNonNull(obj));
        }
        return Collections.unmodifiableList(arrayList);
    }

    public static /* synthetic */ boolean l(Unsafe unsafe, Object obj, long j4, Object obj2) {
        while (!unsafe.compareAndSwapObject(obj, j4, (Object) null, obj2)) {
            if (unsafe.getObject(obj, j4) != null) {
                return false;
            }
        }
        return true;
    }

    public static /* synthetic */ long m(long j4, long j5) {
        long j6 = j4 % j5;
        if (j6 == 0) {
            return 0L;
        }
        return (((j4 ^ j5) >> 63) | 1) > 0 ? j6 : j6 + j5;
    }

    public static /* synthetic */ long n(long j4, long j5) {
        long j6 = j4 / j5;
        return (j4 - (j5 * j6) != 0 && (((j4 ^ j5) >> 63) | 1) < 0) ? j6 - 1 : j6;
    }

    public static /* synthetic */ long o(long j4, long j5) {
        int numberOfLeadingZeros = Long.numberOfLeadingZeros(~j5) + Long.numberOfLeadingZeros(j5) + Long.numberOfLeadingZeros(~j4) + Long.numberOfLeadingZeros(j4);
        if (numberOfLeadingZeros > 65) {
            return j4 * j5;
        }
        if (numberOfLeadingZeros >= 64) {
            int i4 = (j4 > 0L ? 1 : (j4 == 0L ? 0 : -1));
            if ((i4 >= 0) | (j5 != Long.MIN_VALUE)) {
                long j6 = j4 * j5;
                if (i4 == 0 || j6 / j4 == j5) {
                    return j6;
                }
            }
        }
        throw new ArithmeticException();
    }

    public static /* synthetic */ long p(long j4, long j5) {
        long j6 = j4 - j5;
        if (((j5 ^ j4) >= 0) || ((j4 ^ j6) >= 0)) {
            return j6;
        }
        throw new ArithmeticException();
    }
}
