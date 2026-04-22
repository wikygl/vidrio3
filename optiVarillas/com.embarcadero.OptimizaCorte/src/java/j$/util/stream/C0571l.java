package j$.util.stream;

import j$.util.C0505h;
import j$.util.C0506i;
import j$.util.C0508k;
import j$.util.function.Function$CC;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: j$.util.stream.l  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0571l implements BinaryOperator, Function, BiConsumer, ObjDoubleConsumer, Supplier, LongFunction, IntFunction, DoubleBinaryOperator {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4536a;

    public /* synthetic */ C0571l(int i4) {
        this.f4536a = i4;
    }

    @Override // java.util.function.ObjDoubleConsumer
    public void accept(Object obj, double d4) {
        switch (this.f4536a) {
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                double[] dArr = (double[]) obj;
                dArr[2] = dArr[2] + 1.0d;
                Collectors.a(dArr, d4);
                dArr[3] = dArr[3] + d4;
                return;
            case 4:
            default:
                ((C0505h) obj).accept(d4);
                return;
            case 5:
                double[] dArr2 = (double[]) obj;
                Collectors.a(dArr2, d4);
                dArr2[2] = dArr2[2] + d4;
                return;
        }
    }

    @Override // java.util.function.BiConsumer
    public void accept(Object obj, Object obj2) {
        switch (this.f4536a) {
            case 2:
                double[] dArr = (double[]) obj;
                double[] dArr2 = (double[]) obj2;
                Collectors.a(dArr, dArr2[0]);
                Collectors.a(dArr, dArr2[1]);
                dArr[2] = dArr[2] + dArr2[2];
                return;
            case 4:
                double[] dArr3 = (double[]) obj;
                double[] dArr4 = (double[]) obj2;
                Collectors.a(dArr3, dArr4[0]);
                Collectors.a(dArr3, dArr4[1]);
                dArr3[2] = dArr3[2] + dArr4[2];
                dArr3[3] = dArr3[3] + dArr4[3];
                return;
            case 20:
                ((List) obj).add(obj2);
                return;
            case 24:
                ((LinkedHashSet) obj).add(obj2);
                return;
            case 25:
                ((LinkedHashSet) obj).addAll((LinkedHashSet) obj2);
                return;
            default:
                ((C0505h) obj).b((C0505h) obj2);
                return;
        }
    }

    @Override // java.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        switch (this.f4536a) {
            case 2:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
            case 4:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
            case 20:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
            case 24:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
            case 25:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
            default:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
        }
    }

    @Override // java.util.function.BiFunction, java.util.function.Function
    public /* synthetic */ BiFunction andThen(Function function) {
        switch (this.f4536a) {
            case 0:
                return j$.com.android.tools.r8.a.c(this, function);
            case 11:
                return j$.com.android.tools.r8.a.c(this, function);
            case 13:
                return j$.com.android.tools.r8.a.c(this, function);
            case 15:
                return j$.com.android.tools.r8.a.c(this, function);
            default:
                return j$.com.android.tools.r8.a.c(this, function);
        }
    }

    @Override // java.util.function.BiFunction, java.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return Function$CC.$default$andThen(this, function);
    }

    @Override // java.util.function.IntFunction
    public Object apply(int i4) {
        return new Object[i4];
    }

    @Override // java.util.function.LongFunction
    public Object apply(long j4) {
        switch (this.f4536a) {
            case 10:
                return AbstractC0637z0.J(j4);
            case 11:
            default:
                return AbstractC0637z0.V(j4);
            case 12:
                return AbstractC0637z0.T(j4);
        }
    }

    @Override // java.util.function.Function
    public Object apply(Object obj) {
        Set set = Collectors.f4262a;
        return obj;
    }

    @Override // java.util.function.BiFunction
    public Object apply(Object obj, Object obj2) {
        switch (this.f4536a) {
            case 0:
                List list = (List) obj;
                Set set = Collectors.f4262a;
                list.addAll((List) obj2);
                return list;
            case 11:
                return new N0((F0) obj, (F0) obj2);
            case 13:
                return new N0((H0) obj, (H0) obj2);
            case 15:
                return new N0((J0) obj, (J0) obj2);
            default:
                return new N0((L0) obj, (L0) obj2);
        }
    }

    @Override // java.util.function.DoubleBinaryOperator
    public double applyAsDouble(double d4, double d5) {
        return Math.min(d4, d5);
    }

    @Override // java.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return Function$CC.$default$compose(this, function);
    }

    @Override // java.util.function.Supplier
    public Object get() {
        switch (this.f4536a) {
            case 6:
                return new Object();
            case 7:
                return new Object();
            case 8:
                return new Object();
            case 9:
                return new Object();
            case 18:
                return new C0505h();
            case 19:
                return new ArrayList();
            case 21:
                return new C0506i();
            case 22:
                return new C0508k();
            case 23:
                return new LinkedHashSet();
            default:
                return new double[4];
        }
    }
}
