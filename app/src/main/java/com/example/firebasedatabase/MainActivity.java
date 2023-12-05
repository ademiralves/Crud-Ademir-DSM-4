package com.example.firebasedatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FloatingActionButton add = findViewById(R.id.addProduct);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_product_dialog, null);
                TextInputLayout nameLayout, priceLayout, quantityLayout;
                nameLayout = view1.findViewById(R.id.nameLayout);
                priceLayout = view1.findViewById(R.id.priceLayout);
                quantityLayout = view1.findViewById(R.id.quantityLayout);
                TextInputEditText nameET, priceET, quantityET;
                nameET = view1.findViewById(R.id.nameET);
                priceET = view1.findViewById(R.id.priceET);
                quantityET = view1.findViewById(R.id.quantityET);
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Adicione um produto")
                        .setView(view1)
                        .setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Objects.requireNonNull(nameET.getText()).toString().isEmpty()) {
                                    nameLayout.setError("Campo requerido!");
                                } else if (Objects.requireNonNull(priceET.getText()).toString().isEmpty()) {
                                    priceLayout.setError("Campo requerido!");
                                } else if (Objects.requireNonNull(quantityET.getText()).toString().isEmpty()) {
                                    quantityLayout.setError("Campo requerido!");
                                } else {
                                    ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                                    dialog.setMessage("Salvado no banco de dados...");
                                    dialog.show();
                                    Product product = new Product();
                                    product.setName(nameET.getText().toString());
                                    product.setPrice(priceET.getText().toString());
                                    product.setQuantity(quantityET.getText().toString());
                                    database.getReference().child("products").push().setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                            dialogInterface.dismiss();
                                            Toast.makeText(MainActivity.this, "Sucesso ao salvar!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Houve um erro ao salvar:", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        TextView empty = findViewById(R.id.empty);

        RecyclerView recyclerView = findViewById(R.id.recycler);

        database.getReference().child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Product> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    Objects.requireNonNull(product).setKey(dataSnapshot.getKey());
                    arrayList.add(product);
                }

                if (arrayList.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                ProductAdapter adapter = new ProductAdapter(MainActivity.this, arrayList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Product product) {
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_product_dialog, null);
                        TextInputLayout nameLayout, priceLayout, quantityLayout;
                        TextInputEditText nameET, priceET, quantityET;

                        nameET = view.findViewById(R.id.nameET);
                        priceET = view.findViewById(R.id.priceET);
                        quantityET = view.findViewById(R.id.quantityET);
                        nameLayout = view.findViewById(R.id.nameLayout);
                        priceLayout = view.findViewById(R.id.priceLayout);
                        quantityLayout = view.findViewById(R.id.quantityLayout);

                        nameET.setText(product.getName());
                        priceET.setText(product.getPrice());
                        quantityET.setText(product.getQuantity());

                        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Editar")
                                .setView(view)
                                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (Objects.requireNonNull(nameET.getText()).toString().isEmpty()) {
                                            nameLayout.setError("Campo requerido!");
                                        } else if (Objects.requireNonNull(priceET.getText()).toString().isEmpty()) {
                                            priceLayout.setError("Campo requerido!");
                                        } else if (Objects.requireNonNull(quantityET.getText()).toString().isEmpty()) {
                                            quantityLayout.setError("Campo requerido!");
                                        } else {
                                            progressDialog.setMessage("Atualizando...");
                                            progressDialog.show();
                                            Product product1 = new Product();
                                            product1.setName(nameET.getText().toString());
                                            product1.setPrice(priceET.getText().toString());
                                            product1.setQuantity(priceET.getText().toString());
                                            database.getReference().child("products").child(product.getKey()).setValue(product1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(MainActivity.this, "Atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Houve um erro ao atualizar", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                })
                                .setNeutralButton("Fechar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressDialog.setTitle("Deletando...");
                                        progressDialog.show();
                                        database.getReference().child("products").child(product.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(MainActivity.this, "Deletado com sucesso!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }).create();
                        alertDialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}