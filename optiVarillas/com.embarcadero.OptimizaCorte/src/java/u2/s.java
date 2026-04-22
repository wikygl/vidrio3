package U2;

import F.a;
import M.O;
import M.V;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;
import l.C0694c;
import l.L;
import q2.C0771a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class s extends C0694c {

    /* renamed from: n  reason: collision with root package name */
    public final L f2461n;

    /* renamed from: o  reason: collision with root package name */
    public final AccessibilityManager f2462o;

    /* renamed from: p  reason: collision with root package name */
    public final Rect f2463p;

    /* renamed from: q  reason: collision with root package name */
    public final int f2464q;

    /* renamed from: r  reason: collision with root package name */
    public final float f2465r;

    /* renamed from: s  reason: collision with root package name */
    public ColorStateList f2466s;

    /* renamed from: t  reason: collision with root package name */
    public int f2467t;

    /* renamed from: u  reason: collision with root package name */
    public ColorStateList f2468u;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public class a<T> extends ArrayAdapter<String> {

        /* renamed from: j  reason: collision with root package name */
        public ColorStateList f2469j;

        /* renamed from: k  reason: collision with root package name */
        public ColorStateList f2470k;

        public a(Context context, int i4, String[] strArr) {
            super(context, i4, strArr);
            b();
        }

        public final void b() {
            boolean z4;
            ColorStateList colorStateList;
            s sVar = s.this;
            ColorStateList colorStateList2 = sVar.f2468u;
            if (colorStateList2 != null) {
                z4 = true;
            } else {
                z4 = false;
            }
            ColorStateList colorStateList3 = null;
            if (!z4) {
                colorStateList = null;
            } else {
                int[] iArr = {16842919};
                colorStateList = new ColorStateList(new int[][]{iArr, new int[0]}, new int[]{colorStateList2.getColorForState(iArr, 0), 0});
            }
            this.f2470k = colorStateList;
            if (sVar.f2467t != 0 && sVar.f2468u != null) {
                int[] iArr2 = {16843623, -16842919};
                int[] iArr3 = {16842913, -16842919};
                colorStateList3 = new ColorStateList(new int[][]{iArr3, iArr2, new int[0]}, new int[]{E.a.b(sVar.f2468u.getColorForState(iArr3, 0), sVar.f2467t), E.a.b(sVar.f2468u.getColorForState(iArr2, 0), sVar.f2467t), sVar.f2467t});
            }
            this.f2469j = colorStateList3;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public final View getView(int i4, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i4, view, viewGroup);
            if (view2 instanceof TextView) {
                TextView textView = (TextView) view2;
                s sVar = s.this;
                RippleDrawable rippleDrawable = null;
                if (sVar.getText().toString().contentEquals(textView.getText()) && sVar.f2467t != 0) {
                    ColorDrawable colorDrawable = new ColorDrawable(sVar.f2467t);
                    if (this.f2470k != null) {
                        a.C0006a.h(colorDrawable, this.f2469j);
                        rippleDrawable = new RippleDrawable(this.f2470k, colorDrawable, null);
                    } else {
                        rippleDrawable = colorDrawable;
                    }
                }
                WeakHashMap<View, V> weakHashMap = O.f1526a;
                textView.setBackground(rippleDrawable);
            }
            return view2;
        }
    }

    public s(Context context, AttributeSet attributeSet) {
        super(W2.a.a(context, attributeSet, 2130903101, 0), attributeSet, 0);
        this.f2463p = new Rect();
        Context context2 = getContext();
        TypedArray d4 = H2.p.d(context2, attributeSet, C0771a.f5617k, 2130903101, 2131886842, new int[0]);
        if (d4.hasValue(0) && d4.getInt(0, 0) == 0) {
            setKeyListener(null);
        }
        this.f2464q = d4.getResourceId(3, 2131427422);
        this.f2465r = d4.getDimensionPixelOffset(1, 2131100335);
        if (d4.hasValue(2)) {
            this.f2466s = ColorStateList.valueOf(d4.getColor(2, 0));
        }
        this.f2467t = d4.getColor(4, 0);
        this.f2468u = L2.c.a(context2, d4, 5);
        this.f2462o = (AccessibilityManager) context2.getSystemService("accessibility");
        L l2 = new L(context2, null, 2130903714, 0);
        this.f2461n = l2;
        l2.f5021H = true;
        l2.f5022I.setFocusable(true);
        l2.f5037x = this;
        l2.f5022I.setInputMethodMode(2);
        l2.p(getAdapter());
        l2.f5038y = new r(this);
        if (d4.hasValue(6)) {
            setSimpleItems(d4.getResourceId(6, 0));
        }
        d4.recycle();
    }

    public static void a(s sVar, Object obj) {
        sVar.setText(sVar.convertSelectionToString(obj), false);
    }

    public final TextInputLayout b() {
        for (ViewParent parent = getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof TextInputLayout) {
                return (TextInputLayout) parent;
            }
        }
        return null;
    }

    public final boolean c() {
        List<AccessibilityServiceInfo> enabledAccessibilityServiceList;
        AccessibilityManager accessibilityManager = this.f2462o;
        if (accessibilityManager == null || !accessibilityManager.isTouchExplorationEnabled()) {
            if (accessibilityManager != null && accessibilityManager.isEnabled() && (enabledAccessibilityServiceList = accessibilityManager.getEnabledAccessibilityServiceList(16)) != null) {
                for (AccessibilityServiceInfo accessibilityServiceInfo : enabledAccessibilityServiceList) {
                    if (accessibilityServiceInfo.getSettingsActivityName() == null || !accessibilityServiceInfo.getSettingsActivityName().contains("SwitchAccess")) {
                    }
                }
            }
            return false;
        }
        return true;
    }

    @Override // android.widget.AutoCompleteTextView
    public final void dismissDropDown() {
        if (c()) {
            this.f2461n.dismiss();
        } else {
            super.dismissDropDown();
        }
    }

    public ColorStateList getDropDownBackgroundTintList() {
        return this.f2466s;
    }

    @Override // android.widget.TextView
    public CharSequence getHint() {
        TextInputLayout b4 = b();
        if (b4 != null && b4.N) {
            return b4.getHint();
        }
        return super.getHint();
    }

    public float getPopupElevation() {
        return this.f2465r;
    }

    public int getSimpleItemSelectedColor() {
        return this.f2467t;
    }

    public ColorStateList getSimpleItemSelectedRippleColor() {
        return this.f2468u;
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
    public final void onAttachedToWindow() {
        String str;
        super.onAttachedToWindow();
        TextInputLayout b4 = b();
        if (b4 != null && b4.N && super.getHint() == null) {
            String str2 = Build.MANUFACTURER;
            if (str2 == null) {
                str = "";
            } else {
                str = str2.toLowerCase(Locale.ENGLISH);
            }
            if (str.equals("meizu")) {
                setHint("");
            }
        }
    }

    @Override // android.widget.AutoCompleteTextView, android.view.View
    public final void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.f2461n.dismiss();
    }

    @Override // android.widget.TextView, android.view.View
    public final void onMeasure(int i4, int i5) {
        int selectedItemPosition;
        super.onMeasure(i4, i5);
        if (View.MeasureSpec.getMode(i4) == Integer.MIN_VALUE) {
            int measuredWidth = getMeasuredWidth();
            ListAdapter adapter = getAdapter();
            TextInputLayout b4 = b();
            int i6 = 0;
            if (adapter != null && b4 != null) {
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
                int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
                L l2 = this.f2461n;
                if (!l2.f5022I.isShowing()) {
                    selectedItemPosition = -1;
                } else {
                    selectedItemPosition = l2.f5025l.getSelectedItemPosition();
                }
                int min = Math.min(adapter.getCount(), Math.max(0, selectedItemPosition) + 15);
                View view = null;
                int i7 = 0;
                for (int max = Math.max(0, min - 15); max < min; max++) {
                    int itemViewType = adapter.getItemViewType(max);
                    if (itemViewType != i6) {
                        view = null;
                        i6 = itemViewType;
                    }
                    view = adapter.getView(max, view, b4);
                    if (view.getLayoutParams() == null) {
                        view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
                    }
                    view.measure(makeMeasureSpec, makeMeasureSpec2);
                    i7 = Math.max(i7, view.getMeasuredWidth());
                }
                Drawable background = l2.f5022I.getBackground();
                if (background != null) {
                    Rect rect = this.f2463p;
                    background.getPadding(rect);
                    i7 += rect.left + rect.right;
                }
                i6 = b4.getEndIconView().getMeasuredWidth() + i7;
            }
            setMeasuredDimension(Math.min(Math.max(measuredWidth, i6), View.MeasureSpec.getSize(i4)), getMeasuredHeight());
        }
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
    public final void onWindowFocusChanged(boolean z4) {
        if (c()) {
            return;
        }
        super.onWindowFocusChanged(z4);
    }

    @Override // android.widget.AutoCompleteTextView
    public <T extends ListAdapter & Filterable> void setAdapter(T t3) {
        super.setAdapter(t3);
        this.f2461n.p(getAdapter());
    }

    @Override // android.widget.AutoCompleteTextView
    public void setDropDownBackgroundDrawable(Drawable drawable) {
        super.setDropDownBackgroundDrawable(drawable);
        L l2 = this.f2461n;
        if (l2 != null) {
            l2.h(drawable);
        }
    }

    public void setDropDownBackgroundTint(int i4) {
        setDropDownBackgroundTintList(ColorStateList.valueOf(i4));
    }

    public void setDropDownBackgroundTintList(ColorStateList colorStateList) {
        this.f2466s = colorStateList;
        Drawable dropDownBackground = getDropDownBackground();
        if (dropDownBackground instanceof P2.f) {
            ((P2.f) dropDownBackground).l(this.f2466s);
        }
    }

    @Override // android.widget.AutoCompleteTextView
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        super.setOnItemSelectedListener(onItemSelectedListener);
        this.f2461n.f5039z = getOnItemSelectedListener();
    }

    @Override // android.widget.TextView
    public void setRawInputType(int i4) {
        super.setRawInputType(i4);
        TextInputLayout b4 = b();
        if (b4 != null) {
            b4.s();
        }
    }

    public void setSimpleItemSelectedColor(int i4) {
        this.f2467t = i4;
        if (getAdapter() instanceof a) {
            ((a) getAdapter()).b();
        }
    }

    public void setSimpleItemSelectedRippleColor(ColorStateList colorStateList) {
        this.f2468u = colorStateList;
        if (getAdapter() instanceof a) {
            ((a) getAdapter()).b();
        }
    }

    public void setSimpleItems(int i4) {
        setSimpleItems(getResources().getStringArray(i4));
    }

    @Override // android.widget.AutoCompleteTextView
    public final void showDropDown() {
        if (c()) {
            this.f2461n.a();
        } else {
            super.showDropDown();
        }
    }

    public void setSimpleItems(String[] strArr) {
        setAdapter(new a(getContext(), this.f2464q, strArr));
    }
}
