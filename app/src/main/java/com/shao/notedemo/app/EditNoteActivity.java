package com.shao.notedemo.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shao.notedemo.R;
import com.shao.notedemo.contract.EditNoteContract;
import com.shao.notedemo.presenter.EditNotePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 16-12-26.
 */

public class EditNoteActivity extends AppCompatActivity implements EditNoteContract.View {
    @BindView(R.id.editnote_title)
    EditText titleEditText;
    @BindView(R.id.editnote_content)
    EditText contentEditText;
    @BindView(R.id.editnote_submit)
    Button submitButton;
    @OnClick(R.id.editnote_submit)
    void submit() {
        mPresenter.editNote(titleEditText.getText().toString(),
                contentEditText.getText().toString());
    }
    private EditNoteContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new EditNotePresenter(this,this);
        setContentView(R.layout.activity_editnote);
        ButterKnife.bind(this);
        mPresenter.start();
    }

    @Override
    public void editNoteSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void setTitle(String title) {
        titleEditText.setText(title);
    }

    @Override
    public void setContent(String content) {
        contentEditText.setText(content);
    }
}
