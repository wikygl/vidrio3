package j$.time;

import j$.util.Objects;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C extends A {

    /* renamed from: c  reason: collision with root package name */
    public static final /* synthetic */ int f3842c = 0;
    private static final long serialVersionUID = 8386373296231747096L;

    /* renamed from: a  reason: collision with root package name */
    private final String f3843a;

    /* renamed from: b  reason: collision with root package name */
    private final transient j$.time.zone.f f3844b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C(String str, j$.time.zone.f fVar) {
        this.f3843a = str;
        this.f3844b = fVar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static C I(String str) {
        j$.time.zone.f fVar;
        Objects.requireNonNull(str, "zoneId");
        int length = str.length();
        if (length >= 2) {
            for (int i4 = 0; i4 < length; i4++) {
                char charAt = str.charAt(i4);
                if ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && ((charAt != '/' || i4 == 0) && ((charAt < '0' || charAt > '9' || i4 == 0) && ((charAt != '~' || i4 == 0) && ((charAt != '.' || i4 == 0) && ((charAt != '_' || i4 == 0) && ((charAt != '+' || i4 == 0) && (charAt != '-' || i4 == 0))))))))) {
                    throw new RuntimeException("Invalid ID for region-based ZoneId, invalid format: ".concat(str));
                }
            }
            try {
                fVar = j$.time.zone.j.a(str, true);
            } catch (j$.time.zone.g unused) {
                fVar = null;
            }
            return new C(str, fVar);
        }
        throw new RuntimeException("Invalid ID for region-based ZoneId, invalid format: ".concat(str));
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 7, this);
    }

    @Override // j$.time.A
    public final j$.time.zone.f D() {
        j$.time.zone.f fVar = this.f3844b;
        return fVar != null ? fVar : j$.time.zone.j.a(this.f3843a, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.time.A
    public final void H(DataOutput dataOutput) {
        dataOutput.writeByte(7);
        dataOutput.writeUTF(this.f3843a);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void J(DataOutput dataOutput) {
        dataOutput.writeUTF(this.f3843a);
    }

    @Override // j$.time.A
    public final String i() {
        return this.f3843a;
    }
}
