package f1;

/* renamed from: f1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0411a<T> extends AbstractC0413c<T> {

    /* renamed from: a  reason: collision with root package name */
    public final Integer f3389a;

    /* renamed from: b  reason: collision with root package name */
    public final T f3390b;

    /* renamed from: c  reason: collision with root package name */
    public final d f3391c;

    /* JADX WARN: Multi-variable type inference failed */
    public C0411a(Object obj) {
        d dVar = d.f3393j;
        this.f3389a = null;
        this.f3390b = obj;
        this.f3391c = dVar;
    }

    @Override // f1.AbstractC0413c
    public final Integer a() {
        return this.f3389a;
    }

    @Override // f1.AbstractC0413c
    public final T b() {
        return this.f3390b;
    }

    @Override // f1.AbstractC0413c
    public final d c() {
        return this.f3391c;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractC0413c)) {
            return false;
        }
        AbstractC0413c abstractC0413c = (AbstractC0413c) obj;
        Integer num = this.f3389a;
        if (num != null ? num.equals(abstractC0413c.a()) : abstractC0413c.a() == null) {
            if (this.f3390b.equals(abstractC0413c.b()) && this.f3391c.equals(abstractC0413c.c())) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        int hashCode;
        Integer num = this.f3389a;
        if (num == null) {
            hashCode = 0;
        } else {
            hashCode = num.hashCode();
        }
        return ((((hashCode ^ 1000003) * 1000003) ^ this.f3390b.hashCode()) * 1000003) ^ this.f3391c.hashCode();
    }

    public final String toString() {
        return "Event{code=" + this.f3389a + ", payload=" + this.f3390b + ", priority=" + this.f3391c + "}";
    }
}
