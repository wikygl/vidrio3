package j$.time.chrono;

import j$.util.Objects;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/* renamed from: j$.time.chrono.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class C0490h implements Serializable {

    /* renamed from: e  reason: collision with root package name */
    public static final /* synthetic */ int f3878e = 0;
    private static final long serialVersionUID = 57387258289L;

    /* renamed from: a  reason: collision with root package name */
    private final n f3879a;

    /* renamed from: b  reason: collision with root package name */
    final int f3880b;

    /* renamed from: c  reason: collision with root package name */
    final int f3881c;

    /* renamed from: d  reason: collision with root package name */
    final int f3882d;

    static {
        j$.com.android.tools.r8.a.k(new Object[]{j$.time.temporal.b.YEARS, j$.time.temporal.b.MONTHS, j$.time.temporal.b.DAYS});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0490h(n nVar, int i4, int i5, int i6) {
        Objects.requireNonNull(nVar, "chrono");
        this.f3879a = nVar;
        this.f3880b = i4;
        this.f3881c = i5;
        this.f3882d = i6;
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a(DataOutput dataOutput) {
        dataOutput.writeUTF(this.f3879a.i());
        dataOutput.writeInt(this.f3880b);
        dataOutput.writeInt(this.f3881c);
        dataOutput.writeInt(this.f3882d);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof C0490h) {
            C0490h c0490h = (C0490h) obj;
            if (this.f3880b == c0490h.f3880b && this.f3881c == c0490h.f3881c && this.f3882d == c0490h.f3882d) {
                if (((AbstractC0483a) this.f3879a).equals(c0490h.f3879a)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public final int hashCode() {
        return ((AbstractC0483a) this.f3879a).hashCode() ^ (Integer.rotateLeft(this.f3882d, 16) + (Integer.rotateLeft(this.f3881c, 8) + this.f3880b));
    }

    public final String toString() {
        n nVar = this.f3879a;
        int i4 = this.f3882d;
        int i5 = this.f3881c;
        int i6 = this.f3880b;
        if (i6 == 0 && i5 == 0 && i4 == 0) {
            String i7 = ((AbstractC0483a) nVar).i();
            return i7 + " P0D";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(((AbstractC0483a) nVar).i());
        sb.append(" P");
        if (i6 != 0) {
            sb.append(i6);
            sb.append('Y');
        }
        if (i5 != 0) {
            sb.append(i5);
            sb.append('M');
        }
        if (i4 != 0) {
            sb.append(i4);
            sb.append('D');
        }
        return sb.toString();
    }

    protected Object writeReplace() {
        return new G((byte) 9, this);
    }
}
