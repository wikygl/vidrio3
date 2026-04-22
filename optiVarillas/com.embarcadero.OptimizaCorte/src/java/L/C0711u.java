package l;

import M.C0226h;
import android.app.Activity;
import android.content.ClipData;
import android.os.Build;
import android.text.Selection;
import android.text.Spannable;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;

/* renamed from: l.u  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0711u {
    /* JADX WARN: Type inference failed for: r4v4, types: [M.h$c, java.lang.Object] */
    public static boolean a(DragEvent dragEvent, TextView textView, Activity activity) {
        C0226h.a aVar;
        activity.requestDragAndDropPermissions(dragEvent);
        int offsetForPosition = textView.getOffsetForPosition(dragEvent.getX(), dragEvent.getY());
        textView.beginBatchEdit();
        try {
            Selection.setSelection((Spannable) textView.getText(), offsetForPosition);
            ClipData clipData = dragEvent.getClipData();
            if (Build.VERSION.SDK_INT >= 31) {
                aVar = new C0226h.a(clipData, 3);
            } else {
                ?? obj = new Object();
                obj.f1618a = clipData;
                obj.f1619b = 3;
                aVar = obj;
            }
            M.O.l(textView, aVar.a());
            textView.endBatchEdit();
            return true;
        } catch (Throwable th) {
            textView.endBatchEdit();
            throw th;
        }
    }

    /* JADX WARN: Type inference failed for: r4v2, types: [M.h$c, java.lang.Object] */
    public static boolean b(DragEvent dragEvent, View view, Activity activity) {
        C0226h.a aVar;
        activity.requestDragAndDropPermissions(dragEvent);
        ClipData clipData = dragEvent.getClipData();
        if (Build.VERSION.SDK_INT >= 31) {
            aVar = new C0226h.a(clipData, 3);
        } else {
            ?? obj = new Object();
            obj.f1618a = clipData;
            obj.f1619b = 3;
            aVar = obj;
        }
        M.O.l(view, aVar.a());
        return true;
    }
}
