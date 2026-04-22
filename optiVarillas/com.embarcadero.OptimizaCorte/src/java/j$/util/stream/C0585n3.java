package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.Comparator;
import java.util.function.Consumer;

/* renamed from: j$.util.stream.n3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0585n3 implements Spliterator, Consumer {

    /* renamed from: d  reason: collision with root package name */
    private static final Object f4551d = new Object();

    /* renamed from: a  reason: collision with root package name */
    private final Spliterator f4552a;

    /* renamed from: b  reason: collision with root package name */
    private final ConcurrentHashMap f4553b;

    /* renamed from: c  reason: collision with root package name */
    private Object f4554c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0585n3(Spliterator spliterator) {
        this(spliterator, new ConcurrentHashMap());
    }

    private C0585n3(Spliterator spliterator, ConcurrentHashMap concurrentHashMap) {
        this.f4552a = spliterator;
        this.f4553b = concurrentHashMap;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f4554c = obj;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void b(Consumer consumer, Object obj) {
        if (this.f4553b.putIfAbsent(obj != null ? obj : f4551d, Boolean.TRUE) == null) {
            consumer.accept(obj);
        }
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return (this.f4552a.characteristics() & (-16469)) | 1;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        return this.f4552a.estimateSize();
    }

    @Override // j$.util.Spliterator
    public final void forEachRemaining(Consumer consumer) {
        this.f4552a.forEachRemaining(new C0597q0(1, this, consumer));
    }

    @Override // j$.util.Spliterator
    public final Comparator getComparator() {
        return this.f4552a.getComparator();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        return j$.util.D.d(this);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return j$.util.D.e(this, i4);
    }

    @Override // j$.util.Spliterator
    public final boolean tryAdvance(Consumer consumer) {
        while (this.f4552a.tryAdvance(this)) {
            Object obj = this.f4554c;
            if (obj == null) {
                obj = f4551d;
            }
            if (this.f4553b.putIfAbsent(obj, Boolean.TRUE) == null) {
                consumer.accept(this.f4554c);
                this.f4554c = null;
                return true;
            }
        }
        return false;
    }

    @Override // j$.util.Spliterator
    public final Spliterator trySplit() {
        Spliterator trySplit = this.f4552a.trySplit();
        if (trySplit != null) {
            return new C0585n3(trySplit, this.f4553b);
        }
        return null;
    }
}
