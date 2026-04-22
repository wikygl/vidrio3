package N;

import N.p;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import j$.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class l {

    /* renamed from: d  reason: collision with root package name */
    public static int f1742d;

    /* renamed from: a  reason: collision with root package name */
    public final AccessibilityNodeInfo f1743a;

    /* renamed from: b  reason: collision with root package name */
    public int f1744b = -1;

    /* renamed from: c  reason: collision with root package name */
    public int f1745c = -1;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a {

        /* renamed from: e  reason: collision with root package name */
        public static final a f1746e;
        public static final a f;

        /* renamed from: g  reason: collision with root package name */
        public static final a f1747g;

        /* renamed from: h  reason: collision with root package name */
        public static final a f1748h;

        /* renamed from: i  reason: collision with root package name */
        public static final a f1749i;

        /* renamed from: j  reason: collision with root package name */
        public static final a f1750j;

        /* renamed from: k  reason: collision with root package name */
        public static final a f1751k;

        /* renamed from: l  reason: collision with root package name */
        public static final a f1752l;

        /* renamed from: a  reason: collision with root package name */
        public final Object f1753a;

        /* renamed from: b  reason: collision with root package name */
        public final int f1754b;

        /* renamed from: c  reason: collision with root package name */
        public final Class<? extends p.a> f1755c;

        /* renamed from: d  reason: collision with root package name */
        public final p f1756d;

        static {
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction2;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction3;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction4;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction5;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction6;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction7;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction8;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction9;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction10;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction11;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction12;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction13;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction14;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction15;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction16;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction17;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction18;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction19;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction20;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction21;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction22;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction23;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction24;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction25;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction26;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction27;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction28;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction29;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction30;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction31;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction32;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction33;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction34;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction35;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction36;
            new a(1);
            new a(2);
            new a(4);
            new a(8);
            f1746e = new a(16);
            new a(32);
            new a(64);
            new a(128);
            new a(256, p.b.class);
            new a(512, p.b.class);
            new a(1024, p.c.class);
            new a(2048, p.c.class);
            f = new a(4096);
            f1747g = new a(8192);
            new a(16384);
            new a(32768);
            new a(65536);
            new a(131072, p.g.class);
            f1748h = new a(262144);
            f1749i = new a(524288);
            f1750j = new a(1048576);
            new a(2097152, p.h.class);
            int i4 = Build.VERSION.SDK_INT;
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction37 = null;
            if (i4 >= 23) {
                accessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_ON_SCREEN;
            } else {
                accessibilityAction = null;
            }
            new a(accessibilityAction, 16908342, null, null, null);
            if (i4 >= 23) {
                accessibilityAction36 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_TO_POSITION;
                accessibilityAction2 = accessibilityAction36;
            } else {
                accessibilityAction2 = null;
            }
            new a(accessibilityAction2, 16908343, null, null, p.e.class);
            if (i4 >= 23) {
                accessibilityAction35 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP;
                accessibilityAction3 = accessibilityAction35;
            } else {
                accessibilityAction3 = null;
            }
            f1751k = new a(accessibilityAction3, 16908344, null, null, null);
            if (i4 >= 23) {
                accessibilityAction34 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT;
                accessibilityAction4 = accessibilityAction34;
            } else {
                accessibilityAction4 = null;
            }
            new a(accessibilityAction4, 16908345, null, null, null);
            if (i4 >= 23) {
                accessibilityAction33 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN;
                accessibilityAction5 = accessibilityAction33;
            } else {
                accessibilityAction5 = null;
            }
            f1752l = new a(accessibilityAction5, 16908346, null, null, null);
            if (i4 >= 23) {
                accessibilityAction32 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT;
                accessibilityAction6 = accessibilityAction32;
            } else {
                accessibilityAction6 = null;
            }
            new a(accessibilityAction6, 16908347, null, null, null);
            if (i4 >= 29) {
                accessibilityAction7 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_UP;
            } else {
                accessibilityAction7 = null;
            }
            new a(accessibilityAction7, 16908358, null, null, null);
            if (i4 >= 29) {
                accessibilityAction31 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_DOWN;
                accessibilityAction8 = accessibilityAction31;
            } else {
                accessibilityAction8 = null;
            }
            new a(accessibilityAction8, 16908359, null, null, null);
            if (i4 >= 29) {
                accessibilityAction9 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_LEFT;
            } else {
                accessibilityAction9 = null;
            }
            new a(accessibilityAction9, 16908360, null, null, null);
            if (i4 >= 29) {
                accessibilityAction30 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_RIGHT;
                accessibilityAction10 = accessibilityAction30;
            } else {
                accessibilityAction10 = null;
            }
            new a(accessibilityAction10, 16908361, null, null, null);
            if (i4 >= 23) {
                accessibilityAction29 = AccessibilityNodeInfo.AccessibilityAction.ACTION_CONTEXT_CLICK;
                accessibilityAction11 = accessibilityAction29;
            } else {
                accessibilityAction11 = null;
            }
            new a(accessibilityAction11, 16908348, null, null, null);
            if (i4 >= 24) {
                accessibilityAction28 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS;
                accessibilityAction12 = accessibilityAction28;
            } else {
                accessibilityAction12 = null;
            }
            new a(accessibilityAction12, 16908349, null, null, p.f.class);
            if (i4 >= 26) {
                accessibilityAction13 = AccessibilityNodeInfo.AccessibilityAction.ACTION_MOVE_WINDOW;
            } else {
                accessibilityAction13 = null;
            }
            new a(accessibilityAction13, 16908354, null, null, p.d.class);
            if (i4 >= 28) {
                accessibilityAction27 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_TOOLTIP;
                accessibilityAction14 = accessibilityAction27;
            } else {
                accessibilityAction14 = null;
            }
            new a(accessibilityAction14, 16908356, null, null, null);
            if (i4 >= 28) {
                accessibilityAction26 = AccessibilityNodeInfo.AccessibilityAction.ACTION_HIDE_TOOLTIP;
                accessibilityAction15 = accessibilityAction26;
            } else {
                accessibilityAction15 = null;
            }
            new a(accessibilityAction15, 16908357, null, null, null);
            if (i4 >= 30) {
                accessibilityAction16 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PRESS_AND_HOLD;
            } else {
                accessibilityAction16 = null;
            }
            new a(accessibilityAction16, 16908362, null, null, null);
            if (i4 >= 30) {
                accessibilityAction25 = AccessibilityNodeInfo.AccessibilityAction.ACTION_IME_ENTER;
                accessibilityAction17 = accessibilityAction25;
            } else {
                accessibilityAction17 = null;
            }
            new a(accessibilityAction17, 16908372, null, null, null);
            if (i4 >= 32) {
                accessibilityAction18 = AccessibilityNodeInfo.AccessibilityAction.ACTION_DRAG_START;
            } else {
                accessibilityAction18 = null;
            }
            new a(accessibilityAction18, 16908373, null, null, null);
            if (i4 >= 32) {
                accessibilityAction24 = AccessibilityNodeInfo.AccessibilityAction.ACTION_DRAG_DROP;
                accessibilityAction19 = accessibilityAction24;
            } else {
                accessibilityAction19 = null;
            }
            new a(accessibilityAction19, 16908374, null, null, null);
            if (i4 >= 32) {
                accessibilityAction23 = AccessibilityNodeInfo.AccessibilityAction.ACTION_DRAG_CANCEL;
                accessibilityAction20 = accessibilityAction23;
            } else {
                accessibilityAction20 = null;
            }
            new a(accessibilityAction20, 16908375, null, null, null);
            if (i4 >= 33) {
                accessibilityAction22 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_TEXT_SUGGESTIONS;
                accessibilityAction21 = accessibilityAction22;
            } else {
                accessibilityAction21 = null;
            }
            new a(accessibilityAction21, 16908376, null, null, null);
            if (i4 >= 34) {
                accessibilityAction37 = d.a();
            }
            new a(accessibilityAction37, 16908382, null, null, null);
        }

        public a(int i4) {
            this(null, i4, null, null, null);
        }

        public final int a() {
            return ((AccessibilityNodeInfo.AccessibilityAction) this.f1753a).getId();
        }

        public final boolean equals(Object obj) {
            if (obj == null || !(obj instanceof a)) {
                return false;
            }
            Object obj2 = ((a) obj).f1753a;
            Object obj3 = this.f1753a;
            if (obj3 == null) {
                if (obj2 != null) {
                    return false;
                }
                return true;
            } else if (!obj3.equals(obj2)) {
                return false;
            } else {
                return true;
            }
        }

        public final int hashCode() {
            Object obj = this.f1753a;
            if (obj != null) {
                return obj.hashCode();
            }
            return 0;
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder("AccessibilityActionCompat: ");
            String d4 = l.d(this.f1754b);
            if (d4.equals("ACTION_UNKNOWN")) {
                Object obj = this.f1753a;
                if (((AccessibilityNodeInfo.AccessibilityAction) obj).getLabel() != null) {
                    d4 = ((AccessibilityNodeInfo.AccessibilityAction) obj).getLabel().toString();
                }
            }
            sb.append(d4);
            return sb.toString();
        }

        public a(int i4, Class cls) {
            this(null, i4, null, null, cls);
        }

        public a(Object obj, int i4, String str, p pVar, Class cls) {
            this.f1754b = i4;
            this.f1756d = pVar;
            if (obj == null) {
                this.f1753a = new AccessibilityNodeInfo.AccessibilityAction(i4, str);
            } else {
                this.f1753a = obj;
            }
            this.f1755c = cls;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class b {
        public static Object a(int i4, float f, float f4, float f5) {
            return new AccessibilityNodeInfo.RangeInfo(i4, f, f4, f5);
        }

        public static CharSequence b(AccessibilityNodeInfo accessibilityNodeInfo) {
            return accessibilityNodeInfo.getStateDescription();
        }

        public static void c(AccessibilityNodeInfo accessibilityNodeInfo, CharSequence charSequence) {
            accessibilityNodeInfo.setStateDescription(charSequence);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class c {
        public static e a(boolean z4, int i4, int i5, int i6, int i7, boolean z5, String str, String str2) {
            return new e(new AccessibilityNodeInfo.CollectionItemInfo.Builder().setHeading(z4).setColumnIndex(i4).setRowIndex(i5).setColumnSpan(i6).setRowSpan(i7).setSelected(z5).setRowTitle(str).setColumnTitle(str2).build());
        }

        public static l b(AccessibilityNodeInfo accessibilityNodeInfo, int i4, int i5) {
            AccessibilityNodeInfo child = accessibilityNodeInfo.getChild(i4, i5);
            if (child != null) {
                return new l(child, 0);
            }
            return null;
        }

        public static String c(Object obj) {
            return ((AccessibilityNodeInfo.CollectionItemInfo) obj).getColumnTitle();
        }

        public static String d(Object obj) {
            return ((AccessibilityNodeInfo.CollectionItemInfo) obj).getRowTitle();
        }

        public static AccessibilityNodeInfo.ExtraRenderingInfo e(AccessibilityNodeInfo accessibilityNodeInfo) {
            return accessibilityNodeInfo.getExtraRenderingInfo();
        }

        public static l f(AccessibilityNodeInfo accessibilityNodeInfo, int i4) {
            AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent(i4);
            if (parent != null) {
                return new l(parent, 0);
            }
            return null;
        }

        public static String g(AccessibilityNodeInfo accessibilityNodeInfo) {
            return accessibilityNodeInfo.getUniqueId();
        }

        public static boolean h(AccessibilityNodeInfo accessibilityNodeInfo) {
            return accessibilityNodeInfo.isTextSelectable();
        }

        public static void i(AccessibilityNodeInfo accessibilityNodeInfo, boolean z4) {
            accessibilityNodeInfo.setTextSelectable(z4);
        }

        public static void j(AccessibilityNodeInfo accessibilityNodeInfo, String str) {
            accessibilityNodeInfo.setUniqueId(str);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class d {
        public static AccessibilityNodeInfo.AccessibilityAction a() {
            return AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_IN_DIRECTION;
        }

        public static void b(AccessibilityNodeInfo accessibilityNodeInfo, Rect rect) {
            accessibilityNodeInfo.getBoundsInWindow(rect);
        }

        public static CharSequence c(AccessibilityNodeInfo accessibilityNodeInfo) {
            return accessibilityNodeInfo.getContainerTitle();
        }

        public static long d(AccessibilityNodeInfo accessibilityNodeInfo) {
            return n.a(accessibilityNodeInfo).toMillis();
        }

        public static boolean e(AccessibilityNodeInfo accessibilityNodeInfo) {
            return accessibilityNodeInfo.hasRequestInitialAccessibilityFocus();
        }

        public static boolean f(AccessibilityNodeInfo accessibilityNodeInfo) {
            return accessibilityNodeInfo.isAccessibilityDataSensitive();
        }

        public static void g(AccessibilityNodeInfo accessibilityNodeInfo, boolean z4) {
            accessibilityNodeInfo.setAccessibilityDataSensitive(z4);
        }

        public static void h(AccessibilityNodeInfo accessibilityNodeInfo, Rect rect) {
            accessibilityNodeInfo.setBoundsInWindow(rect);
        }

        public static void i(AccessibilityNodeInfo accessibilityNodeInfo, CharSequence charSequence) {
            accessibilityNodeInfo.setContainerTitle(charSequence);
        }

        public static void j(AccessibilityNodeInfo accessibilityNodeInfo, long j4) {
            m.a(accessibilityNodeInfo, Duration.ofMillis(j4));
        }

        public static void k(AccessibilityNodeInfo accessibilityNodeInfo, View view, boolean z4) {
            accessibilityNodeInfo.setQueryFromAppProcessEnabled(view, z4);
        }

        public static void l(AccessibilityNodeInfo accessibilityNodeInfo, boolean z4) {
            accessibilityNodeInfo.setRequestInitialAccessibilityFocus(z4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class e {

        /* renamed from: a  reason: collision with root package name */
        public final Object f1757a;

        public e(AccessibilityNodeInfo.CollectionItemInfo collectionItemInfo) {
            this.f1757a = collectionItemInfo;
        }

        public static e a(boolean z4, int i4, int i5, int i6, int i7) {
            return new e(AccessibilityNodeInfo.CollectionItemInfo.obtain(i4, i5, i6, i7, false, z4));
        }
    }

    @Deprecated
    public l(AccessibilityNodeInfo accessibilityNodeInfo, int i4) {
        this.f1743a = accessibilityNodeInfo;
    }

    public static String d(int i4) {
        if (i4 != 1) {
            if (i4 != 2) {
                switch (i4) {
                    case 4:
                        return "ACTION_SELECT";
                    case 8:
                        return "ACTION_CLEAR_SELECTION";
                    case 16:
                        return "ACTION_CLICK";
                    case 32:
                        return "ACTION_LONG_CLICK";
                    case 64:
                        return "ACTION_ACCESSIBILITY_FOCUS";
                    case 128:
                        return "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
                    case 256:
                        return "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
                    case 512:
                        return "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
                    case 1024:
                        return "ACTION_NEXT_HTML_ELEMENT";
                    case 2048:
                        return "ACTION_PREVIOUS_HTML_ELEMENT";
                    case 4096:
                        return "ACTION_SCROLL_FORWARD";
                    case 8192:
                        return "ACTION_SCROLL_BACKWARD";
                    case 16384:
                        return "ACTION_COPY";
                    case 32768:
                        return "ACTION_PASTE";
                    case 65536:
                        return "ACTION_CUT";
                    case 131072:
                        return "ACTION_SET_SELECTION";
                    case 262144:
                        return "ACTION_EXPAND";
                    case 524288:
                        return "ACTION_COLLAPSE";
                    case 2097152:
                        return "ACTION_SET_TEXT";
                    case 16908354:
                        return "ACTION_MOVE_WINDOW";
                    case 16908382:
                        return "ACTION_SCROLL_IN_DIRECTION";
                    default:
                        switch (i4) {
                            case 16908342:
                                return "ACTION_SHOW_ON_SCREEN";
                            case 16908343:
                                return "ACTION_SCROLL_TO_POSITION";
                            case 16908344:
                                return "ACTION_SCROLL_UP";
                            case 16908345:
                                return "ACTION_SCROLL_LEFT";
                            case 16908346:
                                return "ACTION_SCROLL_DOWN";
                            case 16908347:
                                return "ACTION_SCROLL_RIGHT";
                            case 16908348:
                                return "ACTION_CONTEXT_CLICK";
                            case 16908349:
                                return "ACTION_SET_PROGRESS";
                            default:
                                switch (i4) {
                                    case 16908356:
                                        return "ACTION_SHOW_TOOLTIP";
                                    case 16908357:
                                        return "ACTION_HIDE_TOOLTIP";
                                    case 16908358:
                                        return "ACTION_PAGE_UP";
                                    case 16908359:
                                        return "ACTION_PAGE_DOWN";
                                    case 16908360:
                                        return "ACTION_PAGE_LEFT";
                                    case 16908361:
                                        return "ACTION_PAGE_RIGHT";
                                    case 16908362:
                                        return "ACTION_PRESS_AND_HOLD";
                                    default:
                                        switch (i4) {
                                            case 16908372:
                                                return "ACTION_IME_ENTER";
                                            case 16908373:
                                                return "ACTION_DRAG_START";
                                            case 16908374:
                                                return "ACTION_DRAG_DROP";
                                            case 16908375:
                                                return "ACTION_DRAG_CANCEL";
                                            default:
                                                return "ACTION_UNKNOWN";
                                        }
                                }
                        }
                }
            }
            return "ACTION_CLEAR_FOCUS";
        }
        return "ACTION_FOCUS";
    }

    public final void a(int i4) {
        this.f1743a.addAction(i4);
    }

    public final void b(a aVar) {
        this.f1743a.addAction((AccessibilityNodeInfo.AccessibilityAction) aVar.f1753a);
    }

    public final ArrayList c(String str) {
        AccessibilityNodeInfo accessibilityNodeInfo = this.f1743a;
        ArrayList<Integer> integerArrayList = accessibilityNodeInfo.getExtras().getIntegerArrayList(str);
        if (integerArrayList == null) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            accessibilityNodeInfo.getExtras().putIntegerArrayList(str, arrayList);
            return arrayList;
        }
        return integerArrayList;
    }

    public final boolean e(int i4) {
        Bundle extras = this.f1743a.getExtras();
        if (extras == null || (extras.getInt("androidx.view.accessibility.AccessibilityNodeInfoCompat.BOOLEAN_PROPERTY_KEY", 0) & i4) != i4) {
            return false;
        }
        return true;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof l)) {
            return false;
        }
        l lVar = (l) obj;
        AccessibilityNodeInfo accessibilityNodeInfo = lVar.f1743a;
        AccessibilityNodeInfo accessibilityNodeInfo2 = this.f1743a;
        if (accessibilityNodeInfo2 == null) {
            if (accessibilityNodeInfo != null) {
                return false;
            }
        } else if (!accessibilityNodeInfo2.equals(accessibilityNodeInfo)) {
            return false;
        }
        if (this.f1745c == lVar.f1745c && this.f1744b == lVar.f1744b) {
            return true;
        }
        return false;
    }

    @Deprecated
    public final void f(Rect rect) {
        this.f1743a.getBoundsInParent(rect);
    }

    public final CharSequence g() {
        boolean z4 = !c("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY").isEmpty();
        AccessibilityNodeInfo accessibilityNodeInfo = this.f1743a;
        if (z4) {
            ArrayList c4 = c("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY");
            ArrayList c5 = c("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_END_KEY");
            ArrayList c6 = c("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_FLAGS_KEY");
            ArrayList c7 = c("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ID_KEY");
            SpannableString spannableString = new SpannableString(TextUtils.substring(accessibilityNodeInfo.getText(), 0, accessibilityNodeInfo.getText().length()));
            for (int i4 = 0; i4 < c4.size(); i4++) {
                spannableString.setSpan(new N.a(((Integer) c7.get(i4)).intValue(), this, accessibilityNodeInfo.getExtras().getInt("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ACTION_ID_KEY")), ((Integer) c4.get(i4)).intValue(), ((Integer) c5.get(i4)).intValue(), ((Integer) c6.get(i4)).intValue());
            }
            return spannableString;
        }
        return accessibilityNodeInfo.getText();
    }

    public final void h(int i4, boolean z4) {
        Bundle extras = this.f1743a.getExtras();
        if (extras != null) {
            int i5 = extras.getInt("androidx.view.accessibility.AccessibilityNodeInfoCompat.BOOLEAN_PROPERTY_KEY", 0) & (~i4);
            if (!z4) {
                i4 = 0;
            }
            extras.putInt("androidx.view.accessibility.AccessibilityNodeInfoCompat.BOOLEAN_PROPERTY_KEY", i4 | i5);
        }
    }

    public final int hashCode() {
        AccessibilityNodeInfo accessibilityNodeInfo = this.f1743a;
        if (accessibilityNodeInfo == null) {
            return 0;
        }
        return accessibilityNodeInfo.hashCode();
    }

    public final void i(e eVar) {
        this.f1743a.setCollectionItemInfo((AccessibilityNodeInfo.CollectionItemInfo) eVar.f1757a);
    }

    public final void j(String str) {
        int i4 = Build.VERSION.SDK_INT;
        AccessibilityNodeInfo accessibilityNodeInfo = this.f1743a;
        if (i4 >= 26) {
            accessibilityNodeInfo.setHintText(str);
        } else {
            accessibilityNodeInfo.getExtras().putCharSequence("androidx.view.accessibility.AccessibilityNodeInfoCompat.HINT_TEXT_KEY", str);
        }
    }

    public final void k(boolean z4) {
        this.f1743a.setScrollable(z4);
    }

    public final void l(CharSequence charSequence) {
        this.f1743a.setText(charSequence);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v3, types: [java.util.List] */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.util.List] */
    /* JADX WARN: Type inference failed for: r2v5, types: [java.util.ArrayList] */
    public final String toString() {
        CharSequence charSequence;
        CharSequence charSequence2;
        String string;
        boolean z4;
        CharSequence charSequence3;
        boolean z5;
        boolean e4;
        boolean e5;
        ?? emptyList;
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        Rect rect = new Rect();
        f(rect);
        sb.append("; boundsInParent: " + rect);
        AccessibilityNodeInfo accessibilityNodeInfo = this.f1743a;
        accessibilityNodeInfo.getBoundsInScreen(rect);
        sb.append("; boundsInScreen: " + rect);
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 34) {
            d.b(accessibilityNodeInfo, rect);
        } else {
            Rect rect2 = (Rect) accessibilityNodeInfo.getExtras().getParcelable("androidx.view.accessibility.AccessibilityNodeInfoCompat.BOUNDS_IN_WINDOW_KEY");
            if (rect2 != null) {
                rect.set(rect2.left, rect2.top, rect2.right, rect2.bottom);
            }
        }
        sb.append("; boundsInWindow: " + rect);
        sb.append("; packageName: ");
        sb.append(accessibilityNodeInfo.getPackageName());
        sb.append("; className: ");
        sb.append(accessibilityNodeInfo.getClassName());
        sb.append("; text: ");
        sb.append(g());
        sb.append("; error: ");
        sb.append(accessibilityNodeInfo.getError());
        sb.append("; maxTextLength: ");
        sb.append(accessibilityNodeInfo.getMaxTextLength());
        sb.append("; stateDescription: ");
        if (i4 >= 30) {
            charSequence = b.b(accessibilityNodeInfo);
        } else {
            charSequence = accessibilityNodeInfo.getExtras().getCharSequence("androidx.view.accessibility.AccessibilityNodeInfoCompat.STATE_DESCRIPTION_KEY");
        }
        sb.append(charSequence);
        sb.append("; contentDescription: ");
        sb.append(accessibilityNodeInfo.getContentDescription());
        sb.append("; tooltipText: ");
        if (i4 >= 28) {
            charSequence2 = accessibilityNodeInfo.getTooltipText();
        } else {
            charSequence2 = accessibilityNodeInfo.getExtras().getCharSequence("androidx.view.accessibility.AccessibilityNodeInfoCompat.TOOLTIP_TEXT_KEY");
        }
        sb.append(charSequence2);
        sb.append("; viewIdResName: ");
        sb.append(accessibilityNodeInfo.getViewIdResourceName());
        sb.append("; uniqueId: ");
        if (i4 >= 33) {
            string = c.g(accessibilityNodeInfo);
        } else {
            string = accessibilityNodeInfo.getExtras().getString("androidx.view.accessibility.AccessibilityNodeInfoCompat.UNIQUE_ID_KEY");
        }
        sb.append(string);
        sb.append("; checkable: ");
        sb.append(accessibilityNodeInfo.isCheckable());
        sb.append("; checked: ");
        sb.append(accessibilityNodeInfo.isChecked());
        sb.append("; focusable: ");
        sb.append(accessibilityNodeInfo.isFocusable());
        sb.append("; focused: ");
        sb.append(accessibilityNodeInfo.isFocused());
        sb.append("; selected: ");
        sb.append(accessibilityNodeInfo.isSelected());
        sb.append("; clickable: ");
        sb.append(accessibilityNodeInfo.isClickable());
        sb.append("; longClickable: ");
        sb.append(accessibilityNodeInfo.isLongClickable());
        sb.append("; contextClickable: ");
        if (i4 >= 23) {
            z4 = accessibilityNodeInfo.isContextClickable();
        } else {
            z4 = false;
        }
        sb.append(z4);
        sb.append("; enabled: ");
        sb.append(accessibilityNodeInfo.isEnabled());
        sb.append("; password: ");
        sb.append(accessibilityNodeInfo.isPassword());
        sb.append("; scrollable: " + accessibilityNodeInfo.isScrollable());
        sb.append("; containerTitle: ");
        if (i4 >= 34) {
            charSequence3 = d.c(accessibilityNodeInfo);
        } else {
            charSequence3 = accessibilityNodeInfo.getExtras().getCharSequence("androidx.view.accessibility.AccessibilityNodeInfoCompat.CONTAINER_TITLE_KEY");
        }
        sb.append(charSequence3);
        sb.append("; granularScrollingSupported: ");
        sb.append(e(67108864));
        sb.append("; importantForAccessibility: ");
        if (i4 >= 24) {
            z5 = accessibilityNodeInfo.isImportantForAccessibility();
        } else {
            z5 = true;
        }
        sb.append(z5);
        sb.append("; visible: ");
        sb.append(accessibilityNodeInfo.isVisibleToUser());
        sb.append("; isTextSelectable: ");
        if (i4 >= 33) {
            e4 = c.h(accessibilityNodeInfo);
        } else {
            e4 = e(8388608);
        }
        sb.append(e4);
        sb.append("; accessibilityDataSensitive: ");
        if (i4 >= 34) {
            e5 = d.f(accessibilityNodeInfo);
        } else {
            e5 = e(64);
        }
        sb.append(e5);
        sb.append("; [");
        List<AccessibilityNodeInfo.AccessibilityAction> actionList = accessibilityNodeInfo.getActionList();
        if (actionList != null) {
            emptyList = new ArrayList();
            int size = actionList.size();
            for (int i5 = 0; i5 < size; i5++) {
                emptyList.add(new a(actionList.get(i5), 0, null, null, null));
            }
        } else {
            emptyList = Collections.emptyList();
        }
        for (int i6 = 0; i6 < emptyList.size(); i6++) {
            a aVar = (a) emptyList.get(i6);
            String d4 = d(aVar.a());
            if (d4.equals("ACTION_UNKNOWN")) {
                Object obj = aVar.f1753a;
                if (((AccessibilityNodeInfo.AccessibilityAction) obj).getLabel() != null) {
                    d4 = ((AccessibilityNodeInfo.AccessibilityAction) obj).getLabel().toString();
                }
            }
            sb.append(d4);
            if (i6 != emptyList.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public l(AccessibilityNodeInfo accessibilityNodeInfo) {
        this.f1743a = accessibilityNodeInfo;
    }
}
