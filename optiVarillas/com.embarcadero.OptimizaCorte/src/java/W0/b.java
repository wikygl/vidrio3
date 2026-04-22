package w0;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.SparseIntArray;
import java.lang.reflect.Method;
import r.C0773b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class b extends AbstractC0836a {

    /* renamed from: d  reason: collision with root package name */
    public final SparseIntArray f6379d;

    /* renamed from: e  reason: collision with root package name */
    public final Parcel f6380e;
    public final int f;

    /* renamed from: g  reason: collision with root package name */
    public final int f6381g;

    /* renamed from: h  reason: collision with root package name */
    public final String f6382h;

    /* renamed from: i  reason: collision with root package name */
    public int f6383i;

    /* renamed from: j  reason: collision with root package name */
    public int f6384j;

    /* renamed from: k  reason: collision with root package name */
    public int f6385k;

    public b(Parcel parcel) {
        this(parcel, parcel.dataPosition(), parcel.dataSize(), "", new C0773b(), new C0773b(), new C0773b());
    }

    @Override // w0.AbstractC0836a
    public final b a() {
        Parcel parcel = this.f6380e;
        int dataPosition = parcel.dataPosition();
        int i4 = this.f6384j;
        if (i4 == this.f) {
            i4 = this.f6381g;
        }
        return new b(parcel, dataPosition, i4, C.b.c(new StringBuilder(), this.f6382h, "  "), this.f6376a, this.f6377b, this.f6378c);
    }

    @Override // w0.AbstractC0836a
    public final boolean e() {
        if (this.f6380e.readInt() != 0) {
            return true;
        }
        return false;
    }

    @Override // w0.AbstractC0836a
    public final byte[] f() {
        Parcel parcel = this.f6380e;
        int readInt = parcel.readInt();
        if (readInt < 0) {
            return null;
        }
        byte[] bArr = new byte[readInt];
        parcel.readByteArray(bArr);
        return bArr;
    }

    @Override // w0.AbstractC0836a
    public final CharSequence g() {
        return (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(this.f6380e);
    }

    @Override // w0.AbstractC0836a
    public final boolean h(int i4) {
        while (this.f6384j < this.f6381g) {
            int i5 = this.f6385k;
            if (i5 == i4) {
                return true;
            }
            if (String.valueOf(i5).compareTo(String.valueOf(i4)) > 0) {
                return false;
            }
            int i6 = this.f6384j;
            Parcel parcel = this.f6380e;
            parcel.setDataPosition(i6);
            int readInt = parcel.readInt();
            this.f6385k = parcel.readInt();
            this.f6384j += readInt;
        }
        if (this.f6385k != i4) {
            return false;
        }
        return true;
    }

    @Override // w0.AbstractC0836a
    public final int i() {
        return this.f6380e.readInt();
    }

    @Override // w0.AbstractC0836a
    public final <T extends Parcelable> T j() {
        return (T) this.f6380e.readParcelable(b.class.getClassLoader());
    }

    @Override // w0.AbstractC0836a
    public final String k() {
        return this.f6380e.readString();
    }

    @Override // w0.AbstractC0836a
    public final void m(int i4) {
        u();
        this.f6383i = i4;
        this.f6379d.put(i4, this.f6380e.dataPosition());
        q(0);
        q(i4);
    }

    @Override // w0.AbstractC0836a
    public final void n(boolean z4) {
        this.f6380e.writeInt(z4 ? 1 : 0);
    }

    @Override // w0.AbstractC0836a
    public final void o(byte[] bArr) {
        Parcel parcel = this.f6380e;
        if (bArr != null) {
            parcel.writeInt(bArr.length);
            parcel.writeByteArray(bArr);
            return;
        }
        parcel.writeInt(-1);
    }

    @Override // w0.AbstractC0836a
    public final void p(CharSequence charSequence) {
        TextUtils.writeToParcel(charSequence, this.f6380e, 0);
    }

    @Override // w0.AbstractC0836a
    public final void q(int i4) {
        this.f6380e.writeInt(i4);
    }

    @Override // w0.AbstractC0836a
    public final void r(Parcelable parcelable) {
        this.f6380e.writeParcelable(parcelable, 0);
    }

    @Override // w0.AbstractC0836a
    public final void s(String str) {
        this.f6380e.writeString(str);
    }

    public final void u() {
        int i4 = this.f6383i;
        if (i4 >= 0) {
            int i5 = this.f6379d.get(i4);
            Parcel parcel = this.f6380e;
            int dataPosition = parcel.dataPosition();
            parcel.setDataPosition(i5);
            parcel.writeInt(dataPosition - i5);
            parcel.setDataPosition(dataPosition);
        }
    }

    public b(Parcel parcel, int i4, int i5, String str, C0773b<String, Method> c0773b, C0773b<String, Method> c0773b2, C0773b<String, Class> c0773b3) {
        super(c0773b, c0773b2, c0773b3);
        this.f6379d = new SparseIntArray();
        this.f6383i = -1;
        this.f6385k = -1;
        this.f6380e = parcel;
        this.f = i4;
        this.f6381g = i5;
        this.f6384j = i4;
        this.f6382h = str;
    }
}
