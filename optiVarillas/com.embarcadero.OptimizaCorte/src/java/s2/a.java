package S2;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import r.j;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class a extends U.a {
    public static final Parcelable.Creator<a> CREATOR = new Object();

    /* renamed from: l  reason: collision with root package name */
    public final j<String, Bundle> f2306l;

    /* renamed from: S2.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public class C0024a implements Parcelable.ClassLoaderCreator<a> {
        @Override // android.os.Parcelable.ClassLoaderCreator
        public final a createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new a(parcel, classLoader);
        }

        @Override // android.os.Parcelable.Creator
        public final Object[] newArray(int i4) {
            return new a[i4];
        }

        @Override // android.os.Parcelable.Creator
        public final Object createFromParcel(Parcel parcel) {
            return new a(parcel, null);
        }
    }

    public a(Parcel parcel, ClassLoader classLoader) {
        super(parcel, classLoader);
        int readInt = parcel.readInt();
        String[] strArr = new String[readInt];
        parcel.readStringArray(strArr);
        Bundle[] bundleArr = new Bundle[readInt];
        parcel.readTypedArray(bundleArr, Bundle.CREATOR);
        this.f2306l = new j<>(readInt);
        for (int i4 = 0; i4 < readInt; i4++) {
            this.f2306l.put(strArr[i4], bundleArr[i4]);
        }
    }

    public final String toString() {
        return "ExtendableSavedState{" + Integer.toHexString(System.identityHashCode(this)) + " states=" + this.f2306l + "}";
    }

    @Override // U.a, android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        super.writeToParcel(parcel, i4);
        j<String, Bundle> jVar = this.f2306l;
        int i5 = jVar.f5685l;
        parcel.writeInt(i5);
        String[] strArr = new String[i5];
        Bundle[] bundleArr = new Bundle[i5];
        for (int i6 = 0; i6 < i5; i6++) {
            strArr[i6] = jVar.h(i6);
            bundleArr[i6] = jVar.j(i6);
        }
        parcel.writeStringArray(strArr);
        parcel.writeTypedArray(bundleArr, 0);
    }
}
