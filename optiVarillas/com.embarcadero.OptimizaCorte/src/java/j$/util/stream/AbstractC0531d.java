package j$.util.stream;

/* renamed from: j$.util.stream.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
abstract class AbstractC0531d {

    /* renamed from: a  reason: collision with root package name */
    protected final int f4465a;

    /* renamed from: b  reason: collision with root package name */
    protected int f4466b;

    /* renamed from: c  reason: collision with root package name */
    protected int f4467c;

    /* renamed from: d  reason: collision with root package name */
    protected long[] f4468d;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractC0531d() {
        this.f4465a = 4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractC0531d(int i4) {
        if (i4 >= 0) {
            this.f4465a = Math.max(4, 32 - Integer.numberOfLeadingZeros(i4 - 1));
            return;
        }
        throw new IllegalArgumentException("Illegal Capacity: " + i4);
    }

    public abstract void clear();

    public final long count() {
        int i4 = this.f4467c;
        return i4 == 0 ? this.f4466b : this.f4468d[i4] + this.f4466b;
    }
}
