package j$.util;

import j$.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract /* synthetic */ class D {
    public static void a(G g4, Consumer consumer) {
        if (consumer instanceof DoubleConsumer) {
            g4.forEachRemaining((DoubleConsumer) consumer);
        } else if (g0.f4227a) {
            g0.a(g4.getClass(), "{0} calling Spliterator.OfDouble.forEachRemaining((DoubleConsumer) action::accept)");
            throw null;
        } else {
            Objects.requireNonNull(consumer);
            g4.forEachRemaining((DoubleConsumer) new C0513p(consumer));
        }
    }

    public static void b(J j4, Consumer consumer) {
        if (consumer instanceof IntConsumer) {
            j4.forEachRemaining((IntConsumer) consumer);
        } else if (g0.f4227a) {
            g0.a(j4.getClass(), "{0} calling Spliterator.OfInt.forEachRemaining((IntConsumer) action::accept)");
            throw null;
        } else {
            Objects.requireNonNull(consumer);
            j4.forEachRemaining((IntConsumer) new C0640t(consumer));
        }
    }

    public static void c(M m4, Consumer consumer) {
        if (consumer instanceof LongConsumer) {
            m4.forEachRemaining((LongConsumer) consumer);
        } else if (g0.f4227a) {
            g0.a(m4.getClass(), "{0} calling Spliterator.OfLong.forEachRemaining((LongConsumer) action::accept)");
            throw null;
        } else {
            Objects.requireNonNull(consumer);
            m4.forEachRemaining((LongConsumer) new C0644x(consumer));
        }
    }

    public static long d(Spliterator spliterator) {
        if ((spliterator.characteristics() & 64) == 0) {
            return -1L;
        }
        return spliterator.estimateSize();
    }

    public static boolean e(Spliterator spliterator, int i4) {
        return (spliterator.characteristics() & i4) == i4;
    }

    public static boolean f(G g4, Consumer consumer) {
        if (consumer instanceof DoubleConsumer) {
            return g4.tryAdvance((DoubleConsumer) consumer);
        }
        if (g0.f4227a) {
            g0.a(g4.getClass(), "{0} calling Spliterator.OfDouble.tryAdvance((DoubleConsumer) action::accept)");
            throw null;
        }
        Objects.requireNonNull(consumer);
        return g4.tryAdvance((DoubleConsumer) new C0513p(consumer));
    }

    public static boolean g(J j4, Consumer consumer) {
        if (consumer instanceof IntConsumer) {
            return j4.tryAdvance((IntConsumer) consumer);
        }
        if (g0.f4227a) {
            g0.a(j4.getClass(), "{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)");
            throw null;
        }
        Objects.requireNonNull(consumer);
        return j4.tryAdvance((IntConsumer) new C0640t(consumer));
    }

    public static boolean h(M m4, Consumer consumer) {
        if (consumer instanceof LongConsumer) {
            return m4.tryAdvance((LongConsumer) consumer);
        }
        if (g0.f4227a) {
            g0.a(m4.getClass(), "{0} calling Spliterator.OfLong.tryAdvance((LongConsumer) action::accept)");
            throw null;
        }
        Objects.requireNonNull(consumer);
        return m4.tryAdvance((LongConsumer) new C0644x(consumer));
    }

    public static C0509l i(Optional optional) {
        if (optional == null) {
            return null;
        }
        return optional.isPresent() ? C0509l.d(optional.get()) : C0509l.a();
    }

    public static C0510m j(OptionalDouble optionalDouble) {
        if (optionalDouble == null) {
            return null;
        }
        return optionalDouble.isPresent() ? C0510m.d(optionalDouble.getAsDouble()) : C0510m.a();
    }

    public static C0511n k(OptionalInt optionalInt) {
        if (optionalInt == null) {
            return null;
        }
        return optionalInt.isPresent() ? C0511n.d(optionalInt.getAsInt()) : C0511n.a();
    }

    public static C0512o l(OptionalLong optionalLong) {
        if (optionalLong == null) {
            return null;
        }
        return optionalLong.isPresent() ? C0512o.d(optionalLong.getAsLong()) : C0512o.a();
    }

    public static Optional m(C0509l c0509l) {
        if (c0509l == null) {
            return null;
        }
        return c0509l.c() ? Optional.of(c0509l.b()) : Optional.empty();
    }

    public static OptionalDouble n(C0510m c0510m) {
        if (c0510m == null) {
            return null;
        }
        return c0510m.c() ? OptionalDouble.of(c0510m.b()) : OptionalDouble.empty();
    }

    public static OptionalInt o(C0511n c0511n) {
        if (c0511n == null) {
            return null;
        }
        return c0511n.c() ? OptionalInt.of(c0511n.b()) : OptionalInt.empty();
    }

    public static OptionalLong p(C0512o c0512o) {
        if (c0512o == null) {
            return null;
        }
        return c0512o.c() ? OptionalLong.of(c0512o.b()) : OptionalLong.empty();
    }

    public static /* synthetic */ Object q(java.util.Map map, Object obj, Object obj2) {
        return map instanceof Map ? ((Map) map).putIfAbsent(obj, obj2) : Map.CC.$default$putIfAbsent(map, obj, obj2);
    }

    public int characteristics() {
        return 16448;
    }

    public long estimateSize() {
        return 0L;
    }

    public void forEachRemaining(Object obj) {
        Objects.requireNonNull(obj);
    }

    public boolean tryAdvance(Object obj) {
        Objects.requireNonNull(obj);
        return false;
    }

    public Spliterator trySplit() {
        return null;
    }
}
