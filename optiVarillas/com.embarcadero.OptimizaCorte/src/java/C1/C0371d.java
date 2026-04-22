package c1;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import com.google.android.material.textfield.TextInputEditText;
import j$.util.Objects;

/* renamed from: c1.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final /* synthetic */ class C0371d implements InputFilter {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ TextInputEditText f2943a;

    public /* synthetic */ C0371d(TextInputEditText textInputEditText) {
        this.f2943a = textInputEditText;
    }

    @Override // android.text.InputFilter
    public final CharSequence filter(CharSequence charSequence, int i4, int i5, Spanned spanned, int i6, int i7) {
        if (charSequence != null) {
            for (int i8 = 0; i8 < charSequence.length(); i8++) {
                TextInputEditText textInputEditText = this.f2943a;
                Editable text = textInputEditText.getText();
                Objects.requireNonNull(text);
                if (text.toString().contains(".") && String.valueOf(charSequence.charAt(i8)).equals(".")) {
                    return charSequence.toString().substring(0, i8);
                }
                if (textInputEditText.getText().toString().contains(" ") && String.valueOf(charSequence.charAt(i8)).equals(" ")) {
                    return charSequence.toString().substring(0, i8);
                }
                if (textInputEditText.getText().toString().contains("/") && String.valueOf(charSequence.charAt(i8)).equals("/")) {
                    return charSequence.toString().substring(0, i8);
                }
                if (!"0123456789. /".contains(String.valueOf(charSequence.charAt(i8)))) {
                    return charSequence.toString().substring(0, i8);
                }
            }
            return charSequence;
        }
        Log.d("TECLADO", "createFilterFrac: source is null");
        return charSequence;
    }
}
