package j$.time.zone;

import j$.time.B;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;
import java.util.TimeZone;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class a implements Externalizable {
    private static final long serialVersionUID = -8885321777449118786L;

    /* renamed from: a  reason: collision with root package name */
    private byte f4046a;

    /* renamed from: b  reason: collision with root package name */
    private Object f4047b;

    public a() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public a(byte b4, Object obj) {
        this.f4046a = b4;
        this.f4047b = obj;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long a(DataInput dataInput) {
        int readByte = dataInput.readByte() & 255;
        if (readByte == 255) {
            return dataInput.readLong();
        }
        return ((((readByte << 16) + ((dataInput.readByte() & 255) << 8)) + (dataInput.readByte() & 255)) * 900) - 4575744000L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static B b(DataInput dataInput) {
        byte readByte = dataInput.readByte();
        return readByte == Byte.MAX_VALUE ? B.M(dataInput.readInt()) : B.M(readByte * 900);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void c(long j4, DataOutput dataOutput) {
        if (j4 < -4575744000L || j4 >= 10413792000L || j4 % 900 != 0) {
            dataOutput.writeByte(255);
            dataOutput.writeLong(j4);
            return;
        }
        int i4 = (int) ((j4 + 4575744000L) / 900);
        dataOutput.writeByte((i4 >>> 16) & 255);
        dataOutput.writeByte((i4 >>> 8) & 255);
        dataOutput.writeByte(i4 & 255);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void d(B b4, DataOutput dataOutput) {
        int J3 = b4.J();
        int i4 = J3 % 900 == 0 ? J3 / 900 : 127;
        dataOutput.writeByte(i4);
        if (i4 == 127) {
            dataOutput.writeInt(J3);
        }
    }

    private Object readResolve() {
        return this.f4047b;
    }

    @Override // java.io.Externalizable
    public final void readExternal(ObjectInput objectInput) {
        Object k4;
        byte readByte = objectInput.readByte();
        this.f4046a = readByte;
        if (readByte == 1) {
            k4 = f.k(objectInput);
        } else if (readByte == 2) {
            long a4 = a(objectInput);
            B b4 = b(objectInput);
            B b5 = b(objectInput);
            if (b4.equals(b5)) {
                throw new IllegalArgumentException("Offsets must not be equal");
            }
            k4 = new b(a4, b4, b5);
        } else if (readByte == 3) {
            k4 = e.b(objectInput);
        } else if (readByte != 100) {
            throw new StreamCorruptedException("Unknown serialized type");
        } else {
            k4 = new f(TimeZone.getTimeZone(objectInput.readUTF()));
        }
        this.f4047b = k4;
    }

    @Override // java.io.Externalizable
    public final void writeExternal(ObjectOutput objectOutput) {
        byte b4 = this.f4046a;
        Object obj = this.f4047b;
        objectOutput.writeByte(b4);
        if (b4 == 1) {
            ((f) obj).l(objectOutput);
        } else if (b4 == 2) {
            ((b) obj).D(objectOutput);
        } else if (b4 == 3) {
            ((e) obj).c(objectOutput);
        } else if (b4 != 100) {
            throw new InvalidClassException("Unknown serialized type");
        } else {
            ((f) obj).m(objectOutput);
        }
    }
}
