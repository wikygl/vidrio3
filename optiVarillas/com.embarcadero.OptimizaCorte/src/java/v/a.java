package V;

import M.C0219a;
import M.O;
import M.V;
import N.l;
import N.o;
import V.b;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class a extends C0219a {

    /* renamed from: n  reason: collision with root package name */
    public static final Rect f2484n = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    /* renamed from: o  reason: collision with root package name */
    public static final C0029a f2485o = new Object();

    /* renamed from: p  reason: collision with root package name */
    public static final b f2486p = new Object();

    /* renamed from: h  reason: collision with root package name */
    public final AccessibilityManager f2490h;

    /* renamed from: i  reason: collision with root package name */
    public final View f2491i;

    /* renamed from: j  reason: collision with root package name */
    public c f2492j;

    /* renamed from: d  reason: collision with root package name */
    public final Rect f2487d = new Rect();

    /* renamed from: e  reason: collision with root package name */
    public final Rect f2488e = new Rect();
    public final Rect f = new Rect();

    /* renamed from: g  reason: collision with root package name */
    public final int[] f2489g = new int[2];

    /* renamed from: k  reason: collision with root package name */
    public int f2493k = Integer.MIN_VALUE;

    /* renamed from: l  reason: collision with root package name */
    public int f2494l = Integer.MIN_VALUE;

    /* renamed from: m  reason: collision with root package name */
    public int f2495m = Integer.MIN_VALUE;

    /* renamed from: V.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class C0029a implements b.a<l> {
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public class b {
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class c extends o {
        public c() {
        }

        @Override // N.o
        public final l a(int i4) {
            return new l(AccessibilityNodeInfo.obtain(a.this.n(i4).f1743a));
        }

        @Override // N.o
        public final l b(int i4) {
            int i5;
            a aVar = a.this;
            if (i4 == 2) {
                i5 = aVar.f2493k;
            } else {
                i5 = aVar.f2494l;
            }
            if (i5 == Integer.MIN_VALUE) {
                return null;
            }
            return a(i5);
        }

        @Override // N.o
        public final boolean c(int i4, int i5, Bundle bundle) {
            int i6;
            Chip.b bVar = a.this;
            View view = bVar.f2491i;
            if (i4 != -1) {
                boolean z4 = true;
                if (i5 != 1) {
                    if (i5 != 2) {
                        boolean z5 = false;
                        if (i5 != 64) {
                            if (i5 != 128) {
                                Chip.b bVar2 = bVar;
                                if (i5 != 16) {
                                    return false;
                                }
                                View view2 = bVar2.q;
                                if (i4 == 0) {
                                    return view2.performClick();
                                }
                                if (i4 != 1) {
                                    return false;
                                }
                                view2.playSoundEffect(0);
                                View.OnClickListener onClickListener = ((Chip) view2).q;
                                if (onClickListener != null) {
                                    onClickListener.onClick(view2);
                                    z5 = true;
                                }
                                if (((Chip) view2).B) {
                                    ((Chip) view2).A.q(1, 1);
                                    return z5;
                                }
                                return z5;
                            }
                            if (bVar.f2493k == i4) {
                                bVar.f2493k = Integer.MIN_VALUE;
                                view.invalidate();
                                bVar.q(i4, 65536);
                            }
                            z4 = false;
                        } else {
                            AccessibilityManager accessibilityManager = bVar.f2490h;
                            if (accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled() && (i6 = bVar.f2493k) != i4) {
                                if (i6 != Integer.MIN_VALUE) {
                                    bVar.f2493k = Integer.MIN_VALUE;
                                    bVar.f2491i.invalidate();
                                    bVar.q(i6, 65536);
                                }
                                bVar.f2493k = i4;
                                view.invalidate();
                                bVar.q(i4, 32768);
                            }
                            z4 = false;
                        }
                        return z4;
                    }
                    return bVar.j(i4);
                }
                return bVar.p(i4);
            }
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            return view.performAccessibilityAction(i5, bundle);
        }
    }

    public a(View view) {
        if (view != null) {
            this.f2491i = view;
            this.f2490h = (AccessibilityManager) view.getContext().getSystemService("accessibility");
            view.setFocusable(true);
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            if (view.getImportantForAccessibility() == 0) {
                view.setImportantForAccessibility(1);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("View may not be null");
    }

    @Override // M.C0219a
    public final o b(View view) {
        if (this.f2492j == null) {
            this.f2492j = new c();
        }
        return this.f2492j;
    }

    @Override // M.C0219a
    public final void d(View view, l lVar) {
        View.AccessibilityDelegate accessibilityDelegate = this.f1582a;
        AccessibilityNodeInfo accessibilityNodeInfo = lVar.f1743a;
        accessibilityDelegate.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        Chip chip = ((Chip.b) this).q;
        accessibilityNodeInfo.setCheckable(chip.e());
        accessibilityNodeInfo.setClickable(chip.isClickable());
        accessibilityNodeInfo.setClassName(chip.getAccessibilityClassName());
        CharSequence text = chip.getText();
        if (Build.VERSION.SDK_INT >= 23) {
            lVar.l(text);
        } else {
            accessibilityNodeInfo.setContentDescription(text);
        }
    }

    public final boolean j(int i4) {
        if (this.f2494l != i4) {
            return false;
        }
        this.f2494l = Integer.MIN_VALUE;
        Chip.b bVar = (Chip.b) this;
        if (i4 == 1) {
            Chip chip = bVar.q;
            chip.v = false;
            chip.refreshDrawableState();
        }
        q(i4, 8);
        return true;
    }

    public final l k(int i4) {
        boolean z4;
        AccessibilityNodeInfo obtain = AccessibilityNodeInfo.obtain();
        l lVar = new l(obtain);
        obtain.setEnabled(true);
        obtain.setFocusable(true);
        obtain.setClassName("android.view.View");
        Rect rect = f2484n;
        obtain.setBoundsInParent(rect);
        obtain.setBoundsInScreen(rect);
        lVar.f1744b = -1;
        View view = this.f2491i;
        obtain.setParent(view);
        o(i4, lVar);
        if (lVar.g() == null && obtain.getContentDescription() == null) {
            throw new RuntimeException("Callbacks must add text or a content description in populateNodeForVirtualViewId()");
        }
        Rect rect2 = this.f2488e;
        lVar.f(rect2);
        if (!rect2.equals(rect)) {
            int actions = obtain.getActions();
            if ((actions & 64) == 0) {
                if ((actions & 128) == 0) {
                    obtain.setPackageName(view.getContext().getPackageName());
                    lVar.f1745c = i4;
                    obtain.setSource(view, i4);
                    if (this.f2493k == i4) {
                        obtain.setAccessibilityFocused(true);
                        lVar.a(128);
                    } else {
                        obtain.setAccessibilityFocused(false);
                        lVar.a(64);
                    }
                    if (this.f2494l == i4) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    if (z4) {
                        lVar.a(2);
                    } else if (obtain.isFocusable()) {
                        lVar.a(1);
                    }
                    obtain.setFocused(z4);
                    int[] iArr = this.f2489g;
                    view.getLocationOnScreen(iArr);
                    Rect rect3 = this.f2487d;
                    obtain.getBoundsInScreen(rect3);
                    if (rect3.equals(rect)) {
                        lVar.f(rect3);
                        if (lVar.f1744b != -1) {
                            l lVar2 = new l(AccessibilityNodeInfo.obtain());
                            for (int i5 = lVar.f1744b; i5 != -1; i5 = lVar2.f1744b) {
                                lVar2.f1744b = -1;
                                AccessibilityNodeInfo accessibilityNodeInfo = lVar2.f1743a;
                                accessibilityNodeInfo.setParent(view, -1);
                                accessibilityNodeInfo.setBoundsInParent(rect);
                                o(i5, lVar2);
                                lVar2.f(rect2);
                                rect3.offset(rect2.left, rect2.top);
                            }
                        }
                        rect3.offset(iArr[0] - view.getScrollX(), iArr[1] - view.getScrollY());
                    }
                    Rect rect4 = this.f;
                    if (view.getLocalVisibleRect(rect4)) {
                        rect4.offset(iArr[0] - view.getScrollX(), iArr[1] - view.getScrollY());
                        if (rect3.intersect(rect4)) {
                            lVar.f1743a.setBoundsInScreen(rect3);
                            if (!rect3.isEmpty() && view.getWindowVisibility() == 0) {
                                ViewParent parent = view.getParent();
                                while (true) {
                                    if (parent instanceof View) {
                                        View view2 = (View) parent;
                                        if (view2.getAlpha() <= 0.0f || view2.getVisibility() != 0) {
                                            break;
                                        }
                                        parent = view2.getParent();
                                    } else if (parent != null) {
                                        obtain.setVisibleToUser(true);
                                    }
                                }
                            }
                        }
                    }
                    return lVar;
                }
                throw new RuntimeException("Callbacks must not add ACTION_CLEAR_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
            }
            throw new RuntimeException("Callbacks must not add ACTION_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
        }
        throw new RuntimeException("Callbacks must set parent bounds in populateNodeForVirtualViewId()");
    }

    public abstract void l(ArrayList arrayList);

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00bc  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x00eb  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0104  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean m(int r20, android.graphics.Rect r21) {
        /*
            Method dump skipped, instructions count: 485
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: V.a.m(int, android.graphics.Rect):boolean");
    }

    public final l n(int i4) {
        if (i4 == -1) {
            View view = this.f2491i;
            AccessibilityNodeInfo obtain = AccessibilityNodeInfo.obtain(view);
            l lVar = new l(obtain);
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            view.onInitializeAccessibilityNodeInfo(obtain);
            ArrayList arrayList = new ArrayList();
            l(arrayList);
            if (obtain.getChildCount() > 0 && arrayList.size() > 0) {
                throw new RuntimeException("Views cannot have both real and virtual children");
            }
            int size = arrayList.size();
            for (int i5 = 0; i5 < size; i5++) {
                lVar.f1743a.addChild(view, ((Integer) arrayList.get(i5)).intValue());
            }
            return lVar;
        }
        return k(i4);
    }

    public abstract void o(int i4, l lVar);

    public final boolean p(int i4) {
        int i5;
        View view = this.f2491i;
        if ((!view.isFocused() && !view.requestFocus()) || (i5 = this.f2494l) == i4) {
            return false;
        }
        if (i5 != Integer.MIN_VALUE) {
            j(i5);
        }
        if (i4 == Integer.MIN_VALUE) {
            return false;
        }
        this.f2494l = i4;
        Chip.b bVar = (Chip.b) this;
        if (i4 == 1) {
            Chip chip = bVar.q;
            chip.v = true;
            chip.refreshDrawableState();
        }
        q(i4, 8);
        return true;
    }

    public final void q(int i4, int i5) {
        View view;
        ViewParent parent;
        AccessibilityEvent obtain;
        if (i4 == Integer.MIN_VALUE || !this.f2490h.isEnabled() || (parent = (view = this.f2491i).getParent()) == null) {
            return;
        }
        if (i4 != -1) {
            obtain = AccessibilityEvent.obtain(i5);
            l n4 = n(i4);
            obtain.getText().add(n4.g());
            AccessibilityNodeInfo accessibilityNodeInfo = n4.f1743a;
            obtain.setContentDescription(accessibilityNodeInfo.getContentDescription());
            obtain.setScrollable(accessibilityNodeInfo.isScrollable());
            obtain.setPassword(accessibilityNodeInfo.isPassword());
            obtain.setEnabled(accessibilityNodeInfo.isEnabled());
            obtain.setChecked(accessibilityNodeInfo.isChecked());
            if (obtain.getText().isEmpty() && obtain.getContentDescription() == null) {
                throw new RuntimeException("Callbacks must add text or a content description in populateEventForVirtualViewId()");
            }
            obtain.setClassName(accessibilityNodeInfo.getClassName());
            obtain.setSource(view, i4);
            obtain.setPackageName(view.getContext().getPackageName());
        } else {
            obtain = AccessibilityEvent.obtain(i5);
            view.onInitializeAccessibilityEvent(obtain);
        }
        parent.requestSendAccessibilityEvent(view, obtain);
    }
}
