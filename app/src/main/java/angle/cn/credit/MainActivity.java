package angle.cn.credit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import angle.cn.credit.view.RoundIndicator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RoundIndicator roundIndicator = (RoundIndicator) findViewById(R.id.rind);
        EditText etCurrentNum = (EditText) findViewById(R.id.et_currentNum);
        roundIndicator.setCurrentNumAnim(400);
        etCurrentNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if (!TextUtils.isEmpty(s)) {
                    roundIndicator.setCurrentNumAnim(Integer.parseInt(s));
                } else {
                    roundIndicator.setCurrentNumAnim(0);
                }
            }
        });
    }
}
