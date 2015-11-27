package com.alanddev.manwal.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alanddev.manwal.R;
import com.alanddev.manwal.controller.WalletController;
import com.alanddev.manwal.model.Wallet;
import com.alanddev.manwal.util.Constant;
import com.alanddev.manwal.util.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WalletAddActivity extends AppCompatActivity {

    WalletController walletController;
    Utils utils;
    // Full path of image
    String imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        walletController = new WalletController(this);
        walletController.open();
        if (walletController.getCount() == 0){
            CheckBox chooseCB = (CheckBox)findViewById(R.id.choose);
            chooseCB.setChecked(true);
            chooseCB.setEnabled(false);
        }
        utils = new Utils();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        if (id == R.id.save){
            saveWallet();
        }

        return super.onOptionsItemSelected(item);
    }


    public void saveWallet() {
        EditText nameEdit   = (EditText)findViewById(R.id.txtName);
        EditText currEdit   = (EditText)findViewById(R.id.txtCurrency);
        EditText amountEdit   = (EditText)findViewById(R.id.txtAmount);
        CheckBox chooseCB = (CheckBox)findViewById(R.id.choose);

        String imageFileName = "";
        String nameWallet = nameEdit.getText().toString();
        String currency = currEdit.getText().toString();
        Double amount = 0.0;
        if (!amountEdit.getText().toString().equals("")) {
            amount = Double.parseDouble(amountEdit.getText().toString());
        }

        if (!imagePath.equals("")) {
            File image = new File(imagePath);
            imageFileName = image.getName();
            try {
                 utils.copyFile(imagePath, Constant.PATH_IMG + "/" + imageFileName);
                }
            catch (IOException e){};

        }

        if (nameWallet.equals("")){
            Toast.makeText(this,getResources().getString(R.string.warning_wallet_name),Toast.LENGTH_LONG).show();
        } else if (currency.equals("")){
            Toast.makeText(this,getResources().getString(R.string.warning_wallet_cur),Toast.LENGTH_LONG).show();
        }else if (amount == 0.0){
            Toast.makeText(this,getResources().getString(R.string.warning_wallet_amount),Toast.LENGTH_LONG).show();
        }else {
            Wallet newWallet = new Wallet(nameWallet, amount, currency, imageFileName);
            //db.createWallet();
            walletController = new WalletController(getApplicationContext());
            walletController.open();

            Wallet walletSaved = (Wallet)walletController.create(newWallet);
//            if (walletController.getCount() == 1){
//                chooseCB.setChecked(true);
//                utils.setSharedPreferencesValue(this, Constant.WALLET_ID, walletSaved.getId());
//            }else if (walletController.getCount() > 1) {
            if (chooseCB.isChecked()) {
                utils.setSharedPreferencesValue(this, Constant.WALLET_ID, walletSaved.getId());
                Utils.setWallet_id(walletSaved.getId());
            }
            //}
            finish();
        }
    }

    @Override
    protected void onResume() {
        if(walletController!=null) {
            walletController.open();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(walletController!=null) {
            walletController.close();
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
        //if (requestCode == Constant.CUR_WALLET_REQUEST) {
            case Constant.CUR_WALLET_REQUEST:
                if (data != null) {
                    String name = data.getStringExtra("cur_code");
                    String code = data.getStringExtra("cur_code");

                    ImageView imageCurrency = (ImageView) findViewById(R.id.imageCurrency);
                    Resources res = getResources();
                    String srcImg = "ic_currency_" + code.toLowerCase();
                    int id = res.getIdentifier(srcImg, "mipmap", getPackageName());
                    Drawable image = getResources().getDrawable(id);
                    imageCurrency.setImageDrawable(image);

                    TextView tvName = (TextView) findViewById(R.id.txtCurrency);
                    tvName.setText(name);
                }
                break;
            case Constant.GALLERY_WALLET_REQUEST :
                if (data !=null){
                    Uri selectedImageUri = data.getData();
                    imagePath = utils.getRealPathFromURI(this,selectedImageUri);
                    //Toast.makeText(this,path,Toast.LENGTH_LONG).show();
                    File image = new File(imagePath);
                    ImageView imgWallet = (ImageView)findViewById(R.id.imageWallet);
                    imgWallet.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                }
                break;
            default:
                break;
        }
    }


    public void onClickCurrency(View v){
        Intent intent = new Intent(this, ListCurrencyActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, Constant.CUR_WALLET_REQUEST);
        //finish();
    }


    public void clickChooseImage(View v){
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent , Constant.GALLERY_WALLET_REQUEST );
    }

//    public void createChart(){
//        // Creating a Data Set;
//        // Defining Y Axis;
//        ArrayList<BarEntry> entries = new ArrayList<>();
//        entries.add(new BarEntry(4f, 0));
//        entries.add(new BarEntry(8f, 1));
//        entries.add(new BarEntry(6f, 2));
//        entries.add(new BarEntry(12f, 3));
//        entries.add(new BarEntry(18f, 4));
//        entries.add(new BarEntry(9f, 5));
//        BarDataSet dataset = new BarDataSet(entries, "# of Calls");
//        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
//        // Defining X Axis;
//        ArrayList<String> labels = new ArrayList<String>();
//        labels.add("January");
//        labels.add("February");
//        labels.add("March");
//        labels.add("April");
//        labels.add("May");
//        labels.add("June");
//        BarChart chart = (BarChart)findViewById(R.id.chart);
//        BarData data = new BarData(labels, dataset);
//        chart.setData(data);
//        chart.setDescription("# of times Alice called Bob");
//        chart.animateY(5000);
//    }
//


}
