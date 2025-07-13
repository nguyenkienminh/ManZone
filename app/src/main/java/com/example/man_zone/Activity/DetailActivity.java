package com.example.man_zone.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.man_zone.Model.CartItem;
import com.example.man_zone.Model.ProductModel;
import com.example.man_zone.R;
import com.example.man_zone.helpers.CartManager;
import com.example.man_zone.helpers.FavoriteManager;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public class DetailActivity extends BaseActivity {

    private ImageView picMain, btnBack, btnFav, btnPlus, btnMinus, btnCart, callJSS, btnChat;
    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvDescription;
    private EditText etQuantity;
    private static final String PHONE_NUMBER = "1800 8386";

    private String title;
    private double price;
    private String description;
    private String img;
    private UUID productId;
    private String barcode;
    private double manufactureCost;
    private double weight;
    private int counterId;
    private int typeId;
    private String certificateUrl;
    private ProductModel.ProductStatuses productStatus;
    private double markupRate;
    private ProductModel.Units weightUnit;
    private double stonePrice;


    private int quantity = 1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        context = this;
        getIntentData();
        initViews();
        setData();
        setupListeners();

        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView btnCart = findViewById(R.id.btnCart);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(0);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // Existing data
            title = bundle.getString("title", "");
            price = bundle.getDouble("price", 0);
            description = bundle.getString("description", "");
            img = bundle.getString("img", "");

            // New data from ProductModel
            String productIdStr = bundle.getString("productId", "");
            productId = productIdStr.isEmpty() ? null : UUID.fromString(productIdStr);
            barcode = bundle.getString("barcode", "");
            manufactureCost = bundle.getDouble("manufactureCost", 0);
            weight = bundle.getDouble("weight", 0);
            counterId = bundle.getInt("counterId", 0);
            typeId = bundle.getInt("typeId", 0);
            certificateUrl = bundle.getString("certificateUrl", "");
            productStatus = (ProductModel.ProductStatuses) bundle.getSerializable("productStatus");
            markupRate = bundle.getDouble("markupRate", 0);
            weightUnit = (ProductModel.Units) bundle.getSerializable("weightUnit");
            stonePrice = bundle.getDouble("stonePrice", 0);
        }
    }


    private void initViews() {
        picMain = findViewById(R.id.picMain);
        btnBack = findViewById(R.id.btnBack);
        btnFav = findViewById(R.id.btnFav);
        tvTitle = findViewById(R.id.tvTitle);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.description);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        etQuantity = findViewById(R.id.etQuantity);
        btnCart = findViewById(R.id.btnCart);
        callJSS = findViewById(R.id.callJSS);
        btnChat = findViewById(R.id.btnChat);
    }

    private void setData() {
        tvTitle.setText(title);
        tvPrice.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(price)
                .replace("₫", " đ"));
        tvDescription.setText(description);
        etQuantity.setText(String.valueOf(quantity));
        Glide.with(context).load(img).into(picMain);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnFav.setOnClickListener(v -> {
            ProductModel product = new ProductModel(title, price, description, img);
            FavoriteManager favoriteManager = FavoriteManager.getInstance(this);

            if (favoriteManager.isInFavorites(product)) {
                favoriteManager.removeFromFavorites(product);
                btnFav.setSelected(false);
                Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                favoriteManager.addToFavorites(product);
                btnFav.setSelected(true);
                Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });

        callJSS.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + PHONE_NUMBER.replace(" ", "")));
            startActivity(intent);
        });

        btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("sms:" + PHONE_NUMBER.replace(" ", "")));
            startActivity(intent);
        });

        btnPlus.setOnClickListener(v -> updateQuantity(1));
        btnMinus.setOnClickListener(v -> updateQuantity(-1));

        findViewById(R.id.btnAddToCart).setOnClickListener(v -> addToCart());

        btnCart.setOnClickListener(v -> {
            Toast.makeText(this, "Opening cart...", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateQuantity(int change) {
        quantity += change;
        if (quantity < 1) quantity = 1;
        etQuantity.setText(String.valueOf(quantity));
    }

    @SuppressLint("DefaultLocale")
    private void addToCart() {
        String quantityStr = etQuantity.getText().toString();
        try {
            int qty = Integer.parseInt(quantityStr);

            // Create a ProductModel with all fields
            ProductModel product = new ProductModel(productId, title, price, description, img);
            product.setProductId(productId);
            product.setBarcode(barcode);
            product.setManufactureCost(manufactureCost);
            product.setWeight(weight);
            product.setCounterId(counterId);
            product.setTypeId(typeId);
            product.setCertificateUrl(certificateUrl);
            product.setProductStatus(productStatus);
            product.setMarkupRate(markupRate);
            product.setWeightUnit(weightUnit);
            product.setStonePrice(stonePrice);

            // Create CartItem using the ProductModel constructor
            CartItem cartItem = new CartItem(product, qty);

            CartManager.getInstance(context).addToCart(cartItem);

            double total = qty * price;
            Toast.makeText(this, String.format("Added %d service(s) to cart. Total: %,.0f VND", qty, total), Toast.LENGTH_LONG).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
        }
    }
}