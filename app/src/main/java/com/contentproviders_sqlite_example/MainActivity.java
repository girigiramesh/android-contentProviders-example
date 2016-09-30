package com.contentproviders_sqlite_example;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText deleteIDEditText, idLookupEditText, addNameEditText;
    Button btnRetrieve, deleteIDButton, findContactButton, addContactButton;
    TextView contactsTextView = null;
    ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resolver = getContentResolver();

        contactsTextView = (TextView) findViewById(R.id.contactsTextView);

        deleteIDEditText = (EditText) findViewById(R.id.deleteIDEditText);
        idLookupEditText = (EditText) findViewById(R.id.idLookupEditText);
        addNameEditText = (EditText) findViewById(R.id.addNameEditText);

        btnRetrieve = (Button) findViewById(R.id.btnRetrieve);
        deleteIDButton = (Button) findViewById(R.id.deleteIDButton);
        findContactButton = (Button) findViewById(R.id.findContactButton);
        addContactButton = (Button) findViewById(R.id.addContactButton);
        btnRetrieve.setOnClickListener(this);
        deleteIDButton.setOnClickListener(this);
        findContactButton.setOnClickListener(this);
        addContactButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRetrieve:
                String[] strings = new String[]{"id", "name"};
                Cursor cursor = resolver.query(Uri.parse(MyContentProvider.URL), strings, null, null, null);

                String contactList = "";
                if (cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(cursor.getColumnIndex("id"));
                        String name = cursor.getString(cursor.getColumnIndex("name"));

                        contactList = contactList + id + " : " + name + "\n";
                    } while (cursor.moveToNext());
                }
                contactsTextView.setText(contactList);
                break;

            case R.id.deleteIDButton:
                String idToDelete = deleteIDEditText.getText().toString();
                long idDeleted = resolver.delete(Uri.parse(MyContentProvider.URL), "id = ? ", new String[]{idToDelete});
                break;

            case R.id.findContactButton:
                String idToFind = idLookupEditText.getText().toString();
                strings = new String[]{"id", "name"};
                cursor = resolver.query(Uri.parse(MyContentProvider.URL), strings, "id = ? ", new String[]{idToFind}, null);

                String contact = "";
                if (cursor.moveToFirst()) {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));

                    contact = contact + id + " : " + name + "\n";
                } else {
                    Toast.makeText(this, "Contact Not Found", Toast.LENGTH_SHORT).show();
                }
                contactsTextView.setText(contact);
                break;

            case R.id.addContactButton:
                String nameToAdd = addNameEditText.getText().toString();
                ContentValues values = new ContentValues();
                values.put("name", nameToAdd);

                resolver.insert(Uri.parse(MyContentProvider.URL), values);
                break;

            default:
                finish();
        }
    }
}
