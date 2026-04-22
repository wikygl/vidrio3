package s2;

import M.O;
import M.V;
import M.e0;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.search.SearchBar;
import java.util.ArrayList;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class b extends c<View> {

    /* renamed from: c  reason: collision with root package name */
    public final Rect f5772c;

    /* renamed from: d  reason: collision with root package name */
    public final Rect f5773d;

    /* renamed from: e  reason: collision with root package name */
    public int f5774e;
    public int f;

    public b() {
        this.f5772c = new Rect();
        this.f5773d = new Rect();
        this.f5774e = 0;
    }

    public final boolean i(CoordinatorLayout coordinatorLayout, View view, int i4, int i5, int i6) {
        AppBarLayout v4;
        int i7;
        e0 lastWindowInsets;
        int i8 = view.getLayoutParams().height;
        if ((i8 == -1 || i8 == -2) && (v4 = v(coordinatorLayout.d(view))) != null) {
            int size = View.MeasureSpec.getSize(i6);
            if (size > 0) {
                WeakHashMap<View, V> weakHashMap = O.f1526a;
                if (v4.getFitsSystemWindows() && (lastWindowInsets = coordinatorLayout.getLastWindowInsets()) != null) {
                    size += lastWindowInsets.a() + lastWindowInsets.d();
                }
            } else {
                size = coordinatorLayout.getHeight();
            }
            int x4 = x(v4) + size;
            int measuredHeight = v4.getMeasuredHeight();
            if (this instanceof SearchBar.ScrollingViewBehavior) {
                view.setTranslationY(-measuredHeight);
            } else {
                view.setTranslationY(0.0f);
                x4 -= measuredHeight;
            }
            if (i8 == -1) {
                i7 = 1073741824;
            } else {
                i7 = Integer.MIN_VALUE;
            }
            coordinatorLayout.r(view, i4, i5, View.MeasureSpec.makeMeasureSpec(x4, i7));
            return true;
        }
        return false;
    }

    @Override // s2.c
    public final void u(CoordinatorLayout coordinatorLayout, View view, int i4) {
        int i5;
        AppBarLayout v4 = v(coordinatorLayout.d(view));
        int i6 = 0;
        if (v4 != null) {
            CoordinatorLayout.f layoutParams = view.getLayoutParams();
            int paddingLeft = coordinatorLayout.getPaddingLeft() + ((ViewGroup.MarginLayoutParams) layoutParams).leftMargin;
            int bottom = v4.getBottom() + ((ViewGroup.MarginLayoutParams) layoutParams).topMargin;
            int width = (coordinatorLayout.getWidth() - coordinatorLayout.getPaddingRight()) - ((ViewGroup.MarginLayoutParams) layoutParams).rightMargin;
            int height = coordinatorLayout.getHeight();
            Rect rect = this.f5772c;
            rect.set(paddingLeft, bottom, width, ((v4.getBottom() + height) - coordinatorLayout.getPaddingBottom()) - ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin);
            e0 lastWindowInsets = coordinatorLayout.getLastWindowInsets();
            if (lastWindowInsets != null) {
                WeakHashMap<View, V> weakHashMap = O.f1526a;
                if (coordinatorLayout.getFitsSystemWindows() && !view.getFitsSystemWindows()) {
                    rect.left = lastWindowInsets.b() + rect.left;
                    rect.right -= lastWindowInsets.c();
                }
            }
            int i7 = layoutParams.c;
            if (i7 == 0) {
                i5 = 8388659;
            } else {
                i5 = i7;
            }
            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();
            Rect rect2 = this.f5773d;
            Gravity.apply(i5, measuredWidth, measuredHeight, rect, rect2, i4);
            if (this.f != 0) {
                float w4 = w(v4);
                int i8 = this.f;
                i6 = H.a.f((int) (w4 * i8), 0, i8);
            }
            view.layout(rect2.left, rect2.top - i6, rect2.right, rect2.bottom - i6);
            this.f5774e = rect2.top - v4.getBottom();
            return;
        }
        coordinatorLayout.q(view, i4);
        this.f5774e = 0;
    }

    public abstract AppBarLayout v(ArrayList arrayList);

    public float w(View view) {
        return 1.0f;
    }

    public int x(View view) {
        return view.getMeasuredHeight();
    }

    public b(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f5772c = new Rect();
        this.f5773d = new Rect();
        this.f5774e = 0;
    }
}
