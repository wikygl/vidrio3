package j$.util.stream;

import j$.util.Spliterator;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class Z2 extends AbstractC0531d implements Iterable {

    /* renamed from: e  reason: collision with root package name */
    Object f4444e;
    Object[] f;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Z2() {
        this.f4444e = c(16);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Z2(int i4) {
        super(i4);
        this.f4444e = c(1 << this.f4465a);
    }

    public abstract Object c(int i4);

    @Override // j$.util.stream.AbstractC0531d
    public final void clear() {
        Object[] objArr = this.f;
        if (objArr != null) {
            this.f4444e = objArr[0];
            this.f = null;
            this.f4468d = null;
        }
        this.f4466b = 0;
        this.f4467c = 0;
    }

    public void d(Object obj, int i4) {
        long j4 = i4;
        long count = count() + j4;
        if (count > s(obj) || count < j4) {
            throw new IndexOutOfBoundsException("does not fit");
        }
        if (this.f4467c == 0) {
            System.arraycopy(this.f4444e, 0, obj, i4, this.f4466b);
            return;
        }
        for (int i5 = 0; i5 < this.f4467c; i5++) {
            Object obj2 = this.f[i5];
            System.arraycopy(obj2, 0, obj, i4, s(obj2));
            i4 += s(this.f[i5]);
        }
        int i6 = this.f4466b;
        if (i6 > 0) {
            System.arraycopy(this.f4444e, 0, obj, i4, i6);
        }
    }

    public Object e() {
        long count = count();
        if (count < 2147483639) {
            Object c4 = c((int) count);
            d(c4, 0);
            return c4;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void f(Object obj) {
        for (int i4 = 0; i4 < this.f4467c; i4++) {
            Object obj2 = this.f[i4];
            r(obj2, 0, s(obj2), obj);
        }
        r(this.f4444e, 0, this.f4466b, obj);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void r(Object obj, int i4, int i5, Object obj2);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int s(Object obj);

    public abstract Spliterator spliterator();

    @Override // java.lang.Iterable
    public final /* synthetic */ java.util.Spliterator spliterator() {
        return Spliterator.Wrapper.convert(spliterator());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int t(long j4) {
        if (this.f4467c == 0) {
            if (j4 < this.f4466b) {
                return 0;
            }
            throw new IndexOutOfBoundsException(Long.toString(j4));
        } else if (j4 < count()) {
            for (int i4 = 0; i4 <= this.f4467c; i4++) {
                if (j4 < this.f4468d[i4] + s(this.f[i4])) {
                    return i4;
                }
            }
            throw new IndexOutOfBoundsException(Long.toString(j4));
        } else {
            throw new IndexOutOfBoundsException(Long.toString(j4));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void u(long j4) {
        long s4;
        int i4;
        int i5 = this.f4467c;
        if (i5 == 0) {
            s4 = s(this.f4444e);
        } else {
            s4 = s(this.f[i5]) + this.f4468d[i5];
        }
        if (j4 > s4) {
            if (this.f == null) {
                Object[] v4 = v();
                this.f = v4;
                this.f4468d = new long[8];
                v4[0] = this.f4444e;
            }
            int i6 = this.f4467c + 1;
            while (j4 > s4) {
                Object[] objArr = this.f;
                if (i6 >= objArr.length) {
                    int length = objArr.length * 2;
                    this.f = Arrays.copyOf(objArr, length);
                    this.f4468d = Arrays.copyOf(this.f4468d, length);
                }
                int i7 = this.f4465a;
                if (i6 != 0 && i6 != 1) {
                    i7 = Math.min((i7 + i6) - 1, 30);
                }
                int i8 = 1 << i7;
                this.f[i6] = c(i8);
                long[] jArr = this.f4468d;
                jArr[i6] = jArr[i6 - 1] + s(this.f[i4]);
                s4 += i8;
                i6++;
            }
        }
    }

    protected abstract Object[] v();

    /* JADX INFO: Access modifiers changed from: protected */
    public final void w() {
        long s4;
        if (this.f4466b == s(this.f4444e)) {
            if (this.f == null) {
                Object[] v4 = v();
                this.f = v4;
                this.f4468d = new long[8];
                v4[0] = this.f4444e;
            }
            int i4 = this.f4467c;
            int i5 = i4 + 1;
            Object[] objArr = this.f;
            if (i5 >= objArr.length || objArr[i5] == null) {
                if (i4 == 0) {
                    s4 = s(this.f4444e);
                } else {
                    s4 = s(objArr[i4]) + this.f4468d[i4];
                }
                u(s4 + 1);
            }
            this.f4466b = 0;
            int i6 = this.f4467c + 1;
            this.f4467c = i6;
            this.f4444e = this.f[i6];
        }
    }
}
