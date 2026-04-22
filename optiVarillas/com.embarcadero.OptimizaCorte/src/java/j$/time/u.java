package j$.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.regex.Pattern;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class u implements Serializable {

    /* renamed from: d  reason: collision with root package name */
    public static final u f4032d = new u(0, 0, 0);
    private static final long serialVersionUID = -3587258372562876L;

    /* renamed from: a  reason: collision with root package name */
    private final int f4033a;

    /* renamed from: b  reason: collision with root package name */
    private final int f4034b;

    /* renamed from: c  reason: collision with root package name */
    private final int f4035c;

    static {
        Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)Y)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)W)?(?:([-+]?[0-9]+)D)?", 2);
        j$.com.android.tools.r8.a.k(new Object[]{j$.time.temporal.b.YEARS, j$.time.temporal.b.MONTHS, j$.time.temporal.b.DAYS});
    }

    private u(int i4, int i5, int i6) {
        this.f4033a = i4;
        this.f4034b = i5;
        this.f4035c = i6;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static u a(DataInput dataInput) {
        int readInt = dataInput.readInt();
        int readInt2 = dataInput.readInt();
        int readInt3 = dataInput.readInt();
        return ((readInt | readInt2) | readInt3) == 0 ? f4032d : new u(readInt, readInt2, readInt3);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 14, this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void b(DataOutput dataOutput) {
        dataOutput.writeInt(this.f4033a);
        dataOutput.writeInt(this.f4034b);
        dataOutput.writeInt(this.f4035c);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof u) {
            u uVar = (u) obj;
            return this.f4033a == uVar.f4033a && this.f4034b == uVar.f4034b && this.f4035c == uVar.f4035c;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.rotateLeft(this.f4035c, 16) + Integer.rotateLeft(this.f4034b, 8) + this.f4033a;
    }

    public final String toString() {
        if (this == f4032d) {
            return "P0D";
        }
        StringBuilder sb = new StringBuilder("P");
        int i4 = this.f4033a;
        if (i4 != 0) {
            sb.append(i4);
            sb.append('Y');
        }
        int i5 = this.f4034b;
        if (i5 != 0) {
            sb.append(i5);
            sb.append('M');
        }
        int i6 = this.f4035c;
        if (i6 != 0) {
            sb.append(i6);
            sb.append('D');
        }
        return sb.toString();
    }
}
