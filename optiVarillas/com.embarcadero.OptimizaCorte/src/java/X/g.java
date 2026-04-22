package X;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.emoji2.text.f;
import java.lang.ref.WeakReference;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class g implements TextWatcher {

    /* renamed from: j  reason: collision with root package name */
    public final EditText f2799j;

    /* renamed from: l  reason: collision with root package name */
    public a f2801l;

    /* renamed from: k  reason: collision with root package name */
    public final boolean f2800k = false;

    /* renamed from: m  reason: collision with root package name */
    public boolean f2802m = true;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class a extends f.f {

        /* renamed from: a  reason: collision with root package name */
        public final WeakReference f2803a;

        public a(EditText editText) {
            this.f2803a = new WeakReference(editText);
        }

        public final void b() {
            g.a((EditText) this.f2803a.get(), 1);
        }
    }

    public g(EditText editText) {
        this.f2799j = editText;
    }

    public static void a(EditText editText, int i4) {
        int length;
        if (i4 == 1 && editText != null && editText.isAttachedToWindow()) {
            Editable editableText = editText.getEditableText();
            int selectionStart = Selection.getSelectionStart(editableText);
            int selectionEnd = Selection.getSelectionEnd(editableText);
            androidx.emoji2.text.f a4 = androidx.emoji2.text.f.a();
            if (editableText == null) {
                length = 0;
            } else {
                a4.getClass();
                length = editableText.length();
            }
            a4.f(editableText, 0, length);
            if (selectionStart >= 0 && selectionEnd >= 0) {
                Selection.setSelection(editableText, selectionStart, selectionEnd);
            } else if (selectionStart >= 0) {
                Selection.setSelection(editableText, selectionStart);
            } else if (selectionEnd >= 0) {
                Selection.setSelection(editableText, selectionEnd);
            }
        }
    }

    @Override // android.text.TextWatcher
    public final void onTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
        EditText editText = this.f2799j;
        if (!editText.isInEditMode() && this.f2802m) {
            if ((this.f2800k || androidx.emoji2.text.f.k != null) && i5 <= i6 && (charSequence instanceof Spannable)) {
                int b4 = androidx.emoji2.text.f.a().b();
                if (b4 != 0) {
                    if (b4 != 1) {
                        if (b4 != 3) {
                            return;
                        }
                    } else {
                        androidx.emoji2.text.f.a().f((Spannable) charSequence, i4, i6 + i4);
                        return;
                    }
                }
                androidx.emoji2.text.f a4 = androidx.emoji2.text.f.a();
                if (this.f2801l == null) {
                    this.f2801l = new a(editText);
                }
                a4.g(this.f2801l);
            }
        }
    }

    @Override // android.text.TextWatcher
    public final void afterTextChanged(Editable editable) {
    }

    @Override // android.text.TextWatcher
    public final void beforeTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
    }
}
