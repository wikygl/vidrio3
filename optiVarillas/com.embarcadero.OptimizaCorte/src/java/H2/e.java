package H2;

import M.O;
import M.V;
import android.view.View;
import android.view.ViewGroup;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public class e extends ViewGroup {

    /* renamed from: j  reason: collision with root package name */
    public int f1081j;

    /* renamed from: k  reason: collision with root package name */
    public int f1082k;

    /* renamed from: l  reason: collision with root package name */
    public boolean f1083l;

    /* renamed from: m  reason: collision with root package name */
    public int f1084m;

    public boolean a() {
        return this.f1083l;
    }

    public int getItemSpacing() {
        return this.f1082k;
    }

    public int getLineSpacing() {
        return this.f1081j;
    }

    public int getRowCount() {
        return this.f1084m;
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void onLayout(boolean z4, int i4, int i5, int i6, int i7) {
        boolean z5;
        int paddingLeft;
        int paddingRight;
        int i8;
        int i9;
        if (getChildCount() == 0) {
            this.f1084m = 0;
            return;
        }
        this.f1084m = 1;
        WeakHashMap<View, V> weakHashMap = O.f1526a;
        if (getLayoutDirection() == 1) {
            z5 = true;
        } else {
            z5 = false;
        }
        if (z5) {
            paddingLeft = getPaddingRight();
        } else {
            paddingLeft = getPaddingLeft();
        }
        if (z5) {
            paddingRight = getPaddingLeft();
        } else {
            paddingRight = getPaddingRight();
        }
        int paddingTop = getPaddingTop();
        int i10 = (i6 - i4) - paddingRight;
        int i11 = paddingLeft;
        int i12 = paddingTop;
        for (int i13 = 0; i13 < getChildCount(); i13++) {
            View childAt = getChildAt(i13);
            if (childAt.getVisibility() == 8) {
                childAt.setTag(2131231180, -1);
            } else {
                ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    i9 = marginLayoutParams.getMarginStart();
                    i8 = marginLayoutParams.getMarginEnd();
                } else {
                    i8 = 0;
                    i9 = 0;
                }
                int measuredWidth = childAt.getMeasuredWidth() + i11 + i9;
                if (!this.f1083l && measuredWidth > i10) {
                    i12 = this.f1081j + paddingTop;
                    this.f1084m++;
                    i11 = paddingLeft;
                }
                childAt.setTag(2131231180, Integer.valueOf(this.f1084m - 1));
                int i14 = i11 + i9;
                int measuredWidth2 = childAt.getMeasuredWidth() + i14;
                int measuredHeight = childAt.getMeasuredHeight() + i12;
                if (z5) {
                    childAt.layout(i10 - measuredWidth2, i12, (i10 - i11) - i9, measuredHeight);
                } else {
                    childAt.layout(i14, i12, measuredWidth2, measuredHeight);
                }
                i11 += childAt.getMeasuredWidth() + i9 + i8 + this.f1082k;
                paddingTop = measuredHeight;
            }
        }
    }

    @Override // android.view.View
    public final void onMeasure(int i4, int i5) {
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int size = View.MeasureSpec.getSize(i4);
        int mode = View.MeasureSpec.getMode(i4);
        int size2 = View.MeasureSpec.getSize(i5);
        int mode2 = View.MeasureSpec.getMode(i5);
        if (mode != Integer.MIN_VALUE && mode != 1073741824) {
            i6 = Integer.MAX_VALUE;
        } else {
            i6 = size;
        }
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = i6 - getPaddingRight();
        int i11 = paddingTop;
        int i12 = 0;
        for (int i13 = 0; i13 < getChildCount(); i13++) {
            View childAt = getChildAt(i13);
            if (childAt.getVisibility() != 8) {
                measureChild(childAt, i4, i5);
                ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    i9 = marginLayoutParams.leftMargin;
                    i8 = marginLayoutParams.rightMargin;
                } else {
                    i8 = 0;
                    i9 = 0;
                }
                int i14 = paddingLeft;
                if (childAt.getMeasuredWidth() + paddingLeft + i9 > paddingRight && !a()) {
                    i10 = getPaddingLeft();
                    i11 = this.f1081j + paddingTop;
                } else {
                    i10 = i14;
                }
                int measuredWidth = childAt.getMeasuredWidth() + i10 + i9;
                int measuredHeight = childAt.getMeasuredHeight() + i11;
                if (measuredWidth > i12) {
                    i12 = measuredWidth;
                }
                int measuredWidth2 = childAt.getMeasuredWidth() + i9 + i8 + this.f1082k + i10;
                if (i13 == getChildCount() - 1) {
                    i12 += i8;
                }
                paddingLeft = measuredWidth2;
                paddingTop = measuredHeight;
            }
        }
        int paddingRight2 = getPaddingRight() + i12;
        int paddingBottom = getPaddingBottom() + paddingTop;
        if (mode != Integer.MIN_VALUE) {
            i7 = 1073741824;
            if (mode != 1073741824) {
                size = paddingRight2;
            }
        } else {
            i7 = 1073741824;
            size = Math.min(paddingRight2, size);
        }
        if (mode2 != Integer.MIN_VALUE) {
            if (mode2 != i7) {
                size2 = paddingBottom;
            }
        } else {
            size2 = Math.min(paddingBottom, size2);
        }
        setMeasuredDimension(size, size2);
    }

    public void setItemSpacing(int i4) {
        this.f1082k = i4;
    }

    public void setLineSpacing(int i4) {
        this.f1081j = i4;
    }

    public void setSingleLine(boolean z4) {
        this.f1083l = z4;
    }
}
