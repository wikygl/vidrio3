package x;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.ArrayList;

/* renamed from: x.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0848a {

    /* renamed from: a  reason: collision with root package name */
    public SparseArray<C0079a> f6415a;

    /* renamed from: b  reason: collision with root package name */
    public SparseArray<androidx.constraintlayout.widget.d> f6416b;

    /* renamed from: x.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class C0079a {

        /* renamed from: a  reason: collision with root package name */
        public final int f6417a;

        /* renamed from: b  reason: collision with root package name */
        public final ArrayList<b> f6418b = new ArrayList<>();

        /* renamed from: c  reason: collision with root package name */
        public final int f6419c;

        public C0079a(Context context, XmlResourceParser xmlResourceParser) {
            this.f6419c = -1;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(xmlResourceParser), C0851d.f6431h);
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i4 = 0; i4 < indexCount; i4++) {
                int index = obtainStyledAttributes.getIndex(i4);
                if (index == 0) {
                    this.f6417a = obtainStyledAttributes.getResourceId(index, this.f6417a);
                } else if (index == 1) {
                    int resourceId = obtainStyledAttributes.getResourceId(index, this.f6419c);
                    this.f6419c = resourceId;
                    String resourceTypeName = context.getResources().getResourceTypeName(resourceId);
                    context.getResources().getResourceName(resourceId);
                    if ("layout".equals(resourceTypeName)) {
                        new androidx.constraintlayout.widget.d().b(LayoutInflater.from(context).inflate(resourceId, (ViewGroup) null));
                    }
                }
            }
            obtainStyledAttributes.recycle();
        }
    }

    /* renamed from: x.a$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public final float f6420a;

        /* renamed from: b  reason: collision with root package name */
        public final float f6421b;

        /* renamed from: c  reason: collision with root package name */
        public final float f6422c;

        /* renamed from: d  reason: collision with root package name */
        public final float f6423d;

        /* renamed from: e  reason: collision with root package name */
        public final int f6424e;

        public b(Context context, XmlResourceParser xmlResourceParser) {
            this.f6420a = Float.NaN;
            this.f6421b = Float.NaN;
            this.f6422c = Float.NaN;
            this.f6423d = Float.NaN;
            this.f6424e = -1;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(xmlResourceParser), C0851d.f6433j);
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i4 = 0; i4 < indexCount; i4++) {
                int index = obtainStyledAttributes.getIndex(i4);
                if (index == 0) {
                    int resourceId = obtainStyledAttributes.getResourceId(index, this.f6424e);
                    this.f6424e = resourceId;
                    String resourceTypeName = context.getResources().getResourceTypeName(resourceId);
                    context.getResources().getResourceName(resourceId);
                    if ("layout".equals(resourceTypeName)) {
                        new androidx.constraintlayout.widget.d().b(LayoutInflater.from(context).inflate(resourceId, (ViewGroup) null));
                    }
                } else if (index == 1) {
                    this.f6423d = obtainStyledAttributes.getDimension(index, this.f6423d);
                } else if (index == 2) {
                    this.f6421b = obtainStyledAttributes.getDimension(index, this.f6421b);
                } else if (index == 3) {
                    this.f6422c = obtainStyledAttributes.getDimension(index, this.f6422c);
                } else if (index == 4) {
                    this.f6420a = obtainStyledAttributes.getDimension(index, this.f6420a);
                } else {
                    Log.v("ConstraintLayoutStates", "Unknown tag");
                }
            }
            obtainStyledAttributes.recycle();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:117:0x01d3, code lost:
        continue;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void a(android.content.Context r13, android.content.res.XmlResourceParser r14) {
        /*
            Method dump skipped, instructions count: 546
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: x.C0848a.a(android.content.Context, android.content.res.XmlResourceParser):void");
    }
}
