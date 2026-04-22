package u0;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import r.C0773b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class j {

    /* renamed from: a  reason: collision with root package name */
    public static final C0820a f5984a;

    /* renamed from: b  reason: collision with root package name */
    public static final ThreadLocal<WeakReference<C0773b<ViewGroup, ArrayList<f>>>> f5985b;

    /* renamed from: c  reason: collision with root package name */
    public static final ArrayList<ViewGroup> f5986c;

    /* JADX WARN: Type inference failed for: r0v0, types: [u0.a, u0.k] */
    static {
        ?? kVar = new k();
        kVar.f5992K = false;
        kVar.H(new c(2));
        kVar.H(new f());
        kVar.H(new c(1));
        f5984a = kVar;
        f5985b = new ThreadLocal<>();
        f5986c = new ArrayList<>();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v6, types: [u0.j$a, android.view.ViewTreeObserver$OnPreDrawListener, java.lang.Object, android.view.View$OnAttachStateChangeListener] */
    public static void a(ViewGroup viewGroup, f fVar) {
        ArrayList<ViewGroup> arrayList = f5986c;
        if (!arrayList.contains(viewGroup) && viewGroup.isLaidOut()) {
            arrayList.add(viewGroup);
            if (fVar == null) {
                fVar = f5984a;
            }
            f clone = fVar.clone();
            ArrayList<f> orDefault = b().getOrDefault(viewGroup, null);
            if (orDefault != null && orDefault.size() > 0) {
                Iterator<f> it = orDefault.iterator();
                while (it.hasNext()) {
                    it.next().v(viewGroup);
                }
            }
            clone.h(viewGroup, true);
            if (((e) viewGroup.getTag(2131231297)) == null) {
                viewGroup.setTag(2131231297, null);
                ?? obj = new Object();
                obj.f5987j = clone;
                obj.f5988k = viewGroup;
                viewGroup.addOnAttachStateChangeListener(obj);
                viewGroup.getViewTreeObserver().addOnPreDrawListener(obj);
                return;
            }
            throw null;
        }
    }

    public static C0773b<ViewGroup, ArrayList<f>> b() {
        C0773b<ViewGroup, ArrayList<f>> c0773b;
        ThreadLocal<WeakReference<C0773b<ViewGroup, ArrayList<f>>>> threadLocal = f5985b;
        WeakReference<C0773b<ViewGroup, ArrayList<f>>> weakReference = threadLocal.get();
        if (weakReference != null && (c0773b = weakReference.get()) != null) {
            return c0773b;
        }
        C0773b<ViewGroup, ArrayList<f>> c0773b2 = new C0773b<>();
        threadLocal.set(new WeakReference<>(c0773b2));
        return c0773b2;
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a implements ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener {

        /* renamed from: j  reason: collision with root package name */
        public f f5987j;

        /* renamed from: k  reason: collision with root package name */
        public ViewGroup f5988k;

        /* renamed from: u0.j$a$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
        public class C0073a extends i {

            /* renamed from: a  reason: collision with root package name */
            public final /* synthetic */ C0773b f5989a;

            public C0073a(C0773b c0773b) {
                this.f5989a = c0773b;
            }

            @Override // u0.f.d
            public final void d(f fVar) {
                ((ArrayList) this.f5989a.getOrDefault(a.this.f5988k, null)).remove(fVar);
                fVar.w(this);
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:105:0x0238  */
        /* JADX WARN: Removed duplicated region for block: B:134:0x01e7 A[EDGE_INSN: B:134:0x01e7->B:89:0x01e7 ?: BREAK  , SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:14:0x004f  */
        /* JADX WARN: Removed duplicated region for block: B:21:0x008d  */
        /* JADX WARN: Removed duplicated region for block: B:92:0x01ed  */
        /* JADX WARN: Removed duplicated region for block: B:99:0x020e  */
        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final boolean onPreDraw() {
            /*
                Method dump skipped, instructions count: 689
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: u0.j.a.onPreDraw():boolean");
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public final void onViewDetachedFromWindow(View view) {
            ViewGroup viewGroup = this.f5988k;
            viewGroup.getViewTreeObserver().removeOnPreDrawListener(this);
            viewGroup.removeOnAttachStateChangeListener(this);
            ArrayList<ViewGroup> arrayList = j.f5986c;
            ViewGroup viewGroup2 = this.f5988k;
            arrayList.remove(viewGroup2);
            ArrayList<f> orDefault = j.b().getOrDefault(viewGroup2, null);
            if (orDefault != null && orDefault.size() > 0) {
                Iterator<f> it = orDefault.iterator();
                while (it.hasNext()) {
                    it.next().x(viewGroup2);
                }
            }
            this.f5987j.i(true);
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public final void onViewAttachedToWindow(View view) {
        }
    }
}
