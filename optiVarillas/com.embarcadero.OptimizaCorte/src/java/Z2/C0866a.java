package z2;

import D2.f;
import F.a;
import H2.u;
import S.b;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.autofill.AutofillManager;
import android.widget.CompoundButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import l.C0697f;
import v0.AbstractC0830c;
import v0.C0829b;
import v0.d;

/* renamed from: z2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0866a extends C0697f {

    /* renamed from: H  reason: collision with root package name */
    public static final int[] f6601H = {2130903990};

    /* renamed from: I  reason: collision with root package name */
    public static final int[] f6602I = {2130903989};

    /* renamed from: J  reason: collision with root package name */
    public static final int[][] f6603J = {new int[]{16842910, 2130903989}, new int[]{16842910, 16842912}, new int[]{16842910, -16842912}, new int[]{-16842910, 16842912}, new int[]{-16842910, -16842912}};
    @SuppressLint({"DiscouragedApi"})

    /* renamed from: K  reason: collision with root package name */
    public static final int f6604K = Resources.getSystem().getIdentifier("btn_check_material_anim", "drawable", "android");

    /* renamed from: A  reason: collision with root package name */
    public int f6605A;

    /* renamed from: B  reason: collision with root package name */
    public int[] f6606B;

    /* renamed from: C  reason: collision with root package name */
    public boolean f6607C;

    /* renamed from: D  reason: collision with root package name */
    public CharSequence f6608D;

    /* renamed from: E  reason: collision with root package name */
    public CompoundButton.OnCheckedChangeListener f6609E;

    /* renamed from: F  reason: collision with root package name */
    public final v0.d f6610F;

    /* renamed from: G  reason: collision with root package name */
    public final C0082a f6611G;

    /* renamed from: n  reason: collision with root package name */
    public final LinkedHashSet<c> f6612n;

    /* renamed from: o  reason: collision with root package name */
    public final LinkedHashSet<b> f6613o;

    /* renamed from: p  reason: collision with root package name */
    public ColorStateList f6614p;

    /* renamed from: q  reason: collision with root package name */
    public boolean f6615q;

    /* renamed from: r  reason: collision with root package name */
    public boolean f6616r;

    /* renamed from: s  reason: collision with root package name */
    public boolean f6617s;

    /* renamed from: t  reason: collision with root package name */
    public CharSequence f6618t;

    /* renamed from: u  reason: collision with root package name */
    public Drawable f6619u;

    /* renamed from: v  reason: collision with root package name */
    public Drawable f6620v;

    /* renamed from: w  reason: collision with root package name */
    public boolean f6621w;

    /* renamed from: x  reason: collision with root package name */
    public ColorStateList f6622x;

    /* renamed from: y  reason: collision with root package name */
    public ColorStateList f6623y;

    /* renamed from: z  reason: collision with root package name */
    public PorterDuff.Mode f6624z;

    /* renamed from: z2.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class C0082a extends AbstractC0830c {
        public C0082a() {
        }

        @Override // v0.AbstractC0830c
        public final void a(Drawable drawable) {
            ColorStateList colorStateList = C0866a.this.f6622x;
            if (colorStateList != null) {
                a.C0006a.h(drawable, colorStateList);
            }
        }

        @Override // v0.AbstractC0830c
        public final void b(Drawable drawable) {
            C0866a c0866a = C0866a.this;
            ColorStateList colorStateList = c0866a.f6622x;
            if (colorStateList != null) {
                a.C0006a.g(drawable, colorStateList.getColorForState(c0866a.f6606B, colorStateList.getDefaultColor()));
            }
        }
    }

    /* renamed from: z2.a$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface b {
        void a();
    }

    /* renamed from: z2.a$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface c {
        void a();
    }

    /* renamed from: z2.a$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class d extends View.BaseSavedState {
        public static final Parcelable.Creator<d> CREATOR = new Object();

        /* renamed from: j  reason: collision with root package name */
        public int f6626j;

        /* renamed from: z2.a$d$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
        public class C0083a implements Parcelable.Creator<d> {
            /* JADX WARN: Type inference failed for: r0v0, types: [android.view.View$BaseSavedState, z2.a$d] */
            @Override // android.os.Parcelable.Creator
            public final d createFromParcel(Parcel parcel) {
                ?? baseSavedState = new View.BaseSavedState(parcel);
                baseSavedState.f6626j = ((Integer) parcel.readValue(d.class.getClassLoader())).intValue();
                return baseSavedState;
            }

            @Override // android.os.Parcelable.Creator
            public final d[] newArray(int i4) {
                return new d[i4];
            }
        }

        public final String toString() {
            String str;
            StringBuilder sb = new StringBuilder("MaterialCheckBox.SavedState{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" CheckedState=");
            int i4 = this.f6626j;
            if (i4 != 1) {
                if (i4 != 2) {
                    str = "unchecked";
                } else {
                    str = "indeterminate";
                }
            } else {
                str = "checked";
            }
            return C.b.c(sb, str, "}");
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public final void writeToParcel(Parcel parcel, int i4) {
            super.writeToParcel(parcel, i4);
            parcel.writeValue(Integer.valueOf(this.f6626j));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x00d6  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00ff  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0143  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public C0866a(android.content.Context r14, android.util.AttributeSet r15) {
        /*
            Method dump skipped, instructions count: 337
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: z2.C0866a.<init>(android.content.Context, android.util.AttributeSet):void");
    }

    private String getButtonStateDescription() {
        int i4 = this.f6605A;
        if (i4 == 1) {
            return getResources().getString(2131820801);
        }
        if (i4 == 0) {
            return getResources().getString(2131820803);
        }
        return getResources().getString(2131820802);
    }

    private ColorStateList getMaterialThemeColorsTintList() {
        if (this.f6614p == null) {
            int d4 = B2.a.d(this, 2130903269);
            int d5 = B2.a.d(this, 2130903272);
            int d6 = B2.a.d(this, 2130903311);
            int d7 = B2.a.d(this, 2130903288);
            this.f6614p = new ColorStateList(f6603J, new int[]{B2.a.i(1.0f, d6, d5), B2.a.i(1.0f, d6, d4), B2.a.i(0.54f, d6, d7), B2.a.i(0.38f, d6, d7), B2.a.i(0.38f, d6, d7)});
        }
        return this.f6614p;
    }

    private ColorStateList getSuperButtonTintList() {
        ColorStateList colorStateList = this.f6622x;
        if (colorStateList != null) {
            return colorStateList;
        }
        if (super.getButtonTintList() != null) {
            return super.getButtonTintList();
        }
        return getSupportButtonTintList();
    }

    public final void b() {
        LayerDrawable layerDrawable;
        ColorStateList colorStateList;
        ColorStateList colorStateList2;
        R2.b bVar;
        this.f6619u = f.a(this.f6619u, this.f6622x, b.a.b(this));
        this.f6620v = f.a(this.f6620v, this.f6623y, this.f6624z);
        if (this.f6621w) {
            v0.d dVar = this.f6610F;
            if (dVar != null) {
                Drawable drawable = dVar.f6225j;
                C0082a c0082a = this.f6611G;
                if (drawable != null) {
                    AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
                    if (c0082a.f6210a == null) {
                        c0082a.f6210a = new C0829b(c0082a);
                    }
                    animatedVectorDrawable.unregisterAnimationCallback(c0082a.f6210a);
                }
                ArrayList<AbstractC0830c> arrayList = dVar.f6215n;
                d.b bVar2 = dVar.f6212k;
                if (arrayList != null && c0082a != null) {
                    arrayList.remove(c0082a);
                    if (dVar.f6215n.size() == 0 && (bVar = dVar.f6214m) != null) {
                        bVar2.f6219b.removeListener(bVar);
                        dVar.f6214m = null;
                    }
                }
                Drawable drawable2 = dVar.f6225j;
                if (drawable2 != null) {
                    AnimatedVectorDrawable animatedVectorDrawable2 = (AnimatedVectorDrawable) drawable2;
                    if (c0082a.f6210a == null) {
                        c0082a.f6210a = new C0829b(c0082a);
                    }
                    animatedVectorDrawable2.registerAnimationCallback(c0082a.f6210a);
                } else if (c0082a != null) {
                    if (dVar.f6215n == null) {
                        dVar.f6215n = new ArrayList<>();
                    }
                    if (!dVar.f6215n.contains(c0082a)) {
                        dVar.f6215n.add(c0082a);
                        if (dVar.f6214m == null) {
                            dVar.f6214m = new R2.b(1, dVar);
                        }
                        bVar2.f6219b.addListener(dVar.f6214m);
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= 24) {
                Drawable drawable3 = this.f6619u;
                if ((drawable3 instanceof AnimatedStateListDrawable) && dVar != null) {
                    ((AnimatedStateListDrawable) drawable3).addTransition(2131230863, 2131231321, dVar, false);
                    ((AnimatedStateListDrawable) this.f6619u).addTransition(2131230996, 2131231321, dVar, false);
                }
            }
        }
        Drawable drawable4 = this.f6619u;
        if (drawable4 != null && (colorStateList2 = this.f6622x) != null) {
            a.C0006a.h(drawable4, colorStateList2);
        }
        Drawable drawable5 = this.f6620v;
        if (drawable5 != null && (colorStateList = this.f6623y) != null) {
            a.C0006a.h(drawable5, colorStateList);
        }
        Drawable drawable6 = this.f6619u;
        Drawable drawable7 = this.f6620v;
        if (drawable6 == null) {
            drawable6 = drawable7;
        } else if (drawable7 != null) {
            int intrinsicWidth = drawable7.getIntrinsicWidth();
            if (intrinsicWidth == -1) {
                intrinsicWidth = drawable6.getIntrinsicWidth();
            }
            int intrinsicHeight = drawable7.getIntrinsicHeight();
            if (intrinsicHeight == -1) {
                intrinsicHeight = drawable6.getIntrinsicHeight();
            }
            if (intrinsicWidth > drawable6.getIntrinsicWidth() || intrinsicHeight > drawable6.getIntrinsicHeight()) {
                float f = intrinsicWidth / intrinsicHeight;
                if (f >= drawable6.getIntrinsicWidth() / drawable6.getIntrinsicHeight()) {
                    int intrinsicWidth2 = drawable6.getIntrinsicWidth();
                    intrinsicHeight = (int) (intrinsicWidth2 / f);
                    intrinsicWidth = intrinsicWidth2;
                } else {
                    intrinsicHeight = drawable6.getIntrinsicHeight();
                    intrinsicWidth = (int) (f * intrinsicHeight);
                }
            }
            if (Build.VERSION.SDK_INT >= 23) {
                layerDrawable = new LayerDrawable(new Drawable[]{drawable6, drawable7});
                layerDrawable.setLayerSize(1, intrinsicWidth, intrinsicHeight);
                layerDrawable.setLayerGravity(1, 17);
            } else {
                layerDrawable = new LayerDrawable(new Drawable[]{drawable6, drawable7});
                int max = Math.max((drawable6.getIntrinsicWidth() - intrinsicWidth) / 2, 0);
                int max2 = Math.max((drawable6.getIntrinsicHeight() - intrinsicHeight) / 2, 0);
                layerDrawable.setLayerInset(1, max, max2, max, max2);
            }
            drawable6 = layerDrawable;
        }
        super.setButtonDrawable(drawable6);
        refreshDrawableState();
    }

    @Override // android.widget.CompoundButton
    public Drawable getButtonDrawable() {
        return this.f6619u;
    }

    public Drawable getButtonIconDrawable() {
        return this.f6620v;
    }

    public ColorStateList getButtonIconTintList() {
        return this.f6623y;
    }

    public PorterDuff.Mode getButtonIconTintMode() {
        return this.f6624z;
    }

    @Override // android.widget.CompoundButton
    public ColorStateList getButtonTintList() {
        return this.f6622x;
    }

    public int getCheckedState() {
        return this.f6605A;
    }

    public CharSequence getErrorAccessibilityLabel() {
        return this.f6618t;
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public final boolean isChecked() {
        if (this.f6605A == 1) {
            return true;
        }
        return false;
    }

    @Override // android.widget.TextView, android.view.View
    public final void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.f6615q && this.f6622x == null && this.f6623y == null) {
            setUseMaterialThemeColors(true);
        }
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public final int[] onCreateDrawableState(int i4) {
        int[] copyOf;
        int[] onCreateDrawableState = super.onCreateDrawableState(i4 + 2);
        if (getCheckedState() == 2) {
            View.mergeDrawableStates(onCreateDrawableState, f6601H);
        }
        if (this.f6617s) {
            View.mergeDrawableStates(onCreateDrawableState, f6602I);
        }
        int i5 = 0;
        while (true) {
            if (i5 < onCreateDrawableState.length) {
                int i6 = onCreateDrawableState[i5];
                if (i6 == 16842912) {
                    copyOf = onCreateDrawableState;
                    break;
                } else if (i6 == 0) {
                    copyOf = (int[]) onCreateDrawableState.clone();
                    copyOf[i5] = 16842912;
                    break;
                } else {
                    i5++;
                }
            } else {
                copyOf = Arrays.copyOf(onCreateDrawableState, onCreateDrawableState.length + 1);
                copyOf[onCreateDrawableState.length] = 16842912;
                break;
            }
        }
        this.f6606B = copyOf;
        return onCreateDrawableState;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public final void onDraw(Canvas canvas) {
        Drawable a4;
        int i4;
        if (this.f6616r && TextUtils.isEmpty(getText()) && (a4 = S.b.a(this)) != null) {
            if (u.b(this)) {
                i4 = -1;
            } else {
                i4 = 1;
            }
            int width = ((getWidth() - a4.getIntrinsicWidth()) / 2) * i4;
            int save = canvas.save();
            canvas.translate(width, 0.0f);
            super.onDraw(canvas);
            canvas.restoreToCount(save);
            if (getBackground() != null) {
                Rect bounds = a4.getBounds();
                a.C0006a.f(getBackground(), bounds.left + width, bounds.top, bounds.right + width, bounds.bottom);
                return;
            }
            return;
        }
        super.onDraw(canvas);
    }

    @Override // android.view.View
    public final void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (accessibilityNodeInfo != null && this.f6617s) {
            accessibilityNodeInfo.setText(((Object) accessibilityNodeInfo.getText()) + ", " + ((Object) this.f6618t));
        }
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public final void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof d)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        d dVar = (d) parcelable;
        super.onRestoreInstanceState(dVar.getSuperState());
        setCheckedState(dVar.f6626j);
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [android.view.View$BaseSavedState, android.os.Parcelable, z2.a$d] */
    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public final Parcelable onSaveInstanceState() {
        ?? baseSavedState = new View.BaseSavedState(super.onSaveInstanceState());
        baseSavedState.f6626j = getCheckedState();
        return baseSavedState;
    }

    @Override // l.C0697f, android.widget.CompoundButton
    public void setButtonDrawable(int i4) {
        setButtonDrawable(B2.a.f(getContext(), i4));
    }

    public void setButtonIconDrawable(Drawable drawable) {
        this.f6620v = drawable;
        b();
    }

    public void setButtonIconDrawableResource(int i4) {
        setButtonIconDrawable(B2.a.f(getContext(), i4));
    }

    public void setButtonIconTintList(ColorStateList colorStateList) {
        if (this.f6623y == colorStateList) {
            return;
        }
        this.f6623y = colorStateList;
        b();
    }

    public void setButtonIconTintMode(PorterDuff.Mode mode) {
        if (this.f6624z == mode) {
            return;
        }
        this.f6624z = mode;
        b();
    }

    @Override // android.widget.CompoundButton
    public void setButtonTintList(ColorStateList colorStateList) {
        if (this.f6622x == colorStateList) {
            return;
        }
        this.f6622x = colorStateList;
        b();
    }

    @Override // android.widget.CompoundButton
    public void setButtonTintMode(PorterDuff.Mode mode) {
        setSupportButtonTintMode(mode);
        b();
    }

    public void setCenterIfNoTextEnabled(boolean z4) {
        this.f6616r = z4;
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean z4) {
        setCheckedState(z4 ? 1 : 0);
    }

    public void setCheckedState(int i4) {
        boolean z4;
        AutofillManager autofillManager;
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
        if (this.f6605A != i4) {
            this.f6605A = i4;
            if (i4 == 1) {
                z4 = true;
            } else {
                z4 = false;
            }
            super.setChecked(z4);
            refreshDrawableState();
            if (Build.VERSION.SDK_INT >= 30 && this.f6608D == null) {
                super.setStateDescription(getButtonStateDescription());
            }
            if (this.f6607C) {
                return;
            }
            this.f6607C = true;
            LinkedHashSet<b> linkedHashSet = this.f6613o;
            if (linkedHashSet != null) {
                Iterator<b> it = linkedHashSet.iterator();
                while (it.hasNext()) {
                    it.next().a();
                }
            }
            if (this.f6605A != 2 && (onCheckedChangeListener = this.f6609E) != null) {
                onCheckedChangeListener.onCheckedChanged(this, isChecked());
            }
            if (Build.VERSION.SDK_INT >= 26 && (autofillManager = (AutofillManager) getContext().getSystemService(AutofillManager.class)) != null) {
                autofillManager.notifyValueChanged(this);
            }
            this.f6607C = false;
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void setEnabled(boolean z4) {
        super.setEnabled(z4);
    }

    public void setErrorAccessibilityLabel(CharSequence charSequence) {
        this.f6618t = charSequence;
    }

    public void setErrorAccessibilityLabelResource(int i4) {
        CharSequence charSequence;
        if (i4 != 0) {
            charSequence = getResources().getText(i4);
        } else {
            charSequence = null;
        }
        setErrorAccessibilityLabel(charSequence);
    }

    public void setErrorShown(boolean z4) {
        if (this.f6617s == z4) {
            return;
        }
        this.f6617s = z4;
        refreshDrawableState();
        Iterator<c> it = this.f6612n.iterator();
        while (it.hasNext()) {
            it.next().a();
        }
    }

    @Override // android.widget.CompoundButton
    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.f6609E = onCheckedChangeListener;
    }

    @Override // android.widget.CompoundButton, android.view.View
    public void setStateDescription(CharSequence charSequence) {
        this.f6608D = charSequence;
        if (charSequence == null) {
            if (Build.VERSION.SDK_INT >= 30 && charSequence == null) {
                super.setStateDescription(getButtonStateDescription());
                return;
            }
            return;
        }
        super.setStateDescription(charSequence);
    }

    public void setUseMaterialThemeColors(boolean z4) {
        this.f6615q = z4;
        if (z4) {
            b.a.c(this, getMaterialThemeColorsTintList());
        } else {
            b.a.c(this, null);
        }
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public final void toggle() {
        setChecked(!isChecked());
    }

    @Override // l.C0697f, android.widget.CompoundButton
    public void setButtonDrawable(Drawable drawable) {
        this.f6619u = drawable;
        this.f6621w = false;
        b();
    }
}
