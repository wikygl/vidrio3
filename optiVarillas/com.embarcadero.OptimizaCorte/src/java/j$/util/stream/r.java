package j$.util.stream;

import j$.util.C0506i;
import j$.util.C0508k;
import j$.util.C0509l;
import j$.util.C0510m;
import j$.util.C0511n;
import j$.util.C0512o;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.LongFunction;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class r implements DoubleFunction, ToDoubleFunction, IntFunction, DoubleBinaryOperator, Supplier, Predicate, ToIntFunction, IntBinaryOperator, ObjIntConsumer, BiConsumer, ObjLongConsumer, LongBinaryOperator, ToLongFunction, LongFunction {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4574a;

    @Override // java.util.function.ObjIntConsumer
    public void accept(Object obj, int i4) {
        switch (this.f4574a) {
            case 14:
                ((C0506i) obj).accept(i4);
                return;
            default:
                long[] jArr = (long[]) obj;
                jArr[0] = jArr[0] + 1;
                jArr[1] = jArr[1] + i4;
                return;
        }
    }

    @Override // java.util.function.ObjLongConsumer
    public void accept(Object obj, long j4) {
        switch (this.f4574a) {
            case 21:
                ((C0508k) obj).accept(j4);
                return;
            default:
                long[] jArr = (long[]) obj;
                jArr[0] = jArr[0] + 1;
                jArr[1] = jArr[1] + j4;
                return;
        }
    }

    @Override // java.util.function.BiConsumer
    public void accept(Object obj, Object obj2) {
        switch (this.f4574a) {
            case 15:
                ((C0506i) obj).b((C0506i) obj2);
                return;
            case 20:
                long[] jArr = (long[]) obj;
                long[] jArr2 = (long[]) obj2;
                jArr[0] = jArr[0] + jArr2[0];
                jArr[1] = jArr[1] + jArr2[1];
                return;
            case 24:
                ((C0508k) obj).b((C0508k) obj2);
                return;
            default:
                long[] jArr3 = (long[]) obj;
                long[] jArr4 = (long[]) obj2;
                jArr3[0] = jArr3[0] + jArr4[0];
                jArr3[1] = jArr3[1] + jArr4[1];
                return;
        }
    }

    @Override // java.util.function.Predicate
    public /* synthetic */ Predicate and(Predicate predicate) {
        switch (this.f4574a) {
            case 5:
                return j$.com.android.tools.r8.a.a(this, predicate);
            case 6:
                return j$.com.android.tools.r8.a.a(this, predicate);
            case 7:
                return j$.com.android.tools.r8.a.a(this, predicate);
            default:
                return j$.com.android.tools.r8.a.a(this, predicate);
        }
    }

    @Override // java.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        switch (this.f4574a) {
            case 15:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
            case 20:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
            case 24:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
            default:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
        }
    }

    @Override // java.util.function.DoubleFunction
    public Object apply(double d4) {
        return Double.valueOf(d4);
    }

    @Override // java.util.function.IntFunction
    public Object apply(int i4) {
        switch (this.f4574a) {
            case 2:
                return new Double[i4];
            case 9:
                return new Object[i4];
            case 10:
                return new Integer[i4];
            case 12:
                return Integer.valueOf(i4);
            default:
                return new Long[i4];
        }
    }

    @Override // java.util.function.LongFunction
    public Object apply(long j4) {
        return Long.valueOf(j4);
    }

    @Override // java.util.function.DoubleBinaryOperator
    public double applyAsDouble(double d4, double d5) {
        return Math.max(d4, d5);
    }

    @Override // java.util.function.ToDoubleFunction
    public double applyAsDouble(Object obj) {
        return ((Double) obj).doubleValue();
    }

    @Override // java.util.function.IntBinaryOperator
    public int applyAsInt(int i4, int i5) {
        switch (this.f4574a) {
            case 13:
                return Math.min(i4, i5);
            case 16:
                return i4 + i5;
            default:
                return Math.max(i4, i5);
        }
    }

    @Override // java.util.function.ToIntFunction
    public int applyAsInt(Object obj) {
        return ((Integer) obj).intValue();
    }

    @Override // java.util.function.LongBinaryOperator
    public long applyAsLong(long j4, long j5) {
        return Math.min(j4, j5);
    }

    @Override // java.util.function.ToLongFunction
    public long applyAsLong(Object obj) {
        return ((Long) obj).longValue();
    }

    @Override // java.util.function.Supplier
    public Object get() {
        switch (this.f4574a) {
            case 4:
                return new double[3];
            case 18:
                return new long[2];
            default:
                return new long[2];
        }
    }

    @Override // java.util.function.Predicate
    public Predicate negate() {
        switch (this.f4574a) {
            case 5:
                return new j$.util.function.a(this, 2);
            case 6:
                return new j$.util.function.a(this, 2);
            case 7:
                return new j$.util.function.a(this, 2);
            default:
                return new j$.util.function.a(this, 2);
        }
    }

    @Override // java.util.function.Predicate
    public /* synthetic */ Predicate or(Predicate predicate) {
        switch (this.f4574a) {
            case 5:
                return j$.com.android.tools.r8.a.h(this, predicate);
            case 6:
                return j$.com.android.tools.r8.a.h(this, predicate);
            case 7:
                return j$.com.android.tools.r8.a.h(this, predicate);
            default:
                return j$.com.android.tools.r8.a.h(this, predicate);
        }
    }

    @Override // java.util.function.Predicate
    public boolean test(Object obj) {
        switch (this.f4574a) {
            case 5:
                return ((C0510m) obj).c();
            case 6:
                return ((C0511n) obj).c();
            case 7:
                return ((C0512o) obj).c();
            default:
                return ((C0509l) obj).c();
        }
    }
}
