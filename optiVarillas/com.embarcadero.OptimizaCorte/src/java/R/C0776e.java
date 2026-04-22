package r;

/* renamed from: r.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0776e {

    /* renamed from: a  reason: collision with root package name */
    public int[] f5648a;

    /* renamed from: b  reason: collision with root package name */
    public int f5649b;

    /* renamed from: c  reason: collision with root package name */
    public int f5650c;

    /* renamed from: d  reason: collision with root package name */
    public int f5651d;

    public C0776e() {
        int highestOneBit = Integer.bitCount(8) != 1 ? Integer.highestOneBit(7) << 1 : 8;
        this.f5651d = highestOneBit - 1;
        this.f5648a = new int[highestOneBit];
    }

    public final void a(int i4) {
        int[] iArr = this.f5648a;
        int i5 = this.f5650c;
        iArr[i5] = i4;
        int i6 = this.f5651d & (i5 + 1);
        this.f5650c = i6;
        int i7 = this.f5649b;
        if (i6 == i7) {
            int length = iArr.length;
            int i8 = length - i7;
            int i9 = length << 1;
            if (i9 >= 0) {
                int[] iArr2 = new int[i9];
                System.arraycopy(iArr, i7, iArr2, 0, i8);
                System.arraycopy(this.f5648a, 0, iArr2, i8, this.f5649b);
                this.f5648a = iArr2;
                this.f5649b = 0;
                this.f5650c = length;
                this.f5651d = i9 - 1;
                return;
            }
            throw new RuntimeException("Max array capacity exceeded");
        }
    }
}
