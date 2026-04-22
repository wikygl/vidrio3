package j$.util.concurrent;

import j$.util.stream.AbstractC0637z0;
import j$.util.stream.C0582n0;
import j$.util.stream.E;
import j$.util.stream.IntStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class ThreadLocalRandom extends Random {
    private static final long serialVersionUID = -5851777807851030925L;

    /* renamed from: a  reason: collision with root package name */
    long f4149a;

    /* renamed from: b  reason: collision with root package name */
    int f4150b;

    /* renamed from: c  reason: collision with root package name */
    boolean f4151c;
    private static final ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("rnd", Long.TYPE), new ObjectStreamField("initialized", Boolean.TYPE)};

    /* renamed from: d  reason: collision with root package name */
    private static final ThreadLocal f4146d = new ThreadLocal();

    /* renamed from: e  reason: collision with root package name */
    private static final AtomicInteger f4147e = new AtomicInteger();
    private static final ThreadLocal f = new ThreadLocal();

    /* renamed from: g  reason: collision with root package name */
    private static final AtomicLong f4148g = new AtomicLong(h(System.currentTimeMillis()) ^ h(System.nanoTime()));

    /* JADX WARN: Type inference failed for: r1v6, types: [java.lang.Object, java.security.PrivilegedAction] */
    static {
        if (((Boolean) AccessController.doPrivileged((PrivilegedAction<Object>) new Object())).booleanValue()) {
            byte[] seed = SecureRandom.getSeed(8);
            long j4 = seed[0] & 255;
            for (int i4 = 1; i4 < 8; i4++) {
                j4 = (j4 << 8) | (seed[i4] & 255);
            }
            f4148g.set(j4);
        }
    }

    private ThreadLocalRandom() {
        this.f4151c = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public /* synthetic */ ThreadLocalRandom(int i4) {
        this();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int a(int i4) {
        int i5 = i4 ^ (i4 << 13);
        int i6 = i5 ^ (i5 >>> 17);
        int i7 = i6 ^ (i6 << 5);
        ((ThreadLocalRandom) f.get()).f4150b = i7;
        return i7;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int b() {
        return ((ThreadLocalRandom) f.get()).f4150b;
    }

    public static ThreadLocalRandom current() {
        ThreadLocalRandom threadLocalRandom = (ThreadLocalRandom) f.get();
        if (threadLocalRandom.f4150b == 0) {
            f();
        }
        return threadLocalRandom;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void f() {
        int addAndGet = f4147e.addAndGet(-1640531527);
        if (addAndGet == 0) {
            addAndGet = 1;
        }
        long h4 = h(f4148g.getAndAdd(-4942790177534073029L));
        ThreadLocalRandom threadLocalRandom = (ThreadLocalRandom) f.get();
        threadLocalRandom.f4149a = h4;
        threadLocalRandom.f4150b = addAndGet;
    }

    private static int g(long j4) {
        long j5 = (j4 ^ (j4 >>> 33)) * (-49064778989728563L);
        return (int) (((j5 ^ (j5 >>> 33)) * (-4265267296055464877L)) >>> 32);
    }

    private static long h(long j4) {
        long j5 = (j4 ^ (j4 >>> 33)) * (-49064778989728563L);
        long j6 = (j5 ^ (j5 >>> 33)) * (-4265267296055464877L);
        return j6 ^ (j6 >>> 33);
    }

    private Object readResolve() {
        return current();
    }

    private void writeObject(ObjectOutputStream objectOutputStream) {
        ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("rnd", this.f4149a);
        putFields.put("initialized", true);
        objectOutputStream.writeFields();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final double c(double d4, double d5) {
        double nextLong = (nextLong() >>> 11) * 1.1102230246251565E-16d;
        if (d4 < d5) {
            double d6 = ((d5 - d4) * nextLong) + d4;
            return d6 >= d5 ? Double.longBitsToDouble(Double.doubleToLongBits(d5) - 1) : d6;
        }
        return nextLong;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int d(int i4, int i5) {
        int i6;
        int g4 = g(i());
        if (i4 < i5) {
            int i7 = i5 - i4;
            int i8 = i7 - 1;
            if ((i7 & i8) == 0) {
                i6 = g4 & i8;
            } else if (i7 > 0) {
                int i9 = g4 >>> 1;
                while (true) {
                    int i10 = i9 + i8;
                    i6 = i9 % i7;
                    if (i10 - i6 >= 0) {
                        break;
                    }
                    i9 = g(i()) >>> 1;
                }
            } else {
                while (true) {
                    if (g4 >= i4 && g4 < i5) {
                        return g4;
                    }
                    g4 = g(i());
                }
            }
            return i6 + i4;
        }
        return g4;
    }

    @Override // java.util.Random
    public final DoubleStream doubles() {
        return E.w(AbstractC0637z0.K(new y(0L, Long.MAX_VALUE, Double.MAX_VALUE, 0.0d)));
    }

    @Override // java.util.Random
    public final DoubleStream doubles(double d4, double d5) {
        if (d4 < d5) {
            return E.w(AbstractC0637z0.K(new y(0L, Long.MAX_VALUE, d4, d5)));
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    @Override // java.util.Random
    public final DoubleStream doubles(long j4) {
        if (j4 >= 0) {
            return E.w(AbstractC0637z0.K(new y(0L, j4, Double.MAX_VALUE, 0.0d)));
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    @Override // java.util.Random
    public final DoubleStream doubles(long j4, double d4, double d5) {
        if (j4 >= 0) {
            if (d4 < d5) {
                return E.w(AbstractC0637z0.K(new y(0L, j4, d4, d5)));
            }
            throw new IllegalArgumentException("bound must be greater than origin");
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final long e(long j4, long j5) {
        long h4 = h(i());
        if (j4 >= j5) {
            return h4;
        }
        long j6 = j5 - j4;
        long j7 = j6 - 1;
        if ((j6 & j7) == 0) {
            return (h4 & j7) + j4;
        }
        if (j6 > 0) {
            while (true) {
                long j8 = h4 >>> 1;
                long j9 = j8 + j7;
                long j10 = j8 % j6;
                if (j9 - j10 >= 0) {
                    return j10 + j4;
                }
                h4 = h(i());
            }
        } else {
            while (true) {
                if (h4 >= j4 && h4 < j5) {
                    return h4;
                }
                h4 = h(i());
            }
        }
    }

    final long i() {
        long j4 = this.f4149a - 7046029254386353131L;
        this.f4149a = j4;
        return j4;
    }

    @Override // java.util.Random
    public final IntStream ints() {
        return IntStream.Wrapper.convert(AbstractC0637z0.U(new z(0L, Long.MAX_VALUE, Integer.MAX_VALUE, 0)));
    }

    @Override // java.util.Random
    public final java.util.stream.IntStream ints(int i4, int i5) {
        if (i4 < i5) {
            return IntStream.Wrapper.convert(AbstractC0637z0.U(new z(0L, Long.MAX_VALUE, i4, i5)));
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    @Override // java.util.Random
    public final java.util.stream.IntStream ints(long j4) {
        if (j4 >= 0) {
            return IntStream.Wrapper.convert(AbstractC0637z0.U(new z(0L, j4, Integer.MAX_VALUE, 0)));
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    @Override // java.util.Random
    public final java.util.stream.IntStream ints(long j4, int i4, int i5) {
        if (j4 >= 0) {
            if (i4 < i5) {
                return IntStream.Wrapper.convert(AbstractC0637z0.U(new z(0L, j4, i4, i5)));
            }
            throw new IllegalArgumentException("bound must be greater than origin");
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    @Override // java.util.Random
    public final LongStream longs() {
        return C0582n0.w(AbstractC0637z0.W(new A(0L, Long.MAX_VALUE, Long.MAX_VALUE, 0L)));
    }

    @Override // java.util.Random
    public final LongStream longs(long j4) {
        if (j4 >= 0) {
            return C0582n0.w(AbstractC0637z0.W(new A(0L, j4, Long.MAX_VALUE, 0L)));
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    @Override // java.util.Random
    public final LongStream longs(long j4, long j5) {
        if (j4 < j5) {
            return C0582n0.w(AbstractC0637z0.W(new A(0L, Long.MAX_VALUE, j4, j5)));
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    @Override // java.util.Random
    public final LongStream longs(long j4, long j5, long j6) {
        if (j4 >= 0) {
            if (j5 < j6) {
                return C0582n0.w(AbstractC0637z0.W(new A(0L, j4, j5, j6)));
            }
            throw new IllegalArgumentException("bound must be greater than origin");
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    @Override // java.util.Random
    protected final int next(int i4) {
        return nextInt() >>> (32 - i4);
    }

    @Override // java.util.Random
    public final boolean nextBoolean() {
        return g(i()) < 0;
    }

    @Override // java.util.Random
    public final double nextDouble() {
        return (h(i()) >>> 11) * 1.1102230246251565E-16d;
    }

    @Override // java.util.Random
    public final float nextFloat() {
        return (g(i()) >>> 8) * 5.9604645E-8f;
    }

    @Override // java.util.Random
    public final double nextGaussian() {
        ThreadLocal threadLocal = f4146d;
        Double d4 = (Double) threadLocal.get();
        if (d4 != null) {
            threadLocal.set(null);
            return d4.doubleValue();
        }
        while (true) {
            double nextDouble = (nextDouble() * 2.0d) - 1.0d;
            double nextDouble2 = (nextDouble() * 2.0d) - 1.0d;
            double d5 = (nextDouble2 * nextDouble2) + (nextDouble * nextDouble);
            if (d5 < 1.0d && d5 != 0.0d) {
                double sqrt = StrictMath.sqrt((StrictMath.log(d5) * (-2.0d)) / d5);
                threadLocal.set(Double.valueOf(nextDouble2 * sqrt));
                return nextDouble * sqrt;
            }
        }
    }

    @Override // java.util.Random
    public int nextInt() {
        return g(i());
    }

    @Override // java.util.Random
    public final int nextInt(int i4) {
        if (i4 <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }
        int g4 = g(i());
        int i5 = i4 - 1;
        if ((i4 & i5) == 0) {
            return g4 & i5;
        }
        while (true) {
            int i6 = g4 >>> 1;
            int i7 = i6 + i5;
            int i8 = i6 % i4;
            if (i7 - i8 >= 0) {
                return i8;
            }
            g4 = g(i());
        }
    }

    @Override // java.util.Random
    public final long nextLong() {
        return h(i());
    }

    @Override // java.util.Random
    public final void setSeed(long j4) {
        if (this.f4151c) {
            throw new UnsupportedOperationException();
        }
    }
}
