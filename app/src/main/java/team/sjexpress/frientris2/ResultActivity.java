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
    int level = intent.getIntExtra("level", 0);
    int score = intent.getIntExtra("score", 0);
    int lines = intent.getIntExtra("lines", 0);
    ((TextView)findViewById(R.id.textViewLevel)).setText("Level " + level);
    ((TextView)findViewById(R.id.textViewScore)).setText("Score " + score);
    ((TextView)findViewById(R.id.textViewLines)).setText("Lines " + lines);
  }
}
