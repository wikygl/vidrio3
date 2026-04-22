package E1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class a extends X1.a {
    public static final Parcelable.Creator<a> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final String f844j;

    /* renamed from: k  reason: collision with root package name */
    public final int f845k;

    /* renamed from: l  reason: collision with root package name */
    public final int f846l;

    /* renamed from: m  reason: collision with root package name */
    public final boolean f847m;

    /* renamed from: n  reason: collision with root package name */
    public final boolean f848n;

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public a(int r10, int r11, boolean r12, boolean r13) {
        /*
            r9 = this;
            if (r12 == 0) goto L5
            java.lang.String r0 = "0"
            goto L7
        L5:
            java.lang.String r0 = "1"
        L7:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r2 = "afma-sdk-a-v"
            r1.<init>(r2)
            r1.append(r10)
            java.lang.String r2 = "."
            r1.append(r2)
            r1.append(r11)
            r1.append(r2)
            r1.append(r0)
            java.lang.String r4 = r1.toString()
            r3 = r9
            r5 = r10
            r6 = r11
            r7 = r12
            r8 = r13
            r3.<init>(r4, r5, r6, r7, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: E1.a.<init>(int, int, boolean, boolean):void");
    }

    public static a h() {
        return new a(12451000, 12451000, true, false);
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.m(parcel, 2, this.f844j);
        H.a.x(parcel, 3, 4);
        parcel.writeInt(this.f845k);
        H.a.x(parcel, 4, 4);
        parcel.writeInt(this.f846l);
        H.a.x(parcel, 5, 4);
        parcel.writeInt(this.f847m ? 1 : 0);
        H.a.x(parcel, 6, 4);
        parcel.writeInt(this.f848n ? 1 : 0);
        H.a.v(parcel, r4);
    }

    public a(String str, int i4, int i5, boolean z4, boolean z5) {
        this.f844j = str;
        this.f845k = i4;
        this.f846l = i5;
        this.f847m = z4;
        this.f848n = z5;
    }
}
