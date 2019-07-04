package me.nunum.whereami.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import me.nunum.whereami.R;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.model.Feedback;
import me.nunum.whereami.service.HttpService;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        final TextView contactView = findViewById(R.id.af_contact);
        final TextView messageView = findViewById(R.id.af_message);

        final Button feedbackBtn = findViewById(R.id.af_submit_feedback_btn);

        final HttpService service = HttpService.create(getApplicationContext(), new Gson());

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String contactInput = contactView.getEditableText().toString();
                final String messageInput = messageView.getEditableText().toString();

                if (contactInput.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.af_empty_contact, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (messageInput.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.af_empty_message, Toast.LENGTH_SHORT).show();
                    return;
                }

                service.submitFeedback(new Feedback(contactInput, messageInput), new OnResponse<Void>() {
                    @Override
                    public void onSuccess(Void o) {
                        Toast.makeText(getApplicationContext(), R.string.af_submit_feedback_request_success, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Toast.makeText(getApplicationContext(), R.string.af_submit_feedback_request_failure, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_options_menu_feedback);
        }
    }
}
