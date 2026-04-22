package y2;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class a {

    /* renamed from: a  reason: collision with root package name */
    public final int f6498a;

    /* renamed from: b  reason: collision with root package name */
    public final float f6499b;

    /* renamed from: c  reason: collision with root package name */
    public int f6500c;

    /* renamed from: d  reason: collision with root package name */
    public int f6501d;

    /* renamed from: e  reason: collision with root package name */
    public final float f6502e;
    public final float f;

    /* renamed from: g  reason: collision with root package name */
    public final int f6503g;

    /* renamed from: h  reason: collision with root package name */
    public final float f6504h;

    /* JADX WARN: Code restructure failed: missing block: B:41:0x00c0, code lost:
        if (r6 > r3.f6499b) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00cf, code lost:
        if (r3.f <= r3.f6499b) goto L36;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public a(int r4, float r5, float r6, float r7, int r8, float r9, int r10, float r11, int r12, float r13) {
        /*
            Method dump skipped, instructions count: 227
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: y2.a.<init>(int, float, float, float, int, float, int, float, int, float):void");
    }

    public static a a(float f, float f4, float f5, float f6, int[] iArr, float f7, int[] iArr2, float f8, int[] iArr3) {
        a aVar = null;
        int i4 = 1;
        for (int i5 : iArr3) {
            int length = iArr2.length;
            int i6 = 0;
            while (i6 < length) {
                int i7 = iArr2[i6];
                int length2 = iArr.length;
                int i8 = 0;
                while (i8 < length2) {
                    int i9 = i8;
                    int i10 = length2;
                    int i11 = i6;
                    int i12 = length;
                    a aVar2 = new a(i4, f4, f5, f6, iArr[i8], f7, i7, f8, i5, f);
                    float f9 = aVar2.f6504h;
                    if (aVar == null || f9 < aVar.f6504h) {
                        if (f9 == 0.0f) {
                            return aVar2;
                        }
                        aVar = aVar2;
                    }
                    i4++;
                    i8 = i9 + 1;
                    length2 = i10;
                    i6 = i11;
                    length = i12;
                }
                i6++;
            }
        }
        return aVar;
    }

    public final String toString() {
        return "Arrangement [priority=" + this.f6498a + ", smallCount=" + this.f6500c + ", smallSize=" + this.f6499b + ", mediumCount=" + this.f6501d + ", mediumSize=" + this.f6502e + ", largeCount=" + this.f6503g + ", largeSize=" + this.f + ", cost=" + this.f6504h + "]";
    }
}
