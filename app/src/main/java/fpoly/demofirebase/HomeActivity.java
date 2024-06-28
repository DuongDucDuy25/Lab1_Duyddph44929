package fpoly.demofirebase;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    FirebaseFirestore database;
    Button btnThem;
    RecyclerView rcItem;
    ProductAdapter productAdapter;
    List<ToDo> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        database = FirebaseFirestore.getInstance();

        btnThem = findViewById(R.id.btnThem);
        rcItem = findViewById(R.id.rcItem);



        fetchProducts();

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProductDialog();
            }
        });
    }

    private void fetchProducts() {
        database.collection("SanPham").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(HomeActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null && !value.isEmpty()) {
                    productList.clear();
                    for (QueryDocumentSnapshot document : value) {
                        ToDo toDo = document.toObject(ToDo.class);
                        toDo.setId(document.getId());
                        productList.add(toDo);
                    }
                    if (!productList.isEmpty()){
                        rcItem.setAdapter(new ProductAdapter(productList));
                        rcItem.setLayoutManager(new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.VERTICAL,false));
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm sản phẩm mới");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, (ViewGroup) findViewById(android.R.id.content), false);

        final EditText etProductName = viewInflated.findViewById(R.id.etProductName);
        final EditText etProductDescription = viewInflated.findViewById(R.id.etProductDescription);
        final EditText etProductPrice = viewInflated.findViewById(R.id.etProductPrice);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String name = etProductName.getText().toString().trim();
                String description = etProductDescription.getText().toString().trim();
                String priceString = etProductPrice.getText().toString().trim();

                if (!name.isEmpty() && !description.isEmpty() && !priceString.isEmpty()) {
                    int price = Integer.parseInt(priceString);
                    ToDo newProduct = new ToDo(name, description, String.valueOf(price));
                    addProductToDatabase(newProduct);
                } else {
                    Toast.makeText(HomeActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addProductToDatabase(ToDo newProduct) {
        database.collection("SanPham").add(newProduct)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(HomeActivity.this, "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
                        fetchProducts();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
