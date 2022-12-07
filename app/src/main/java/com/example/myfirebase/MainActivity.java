package com.example.myfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etTitle, etAuthor, etPrice;
    private ListView listView;
    private List<Book> listBook = new ArrayList<>();
    private ArrayAdapter<Book> adapter;
    private DatabaseReference databaseReference;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lv_book);
        adapter = new ArrayAdapter<Book>(this, android.R.layout.simple_list_item_1, listBook);
        listView.setAdapter(adapter);

        etTitle = findViewById(R.id.et_title);
        etAuthor = findViewById(R.id.et_author);
        etPrice = findViewById(R.id.et_price);

        databaseReference = FirebaseDatabase.getInstance().getReference(Book.class.getSimpleName());
        getAllData();

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertBook();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book item = (Book) adapterView.getAdapter().getItem(i);
                itemId = item.getId();
                etTitle.setText(item.getTitle());
                etAuthor.setText(item.getAuthor());
                etPrice.setText(String.valueOf(item.getPrice()));
            }
        });

        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book book = new Book();
                book.setTitle(etTitle.getText().toString());
                book.setAuthor(etAuthor.getText().toString());
                book.setPrice(Integer.parseInt(etPrice.getText().toString()));
                databaseReference.child(itemId).setValue(book);
                itemId = "";
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book item = (Book) adapterView.getAdapter().getItem(i);
                itemId = item.getId();
                databaseReference.child(itemId).removeValue();
                return true;
            }
        });
    }

    private void getAllData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBook.clear();
                if (snapshot.hasChildren()) {
                    for (DataSnapshot currentData : snapshot.getChildren()) {
                        Book book =  currentData.getValue(Book.class);
                        book.setId(currentData.getKey());
                        listBook.add(book);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void insertBook() {
        Book book = new Book();
        book.setTitle(etTitle.getText().toString());
        book.setAuthor(etAuthor.getText().toString());
        book.setPrice(Integer.parseInt(etPrice.getText().toString()));
        databaseReference.push().setValue(book);
    }
}