package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.a3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public class C0520a3 extends AbstractC0531d implements Consumer, Iterable {

    /* renamed from: e  reason: collision with root package name */
    protected Object[] f4447e = new Object[1 << 4];
    protected Object[][] f;

    @Override // java.util.function.Consumer
    public void accept(Object obj) {
        long length;
        int i4 = this.f4466b;
        Object[] objArr = this.f4447e;
        if (i4 == objArr.length) {
            if (this.f == null) {
                Object[][] objArr2 = new Object[8];
                this.f = objArr2;
                this.f4468d = new long[8];
                objArr2[0] = objArr;
            }
            int i5 = this.f4467c;
            int i6 = i5 + 1;
            Object[][] objArr3 = this.f;
            if (i6 >= objArr3.length || objArr3[i6] == null) {
                if (i5 == 0) {
                    length = objArr.length;
                } else {
                    length = objArr3[i5].length + this.f4468d[i5];
                }
                r(length + 1);
            }
            this.f4466b = 0;
            int i7 = this.f4467c + 1;
            this.f4467c = i7;
            this.f4447e = this.f[i7];
        }
        Object[] objArr4 = this.f4447e;
        int i8 = this.f4466b;
        this.f4466b = i8 + 1;
        objArr4[i8] = obj;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // j$.util.stream.AbstractC0531d
    public final void clear() {
        Object[][] objArr = this.f;
        if (objArr != null) {
            this.f4447e = objArr[0];
            int i4 = 0;
            while (true) {
                Object[] objArr2 = this.f4447e;
                if (i4 >= objArr2.length) {
                    break;
                }
                objArr2[i4] = null;
                i4++;
            }
            this.f = null;
            this.f4468d = null;
        } else {
            for (int i5 = 0; i5 < this.f4466b; i5++) {
                this.f4447e[i5] = null;
            }
        }
        this.f4466b = 0;
        this.f4467c = 0;
    }

    @Override // java.lang.Iterable
    public void forEach(Consumer consumer) {
        for (int i4 = 0; i4 < this.f4467c; i4++) {
            for (Object obj : this.f[i4]) {
                consumer.accept(obj);
            }
        }
        for (int i5 = 0; i5 < this.f4466b; i5++) {
            consumer.accept(this.f4447e[i5]);
        }
    }

    @Override // java.lang.Iterable
    public final Iterator iterator() {
        return Spliterators.i(spliterator());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void r(long j4) {
        Object[][] objArr;
        int i4;
        int i5 = this.f4467c;
        long length = i5 == 0 ? this.f4447e.length : this.f4468d[i5] + this.f[i5].length;
        if (j4 > length) {
            if (this.f == null) {
                Object[][] objArr2 = new Object[8];
                this.f = objArr2;
                this.f4468d = new long[8];
                objArr2[0] = this.f4447e;
            }
            int i6 = i5 + 1;
            while (j4 > length) {
                Object[][] objArr3 = this.f;
                if (i6 >= objArr3.length) {
                    int length2 = objArr3.length * 2;
                    this.f = (Object[][]) Arrays.copyOf(objArr3, length2);
                    this.f4468d = Arrays.copyOf(this.f4468d, length2);
                }
                int i7 = this.f4465a;
                if (i6 != 0 && i6 != 1) {
                    i7 = Math.min((i7 + i6) - 1, 30);
                }
                int i8 = 1 << i7;
                this.f[i6] = new Object[i8];
                long[] jArr = this.f4468d;
                jArr[i6] = jArr[i6 - 1] + objArr[i4].length;
                length += i8;
                i6++;
            }
        }
    }

    @Override // java.lang.Iterable
    public Spliterator spliterator() {
        return new R2(this, 0, this.f4467c, 0, this.f4466b);
    }

    @Override // java.lang.Iterable
    public final /* synthetic */ java.util.Spliterator spliterator() {
        return Spliterator.Wrapper.convert(spliterator());
    }

    public final String toString() {
        ArrayList arrayList = new ArrayList();
        Objects.requireNonNull(arrayList);
        forEach(new C0516a(arrayList, 10));
        String obj = arrayList.toString();
        return "SpinedBuffer:" + obj;
    }
}
