package U2;

import android.view.View;
import android.widget.AdapterView;
import l.L;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class r implements AdapterView.OnItemClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ s f2460j;

    public r(s sVar) {
        this.f2460j = sVar;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public final void onItemClick(AdapterView<?> adapterView, View view, int i4, long j4) {
        Object item;
        View view2 = null;
        s sVar = this.f2460j;
        if (i4 < 0) {
            L l2 = sVar.f2461n;
            if (!l2.f5022I.isShowing()) {
                item = null;
            } else {
                item = l2.f5025l.getSelectedItem();
            }
        } else {
            item = sVar.getAdapter().getItem(i4);
        }
        s.a(sVar, item);
        AdapterView.OnItemClickListener onItemClickListener = sVar.getOnItemClickListener();
        L l4 = sVar.f2461n;
        if (onItemClickListener != null) {
            if (view == null || i4 < 0) {
                if (l4.f5022I.isShowing()) {
                    view2 = l4.f5025l.getSelectedView();
                }
                view = view2;
                if (!l4.f5022I.isShowing()) {
                    i4 = -1;
                } else {
                    i4 = l4.f5025l.getSelectedItemPosition();
                }
                if (!l4.f5022I.isShowing()) {
                    j4 = Long.MIN_VALUE;
                } else {
                    j4 = l4.f5025l.getSelectedItemId();
                }
            }
            onItemClickListener.onItemClick(l4.f5025l, view, i4, j4);
        }
        l4.dismiss();
    }
}
