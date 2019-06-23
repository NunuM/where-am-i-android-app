package me.nunum.whereami.activities;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.View;
import android.widget.TextView;

import me.nunum.whereami.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final TextView version = findViewById(R.id.ab_app_version);

        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            version.setText(Html.fromHtml("<b>Version</b>:" + pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            version.setVisibility(View.INVISIBLE);
        }

        MovementMethod movementMethod = LinkMovementMethod.getInstance();

        final TextView githubProject = findViewById(R.id.ab_github_project);
        githubProject.setMovementMethod(movementMethod);

        final TextView author = findViewById(R.id.ab_app_author);
        author.setMovementMethod(movementMethod);

        final TextView websiteProject = findViewById(R.id.ab_website_project);
        websiteProject.setMovementMethod(movementMethod);

        final TextView donate = findViewById(R.id.ab_donate);
        donate.setMovementMethod(movementMethod);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_options_menu_about);
        }

    }
}
