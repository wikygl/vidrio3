package u0;

import A1.I;
import M.O;
import M.V;
import S0.C0284u0;
import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.graphics.Path;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowId;
import android.widget.ListView;
import com.google.android.gms.internal.ads.R5;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.WeakHashMap;
import r.C0773b;
import r.C0777f;
import r.C0778g;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class f implements Cloneable {

    /* renamed from: F  reason: collision with root package name */
    public static final Animator[] f5946F = new Animator[0];

    /* renamed from: G  reason: collision with root package name */
    public static final int[] f5947G = {2, 1, 3, 4};

    /* renamed from: H  reason: collision with root package name */
    public static final a f5948H = new Object();

    /* renamed from: I  reason: collision with root package name */
    public static final ThreadLocal<C0773b<Animator, b>> f5949I = new ThreadLocal<>();

    /* renamed from: t  reason: collision with root package name */
    public ArrayList<m> f5965t;

    /* renamed from: u  reason: collision with root package name */
    public ArrayList<m> f5966u;

    /* renamed from: v  reason: collision with root package name */
    public d[] f5967v;

    /* renamed from: j  reason: collision with root package name */
    public final String f5955j = getClass().getName();

    /* renamed from: k  reason: collision with root package name */
    public long f5956k = -1;

    /* renamed from: l  reason: collision with root package name */
    public long f5957l = -1;

    /* renamed from: m  reason: collision with root package name */
    public TimeInterpolator f5958m = null;

    /* renamed from: n  reason: collision with root package name */
    public final ArrayList<Integer> f5959n = new ArrayList<>();

    /* renamed from: o  reason: collision with root package name */
    public final ArrayList<View> f5960o = new ArrayList<>();

    /* renamed from: p  reason: collision with root package name */
    public R5 f5961p = new R5();

    /* renamed from: q  reason: collision with root package name */
    public R5 f5962q = new R5();

    /* renamed from: r  reason: collision with root package name */
    public k f5963r = null;

    /* renamed from: s  reason: collision with root package name */
    public final int[] f5964s = f5947G;

    /* renamed from: w  reason: collision with root package name */
    public final ArrayList<Animator> f5968w = new ArrayList<>();

    /* renamed from: x  reason: collision with root package name */
    public Animator[] f5969x = f5946F;

    /* renamed from: y  reason: collision with root package name */
    public int f5970y = 0;

    /* renamed from: z  reason: collision with root package name */
    public boolean f5971z = false;

    /* renamed from: A  reason: collision with root package name */
    public boolean f5950A = false;

    /* renamed from: B  reason: collision with root package name */
    public f f5951B = null;

    /* renamed from: C  reason: collision with root package name */
    public ArrayList<d> f5952C = null;

    /* renamed from: D  reason: collision with root package name */
    public ArrayList<Animator> f5953D = new ArrayList<>();

    /* renamed from: E  reason: collision with root package name */
    public G3.g f5954E = f5948H;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class a extends G3.g {
        @Override // G3.g
        public final Path s(float f, float f4, float f5, float f6) {
            Path path = new Path();
            path.moveTo(f, f4);
            path.lineTo(f5, f6);
            return path;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public View f5972a;

        /* renamed from: b  reason: collision with root package name */
        public String f5973b;

        /* renamed from: c  reason: collision with root package name */
        public m f5974c;

        /* renamed from: d  reason: collision with root package name */
        public WindowId f5975d;

        /* renamed from: e  reason: collision with root package name */
        public f f5976e;
        public Animator f;
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static abstract class c {
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface d {
        void a(f fVar);

        void b();

        void c();

        void d(f fVar);

        void e(f fVar);

        void f(f fVar);

        void g(f fVar);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface e {

        /* renamed from: e  reason: collision with root package name */
        public static final K.c f5977e = new Object();
        public static final F3.e f = new Object();

        /* renamed from: g  reason: collision with root package name */
        public static final I f5978g = new Object();

        /* renamed from: h  reason: collision with root package name */
        public static final C0284u0 f5979h = new Object();

        /* renamed from: i  reason: collision with root package name */
        public static final C.b f5980i = new Object();

        void a(d dVar, f fVar);
    }

    public static void b(R5 r5, View view, m mVar) {
        ((C0773b) r5.j).put(view, mVar);
        int id = view.getId();
        if (id >= 0) {
            SparseArray sparseArray = (SparseArray) r5.k;
            if (sparseArray.indexOfKey(id) >= 0) {
                sparseArray.put(id, null);
            } else {
                sparseArray.put(id, view);
            }
        }
        WeakHashMap<View, V> weakHashMap = O.f1526a;
        String k4 = O.d.k(view);
        if (k4 != null) {
            C0773b c0773b = (C0773b) r5.m;
            if (c0773b.containsKey(k4)) {
                c0773b.put(k4, null);
            } else {
                c0773b.put(k4, view);
            }
        }
        if (view.getParent() instanceof ListView) {
            ListView listView = (ListView) view.getParent();
            if (listView.getAdapter().hasStableIds()) {
                long itemIdAtPosition = listView.getItemIdAtPosition(listView.getPositionForView(view));
                C0778g c0778g = (C0778g) r5.l;
                if (c0778g.f5655j) {
                    c0778g.d();
                }
                if (C0777f.b(c0778g.f5656k, c0778g.f5658m, itemIdAtPosition) >= 0) {
                    View view2 = (View) c0778g.e(itemIdAtPosition, null);
                    if (view2 != null) {
                        view2.setHasTransientState(false);
                        c0778g.g(itemIdAtPosition, null);
                        return;
                    }
                    return;
                }
                view.setHasTransientState(true);
                c0778g.g(itemIdAtPosition, view);
            }
        }
    }

    public static C0773b<Animator, b> p() {
        ThreadLocal<C0773b<Animator, b>> threadLocal = f5949I;
        C0773b<Animator, b> c0773b = threadLocal.get();
        if (c0773b == null) {
            C0773b<Animator, b> c0773b2 = new C0773b<>();
            threadLocal.set(c0773b2);
            return c0773b2;
        }
        return c0773b;
    }

    public void B(TimeInterpolator timeInterpolator) {
        this.f5958m = timeInterpolator;
    }

    public void C(a aVar) {
        if (aVar == null) {
            this.f5954E = f5948H;
        } else {
            this.f5954E = aVar;
        }
    }

    public void E(long j4) {
        this.f5956k = j4;
    }

    public final void F() {
        if (this.f5970y == 0) {
            u(this, e.f5977e);
            this.f5950A = false;
        }
        this.f5970y++;
    }

    public String G(String str) {
        StringBuilder sb = new StringBuilder(str);
        sb.append(getClass().getSimpleName());
        sb.append("@");
        sb.append(Integer.toHexString(hashCode()));
        sb.append(": ");
        if (this.f5957l != -1) {
            sb.append("dur(");
            sb.append(this.f5957l);
            sb.append(") ");
        }
        if (this.f5956k != -1) {
            sb.append("dly(");
            sb.append(this.f5956k);
            sb.append(") ");
        }
        if (this.f5958m != null) {
            sb.append("interp(");
            sb.append(this.f5958m);
            sb.append(") ");
        }
        ArrayList<Integer> arrayList = this.f5959n;
        int size = arrayList.size();
        ArrayList<View> arrayList2 = this.f5960o;
        if (size > 0 || arrayList2.size() > 0) {
            sb.append("tgts(");
            if (arrayList.size() > 0) {
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    if (i4 > 0) {
                        sb.append(", ");
                    }
                    sb.append(arrayList.get(i4));
                }
            }
            if (arrayList2.size() > 0) {
                for (int i5 = 0; i5 < arrayList2.size(); i5++) {
                    if (i5 > 0) {
                        sb.append(", ");
                    }
                    sb.append(arrayList2.get(i5));
                }
            }
            sb.append(")");
        }
        return sb.toString();
    }

    public void a(d dVar) {
        if (this.f5952C == null) {
            this.f5952C = new ArrayList<>();
        }
        this.f5952C.add(dVar);
    }

    public void c() {
        ArrayList<Animator> arrayList = this.f5968w;
        int size = arrayList.size();
        Animator[] animatorArr = (Animator[]) arrayList.toArray(this.f5969x);
        this.f5969x = f5946F;
        for (int i4 = size - 1; i4 >= 0; i4--) {
            Animator animator = animatorArr[i4];
            animatorArr[i4] = null;
            animator.cancel();
        }
        this.f5969x = animatorArr;
        u(this, e.f5978g);
    }

    public abstract void d(m mVar);

    public final void e(View view, boolean z4) {
        if (view == null) {
            return;
        }
        view.getId();
        if (view.getParent() instanceof ViewGroup) {
            m mVar = new m(view);
            if (z4) {
                g(mVar);
            } else {
                d(mVar);
            }
            mVar.f6001c.add(this);
            f(mVar);
            if (z4) {
                b(this.f5961p, view, mVar);
            } else {
                b(this.f5962q, view, mVar);
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i4 = 0; i4 < viewGroup.getChildCount(); i4++) {
                e(viewGroup.getChildAt(i4), z4);
            }
        }
    }

    public abstract void g(m mVar);

    public final void h(ViewGroup viewGroup, boolean z4) {
        i(z4);
        ArrayList<Integer> arrayList = this.f5959n;
        int size = arrayList.size();
        ArrayList<View> arrayList2 = this.f5960o;
        if (size <= 0 && arrayList2.size() <= 0) {
            e(viewGroup, z4);
            return;
        }
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            View findViewById = viewGroup.findViewById(arrayList.get(i4).intValue());
            if (findViewById != null) {
                m mVar = new m(findViewById);
                if (z4) {
                    g(mVar);
                } else {
                    d(mVar);
                }
                mVar.f6001c.add(this);
                f(mVar);
                if (z4) {
                    b(this.f5961p, findViewById, mVar);
                } else {
                    b(this.f5962q, findViewById, mVar);
                }
            }
        }
        for (int i5 = 0; i5 < arrayList2.size(); i5++) {
            View view = arrayList2.get(i5);
            m mVar2 = new m(view);
            if (z4) {
                g(mVar2);
            } else {
                d(mVar2);
            }
            mVar2.f6001c.add(this);
            f(mVar2);
            if (z4) {
                b(this.f5961p, view, mVar2);
            } else {
                b(this.f5962q, view, mVar2);
            }
        }
    }

    public final void i(boolean z4) {
        if (z4) {
            ((C0773b) this.f5961p.j).clear();
            ((SparseArray) this.f5961p.k).clear();
            ((C0778g) this.f5961p.l).b();
            return;
        }
        ((C0773b) this.f5962q.j).clear();
        ((SparseArray) this.f5962q.k).clear();
        ((C0778g) this.f5962q.l).b();
    }

    @Override // 
    /* renamed from: j */
    public f clone() {
        try {
            f fVar = (f) super.clone();
            fVar.f5953D = new ArrayList<>();
            fVar.f5961p = new R5();
            fVar.f5962q = new R5();
            fVar.f5965t = null;
            fVar.f5966u = null;
            fVar.f5951B = this;
            fVar.f5952C = null;
            return fVar;
        } catch (CloneNotSupportedException e4) {
            throw new RuntimeException(e4);
        }
    }

    public Animator k(ViewGroup viewGroup, m mVar, m mVar2) {
        return null;
    }

    /* JADX WARN: Type inference failed for: r3v10, types: [u0.f$b, java.lang.Object] */
    public void l(ViewGroup viewGroup, R5 r5, R5 r52, ArrayList<m> arrayList, ArrayList<m> arrayList2) {
        int i4;
        View view;
        m mVar;
        Animator animator;
        m mVar2;
        r.j p4 = p();
        SparseIntArray sparseIntArray = new SparseIntArray();
        int size = arrayList.size();
        o().getClass();
        int i5 = 0;
        while (i5 < size) {
            m mVar3 = arrayList.get(i5);
            m mVar4 = arrayList2.get(i5);
            if (mVar3 != null && !mVar3.f6001c.contains(this)) {
                mVar3 = null;
            }
            if (mVar4 != null && !mVar4.f6001c.contains(this)) {
                mVar4 = null;
            }
            if ((mVar3 != null || mVar4 != null) && (mVar3 == null || mVar4 == null || s(mVar3, mVar4))) {
                Animator k4 = k(viewGroup, mVar3, mVar4);
                if (k4 != null) {
                    String str = this.f5955j;
                    if (mVar4 != null) {
                        String[] q4 = q();
                        view = mVar4.f6000b;
                        if (q4 != null && q4.length > 0) {
                            mVar2 = new m(view);
                            m mVar5 = (m) ((C0773b) r52.j).getOrDefault(view, null);
                            i4 = size;
                            if (mVar5 != null) {
                                int i6 = 0;
                                while (i6 < q4.length) {
                                    HashMap hashMap = mVar2.f5999a;
                                    String str2 = q4[i6];
                                    hashMap.put(str2, mVar5.f5999a.get(str2));
                                    i6++;
                                    q4 = q4;
                                }
                            }
                            int i7 = p4.f5685l;
                            int i8 = 0;
                            while (true) {
                                if (i8 < i7) {
                                    b bVar = (b) p4.getOrDefault((Animator) p4.h(i8), null);
                                    if (bVar.f5974c != null && bVar.f5972a == view && bVar.f5973b.equals(str) && bVar.f5974c.equals(mVar2)) {
                                        animator = null;
                                        break;
                                    }
                                    i8++;
                                } else {
                                    animator = k4;
                                    break;
                                }
                            }
                        } else {
                            i4 = size;
                            animator = k4;
                            mVar2 = null;
                        }
                        k4 = animator;
                        mVar = mVar2;
                    } else {
                        i4 = size;
                        view = mVar3.f6000b;
                        mVar = null;
                    }
                    if (k4 != null) {
                        WindowId windowId = viewGroup.getWindowId();
                        ?? obj = new Object();
                        obj.f5972a = view;
                        obj.f5973b = str;
                        obj.f5974c = mVar;
                        obj.f5975d = windowId;
                        obj.f5976e = this;
                        obj.f = k4;
                        p4.put(k4, obj);
                        this.f5953D.add(k4);
                    }
                    i5++;
                    size = i4;
                }
            }
            i4 = size;
            i5++;
            size = i4;
        }
        if (sparseIntArray.size() != 0) {
            for (int i9 = 0; i9 < sparseIntArray.size(); i9++) {
                b bVar2 = (b) p4.getOrDefault((Animator) this.f5953D.get(sparseIntArray.keyAt(i9)), null);
                bVar2.f.setStartDelay(bVar2.f.getStartDelay() + (sparseIntArray.valueAt(i9) - Long.MAX_VALUE));
            }
        }
    }

    public final void m() {
        int i4 = this.f5970y - 1;
        this.f5970y = i4;
        if (i4 == 0) {
            u(this, e.f);
            for (int i5 = 0; i5 < ((C0778g) this.f5961p.l).i(); i5++) {
                View view = (View) ((C0778g) this.f5961p.l).j(i5);
                if (view != null) {
                    view.setHasTransientState(false);
                }
            }
            for (int i6 = 0; i6 < ((C0778g) this.f5962q.l).i(); i6++) {
                View view2 = (View) ((C0778g) this.f5962q.l).j(i6);
                if (view2 != null) {
                    view2.setHasTransientState(false);
                }
            }
            this.f5950A = true;
        }
    }

    public final m n(View view, boolean z4) {
        ArrayList<m> arrayList;
        ArrayList<m> arrayList2;
        k kVar = this.f5963r;
        if (kVar != null) {
            return kVar.n(view, z4);
        }
        if (z4) {
            arrayList = this.f5965t;
        } else {
            arrayList = this.f5966u;
        }
        if (arrayList == null) {
            return null;
        }
        int size = arrayList.size();
        int i4 = 0;
        while (true) {
            if (i4 < size) {
                m mVar = arrayList.get(i4);
                if (mVar == null) {
                    return null;
                }
                if (mVar.f6000b == view) {
                    break;
                }
                i4++;
            } else {
                i4 = -1;
                break;
            }
        }
        if (i4 < 0) {
            return null;
        }
        if (z4) {
            arrayList2 = this.f5966u;
        } else {
            arrayList2 = this.f5965t;
        }
        return arrayList2.get(i4);
    }

    public final f o() {
        k kVar = this.f5963r;
        if (kVar != null) {
            return kVar.o();
        }
        return this;
    }

    public String[] q() {
        return null;
    }

    public final m r(View view, boolean z4) {
        R5 r5;
        k kVar = this.f5963r;
        if (kVar != null) {
            return kVar.r(view, z4);
        }
        if (z4) {
            r5 = this.f5961p;
        } else {
            r5 = this.f5962q;
        }
        return (m) ((C0773b) r5.j).getOrDefault(view, null);
    }

    public boolean s(m mVar, m mVar2) {
        boolean z4;
        boolean z5;
        if (mVar == null || mVar2 == null) {
            return false;
        }
        String[] q4 = q();
        HashMap hashMap = mVar.f5999a;
        HashMap hashMap2 = mVar2.f5999a;
        if (q4 != null) {
            for (String str : q4) {
                Object obj = hashMap.get(str);
                Object obj2 = hashMap2.get(str);
                if (obj == null && obj2 == null) {
                    z5 = false;
                } else if (obj == null || obj2 == null) {
                    z5 = true;
                } else {
                    z5 = !obj.equals(obj2);
                }
                if (!z5) {
                }
            }
            return false;
        }
        for (String str2 : hashMap.keySet()) {
            Object obj3 = hashMap.get(str2);
            Object obj4 = hashMap2.get(str2);
            if (obj3 == null && obj4 == null) {
                z4 = false;
                continue;
            } else if (obj3 == null || obj4 == null) {
                z4 = true;
                continue;
            } else {
                z4 = !obj3.equals(obj4);
                continue;
            }
            if (z4) {
            }
        }
        return false;
        return true;
    }

    public final boolean t(View view) {
        int id = view.getId();
        ArrayList<Integer> arrayList = this.f5959n;
        int size = arrayList.size();
        ArrayList<View> arrayList2 = this.f5960o;
        if ((size == 0 && arrayList2.size() == 0) || arrayList.contains(Integer.valueOf(id)) || arrayList2.contains(view)) {
            return true;
        }
        return false;
    }

    public final String toString() {
        return G("");
    }

    public final void u(f fVar, e eVar) {
        f fVar2 = this.f5951B;
        if (fVar2 != null) {
            fVar2.u(fVar, eVar);
        }
        ArrayList<d> arrayList = this.f5952C;
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = this.f5952C.size();
            d[] dVarArr = this.f5967v;
            if (dVarArr == null) {
                dVarArr = new d[size];
            }
            this.f5967v = null;
            d[] dVarArr2 = (d[]) this.f5952C.toArray(dVarArr);
            for (int i4 = 0; i4 < size; i4++) {
                eVar.a(dVarArr2[i4], fVar);
                dVarArr2[i4] = null;
            }
            this.f5967v = dVarArr2;
        }
    }

    public void v(View view) {
        if (!this.f5950A) {
            ArrayList<Animator> arrayList = this.f5968w;
            int size = arrayList.size();
            Animator[] animatorArr = (Animator[]) arrayList.toArray(this.f5969x);
            this.f5969x = f5946F;
            for (int i4 = size - 1; i4 >= 0; i4--) {
                Animator animator = animatorArr[i4];
                animatorArr[i4] = null;
                animator.pause();
            }
            this.f5969x = animatorArr;
            u(this, e.f5979h);
            this.f5971z = true;
        }
    }

    public f w(d dVar) {
        f fVar;
        ArrayList<d> arrayList = this.f5952C;
        if (arrayList == null) {
            return this;
        }
        if (!arrayList.remove(dVar) && (fVar = this.f5951B) != null) {
            fVar.w(dVar);
        }
        if (this.f5952C.size() == 0) {
            this.f5952C = null;
        }
        return this;
    }

    public void x(View view) {
        if (this.f5971z) {
            if (!this.f5950A) {
                ArrayList<Animator> arrayList = this.f5968w;
                int size = arrayList.size();
                Animator[] animatorArr = (Animator[]) arrayList.toArray(this.f5969x);
                this.f5969x = f5946F;
                for (int i4 = size - 1; i4 >= 0; i4--) {
                    Animator animator = animatorArr[i4];
                    animatorArr[i4] = null;
                    animator.resume();
                }
                this.f5969x = animatorArr;
                u(this, e.f5980i);
            }
            this.f5971z = false;
        }
    }

    public void y() {
        F();
        C0773b<Animator, b> p4 = p();
        Iterator<Animator> it = this.f5953D.iterator();
        while (it.hasNext()) {
            Animator next = it.next();
            if (p4.containsKey(next)) {
                F();
                if (next != null) {
                    next.addListener(new g(this, p4));
                    long j4 = this.f5957l;
                    if (j4 >= 0) {
                        next.setDuration(j4);
                    }
                    long j5 = this.f5956k;
                    if (j5 >= 0) {
                        next.setStartDelay(next.getStartDelay() + j5);
                    }
                    TimeInterpolator timeInterpolator = this.f5958m;
                    if (timeInterpolator != null) {
                        next.setInterpolator(timeInterpolator);
                    }
                    next.addListener(new h(this));
                    next.start();
                }
            }
        }
        this.f5953D.clear();
        m();
    }

    public void z(long j4) {
        this.f5957l = j4;
    }

    public void D() {
    }

    public void A(c cVar) {
    }

    public void f(m mVar) {
    }
}
