package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/* renamed from: j$.util.stream.q0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0597q0 implements Supplier, Consumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4567a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Object f4568b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ Object f4569c;

    public /* synthetic */ C0597q0(int i4, Object obj, Object obj2) {
        this.f4567a = i4;
        this.f4568b = obj;
        this.f4569c = obj2;
    }

    @Override // java.util.function.Consumer
    public void accept(Object obj) {
        switch (this.f4567a) {
            case 1:
                ((C0585n3) this.f4568b).b((Consumer) this.f4569c, obj);
                return;
            case 2:
                if (obj == null) {
                    ((AtomicBoolean) this.f4568b).set(true);
                    return;
                }
                ((ConcurrentHashMap) this.f4569c).putIfAbsent(obj, Boolean.TRUE);
                return;
            default:
                ((BiConsumer) this.f4568b).accept(this.f4569c, obj);
                return;
        }
    }

    @Override // java.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.f4567a) {
            case 1:
                return j$.com.android.tools.r8.a.d(this, consumer);
            case 2:
                return j$.com.android.tools.r8.a.d(this, consumer);
            default:
                return j$.com.android.tools.r8.a.d(this, consumer);
        }
    }

    @Override // java.util.function.Supplier
    public Object get() {
        return new C0601r0((EnumC0625w0) this.f4568b, (Predicate) this.f4569c);
    }
}
