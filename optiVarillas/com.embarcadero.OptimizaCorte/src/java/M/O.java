package M;

import M.C0219a;
import M.C0226h;
import M.O;
import M.e0;
import N.l;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContentInfo;
import android.view.KeyEvent;
import android.view.OnReceiveContentListener;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.autofill.AutofillId;
import android.view.contentcapture.ContentCaptureSession;
import j$.util.Objects;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressLint({"PrivateConstructorForUtilityClass"})
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class O {

    /* renamed from: a  reason: collision with root package name */
    public static WeakHashMap<View, V> f1526a;

    /* renamed from: b  reason: collision with root package name */
    public static Field f1527b;

    /* renamed from: c  reason: collision with root package name */
    public static boolean f1528c;

    /* renamed from: d  reason: collision with root package name */
    public static ThreadLocal<Rect> f1529d;

    /* renamed from: e  reason: collision with root package name */
    public static final int[] f1530e = {2131230736, 2131230737, 2131230748, 2131230759, 2131230762, 2131230763, 2131230764, 2131230765, 2131230766, 2131230767, 2131230738, 2131230739, 2131230740, 2131230741, 2131230742, 2131230743, 2131230744, 2131230745, 2131230746, 2131230747, 2131230749, 2131230750, 2131230751, 2131230752, 2131230753, 2131230754, 2131230755, 2131230756, 2131230757, 2131230758, 2131230760, 2131230761};
    public static final J f = new Object();

    /* renamed from: g  reason: collision with root package name */
    public static final a f1531g = new a();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static abstract class b<T> {

        /* renamed from: a  reason: collision with root package name */
        public final int f1533a;

        /* renamed from: b  reason: collision with root package name */
        public final Class<T> f1534b;

        /* renamed from: c  reason: collision with root package name */
        public final int f1535c;

        /* renamed from: d  reason: collision with root package name */
        public final int f1536d;

        public b(int i4, Class<T> cls, int i5, int i6) {
            this.f1533a = i4;
            this.f1534b = cls;
            this.f1536d = i5;
            this.f1535c = i6;
        }

        public abstract T a(View view);

        public abstract void b(View view, T t3);

        public final T c(View view) {
            if (Build.VERSION.SDK_INT >= this.f1535c) {
                return a(view);
            }
            T t3 = (T) view.getTag(this.f1533a);
            if (this.f1534b.isInstance(t3)) {
                return t3;
            }
            return null;
        }

        public final void d(View view, T t3) {
            C0219a c0219a;
            if (Build.VERSION.SDK_INT >= this.f1535c) {
                b(view, t3);
            } else if (e(c(view), t3)) {
                View.AccessibilityDelegate c4 = O.c(view);
                if (c4 == null) {
                    c0219a = null;
                } else if (c4 instanceof C0219a.C0015a) {
                    c0219a = ((C0219a.C0015a) c4).f1584a;
                } else {
                    c0219a = new C0219a(c4);
                }
                if (c0219a == null) {
                    c0219a = new C0219a();
                }
                O.p(view, c0219a);
                view.setTag(this.f1533a, t3);
                O.i(view, this.f1536d);
            }
        }

        public abstract boolean e(T t3, T t4);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class c {
        public static WindowInsets a(View view, WindowInsets windowInsets) {
            return view.dispatchApplyWindowInsets(windowInsets);
        }

        public static WindowInsets b(View view, WindowInsets windowInsets) {
            return view.onApplyWindowInsets(windowInsets);
        }

        public static void c(View view) {
            view.requestApplyInsets();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class d {

        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
        public class a implements View.OnApplyWindowInsetsListener {

            /* renamed from: a  reason: collision with root package name */
            public e0 f1537a = null;

            /* renamed from: b  reason: collision with root package name */
            public final /* synthetic */ View f1538b;

            /* renamed from: c  reason: collision with root package name */
            public final /* synthetic */ InterfaceC0241x f1539c;

            public a(View view, InterfaceC0241x interfaceC0241x) {
                this.f1538b = view;
                this.f1539c = interfaceC0241x;
            }

            @Override // android.view.View.OnApplyWindowInsetsListener
            public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                e0 g4 = e0.g(view, windowInsets);
                int i4 = Build.VERSION.SDK_INT;
                InterfaceC0241x interfaceC0241x = this.f1539c;
                if (i4 < 30) {
                    d.a(windowInsets, this.f1538b);
                    if (g4.equals(this.f1537a)) {
                        return interfaceC0241x.f(view, g4).f();
                    }
                }
                this.f1537a = g4;
                e0 f = interfaceC0241x.f(view, g4);
                if (i4 >= 30) {
                    return f.f();
                }
                WeakHashMap<View, V> weakHashMap = O.f1526a;
                c.c(view);
                return f.f();
            }
        }

        public static void a(WindowInsets windowInsets, View view) {
            View.OnApplyWindowInsetsListener onApplyWindowInsetsListener = (View.OnApplyWindowInsetsListener) view.getTag(2131231256);
            if (onApplyWindowInsetsListener != null) {
                onApplyWindowInsetsListener.onApplyWindowInsets(view, windowInsets);
            }
        }

        public static e0 b(View view, e0 e0Var, Rect rect) {
            WindowInsets f = e0Var.f();
            if (f != null) {
                return e0.g(view, view.computeSystemWindowInsets(f, rect));
            }
            rect.setEmpty();
            return e0Var;
        }

        public static boolean c(View view, float f, float f4, boolean z4) {
            return view.dispatchNestedFling(f, f4, z4);
        }

        public static boolean d(View view, float f, float f4) {
            return view.dispatchNestedPreFling(f, f4);
        }

        public static boolean e(View view, int i4, int i5, int[] iArr, int[] iArr2) {
            return view.dispatchNestedPreScroll(i4, i5, iArr, iArr2);
        }

        public static boolean f(View view, int i4, int i5, int i6, int i7, int[] iArr) {
            return view.dispatchNestedScroll(i4, i5, i6, i7, iArr);
        }

        public static ColorStateList g(View view) {
            return view.getBackgroundTintList();
        }

        public static PorterDuff.Mode h(View view) {
            return view.getBackgroundTintMode();
        }

        public static float i(View view) {
            return view.getElevation();
        }

        public static e0 j(View view) {
            e0.e bVar;
            if (!e0.a.f1591d || !view.isAttachedToWindow()) {
                return null;
            }
            try {
                Object obj = e0.a.f1588a.get(view.getRootView());
                if (obj == null) {
                    return null;
                }
                Rect rect = (Rect) e0.a.f1589b.get(obj);
                Rect rect2 = (Rect) e0.a.f1590c.get(obj);
                if (rect == null || rect2 == null) {
                    return null;
                }
                int i4 = Build.VERSION.SDK_INT;
                if (i4 >= 30) {
                    bVar = new e0.d();
                } else if (i4 >= 29) {
                    bVar = new e0.c();
                } else {
                    bVar = new e0.b();
                }
                bVar.e(E.b.b(rect.left, rect.top, rect.right, rect.bottom));
                bVar.g(E.b.b(rect2.left, rect2.top, rect2.right, rect2.bottom));
                e0 b4 = bVar.b();
                b4.f1587a.p(b4);
                b4.f1587a.d(view.getRootView());
                return b4;
            } catch (IllegalAccessException e4) {
                Log.w("WindowInsetsCompat", "Failed to get insets from AttachInfo. " + e4.getMessage(), e4);
                return null;
            }
        }

        public static String k(View view) {
            return view.getTransitionName();
        }

        public static float l(View view) {
            return view.getTranslationZ();
        }

        public static float m(View view) {
            return view.getZ();
        }

        public static boolean n(View view) {
            return view.hasNestedScrollingParent();
        }

        public static boolean o(View view) {
            return view.isImportantForAccessibility();
        }

        public static boolean p(View view) {
            return view.isNestedScrollingEnabled();
        }

        public static void q(View view, ColorStateList colorStateList) {
            view.setBackgroundTintList(colorStateList);
        }

        public static void r(View view, PorterDuff.Mode mode) {
            view.setBackgroundTintMode(mode);
        }

        public static void s(View view, float f) {
            view.setElevation(f);
        }

        public static void t(View view, boolean z4) {
            view.setNestedScrollingEnabled(z4);
        }

        public static void u(View view, InterfaceC0241x interfaceC0241x) {
            if (Build.VERSION.SDK_INT < 30) {
                view.setTag(2131231248, interfaceC0241x);
            }
            if (interfaceC0241x == null) {
                view.setOnApplyWindowInsetsListener((View.OnApplyWindowInsetsListener) view.getTag(2131231256));
            } else {
                view.setOnApplyWindowInsetsListener(new a(view, interfaceC0241x));
            }
        }

        public static void v(View view, String str) {
            view.setTransitionName(str);
        }

        public static void w(View view, float f) {
            view.setTranslationZ(f);
        }

        public static void x(View view, float f) {
            view.setZ(f);
        }

        public static boolean y(View view, int i4) {
            return view.startNestedScroll(i4);
        }

        public static void z(View view) {
            view.stopNestedScroll();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class e {
        public static e0 a(View view) {
            WindowInsets rootWindowInsets = view.getRootWindowInsets();
            if (rootWindowInsets == null) {
                return null;
            }
            e0 g4 = e0.g(null, rootWindowInsets);
            e0.k kVar = g4.f1587a;
            kVar.p(g4);
            kVar.d(view.getRootView());
            return g4;
        }

        public static int b(View view) {
            return view.getScrollIndicators();
        }

        public static void c(View view, int i4) {
            view.setScrollIndicators(i4);
        }

        public static void d(View view, int i4, int i5) {
            view.setScrollIndicators(i4, i5);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class f {
        public static void a(View view) {
            view.cancelDragAndDrop();
        }

        public static void b(View view) {
            view.dispatchFinishTemporaryDetach();
        }

        public static void c(View view) {
            view.dispatchStartTemporaryDetach();
        }

        public static void d(View view, PointerIcon pointerIcon) {
            view.setPointerIcon(pointerIcon);
        }

        public static boolean e(View view, ClipData clipData, View.DragShadowBuilder dragShadowBuilder, Object obj, int i4) {
            return view.startDragAndDrop(clipData, dragShadowBuilder, obj, i4);
        }

        public static void f(View view, View.DragShadowBuilder dragShadowBuilder) {
            view.updateDragShadow(dragShadowBuilder);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class g {
        public static void a(View view, Collection<View> collection, int i4) {
            view.addKeyboardNavigationClusters(collection, i4);
        }

        public static AutofillId b(View view) {
            return view.getAutofillId();
        }

        public static int c(View view) {
            return view.getImportantForAutofill();
        }

        public static int d(View view) {
            return view.getNextClusterForwardId();
        }

        public static boolean e(View view) {
            return view.hasExplicitFocusable();
        }

        public static boolean f(View view) {
            return view.isFocusedByDefault();
        }

        public static boolean g(View view) {
            return view.isImportantForAutofill();
        }

        public static boolean h(View view) {
            return view.isKeyboardNavigationCluster();
        }

        public static View i(View view, View view2, int i4) {
            return view.keyboardNavigationClusterSearch(view2, i4);
        }

        public static boolean j(View view) {
            return view.restoreDefaultFocus();
        }

        public static void k(View view, String... strArr) {
            view.setAutofillHints(strArr);
        }

        public static void l(View view, boolean z4) {
            view.setFocusedByDefault(z4);
        }

        public static void m(View view, int i4) {
            view.setImportantForAutofill(i4);
        }

        public static void n(View view, boolean z4) {
            view.setKeyboardNavigationCluster(z4);
        }

        public static void o(View view, int i4) {
            view.setNextClusterForwardId(i4);
        }

        public static void p(View view, CharSequence charSequence) {
            view.setTooltipText(charSequence);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class h {
        public static void a(View view, final m mVar) {
            r.j jVar = (r.j) view.getTag(2131231255);
            if (jVar == null) {
                jVar = new r.j();
                view.setTag(2131231255, jVar);
            }
            Objects.requireNonNull(mVar);
            View.OnUnhandledKeyEventListener onUnhandledKeyEventListener = new View.OnUnhandledKeyEventListener() { // from class: M.P
                @Override // android.view.View.OnUnhandledKeyEventListener
                public final boolean onUnhandledKeyEvent(View view2, KeyEvent keyEvent) {
                    return O.m.this.a();
                }
            };
            jVar.put(mVar, onUnhandledKeyEventListener);
            view.addOnUnhandledKeyEventListener(onUnhandledKeyEventListener);
        }

        public static CharSequence b(View view) {
            return view.getAccessibilityPaneTitle();
        }

        public static boolean c(View view) {
            return view.isAccessibilityHeading();
        }

        public static boolean d(View view) {
            return view.isScreenReaderFocusable();
        }

        public static void e(View view, m mVar) {
            View.OnUnhandledKeyEventListener onUnhandledKeyEventListener;
            r.j jVar = (r.j) view.getTag(2131231255);
            if (jVar != null && (onUnhandledKeyEventListener = (View.OnUnhandledKeyEventListener) jVar.getOrDefault(mVar, null)) != null) {
                view.removeOnUnhandledKeyEventListener(onUnhandledKeyEventListener);
            }
        }

        public static <T> T f(View view, int i4) {
            return (T) view.requireViewById(i4);
        }

        public static void g(View view, boolean z4) {
            view.setAccessibilityHeading(z4);
        }

        public static void h(View view, CharSequence charSequence) {
            view.setAccessibilityPaneTitle(charSequence);
        }

        public static void i(View view, P.a aVar) {
            view.setAutofillId(null);
        }

        public static void j(View view, boolean z4) {
            view.setScreenReaderFocusable(z4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class i {
        public static View.AccessibilityDelegate a(View view) {
            return view.getAccessibilityDelegate();
        }

        public static ContentCaptureSession b(View view) {
            return view.getContentCaptureSession();
        }

        public static List<Rect> c(View view) {
            return view.getSystemGestureExclusionRects();
        }

        public static void d(View view, Context context, int[] iArr, AttributeSet attributeSet, TypedArray typedArray, int i4, int i5) {
            view.saveAttributeDataForStyleable(context, iArr, attributeSet, typedArray, i4, i5);
        }

        public static void e(View view, Q.a aVar) {
            view.setContentCaptureSession(null);
        }

        public static void f(View view, List<Rect> list) {
            view.setSystemGestureExclusionRects(list);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class j {
        public static int a(View view) {
            return view.getImportantForContentCapture();
        }

        public static CharSequence b(View view) {
            return view.getStateDescription();
        }

        public static k0 c(View view) {
            WindowInsetsController windowInsetsController = view.getWindowInsetsController();
            if (windowInsetsController != null) {
                return new k0(windowInsetsController);
            }
            return null;
        }

        public static boolean d(View view) {
            return view.isImportantForContentCapture();
        }

        public static void e(View view, int i4) {
            view.setImportantForContentCapture(i4);
        }

        public static void f(View view, CharSequence charSequence) {
            view.setStateDescription(charSequence);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class k {
        public static String[] a(View view) {
            return view.getReceiveContentMimeTypes();
        }

        public static C0226h b(View view, C0226h c0226h) {
            ContentInfo c4 = c0226h.f1616a.c();
            Objects.requireNonNull(c4);
            ContentInfo d4 = G0.d.d(c4);
            ContentInfo performReceiveContent = view.performReceiveContent(d4);
            if (performReceiveContent == null) {
                return null;
            }
            if (performReceiveContent == d4) {
                return c0226h;
            }
            return new C0226h(new C0226h.d(performReceiveContent));
        }

        public static void c(View view, String[] strArr, InterfaceC0242y interfaceC0242y) {
            if (interfaceC0242y == null) {
                view.setOnReceiveContentListener(strArr, null);
            } else {
                view.setOnReceiveContentListener(strArr, new l(interfaceC0242y));
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class l implements OnReceiveContentListener {

        /* renamed from: a  reason: collision with root package name */
        public final InterfaceC0242y f1540a;

        public l(InterfaceC0242y interfaceC0242y) {
            this.f1540a = interfaceC0242y;
        }

        @Override // android.view.OnReceiveContentListener
        public final ContentInfo onReceiveContent(View view, ContentInfo contentInfo) {
            C0226h c0226h = new C0226h(new C0226h.d(contentInfo));
            C0226h a4 = this.f1540a.a(view, c0226h);
            if (a4 == null) {
                return null;
            }
            if (a4 == c0226h) {
                return contentInfo;
            }
            ContentInfo c4 = a4.f1616a.c();
            Objects.requireNonNull(c4);
            return G0.d.d(c4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface m {
        boolean a();
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class n {

        /* renamed from: d  reason: collision with root package name */
        public static final ArrayList<WeakReference<View>> f1541d = new ArrayList<>();

        /* renamed from: a  reason: collision with root package name */
        public WeakHashMap<View, Boolean> f1542a = null;

        /* renamed from: b  reason: collision with root package name */
        public SparseArray<WeakReference<View>> f1543b = null;

        /* renamed from: c  reason: collision with root package name */
        public WeakReference<KeyEvent> f1544c = null;

        public static boolean b(View view, KeyEvent keyEvent) {
            ArrayList arrayList = (ArrayList) view.getTag(2131231255);
            if (arrayList != null) {
                for (int size = arrayList.size() - 1; size >= 0; size--) {
                    if (((m) arrayList.get(size)).a()) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        public final View a(View view, KeyEvent keyEvent) {
            WeakHashMap<View, Boolean> weakHashMap = this.f1542a;
            if (weakHashMap != null && weakHashMap.containsKey(view)) {
                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                        View a4 = a(viewGroup.getChildAt(childCount), keyEvent);
                        if (a4 != null) {
                            return a4;
                        }
                    }
                }
                if (b(view, keyEvent)) {
                    return view;
                }
            }
            return null;
        }
    }

    @Deprecated
    public static V a(View view) {
        if (f1526a == null) {
            f1526a = new WeakHashMap<>();
        }
        V v4 = f1526a.get(view);
        if (v4 == null) {
            V v5 = new V(view);
            f1526a.put(view, v5);
            return v5;
        }
        return v4;
    }

    public static boolean b(View view, KeyEvent keyEvent) {
        if (Build.VERSION.SDK_INT >= 28) {
            return false;
        }
        ArrayList<WeakReference<View>> arrayList = n.f1541d;
        n nVar = (n) view.getTag(2131231254);
        if (nVar == null) {
            nVar = new n();
            view.setTag(2131231254, nVar);
        }
        if (keyEvent.getAction() == 0) {
            WeakHashMap<View, Boolean> weakHashMap = nVar.f1542a;
            if (weakHashMap != null) {
                weakHashMap.clear();
            }
            ArrayList<WeakReference<View>> arrayList2 = n.f1541d;
            if (!arrayList2.isEmpty()) {
                synchronized (arrayList2) {
                    try {
                        if (nVar.f1542a == null) {
                            nVar.f1542a = new WeakHashMap<>();
                        }
                        for (int size = arrayList2.size() - 1; size >= 0; size--) {
                            ArrayList<WeakReference<View>> arrayList3 = n.f1541d;
                            View view2 = arrayList3.get(size).get();
                            if (view2 == null) {
                                arrayList3.remove(size);
                            } else {
                                nVar.f1542a.put(view2, Boolean.TRUE);
                                for (ViewParent parent = view2.getParent(); parent instanceof View; parent = parent.getParent()) {
                                    nVar.f1542a.put((View) parent, Boolean.TRUE);
                                }
                            }
                        }
                    } finally {
                    }
                }
            }
        }
        View a4 = nVar.a(view, keyEvent);
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            if (a4 != null && !KeyEvent.isModifierKey(keyCode)) {
                if (nVar.f1543b == null) {
                    nVar.f1543b = new SparseArray<>();
                }
                nVar.f1543b.put(keyCode, new WeakReference<>(a4));
            }
        }
        if (a4 == null) {
            return false;
        }
        return true;
    }

    public static View.AccessibilityDelegate c(View view) {
        if (Build.VERSION.SDK_INT >= 29) {
            return i.a(view);
        }
        if (f1528c) {
            return null;
        }
        if (f1527b == null) {
            try {
                Field declaredField = View.class.getDeclaredField("mAccessibilityDelegate");
                f1527b = declaredField;
                declaredField.setAccessible(true);
            } catch (Throwable unused) {
                f1528c = true;
                return null;
            }
        }
        try {
            Object obj = f1527b.get(view);
            if (!(obj instanceof View.AccessibilityDelegate)) {
                return null;
            }
            return (View.AccessibilityDelegate) obj;
        } catch (Throwable unused2) {
            f1528c = true;
            return null;
        }
    }

    public static CharSequence d(View view) {
        Object tag;
        if (Build.VERSION.SDK_INT >= 28) {
            tag = h.b(view);
        } else {
            tag = view.getTag(2131231247);
            if (!CharSequence.class.isInstance(tag)) {
                tag = null;
            }
        }
        return (CharSequence) tag;
    }

    public static ArrayList e(View view) {
        ArrayList arrayList = (ArrayList) view.getTag(2131231244);
        if (arrayList == null) {
            ArrayList arrayList2 = new ArrayList();
            view.setTag(2131231244, arrayList2);
            return arrayList2;
        }
        return arrayList;
    }

    public static Rect f() {
        if (f1529d == null) {
            f1529d = new ThreadLocal<>();
        }
        Rect rect = f1529d.get();
        if (rect == null) {
            rect = new Rect();
            f1529d.set(rect);
        }
        rect.setEmpty();
        return rect;
    }

    public static String[] g(View view) {
        if (Build.VERSION.SDK_INT >= 31) {
            return k.a(view);
        }
        return (String[]) view.getTag(2131231250);
    }

    public static e0 h(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            return e.a(view);
        }
        return d.j(view);
    }

    public static void i(View view, int i4) {
        boolean z4;
        AccessibilityManager accessibilityManager = (AccessibilityManager) view.getContext().getSystemService("accessibility");
        if (!accessibilityManager.isEnabled()) {
            return;
        }
        if (d(view) != null && view.isShown() && view.getWindowVisibility() == 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        int i5 = 32;
        if (view.getAccessibilityLiveRegion() == 0 && !z4) {
            if (i4 == 32) {
                AccessibilityEvent obtain = AccessibilityEvent.obtain();
                view.onInitializeAccessibilityEvent(obtain);
                obtain.setEventType(32);
                obtain.setContentChangeTypes(i4);
                obtain.setSource(view);
                view.onPopulateAccessibilityEvent(obtain);
                obtain.getText().add(d(view));
                accessibilityManager.sendAccessibilityEvent(obtain);
                return;
            } else if (view.getParent() != null) {
                try {
                    view.getParent().notifySubtreeAccessibilityStateChanged(view, view, i4);
                    return;
                } catch (AbstractMethodError e4) {
                    Log.e("ViewCompat", view.getParent().getClass().getSimpleName().concat(" does not fully implement ViewParent"), e4);
                    return;
                }
            } else {
                return;
            }
        }
        AccessibilityEvent obtain2 = AccessibilityEvent.obtain();
        if (!z4) {
            i5 = 2048;
        }
        obtain2.setEventType(i5);
        obtain2.setContentChangeTypes(i4);
        if (z4) {
            obtain2.getText().add(d(view));
            if (view.getImportantForAccessibility() == 0) {
                view.setImportantForAccessibility(1);
            }
        }
        view.sendAccessibilityEventUnchecked(obtain2);
    }

    public static void j(View view, int i4) {
        boolean z4;
        if (Build.VERSION.SDK_INT >= 23) {
            view.offsetLeftAndRight(i4);
            return;
        }
        Rect f4 = f();
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            View view2 = (View) parent;
            f4.set(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom());
            z4 = !f4.intersects(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        } else {
            z4 = false;
        }
        view.offsetLeftAndRight(i4);
        if (view.getVisibility() == 0) {
            s(view);
            ViewParent parent2 = view.getParent();
            if (parent2 instanceof View) {
                s((View) parent2);
            }
        }
        if (z4 && f4.intersect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom())) {
            ((View) parent).invalidate(f4);
        }
    }

    public static void k(View view, int i4) {
        boolean z4;
        if (Build.VERSION.SDK_INT >= 23) {
            view.offsetTopAndBottom(i4);
            return;
        }
        Rect f4 = f();
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            View view2 = (View) parent;
            f4.set(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom());
            z4 = !f4.intersects(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        } else {
            z4 = false;
        }
        view.offsetTopAndBottom(i4);
        if (view.getVisibility() == 0) {
            s(view);
            ViewParent parent2 = view.getParent();
            if (parent2 instanceof View) {
                s((View) parent2);
            }
        }
        if (z4 && f4.intersect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom())) {
            ((View) parent).invalidate(f4);
        }
    }

    public static C0226h l(View view, C0226h c0226h) {
        if (Log.isLoggable("ViewCompat", 3)) {
            Log.d("ViewCompat", "performReceiveContent: " + c0226h + ", view=" + view.getClass().getSimpleName() + "[" + view.getId() + "]");
        }
        if (Build.VERSION.SDK_INT >= 31) {
            return k.b(view, c0226h);
        }
        InterfaceC0242y interfaceC0242y = (InterfaceC0242y) view.getTag(2131231249);
        InterfaceC0243z interfaceC0243z = f;
        if (interfaceC0242y != null) {
            C0226h a4 = interfaceC0242y.a(view, c0226h);
            if (a4 == null) {
                return null;
            }
            if (view instanceof InterfaceC0243z) {
                interfaceC0243z = (InterfaceC0243z) view;
            }
            return interfaceC0243z.a(a4);
        }
        if (view instanceof InterfaceC0243z) {
            interfaceC0243z = (InterfaceC0243z) view;
        }
        return interfaceC0243z.a(c0226h);
    }

    public static void m(View view, int i4) {
        ArrayList e4 = e(view);
        for (int i5 = 0; i5 < e4.size(); i5++) {
            if (((l.a) e4.get(i5)).a() == i4) {
                e4.remove(i5);
                return;
            }
        }
    }

    public static void n(View view, l.a aVar, N.p pVar) {
        C0219a c0219a;
        if (pVar == null) {
            m(view, aVar.a());
            i(view, 0);
            return;
        }
        l.a aVar2 = new l.a(null, aVar.f1754b, null, pVar, aVar.f1755c);
        View.AccessibilityDelegate c4 = c(view);
        if (c4 == null) {
            c0219a = null;
        } else if (c4 instanceof C0219a.C0015a) {
            c0219a = ((C0219a.C0015a) c4).f1584a;
        } else {
            c0219a = new C0219a(c4);
        }
        if (c0219a == null) {
            c0219a = new C0219a();
        }
        p(view, c0219a);
        m(view, aVar2.a());
        e(view).add(aVar2);
        i(view, 0);
    }

    public static void o(View view, @SuppressLint({"ContextFirst"}) Context context, int[] iArr, AttributeSet attributeSet, TypedArray typedArray, int i4) {
        if (Build.VERSION.SDK_INT >= 29) {
            i.d(view, context, iArr, attributeSet, typedArray, i4, 0);
        }
    }

    public static void p(View view, C0219a c0219a) {
        C0219a.C0015a c0015a;
        if (c0219a == null && (c(view) instanceof C0219a.C0015a)) {
            c0219a = new C0219a();
        }
        if (view.getImportantForAccessibility() == 0) {
            view.setImportantForAccessibility(1);
        }
        if (c0219a == null) {
            c0015a = null;
        } else {
            c0015a = c0219a.f1583b;
        }
        view.setAccessibilityDelegate(c0015a);
    }

    public static void q(View view, CharSequence charSequence) {
        boolean z4;
        new b(2131231247, CharSequence.class, 8, 28).d(view, charSequence);
        a aVar = f1531g;
        if (charSequence != null) {
            WeakHashMap<View, Boolean> weakHashMap = aVar.f1532j;
            if (view.isShown() && view.getWindowVisibility() == 0) {
                z4 = true;
            } else {
                z4 = false;
            }
            weakHashMap.put(view, Boolean.valueOf(z4));
            view.addOnAttachStateChangeListener(aVar);
            if (view.isAttachedToWindow()) {
                view.getViewTreeObserver().addOnGlobalLayoutListener(aVar);
                return;
            }
            return;
        }
        aVar.f1532j.remove(view);
        view.removeOnAttachStateChangeListener(aVar);
        view.getViewTreeObserver().removeOnGlobalLayoutListener(aVar);
    }

    public static void r(View view, ColorStateList colorStateList) {
        boolean z4;
        int i4 = Build.VERSION.SDK_INT;
        d.q(view, colorStateList);
        if (i4 == 21) {
            Drawable background = view.getBackground();
            if (d.g(view) == null && d.h(view) == null) {
                z4 = false;
            } else {
                z4 = true;
            }
            if (background != null && z4) {
                if (background.isStateful()) {
                    background.setState(view.getDrawableState());
                }
                view.setBackground(background);
            }
        }
    }

    public static void s(View view) {
        float translationY = view.getTranslationY();
        view.setTranslationY(1.0f + translationY);
        view.setTranslationY(translationY);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a implements ViewTreeObserver.OnGlobalLayoutListener, View.OnAttachStateChangeListener {

        /* renamed from: j  reason: collision with root package name */
        public final WeakHashMap<View, Boolean> f1532j = new WeakHashMap<>();

        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public final void onGlobalLayout() {
            boolean z4;
            int i4;
            if (Build.VERSION.SDK_INT < 28) {
                for (Map.Entry<View, Boolean> entry : this.f1532j.entrySet()) {
                    View key = entry.getKey();
                    boolean booleanValue = entry.getValue().booleanValue();
                    if (key.isShown() && key.getWindowVisibility() == 0) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    if (booleanValue != z4) {
                        if (z4) {
                            i4 = 16;
                        } else {
                            i4 = 32;
                        }
                        O.i(key, i4);
                        entry.setValue(Boolean.valueOf(z4));
                    }
                }
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public final void onViewAttachedToWindow(View view) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public final void onViewDetachedFromWindow(View view) {
        }
    }
}
