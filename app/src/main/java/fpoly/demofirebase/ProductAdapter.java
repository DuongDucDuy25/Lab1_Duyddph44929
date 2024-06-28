package fpoly.demofirebase;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ToDo> productList;
    private FirebaseFirestore database;
    private Context context;

    public ProductAdapter(List<ToDo> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ToDo product = productList.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(product.getPrice());
        holder.title.setText(product.getTitle());
        database = FirebaseFirestore.getInstance();

        holder.btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaLogEdit(position);
            }
        });

        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Thông Báo");
                builder.setMessage("Bạn có muốn xóa không");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = product.getId();
                        Log.d("MMM", "id : " + id);
                        if (id != null){
                            database.collection("SanPham")
                                    .document(id)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            productList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position,productList.size());
                                            Toast.makeText(context, "Đã Xóa thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Delete", "Xóa Thất Bại " + e.getMessage());
                                            Toast.makeText(context, "Đã Xóa Thất Bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, title;
        Button btnSua, btnXoa;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            title = itemView.findViewById(R.id.productTitle);
            btnSua = itemView.findViewById(R.id.btnSua);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }

    public void addProduct(ToDo product) {
        productList.add(product);
        notifyItemInserted(productList.size() - 1);
    }

    private void diaLogEdit(int position){
        if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
            return;
        }

        EditText edName, edPrice, edTitle;
        Button btnUpdate, btnCancel;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update_product, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        edName = view.findViewById(R.id.edNameSP);
        edPrice = view.findViewById(R.id.edGiaSP);
        edTitle = view.findViewById(R.id.edTitle);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        edName.setText(productList.get(position).getName());
        edPrice.setText(productList.get(position).getPrice());
        edTitle.setText(productList.get(position).getTitle());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = edName.getText().toString().trim();
                String newPrice = edPrice.getText().toString().trim();
                String newTitle = edTitle.getText().toString().trim();

                database.collection("SanPham")
                        .document(productList.get(position).getId())
                        .update("name", newName,
                                "price", newPrice,
                                "title", newTitle)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                ToDo updateProduct = productList.get(position);
                                updateProduct.setName(newName);
                                updateProduct.setPrice(newPrice);
                                updateProduct.setTitle(newTitle);

                                notifyDataSetChanged();
                                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Edit", "Sửa Thất Bại " + e.getMessage());
                                Toast.makeText(context, "Sửa Thất Bại", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
