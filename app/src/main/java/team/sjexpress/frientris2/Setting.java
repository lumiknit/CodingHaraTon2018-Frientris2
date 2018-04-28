package team.sjexpress.frientris2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private CheckBox Gore, Particle, HOS, Vibration;
    private boolean gore, part, hos, vib;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        pref = getSharedPreferences("setting", MODE_PRIVATE);
        editor = pref.edit();

        gore = pref.getBoolean("gore", true);
        part = pref.getBoolean("particle", true);
        hos = pref.getBoolean("hos", true);
        vib = pref.getBoolean("vibration", true);

        Gore = (CheckBox)findViewById(R.id.checkBox1);
        Particle = (CheckBox)findViewById(R.id.checkBox2);
        HOS = (CheckBox)findViewById(R.id.checkBox3);
        Vibration = (CheckBox)findViewById(R.id.checkBox4);

        Gore.setChecked(gore);
        Particle.setChecked(part);
        HOS.setChecked(hos);
        Vibration.setChecked(vib);

        Button button = (Button)findViewById(R.id.btn_back);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Going to Main Menu", Toast.LENGTH_LONG).show();
                if(Gore.isChecked() == true) editor.putBoolean("gore", true);
                else editor.putBoolean("gore", false);
                if(Particle.isChecked() == true) editor.putBoolean("particle", true);
                else editor.putBoolean("particle", false);
                if(HOS.isChecked() == true) editor.putBoolean("hos", true);
                else editor.putBoolean("hos", false);
                if(Vibration.isChecked() == true) editor.putBoolean("vibration", true);
                else editor.putBoolean("vibration", false);
                editor.commit();
                finish();
            }
        });
    }

}
