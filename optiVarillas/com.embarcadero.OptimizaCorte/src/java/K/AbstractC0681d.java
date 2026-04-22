package k;

import android.content.Context;
import android.graphics.Rect;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import androidx.appcompat.view.menu.h;
import androidx.appcompat.view.menu.j;

/* renamed from: k.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class AbstractC0681d implements InterfaceC0683f, j, AdapterView.OnItemClickListener {

    /* renamed from: j  reason: collision with root package name */
    public Rect f4924j;

    public static int m(ListAdapter listAdapter, Context context, int i4) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(0, 0);
        int count = listAdapter.getCount();
        FrameLayout frameLayout = null;
        View view = null;
        int i5 = 0;
        int i6 = 0;
        for (int i7 = 0; i7 < count; i7++) {
            int itemViewType = listAdapter.getItemViewType(i7);
            if (itemViewType != i6) {
                view = null;
                i6 = itemViewType;
            }
            if (frameLayout == null) {
                frameLayout = new FrameLayout(context);
            }
            view = listAdapter.getView(i7, view, frameLayout);
            view.measure(makeMeasureSpec, makeMeasureSpec2);
            int measuredWidth = view.getMeasuredWidth();
            if (measuredWidth >= i4) {
                return i4;
            }
            if (measuredWidth > i5) {
                i5 = measuredWidth;
            }
        }
        return i5;
    }

    public static boolean u(androidx.appcompat.view.menu.f fVar) {
        int size = fVar.f.size();
        for (int i4 = 0; i4 < size; i4++) {
            MenuItem item = fVar.getItem(i4);
            if (item.isVisible() && item.getIcon() != null) {
                return true;
            }
        }
        return false;
    }

    public final boolean d(h hVar) {
        return false;
    }

    public final boolean k(h hVar) {
        return false;
    }

    public abstract void l(androidx.appcompat.view.menu.f fVar);

    public abstract void n(View view);

    public abstract void o(boolean z4);

    @Override // android.widget.AdapterView.OnItemClickListener
    public final void onItemClick(AdapterView<?> adapterView, View view, int i4, long j4) {
        androidx.appcompat.view.menu.e eVar;
        int i5;
        androidx.appcompat.view.menu.e eVar2 = (ListAdapter) adapterView.getAdapter();
        if (eVar2 instanceof HeaderViewListAdapter) {
            eVar = (androidx.appcompat.view.menu.e) ((HeaderViewListAdapter) eVar2).getWrappedAdapter();
        } else {
            eVar = eVar2;
        }
        androidx.appcompat.view.menu.f fVar = eVar.j;
        MenuItem menuItem = (MenuItem) eVar2.getItem(i4);
        if (!(this instanceof androidx.appcompat.view.menu.b)) {
            i5 = 0;
        } else {
            i5 = 4;
        }
        fVar.q(menuItem, this, i5);
    }

    public abstract void p(int i4);

    public abstract void q(int i4);

    public abstract void r(PopupWindow.OnDismissListener onDismissListener);

    public abstract void s(boolean z4);

    public abstract void t(int i4);

    public final void e(Context context, androidx.appcompat.view.menu.f fVar) {
    }
}
