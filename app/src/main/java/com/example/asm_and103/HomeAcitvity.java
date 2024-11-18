package com.example.asm_and103;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_and103.Adapter.AdapterProduct;
import com.example.asm_and103.Adapter.AdapterProductRecy;
import com.example.asm_and103.Models.ProductModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeAcitvity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    RecyclerView rycProduct;
    List<ProductModel> lstProduct;
    AdapterProductRecy adapter;
    FloatingActionButton btnAdd;
    TextView tvUser;
    EditText edtSearch;
    private ImageView dialogImageView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_acitvity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtSearch = findViewById(R.id.edtSeach);
        tvUser = findViewById(R.id.tvUser);
        tvUser.setText("Hi, " + getIntent().getStringExtra("email"));



        rycProduct = findViewById(R.id.recyclerViewProducts);
        btnAdd = findViewById(R.id.btnAdd);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);
        Call<List<ProductModel>> call = apiService.getProducts();
        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if(response.isSuccessful()){
                    lstProduct = response.body();
                    Log.d("zzz", "onResponse: " + lstProduct);
                    adapter = new AdapterProductRecy(HomeAcitvity.this,lstProduct);
                    Log.d("zz", "onResponse: " + adapter);
                    rycProduct.setAdapter(adapter);
                }
                else {
                    Log.e("API Error", "Response Code: " + response.code());
                    Log.e("API Error", "Response Error Body: " + response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Log.e("zzz", "onFailure: "+ t.getMessage());
            }
        });


        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String key = edtSearch.getText().toString();
                Call<ArrayList<ProductModel>> callAddProduct = apiService.searchProduct(key);

                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    Log.d("keykey", "onEditorAction: " + key);

                    callAddProduct.enqueue(new Callback<ArrayList<ProductModel>>() {
                        @Override
                        public void onResponse(Call<ArrayList<ProductModel>> call, Response<ArrayList<ProductModel>> response) {
                            if(response.isSuccessful()){
                                Log.d("keykey", "onEditorAction: " + key);
                                lstProduct.clear();
                                lstProduct.addAll(response.body());
                                adapter.notifyDataSetChanged();
                            }
                            else {
                                Log.e("API Error", "Response Code: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<ProductModel>> call, Throwable t) {

                        }
                    });
                    return true;
                }
                return false;
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(apiService);
            }
        });

//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ProductModel product = new ProductModel("", "Dâu Tây", "Chuẩn Mùa", 2000, 1);
//                Call<List<ProductModel>> callAddProduct = apiService.addProduct(product); // Lưu ý API có trả về danh sách hay 1 đối tượng?
//
//                callAddProduct.enqueue(new Callback<ProductModel>() {
//                    @Override
//                    public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
//                        if (response.isSuccessful()) {
//                            ProductModel newProduct = response.body();
//                            lstProduct.add(newProduct); // Thêm sản phẩm vào danh sách hiện tại
//                            adapter.notifyItemInserted(lstProduct.size() - 1); // Cập nhật RecyclerView
//                            Toast.makeText(HomeAcitvity.this, "Thêm Thành Công !!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.e("API Error", "Response Code: " + response.code());
//                            try {
//                                Log.e("API Error", "Error Body: " + response.errorBody());
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            Toast.makeText(HomeAcitvity.this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ProductModel> call, Throwable t) {
//                        Log.e("API Failure", "Error: " + t.getMessage());
//                        Toast.makeText(HomeAcitvity.this, "Không thể kết nối API", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });

    }

    void openDialog(APIService apiService){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeAcitvity.this);
        LayoutInflater inflater = ((Activity)HomeAcitvity.this).getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_add_product,null);
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();

        //anh xa
        dialogImageView = v.findViewById(R.id.imageView);
        EditText edtNameProduct = v.findViewById(R.id.edtNameProduct);
        EditText edtCanNang = v.findViewById(R.id.edtCanNang);
        Button btnThem = v.findViewById(R.id.btnThem);

        dialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });


        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtNameProduct.getText().toString();
                String canNang = edtCanNang.getText().toString();
                if(name.isEmpty() || canNang.isEmpty()){
                    Toast.makeText(HomeAcitvity.this, "Vui lòng không để trống !!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(name.length() > 20 || canNang.length() > 10){
                    Toast.makeText(HomeAcitvity.this, "Vui lòng không nhập Tên và Giá quá dài !!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ProductModel product = new ProductModel();
                product.setProductName(edtNameProduct.getText().toString());
                product.setPrice(Double.parseDouble(String.valueOf(edtCanNang.getText().toString())));

                if (selectedImageUri != null) {
                    product.setImage(selectedImageUri.toString());
                } else {
                    Toast.makeText(HomeAcitvity.this, "Chưa chọn ảnh!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Call<List<ProductModel>> callAddProduct = apiService.addProduct(product);

                callAddProduct.enqueue(new Callback<List<ProductModel>>() {
                    @Override
                    public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            lstProduct.clear();
                            lstProduct.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            Toast.makeText(HomeAcitvity.this, "Thêm Thành Công !!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Log.e("API Error", "Response Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                        Log.e("API Error", "Error: " + t.getMessage());
                    }
                });
            }
        });




    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                dialogImageView.setImageBitmap(bitmap); // Hiển thị ảnh trong ImageView
                Log.d("zzzzzz", "onActivityResult: "+ bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}