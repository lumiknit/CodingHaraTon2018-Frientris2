package team.sjexpress.frientris2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    Intent intent = getIntent();
    int score = intent.getIntExtra("score", 0);
    ((TextView)findViewById(R.id.textViewScore)).setText("" + score);
  }
}
