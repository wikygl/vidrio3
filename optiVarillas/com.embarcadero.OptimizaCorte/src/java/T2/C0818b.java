package t2;

import H2.p;
import L2.c;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Xml;
import java.io.IOException;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParserException;
import q2.C0771a;

/* renamed from: t2.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0818b {

    /* renamed from: a  reason: collision with root package name */
    public final a f5830a;

    /* renamed from: b  reason: collision with root package name */
    public final a f5831b = new a();

    /* renamed from: c  reason: collision with root package name */
    public final float f5832c;

    /* renamed from: d  reason: collision with root package name */
    public final float f5833d;

    /* renamed from: e  reason: collision with root package name */
    public final float f5834e;
    public final float f;

    /* renamed from: g  reason: collision with root package name */
    public final float f5835g;

    /* renamed from: h  reason: collision with root package name */
    public final float f5836h;

    /* renamed from: i  reason: collision with root package name */
    public final int f5837i;

    /* renamed from: j  reason: collision with root package name */
    public final int f5838j;

    /* renamed from: k  reason: collision with root package name */
    public final int f5839k;

    /* renamed from: t2.b$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class a implements Parcelable {
        public static final Parcelable.Creator<a> CREATOR = new Object();

        /* renamed from: A  reason: collision with root package name */
        public int f5840A;

        /* renamed from: B  reason: collision with root package name */
        public Integer f5841B;

        /* renamed from: D  reason: collision with root package name */
        public Integer f5843D;

        /* renamed from: E  reason: collision with root package name */
        public Integer f5844E;

        /* renamed from: F  reason: collision with root package name */
        public Integer f5845F;

        /* renamed from: G  reason: collision with root package name */
        public Integer f5846G;

        /* renamed from: H  reason: collision with root package name */
        public Integer f5847H;

        /* renamed from: I  reason: collision with root package name */
        public Integer f5848I;

        /* renamed from: J  reason: collision with root package name */
        public Integer f5849J;

        /* renamed from: K  reason: collision with root package name */
        public Integer f5850K;

        /* renamed from: L  reason: collision with root package name */
        public Integer f5851L;

        /* renamed from: M  reason: collision with root package name */
        public Boolean f5852M;

        /* renamed from: j  reason: collision with root package name */
        public int f5853j;

        /* renamed from: k  reason: collision with root package name */
        public Integer f5854k;

        /* renamed from: l  reason: collision with root package name */
        public Integer f5855l;

        /* renamed from: m  reason: collision with root package name */
        public Integer f5856m;

        /* renamed from: n  reason: collision with root package name */
        public Integer f5857n;

        /* renamed from: o  reason: collision with root package name */
        public Integer f5858o;

        /* renamed from: p  reason: collision with root package name */
        public Integer f5859p;

        /* renamed from: q  reason: collision with root package name */
        public Integer f5860q;

        /* renamed from: s  reason: collision with root package name */
        public String f5862s;

        /* renamed from: w  reason: collision with root package name */
        public Locale f5866w;

        /* renamed from: x  reason: collision with root package name */
        public CharSequence f5867x;

        /* renamed from: y  reason: collision with root package name */
        public CharSequence f5868y;

        /* renamed from: z  reason: collision with root package name */
        public int f5869z;

        /* renamed from: r  reason: collision with root package name */
        public int f5861r = 255;

        /* renamed from: t  reason: collision with root package name */
        public int f5863t = -2;

        /* renamed from: u  reason: collision with root package name */
        public int f5864u = -2;

        /* renamed from: v  reason: collision with root package name */
        public int f5865v = -2;

        /* renamed from: C  reason: collision with root package name */
        public Boolean f5842C = Boolean.TRUE;

        /* renamed from: t2.b$a$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
        public class C0071a implements Parcelable.Creator<a> {
            /* JADX WARN: Type inference failed for: r0v0, types: [t2.b$a, java.lang.Object] */
            @Override // android.os.Parcelable.Creator
            public final a createFromParcel(Parcel parcel) {
                ?? obj = new Object();
                obj.f5861r = 255;
                obj.f5863t = -2;
                obj.f5864u = -2;
                obj.f5865v = -2;
                obj.f5842C = Boolean.TRUE;
                obj.f5853j = parcel.readInt();
                obj.f5854k = (Integer) parcel.readSerializable();
                obj.f5855l = (Integer) parcel.readSerializable();
                obj.f5856m = (Integer) parcel.readSerializable();
                obj.f5857n = (Integer) parcel.readSerializable();
                obj.f5858o = (Integer) parcel.readSerializable();
                obj.f5859p = (Integer) parcel.readSerializable();
                obj.f5860q = (Integer) parcel.readSerializable();
                obj.f5861r = parcel.readInt();
                obj.f5862s = parcel.readString();
                obj.f5863t = parcel.readInt();
                obj.f5864u = parcel.readInt();
                obj.f5865v = parcel.readInt();
                obj.f5867x = parcel.readString();
                obj.f5868y = parcel.readString();
                obj.f5869z = parcel.readInt();
                obj.f5841B = (Integer) parcel.readSerializable();
                obj.f5843D = (Integer) parcel.readSerializable();
                obj.f5844E = (Integer) parcel.readSerializable();
                obj.f5845F = (Integer) parcel.readSerializable();
                obj.f5846G = (Integer) parcel.readSerializable();
                obj.f5847H = (Integer) parcel.readSerializable();
                obj.f5848I = (Integer) parcel.readSerializable();
                obj.f5851L = (Integer) parcel.readSerializable();
                obj.f5849J = (Integer) parcel.readSerializable();
                obj.f5850K = (Integer) parcel.readSerializable();
                obj.f5842C = (Boolean) parcel.readSerializable();
                obj.f5866w = (Locale) parcel.readSerializable();
                obj.f5852M = (Boolean) parcel.readSerializable();
                return obj;
            }

            @Override // android.os.Parcelable.Creator
            public final a[] newArray(int i4) {
                return new a[i4];
            }
        }

        @Override // android.os.Parcelable
        public final int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public final void writeToParcel(Parcel parcel, int i4) {
            String str;
            parcel.writeInt(this.f5853j);
            parcel.writeSerializable(this.f5854k);
            parcel.writeSerializable(this.f5855l);
            parcel.writeSerializable(this.f5856m);
            parcel.writeSerializable(this.f5857n);
            parcel.writeSerializable(this.f5858o);
            parcel.writeSerializable(this.f5859p);
            parcel.writeSerializable(this.f5860q);
            parcel.writeInt(this.f5861r);
            parcel.writeString(this.f5862s);
            parcel.writeInt(this.f5863t);
            parcel.writeInt(this.f5864u);
            parcel.writeInt(this.f5865v);
            CharSequence charSequence = this.f5867x;
            String str2 = null;
            if (charSequence != null) {
                str = charSequence.toString();
            } else {
                str = null;
            }
            parcel.writeString(str);
            CharSequence charSequence2 = this.f5868y;
            if (charSequence2 != null) {
                str2 = charSequence2.toString();
            }
            parcel.writeString(str2);
            parcel.writeInt(this.f5869z);
            parcel.writeSerializable(this.f5841B);
            parcel.writeSerializable(this.f5843D);
            parcel.writeSerializable(this.f5844E);
            parcel.writeSerializable(this.f5845F);
            parcel.writeSerializable(this.f5846G);
            parcel.writeSerializable(this.f5847H);
            parcel.writeSerializable(this.f5848I);
            parcel.writeSerializable(this.f5851L);
            parcel.writeSerializable(this.f5849J);
            parcel.writeSerializable(this.f5850K);
            parcel.writeSerializable(this.f5842C);
            parcel.writeSerializable(this.f5866w);
            parcel.writeSerializable(this.f5852M);
        }
    }

    public C0818b(Context context) {
        AttributeSet attributeSet;
        int i4;
        int i5;
        boolean z4;
        int intValue;
        int intValue2;
        int intValue3;
        int intValue4;
        int intValue5;
        int intValue6;
        int intValue7;
        int intValue8;
        int intValue9;
        int intValue10;
        int intValue11;
        int intValue12;
        int intValue13;
        int intValue14;
        int intValue15;
        int intValue16;
        boolean booleanValue;
        Locale locale;
        Locale.Category category;
        int next;
        a aVar = new a();
        int i6 = aVar.f5853j;
        if (i6 != 0) {
            try {
                XmlResourceParser xml = context.getResources().getXml(i6);
                do {
                    next = xml.next();
                    if (next == 2) {
                        break;
                    }
                } while (next != 1);
                if (next == 2) {
                    if (TextUtils.equals(xml.getName(), "badge")) {
                        AttributeSet asAttributeSet = Xml.asAttributeSet(xml);
                        i4 = asAttributeSet.getStyleAttribute();
                        attributeSet = asAttributeSet;
                    } else {
                        throw new XmlPullParserException("Must have a <" + ((Object) "badge") + "> start tag");
                    }
                } else {
                    throw new XmlPullParserException("No start tag found");
                }
            } catch (IOException | XmlPullParserException e4) {
                Resources.NotFoundException notFoundException = new Resources.NotFoundException("Can't load badge resource ID #0x" + Integer.toHexString(i6));
                notFoundException.initCause(e4);
                throw notFoundException;
            }
        } else {
            attributeSet = null;
            i4 = 0;
        }
        if (i4 == 0) {
            i5 = 2131887101;
        } else {
            i5 = i4;
        }
        TypedArray d4 = p.d(context, attributeSet, C0771a.f5609b, 2130903126, i5, new int[0]);
        Resources resources = context.getResources();
        this.f5832c = d4.getDimensionPixelSize(4, -1);
        this.f5837i = context.getResources().getDimensionPixelSize(2131100247);
        this.f5838j = context.getResources().getDimensionPixelSize(2131100250);
        this.f5833d = d4.getDimensionPixelSize(14, -1);
        this.f5834e = d4.getDimension(12, resources.getDimension(2131099838));
        this.f5835g = d4.getDimension(17, resources.getDimension(2131099842));
        this.f = d4.getDimension(3, resources.getDimension(2131099838));
        this.f5836h = d4.getDimension(13, resources.getDimension(2131099842));
        this.f5839k = d4.getInt(24, 1);
        a aVar2 = this.f5831b;
        int i7 = aVar.f5861r;
        aVar2.f5861r = i7 == -2 ? 255 : i7;
        int i8 = aVar.f5863t;
        if (i8 != -2) {
            aVar2.f5863t = i8;
        } else if (d4.hasValue(23)) {
            this.f5831b.f5863t = d4.getInt(23, 0);
        } else {
            this.f5831b.f5863t = -1;
        }
        String str = aVar.f5862s;
        if (str != null) {
            this.f5831b.f5862s = str;
        } else if (d4.hasValue(7)) {
            this.f5831b.f5862s = d4.getString(7);
        }
        a aVar3 = this.f5831b;
        aVar3.f5867x = aVar.f5867x;
        CharSequence charSequence = aVar.f5868y;
        aVar3.f5868y = charSequence == null ? context.getString(2131820792) : charSequence;
        a aVar4 = this.f5831b;
        int i9 = aVar.f5869z;
        aVar4.f5869z = i9 == 0 ? 2131689472 : i9;
        int i10 = aVar.f5840A;
        aVar4.f5840A = i10 == 0 ? 2131820805 : i10;
        Boolean bool = aVar.f5842C;
        if (bool != null && !bool.booleanValue()) {
            z4 = false;
        } else {
            z4 = true;
        }
        aVar4.f5842C = Boolean.valueOf(z4);
        a aVar5 = this.f5831b;
        int i11 = aVar.f5864u;
        aVar5.f5864u = i11 == -2 ? d4.getInt(21, -2) : i11;
        a aVar6 = this.f5831b;
        int i12 = aVar.f5865v;
        aVar6.f5865v = i12 == -2 ? d4.getInt(22, -2) : i12;
        a aVar7 = this.f5831b;
        Integer num = aVar.f5857n;
        if (num == null) {
            intValue = d4.getResourceId(5, 2131886449);
        } else {
            intValue = num.intValue();
        }
        aVar7.f5857n = Integer.valueOf(intValue);
        a aVar8 = this.f5831b;
        Integer num2 = aVar.f5858o;
        if (num2 == null) {
            intValue2 = d4.getResourceId(6, 0);
        } else {
            intValue2 = num2.intValue();
        }
        aVar8.f5858o = Integer.valueOf(intValue2);
        a aVar9 = this.f5831b;
        Integer num3 = aVar.f5859p;
        if (num3 == null) {
            intValue3 = d4.getResourceId(15, 2131886449);
        } else {
            intValue3 = num3.intValue();
        }
        aVar9.f5859p = Integer.valueOf(intValue3);
        a aVar10 = this.f5831b;
        Integer num4 = aVar.f5860q;
        if (num4 == null) {
            intValue4 = d4.getResourceId(16, 0);
        } else {
            intValue4 = num4.intValue();
        }
        aVar10.f5860q = Integer.valueOf(intValue4);
        a aVar11 = this.f5831b;
        Integer num5 = aVar.f5854k;
        if (num5 == null) {
            intValue5 = c.a(context, d4, 1).getDefaultColor();
        } else {
            intValue5 = num5.intValue();
        }
        aVar11.f5854k = Integer.valueOf(intValue5);
        a aVar12 = this.f5831b;
        Integer num6 = aVar.f5856m;
        if (num6 == null) {
            intValue6 = d4.getResourceId(8, 2131886592);
        } else {
            intValue6 = num6.intValue();
        }
        aVar12.f5856m = Integer.valueOf(intValue6);
        Integer num7 = aVar.f5855l;
        if (num7 != null) {
            this.f5831b.f5855l = num7;
        } else if (d4.hasValue(9)) {
            this.f5831b.f5855l = Integer.valueOf(c.a(context, d4, 9).getDefaultColor());
        } else {
            int intValue17 = this.f5831b.f5856m.intValue();
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(intValue17, C0771a.f5604B);
            obtainStyledAttributes.getDimension(0, 0.0f);
            ColorStateList a4 = c.a(context, obtainStyledAttributes, 3);
            c.a(context, obtainStyledAttributes, 4);
            c.a(context, obtainStyledAttributes, 5);
            obtainStyledAttributes.getInt(2, 0);
            obtainStyledAttributes.getInt(1, 1);
            int i13 = obtainStyledAttributes.hasValue(12) ? 12 : 10;
            obtainStyledAttributes.getResourceId(i13, 0);
            obtainStyledAttributes.getString(i13);
            obtainStyledAttributes.getBoolean(14, false);
            c.a(context, obtainStyledAttributes, 6);
            obtainStyledAttributes.getFloat(7, 0.0f);
            obtainStyledAttributes.getFloat(8, 0.0f);
            obtainStyledAttributes.getFloat(9, 0.0f);
            obtainStyledAttributes.recycle();
            TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(intValue17, C0771a.f5625s);
            obtainStyledAttributes2.hasValue(0);
            obtainStyledAttributes2.getFloat(0, 0.0f);
            obtainStyledAttributes2.recycle();
            this.f5831b.f5855l = Integer.valueOf(a4.getDefaultColor());
        }
        a aVar13 = this.f5831b;
        Integer num8 = aVar.f5841B;
        if (num8 == null) {
            intValue7 = d4.getInt(2, 8388661);
        } else {
            intValue7 = num8.intValue();
        }
        aVar13.f5841B = Integer.valueOf(intValue7);
        a aVar14 = this.f5831b;
        Integer num9 = aVar.f5843D;
        if (num9 == null) {
            intValue8 = d4.getDimensionPixelSize(11, resources.getDimensionPixelSize(2131100248));
        } else {
            intValue8 = num9.intValue();
        }
        aVar14.f5843D = Integer.valueOf(intValue8);
        a aVar15 = this.f5831b;
        Integer num10 = aVar.f5844E;
        if (num10 == null) {
            intValue9 = d4.getDimensionPixelSize(10, resources.getDimensionPixelSize(2131099844));
        } else {
            intValue9 = num10.intValue();
        }
        aVar15.f5844E = Integer.valueOf(intValue9);
        a aVar16 = this.f5831b;
        Integer num11 = aVar.f5845F;
        if (num11 == null) {
            intValue10 = d4.getDimensionPixelOffset(18, 0);
        } else {
            intValue10 = num11.intValue();
        }
        aVar16.f5845F = Integer.valueOf(intValue10);
        a aVar17 = this.f5831b;
        Integer num12 = aVar.f5846G;
        if (num12 == null) {
            intValue11 = d4.getDimensionPixelOffset(25, 0);
        } else {
            intValue11 = num12.intValue();
        }
        aVar17.f5846G = Integer.valueOf(intValue11);
        a aVar18 = this.f5831b;
        Integer num13 = aVar.f5847H;
        if (num13 == null) {
            intValue12 = d4.getDimensionPixelOffset(19, aVar18.f5845F.intValue());
        } else {
            intValue12 = num13.intValue();
        }
        aVar18.f5847H = Integer.valueOf(intValue12);
        a aVar19 = this.f5831b;
        Integer num14 = aVar.f5848I;
        if (num14 == null) {
            intValue13 = d4.getDimensionPixelOffset(26, aVar19.f5846G.intValue());
        } else {
            intValue13 = num14.intValue();
        }
        aVar19.f5848I = Integer.valueOf(intValue13);
        a aVar20 = this.f5831b;
        Integer num15 = aVar.f5851L;
        if (num15 == null) {
            intValue14 = d4.getDimensionPixelOffset(20, 0);
        } else {
            intValue14 = num15.intValue();
        }
        aVar20.f5851L = Integer.valueOf(intValue14);
        a aVar21 = this.f5831b;
        Integer num16 = aVar.f5849J;
        if (num16 == null) {
            intValue15 = 0;
        } else {
            intValue15 = num16.intValue();
        }
        aVar21.f5849J = Integer.valueOf(intValue15);
        a aVar22 = this.f5831b;
        Integer num17 = aVar.f5850K;
        if (num17 == null) {
            intValue16 = 0;
        } else {
            intValue16 = num17.intValue();
        }
        aVar22.f5850K = Integer.valueOf(intValue16);
        a aVar23 = this.f5831b;
        Boolean bool2 = aVar.f5852M;
        if (bool2 == null) {
            booleanValue = d4.getBoolean(0, false);
        } else {
            booleanValue = bool2.booleanValue();
        }
        aVar23.f5852M = Boolean.valueOf(booleanValue);
        d4.recycle();
        Locale locale2 = aVar.f5866w;
        if (locale2 == null) {
            a aVar24 = this.f5831b;
            if (Build.VERSION.SDK_INT >= 24) {
                category = Locale.Category.FORMAT;
                locale = Locale.getDefault(category);
            } else {
                locale = Locale.getDefault();
            }
            aVar24.f5866w = locale;
        } else {
            this.f5831b.f5866w = locale2;
        }
        this.f5830a = aVar;
    }
}
