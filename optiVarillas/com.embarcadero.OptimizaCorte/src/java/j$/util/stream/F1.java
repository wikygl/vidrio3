package j$.util.stream;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class F1 extends AbstractC0637z0 {

    /* renamed from: h  reason: collision with root package name */
    public final /* synthetic */ int f4274h;

    /* renamed from: i  reason: collision with root package name */
    final /* synthetic */ Object f4275i;

    /* renamed from: j  reason: collision with root package name */
    final /* synthetic */ Object f4276j;

    /* renamed from: k  reason: collision with root package name */
    final /* synthetic */ Object f4277k;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ F1(EnumC0545f3 enumC0545f3, Object obj, Object obj2, Object obj3, int i4) {
        super(enumC0545f3);
        this.f4274h = i4;
        this.f4275i = obj;
        this.f4277k = obj2;
        this.f4276j = obj3;
    }

    public /* synthetic */ F1(EnumC0545f3 enumC0545f3, BinaryOperator binaryOperator, Object obj, Supplier supplier, int i4) {
        this.f4274h = i4;
        this.f4275i = binaryOperator;
        this.f4277k = obj;
        this.f4276j = supplier;
    }

    @Override // j$.util.stream.AbstractC0637z0
    public final V1 f0() {
        switch (this.f4274h) {
            case 0:
                return new C1((Supplier) this.f4276j, (ObjLongConsumer) this.f4277k, (BinaryOperator) this.f4275i);
            case 1:
                return new I1((Supplier) this.f4276j, (ObjDoubleConsumer) this.f4277k, (BinaryOperator) this.f4275i);
            case 2:
                return new K1(this.f4276j, (BiFunction) this.f4277k, (BinaryOperator) this.f4275i);
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return new O1((Supplier) this.f4276j, (BiConsumer) this.f4277k, (BiConsumer) this.f4275i);
            default:
                return new S1((Supplier) this.f4276j, (ObjIntConsumer) this.f4277k, (BinaryOperator) this.f4275i);
        }
    }
}
