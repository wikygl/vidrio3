package D;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.Base64;
import android.util.Xml;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class e {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {
        public static int a(TypedArray typedArray, int i4) {
            return typedArray.getType(i4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public interface b {
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class c implements b {

        /* renamed from: a  reason: collision with root package name */
        public final d[] f517a;

        public c(d[] dVarArr) {
            this.f517a = dVarArr;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static final class d {

        /* renamed from: a  reason: collision with root package name */
        public final String f518a;

        /* renamed from: b  reason: collision with root package name */
        public final int f519b;

        /* renamed from: c  reason: collision with root package name */
        public final boolean f520c;

        /* renamed from: d  reason: collision with root package name */
        public final String f521d;

        /* renamed from: e  reason: collision with root package name */
        public final int f522e;
        public final int f;

        public d(String str, int i4, boolean z4, String str2, int i5, int i6) {
            this.f518a = str;
            this.f519b = i4;
            this.f520c = z4;
            this.f521d = str2;
            this.f522e = i5;
            this.f = i6;
        }
    }

    /* renamed from: D.e$e  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class C0004e implements b {

        /* renamed from: a  reason: collision with root package name */
        public final J.e f523a;

        /* renamed from: b  reason: collision with root package name */
        public final int f524b;

        /* renamed from: c  reason: collision with root package name */
        public final int f525c;

        /* renamed from: d  reason: collision with root package name */
        public final String f526d;

        public C0004e(J.e eVar, int i4, int i5, String str) {
            this.f523a = eVar;
            this.f525c = i4;
            this.f524b = i5;
            this.f526d = str;
        }
    }

    public static b a(XmlResourceParser xmlResourceParser, Resources resources) {
        int next;
        int i4;
        boolean z4;
        int i5;
        do {
            next = xmlResourceParser.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next == 2) {
            xmlResourceParser.require(2, null, "font-family");
            if (xmlResourceParser.getName().equals("font-family")) {
                TypedArray obtainAttributes = resources.obtainAttributes(Xml.asAttributeSet(xmlResourceParser), A.a.f1b);
                String string = obtainAttributes.getString(0);
                String string2 = obtainAttributes.getString(4);
                String string3 = obtainAttributes.getString(5);
                int resourceId = obtainAttributes.getResourceId(1, 0);
                int integer = obtainAttributes.getInteger(2, 1);
                int integer2 = obtainAttributes.getInteger(3, 500);
                String string4 = obtainAttributes.getString(6);
                obtainAttributes.recycle();
                if (string != null && string2 != null && string3 != null) {
                    while (xmlResourceParser.next() != 3) {
                        c(xmlResourceParser);
                    }
                    return new C0004e(new J.e(string, string2, string3, b(resources, resourceId)), integer, integer2, string4);
                }
                ArrayList arrayList = new ArrayList();
                while (xmlResourceParser.next() != 3) {
                    if (xmlResourceParser.getEventType() == 2) {
                        if (xmlResourceParser.getName().equals("font")) {
                            TypedArray obtainAttributes2 = resources.obtainAttributes(Xml.asAttributeSet(xmlResourceParser), A.a.f2c);
                            int i6 = 8;
                            if (!obtainAttributes2.hasValue(8)) {
                                i6 = 1;
                            }
                            int i7 = obtainAttributes2.getInt(i6, 400);
                            if (obtainAttributes2.hasValue(6)) {
                                i4 = 6;
                            } else {
                                i4 = 2;
                            }
                            if (1 == obtainAttributes2.getInt(i4, 0)) {
                                z4 = true;
                            } else {
                                z4 = false;
                            }
                            int i8 = 9;
                            if (!obtainAttributes2.hasValue(9)) {
                                i8 = 3;
                            }
                            int i9 = 7;
                            if (!obtainAttributes2.hasValue(7)) {
                                i9 = 4;
                            }
                            String string5 = obtainAttributes2.getString(i9);
                            int i10 = obtainAttributes2.getInt(i8, 0);
                            if (obtainAttributes2.hasValue(5)) {
                                i5 = 5;
                            } else {
                                i5 = 0;
                            }
                            int resourceId2 = obtainAttributes2.getResourceId(i5, 0);
                            String string6 = obtainAttributes2.getString(i5);
                            obtainAttributes2.recycle();
                            while (xmlResourceParser.next() != 3) {
                                c(xmlResourceParser);
                            }
                            arrayList.add(new d(string6, i7, z4, string5, i10, resourceId2));
                        } else {
                            c(xmlResourceParser);
                        }
                    }
                }
                if (!arrayList.isEmpty()) {
                    return new c((d[]) arrayList.toArray(new d[0]));
                }
            } else {
                c(xmlResourceParser);
            }
            return null;
        }
        throw new XmlPullParserException("No start tag found");
    }

    public static List<List<byte[]>> b(Resources resources, int i4) {
        if (i4 == 0) {
            return Collections.emptyList();
        }
        TypedArray obtainTypedArray = resources.obtainTypedArray(i4);
        try {
            if (obtainTypedArray.length() == 0) {
                return Collections.emptyList();
            }
            ArrayList arrayList = new ArrayList();
            if (a.a(obtainTypedArray, 0) == 1) {
                for (int i5 = 0; i5 < obtainTypedArray.length(); i5++) {
                    int resourceId = obtainTypedArray.getResourceId(i5, 0);
                    if (resourceId != 0) {
                        String[] stringArray = resources.getStringArray(resourceId);
                        ArrayList arrayList2 = new ArrayList();
                        for (String str : stringArray) {
                            arrayList2.add(Base64.decode(str, 0));
                        }
                        arrayList.add(arrayList2);
                    }
                }
            } else {
                String[] stringArray2 = resources.getStringArray(i4);
                ArrayList arrayList3 = new ArrayList();
                for (String str2 : stringArray2) {
                    arrayList3.add(Base64.decode(str2, 0));
                }
                arrayList.add(arrayList3);
            }
            return arrayList;
        } finally {
            obtainTypedArray.recycle();
        }
    }

    public static void c(XmlResourceParser xmlResourceParser) {
        int i4 = 1;
        while (i4 > 0) {
            int next = xmlResourceParser.next();
            if (next != 2) {
                if (next == 3) {
                    i4--;
                }
            } else {
                i4++;
            }
        }
    }
}
