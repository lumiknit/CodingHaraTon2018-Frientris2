package team.sjexpress.frientris2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private CheckBox Gore, Particle, HOS, Vibration, ShowArea, LevelSpeed, Ghost;
    private EditText Width;
    private boolean gore, part, hos, vib, sta, ldds, ghost;
    private int width;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        pref = getSharedPreferences("setting", MODE_PRIVATE);

        gore = pref.getBoolean("gore", true);
        part = pref.getBoolean("particle", true);
        hos = pref.getBoolean("hos", true);
        vib = pref.getBoolean("vibration", true);
        sta = pref.getBoolean("sta", false);
        ldds = pref.getBoolean("ldds", true);
        ghost = pref.getBoolean("ghost", true);
        width = pref.getInt("width", 7);

        Gore = (CheckBox)findViewById(R.id.checkBox1);
        Particle = (CheckBox)findViewById(R.id.checkBox2);
        HOS = (CheckBox)findViewById(R.id.checkBox3);
        Vibration = (CheckBox)findViewById(R.id.checkBox4);
        ShowArea = (CheckBox)findViewById(R.id.checkBox5);
        LevelSpeed = (CheckBox)findViewById(R.id.checkBox6);
        Ghost = (CheckBox)findViewById(R.id.checkBox7);
        Width = (EditText)findViewById(R.id.editText1);

        Gore.setChecked(gore);
        Particle.setChecked(part);
        HOS.setChecked(hos);
        Vibration.setChecked(vib);
        ShowArea.setChecked(sta);
        LevelSpeed.setChecked(ldds);
        Ghost.setChecked(ghost);
        Width.setText("" + width);

        Button button = (Button)findViewById(R.id.btn_back);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(getApplicationContext(), "Going to Main Menu", Toast.LENGTH_LONG).show();
        editor = pref.edit();
        editor.putBoolean("gore", Gore.isChecked());
        editor.putBoolean("particle", Particle.isChecked());
        editor.putBoolean("hos", HOS.isChecked());
        editor.putBoolean("vibration", Vibration.isChecked());
        editor.putBoolean("sta", ShowArea.isChecked());
        editor.putBoolean("ldds", LevelSpeed.isChecked());
        editor.putBoolean("ghost", Ghost.isChecked());
        int n;
        try {
            n = Integer.parseInt(Width.getText().toString());
        } catch(Exception e) {
            n = 7;
        }
        if(n < 2) n = 2;
        else if(n > 64) n = 64;
        editor.putInt("width", n);
        editor.apply();
        super.onDestroy();
    }
}
