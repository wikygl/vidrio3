package y3;

import java.util.NoSuchElementException;
import m3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class b extends h {

    /* renamed from: j  reason: collision with root package name */
    public final int f6518j;

    /* renamed from: k  reason: collision with root package name */
    public final int f6519k;

    /* renamed from: l  reason: collision with root package name */
    public boolean f6520l;

    /* renamed from: m  reason: collision with root package name */
    public int f6521m;

    public b(int i4, int i5, int i6) {
        this.f6518j = i6;
        this.f6519k = i5;
        boolean z4 = false;
        if (i6 <= 0 ? i4 >= i5 : i4 <= i5) {
            z4 = true;
        }
        this.f6520l = z4;
        this.f6521m = z4 ? i4 : i5;
    }

    @Override // m3.h
    public final int a() {
        int i4 = this.f6521m;
        if (i4 == this.f6519k) {
            if (this.f6520l) {
                this.f6520l = false;
            } else {
                throw new NoSuchElementException();
            }
        } else {
            this.f6521m = this.f6518j + i4;
        }
        return i4;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f6520l;
    }
}
