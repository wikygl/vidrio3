package M;

import M.O;
import N.l;
import N.p;
import android.os.Build;
import android.os.Bundle;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

/* renamed from: M.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public class C0219a {

    /* renamed from: c  reason: collision with root package name */
    public static final View.AccessibilityDelegate f1581c = new View.AccessibilityDelegate();

    /* renamed from: a  reason: collision with root package name */
    public final View.AccessibilityDelegate f1582a;

    /* renamed from: b  reason: collision with root package name */
    public final C0015a f1583b;

    /* renamed from: M.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class C0015a extends View.AccessibilityDelegate {

        /* renamed from: a  reason: collision with root package name */
        public final C0219a f1584a;

        public C0015a(C0219a c0219a) {
            this.f1584a = c0219a;
        }

        @Override // android.view.View.AccessibilityDelegate
        public final boolean dispatchPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            return this.f1584a.a(view, accessibilityEvent);
        }

        @Override // android.view.View.AccessibilityDelegate
        public final AccessibilityNodeProvider getAccessibilityNodeProvider(View view) {
            N.o b4 = this.f1584a.b(view);
            if (b4 != null) {
                return (AccessibilityNodeProvider) b4.f1758a;
            }
            return null;
        }

        @Override // android.view.View.AccessibilityDelegate
        public final void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            this.f1584a.c(view, accessibilityEvent);
        }

        @Override // android.view.View.AccessibilityDelegate
        public final void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
            Object tag;
            boolean z4;
            Object tag2;
            boolean z5;
            Object tag3;
            ClickableSpan[] clickableSpanArr;
            int i4;
            int i5 = 1;
            N.l lVar = new N.l(accessibilityNodeInfo);
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            if (Build.VERSION.SDK_INT >= 28) {
                tag = Boolean.valueOf(O.h.d(view));
            } else {
                tag = view.getTag(2131231251);
                if (!Boolean.class.isInstance(tag)) {
                    tag = null;
                }
            }
            Boolean bool = (Boolean) tag;
            if (bool != null && bool.booleanValue()) {
                z4 = true;
            } else {
                z4 = false;
            }
            int i6 = Build.VERSION.SDK_INT;
            if (i6 >= 28) {
                accessibilityNodeInfo.setScreenReaderFocusable(z4);
            } else {
                lVar.h(1, z4);
            }
            if (Build.VERSION.SDK_INT >= 28) {
                tag2 = Boolean.valueOf(O.h.c(view));
            } else {
                tag2 = view.getTag(2131231246);
                if (!Boolean.class.isInstance(tag2)) {
                    tag2 = null;
                }
            }
            Boolean bool2 = (Boolean) tag2;
            if (bool2 != null && bool2.booleanValue()) {
                z5 = true;
            } else {
                z5 = false;
            }
            if (i6 >= 28) {
                accessibilityNodeInfo.setHeading(z5);
            } else {
                lVar.h(2, z5);
            }
            CharSequence d4 = O.d(view);
            if (i6 >= 28) {
                accessibilityNodeInfo.setPaneTitle(d4);
            } else {
                accessibilityNodeInfo.getExtras().putCharSequence("androidx.view.accessibility.AccessibilityNodeInfoCompat.PANE_TITLE_KEY", d4);
            }
            if (Build.VERSION.SDK_INT >= 30) {
                tag3 = O.j.b(view);
            } else {
                tag3 = view.getTag(2131231252);
                if (!CharSequence.class.isInstance(tag3)) {
                    tag3 = null;
                }
            }
            CharSequence charSequence = (CharSequence) tag3;
            if (i6 >= 30) {
                l.b.c(accessibilityNodeInfo, charSequence);
            } else {
                accessibilityNodeInfo.getExtras().putCharSequence("androidx.view.accessibility.AccessibilityNodeInfoCompat.STATE_DESCRIPTION_KEY", charSequence);
            }
            this.f1584a.d(view, lVar);
            CharSequence text = accessibilityNodeInfo.getText();
            if (i6 < 26) {
                accessibilityNodeInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY");
                accessibilityNodeInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_END_KEY");
                accessibilityNodeInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_FLAGS_KEY");
                accessibilityNodeInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ID_KEY");
                SparseArray sparseArray = (SparseArray) view.getTag(2131231245);
                if (sparseArray != null) {
                    ArrayList arrayList = new ArrayList();
                    for (int i7 = 0; i7 < sparseArray.size(); i7++) {
                        if (((WeakReference) sparseArray.valueAt(i7)).get() == null) {
                            arrayList.add(Integer.valueOf(i7));
                        }
                    }
                    for (int i8 = 0; i8 < arrayList.size(); i8++) {
                        sparseArray.remove(((Integer) arrayList.get(i8)).intValue());
                    }
                }
                if (text instanceof Spanned) {
                    clickableSpanArr = (ClickableSpan[]) ((Spanned) text).getSpans(0, text.length(), ClickableSpan.class);
                } else {
                    clickableSpanArr = null;
                }
                if (clickableSpanArr != null && clickableSpanArr.length > 0) {
                    accessibilityNodeInfo.getExtras().putInt("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ACTION_ID_KEY", 2131230735);
                    SparseArray sparseArray2 = (SparseArray) view.getTag(2131231245);
                    if (sparseArray2 == null) {
                        sparseArray2 = new SparseArray();
                        view.setTag(2131231245, sparseArray2);
                    }
                    for (int i9 = 0; i9 < clickableSpanArr.length; i9++) {
                        ClickableSpan clickableSpan = clickableSpanArr[i9];
                        int i10 = 0;
                        while (true) {
                            if (i10 < sparseArray2.size()) {
                                if (clickableSpan.equals((ClickableSpan) ((WeakReference) sparseArray2.valueAt(i10)).get())) {
                                    i4 = sparseArray2.keyAt(i10);
                                    break;
                                }
                                i10 += i5;
                            } else {
                                i4 = N.l.f1742d;
                                N.l.f1742d = i4 + 1;
                                break;
                            }
                        }
                        sparseArray2.put(i4, new WeakReference(clickableSpanArr[i9]));
                        ClickableSpan clickableSpan2 = clickableSpanArr[i9];
                        Spanned spanned = (Spanned) text;
                        lVar.c("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY").add(Integer.valueOf(spanned.getSpanStart(clickableSpan2)));
                        lVar.c("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_END_KEY").add(Integer.valueOf(spanned.getSpanEnd(clickableSpan2)));
                        lVar.c("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_FLAGS_KEY").add(Integer.valueOf(spanned.getSpanFlags(clickableSpan2)));
                        lVar.c("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ID_KEY").add(Integer.valueOf(i4));
                        i5 = 1;
                    }
                }
            }
            List list = (List) view.getTag(2131231244);
            if (list == null) {
                list = Collections.emptyList();
            }
            for (int i11 = 0; i11 < list.size(); i11++) {
                lVar.b((l.a) list.get(i11));
            }
        }

        @Override // android.view.View.AccessibilityDelegate
        public final void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            this.f1584a.e(view, accessibilityEvent);
        }

        @Override // android.view.View.AccessibilityDelegate
        public final boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
            return this.f1584a.f(viewGroup, view, accessibilityEvent);
        }

        @Override // android.view.View.AccessibilityDelegate
        public final boolean performAccessibilityAction(View view, int i4, Bundle bundle) {
            return this.f1584a.g(view, i4, bundle);
        }

        @Override // android.view.View.AccessibilityDelegate
        public final void sendAccessibilityEvent(View view, int i4) {
            this.f1584a.h(view, i4);
        }

        @Override // android.view.View.AccessibilityDelegate
        public final void sendAccessibilityEventUnchecked(View view, AccessibilityEvent accessibilityEvent) {
            this.f1584a.i(view, accessibilityEvent);
        }
    }

    public C0219a() {
        this(f1581c);
    }

    public boolean a(View view, AccessibilityEvent accessibilityEvent) {
        return this.f1582a.dispatchPopulateAccessibilityEvent(view, accessibilityEvent);
    }

    public N.o b(View view) {
        AccessibilityNodeProvider accessibilityNodeProvider = this.f1582a.getAccessibilityNodeProvider(view);
        if (accessibilityNodeProvider != null) {
            return new N.o(accessibilityNodeProvider);
        }
        return null;
    }

    public void c(View view, AccessibilityEvent accessibilityEvent) {
        this.f1582a.onInitializeAccessibilityEvent(view, accessibilityEvent);
    }

    public void d(View view, N.l lVar) {
        this.f1582a.onInitializeAccessibilityNodeInfo(view, lVar.f1743a);
    }

    public void e(View view, AccessibilityEvent accessibilityEvent) {
        this.f1582a.onPopulateAccessibilityEvent(view, accessibilityEvent);
    }

    public boolean f(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
        return this.f1582a.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
    }

    public boolean g(View view, int i4, Bundle bundle) {
        ClickableSpan[] clickableSpanArr;
        boolean z4;
        WeakReference weakReference;
        ClickableSpan clickableSpan;
        List list = (List) view.getTag(2131231244);
        if (list == null) {
            list = Collections.emptyList();
        }
        boolean z5 = false;
        int i5 = 0;
        while (true) {
            clickableSpanArr = null;
            if (i5 >= list.size()) {
                break;
            }
            l.a aVar = (l.a) list.get(i5);
            if (aVar.a() == i4) {
                N.p pVar = aVar.f1756d;
                if (pVar != null) {
                    Class<? extends p.a> cls = aVar.f1755c;
                    if (cls != null) {
                        try {
                            cls.getDeclaredConstructor(null).newInstance(null).getClass();
                        } catch (Exception e4) {
                            Log.e("A11yActionCompat", "Failed to execute command with argument class ViewCommandArgument: ".concat(cls.getName()), e4);
                        }
                    }
                    z4 = pVar.a(view);
                }
            } else {
                i5++;
            }
        }
        z4 = false;
        if (!z4) {
            z4 = this.f1582a.performAccessibilityAction(view, i4, bundle);
        }
        if (!z4 && i4 == 2131230735 && bundle != null) {
            int i6 = bundle.getInt("ACCESSIBILITY_CLICKABLE_SPAN_ID", -1);
            SparseArray sparseArray = (SparseArray) view.getTag(2131231245);
            if (sparseArray != null && (weakReference = (WeakReference) sparseArray.get(i6)) != null && (clickableSpan = (ClickableSpan) weakReference.get()) != null) {
                CharSequence text = view.createAccessibilityNodeInfo().getText();
                if (text instanceof Spanned) {
                    clickableSpanArr = (ClickableSpan[]) ((Spanned) text).getSpans(0, text.length(), ClickableSpan.class);
                }
                int i7 = 0;
                while (true) {
                    if (clickableSpanArr == null || i7 >= clickableSpanArr.length) {
                        break;
                    } else if (clickableSpan.equals(clickableSpanArr[i7])) {
                        clickableSpan.onClick(view);
                        z5 = true;
                        break;
                    } else {
                        i7++;
                    }
                }
            }
            return z5;
        }
        return z4;
    }

    public void h(View view, int i4) {
        this.f1582a.sendAccessibilityEvent(view, i4);
    }

    public void i(View view, AccessibilityEvent accessibilityEvent) {
        this.f1582a.sendAccessibilityEventUnchecked(view, accessibilityEvent);
    }

    public C0219a(View.AccessibilityDelegate accessibilityDelegate) {
        this.f1582a = accessibilityDelegate;
        this.f1583b = new C0015a(this);
    }
}
