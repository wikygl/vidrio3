/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.ViewGroup
 *  android.widget.Button
 *  android.widget.EditText
 *  android.widget.LinearLayout
 *  android.widget.RadioButton
 *  android.widget.TextView
 *  androidx.constraintlayout.widget.ConstraintLayout
 *  androidx.recyclerview.widget.RecyclerView
 *  androidx.viewbinding.ViewBinding
 *  androidx.viewbinding.ViewBindings
 *  java.lang.NullPointerException
 *  java.lang.Object
 */
package Jose.jose.databinding;

import Jose.jose.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;

public final class ActivityMainBinding
implements ViewBinding {
    public final Button btArchivar;
    public final Button btcalcular;
    public final EditText etHoja;
    public final EditText etMarco;
    public final EditText etMed1;
    public final EditText etMed2;
    public final LinearLayout linearLayout;
    public final LinearLayout linearLayout2;
    public final ConstraintLayout main;
    public final RadioButton rbPb;
    public final RadioButton rbPh;
    public final RadioButton rbPp;
    private final ConstraintLayout rootView;
    public final RecyclerView rvMateriales;
    public final TextView tvRefe;
    public final TextView txPru;

    private ActivityMainBinding(ConstraintLayout constraintLayout, Button button, Button button2, EditText editText, EditText editText2, EditText editText3, EditText editText4, LinearLayout linearLayout, LinearLayout linearLayout2, ConstraintLayout constraintLayout2, RadioButton radioButton, RadioButton radioButton2, RadioButton radioButton3, RecyclerView recyclerView, TextView textView, TextView textView2) {
        this.rootView = constraintLayout;
        this.btArchivar = button;
        this.btcalcular = button2;
        this.etHoja = editText;
        this.etMarco = editText2;
        this.etMed1 = editText3;
        this.etMed2 = editText4;
        this.linearLayout = linearLayout;
        this.linearLayout2 = linearLayout2;
        this.main = constraintLayout2;
        this.rbPb = radioButton;
        this.rbPh = radioButton2;
        this.rbPp = radioButton3;
        this.rvMateriales = recyclerView;
        this.tvRefe = textView;
        this.txPru = textView2;
    }

    public static ActivityMainBinding bind(View view) {
        LinearLayout linearLayout;
        LinearLayout linearLayout2;
        EditText editText;
        EditText editText2;
        EditText editText3;
        EditText editText4;
        Button button;
        int n = R.id.btArchivar;
        Button button2 = (Button)ViewBindings.findChildViewById((View)view, (int)n);
        if (button2 != null && (button = (Button)ViewBindings.findChildViewById((View)view, (int)(n = R.id.btcalcular))) != null && (editText4 = (EditText)ViewBindings.findChildViewById((View)view, (int)(n = R.id.etHoja))) != null && (editText3 = (EditText)ViewBindings.findChildViewById((View)view, (int)(n = R.id.etMarco))) != null && (editText2 = (EditText)ViewBindings.findChildViewById((View)view, (int)(n = R.id.etMed1))) != null && (editText = (EditText)ViewBindings.findChildViewById((View)view, (int)(n = R.id.etMed2))) != null && (linearLayout2 = (LinearLayout)ViewBindings.findChildViewById((View)view, (int)(n = R.id.linearLayout))) != null && (linearLayout = (LinearLayout)ViewBindings.findChildViewById((View)view, (int)(n = R.id.linearLayout2))) != null) {
            TextView textView;
            TextView textView2;
            RecyclerView recyclerView;
            RadioButton radioButton;
            RadioButton radioButton2;
            ConstraintLayout constraintLayout = (ConstraintLayout)view;
            n = R.id.rbPb;
            RadioButton radioButton3 = (RadioButton)ViewBindings.findChildViewById((View)view, (int)n);
            if (radioButton3 != null && (radioButton2 = (RadioButton)ViewBindings.findChildViewById((View)view, (int)(n = R.id.rbPh))) != null && (radioButton = (RadioButton)ViewBindings.findChildViewById((View)view, (int)(n = R.id.rbPp))) != null && (recyclerView = (RecyclerView)ViewBindings.findChildViewById((View)view, (int)(n = R.id.rvMateriales))) != null && (textView2 = (TextView)ViewBindings.findChildViewById((View)view, (int)(n = R.id.tvRefe))) != null && (textView = (TextView)ViewBindings.findChildViewById((View)view, (int)(n = R.id.txPru))) != null) {
                return new ActivityMainBinding((ConstraintLayout)view, button2, button, editText4, editText3, editText2, editText, linearLayout2, linearLayout, constraintLayout, radioButton3, radioButton2, radioButton, recyclerView, textView2, textView);
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(n)));
    }

    public static ActivityMainBinding inflate(LayoutInflater layoutInflater) {
        return ActivityMainBinding.inflate(layoutInflater, null, false);
    }

    public static ActivityMainBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean bl) {
        layoutInflater = layoutInflater.inflate(R.layout.activity_main, viewGroup, false);
        if (bl) {
            viewGroup.addView((View)layoutInflater);
        }
        return ActivityMainBinding.bind((View)layoutInflater);
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }
}

