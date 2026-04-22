package l;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.Transition;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.HeaderViewListAdapter;
import android.widget.PopupWindow;
import androidx.appcompat.view.menu.ListMenuItemView;
import java.lang.reflect.Method;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class N extends L implements M {

    /* renamed from: N  reason: collision with root package name */
    public static final Method f5045N;

    /* renamed from: M  reason: collision with root package name */
    public M f5046M;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a {
        public static void a(PopupWindow popupWindow, Transition transition) {
            popupWindow.setEnterTransition(transition);
        }

        public static void b(PopupWindow popupWindow, Transition transition) {
            popupWindow.setExitTransition(transition);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class b {
        public static void a(PopupWindow popupWindow, boolean z4) {
            popupWindow.setTouchModal(z4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class c extends H {

        /* renamed from: v  reason: collision with root package name */
        public final int f5047v;

        /* renamed from: w  reason: collision with root package name */
        public final int f5048w;

        /* renamed from: x  reason: collision with root package name */
        public M f5049x;

        /* renamed from: y  reason: collision with root package name */
        public androidx.appcompat.view.menu.h f5050y;

        public c(Context context, boolean z4) {
            super(context, z4);
            if (1 == context.getResources().getConfiguration().getLayoutDirection()) {
                this.f5047v = 21;
                this.f5048w = 22;
                return;
            }
            this.f5047v = 22;
            this.f5048w = 21;
        }

        @Override // l.H, android.view.View
        public final boolean onHoverEvent(MotionEvent motionEvent) {
            androidx.appcompat.view.menu.e eVar;
            int i4;
            androidx.appcompat.view.menu.h hVar;
            int pointToPosition;
            int i5;
            if (this.f5049x != null) {
                androidx.appcompat.view.menu.e adapter = getAdapter();
                if (adapter instanceof HeaderViewListAdapter) {
                    HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) adapter;
                    i4 = headerViewListAdapter.getHeadersCount();
                    eVar = (androidx.appcompat.view.menu.e) headerViewListAdapter.getWrappedAdapter();
                } else {
                    eVar = adapter;
                    i4 = 0;
                }
                if (motionEvent.getAction() != 10 && (pointToPosition = pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY())) != -1 && (i5 = pointToPosition - i4) >= 0 && i5 < eVar.getCount()) {
                    hVar = eVar.c(i5);
                } else {
                    hVar = null;
                }
                androidx.appcompat.view.menu.h hVar2 = this.f5050y;
                if (hVar2 != hVar) {
                    androidx.appcompat.view.menu.f fVar = eVar.j;
                    if (hVar2 != null) {
                        this.f5049x.n(fVar, hVar2);
                    }
                    this.f5050y = hVar;
                    if (hVar != null) {
                        this.f5049x.d(fVar, hVar);
                    }
                }
            }
            return super.onHoverEvent(motionEvent);
        }

        @Override // android.widget.ListView, android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
        public final boolean onKeyDown(int i4, KeyEvent keyEvent) {
            androidx.appcompat.view.menu.e eVar;
            ListMenuItemView selectedView = getSelectedView();
            if (selectedView != null && i4 == this.f5047v) {
                if (selectedView.isEnabled() && selectedView.getItemData().hasSubMenu()) {
                    performItemClick(selectedView, getSelectedItemPosition(), getSelectedItemId());
                }
                return true;
            } else if (selectedView != null && i4 == this.f5048w) {
                setSelection(-1);
                androidx.appcompat.view.menu.e adapter = getAdapter();
                if (adapter instanceof HeaderViewListAdapter) {
                    eVar = (androidx.appcompat.view.menu.e) ((HeaderViewListAdapter) adapter).getWrappedAdapter();
                } else {
                    eVar = adapter;
                }
                eVar.j.c(false);
                return true;
            } else {
                return super.onKeyDown(i4, keyEvent);
            }
        }

        public void setHoverListener(M m4) {
            this.f5049x = m4;
        }

        @Override // l.H, android.widget.AbsListView
        public /* bridge */ /* synthetic */ void setSelector(Drawable drawable) {
            super.setSelector(drawable);
        }
    }

    static {
        try {
            if (Build.VERSION.SDK_INT <= 28) {
                f5045N = PopupWindow.class.getDeclaredMethod("setTouchModal", Boolean.TYPE);
            }
        } catch (NoSuchMethodException unused) {
            Log.i("MenuPopupWindow", "Could not find method setTouchModal() on PopupWindow. Oh well.");
        }
    }

    @Override // l.M
    public final void d(androidx.appcompat.view.menu.f fVar, androidx.appcompat.view.menu.h hVar) {
        M m4 = this.f5046M;
        if (m4 != null) {
            m4.d(fVar, hVar);
        }
    }

    @Override // l.M
    public final void n(androidx.appcompat.view.menu.f fVar, androidx.appcompat.view.menu.h hVar) {
        M m4 = this.f5046M;
        if (m4 != null) {
            m4.n(fVar, hVar);
        }
    }

    @Override // l.L
    public final H q(Context context, boolean z4) {
        c cVar = new c(context, z4);
        cVar.setHoverListener(this);
        return cVar;
    }
}
