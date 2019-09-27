package com.honda.android.personalRoom.utils;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.honda.android.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CloudStorageActivity extends AppCompatActivity {

    ListView m_RootList, rootList_upload;
    ImageView iconFile;
    FloatingActionButton fab_cloud, fab_create, fab_upload;
    TextView textCreate, textUpdate, textRoot;
    Animation fabOpen, fabClose, rotateFrwd, rotateBckwd;
    boolean isOpen = false;
    private List<String> m_item, u_item;
    private List<String> m_path, u_path;
    private List<String> m_files, u_files;
    private List<String> m_filesPath, u_filesPath;
    private String m_root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ByonChatDoc";
    private String u_root = Environment.getExternalStorageDirectory().getAbsolutePath();
    ListAdapter m_listAdapter, u_listAdapter;
    String m_curDir, u_curDir;
    String m_text, u_text;
    AlertDialog show;
    private String CARD_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_storage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Cloud Storage");

        CARD_PATH = getIntent().getStringExtra("card");
        m_RootList = (ListView) findViewById(R.id.lvListRoot);
        fab_cloud = (FloatingActionButton) findViewById(R.id.fab_cloud);
        fab_create = (FloatingActionButton) findViewById(R.id.fab_create_cloud);
        fab_upload = (FloatingActionButton) findViewById(R.id.fab_update_cloud);
        textCreate = (TextView) findViewById(R.id.text_create_cloud);
        textUpdate = (TextView) findViewById(R.id.text_update_cloud);
        iconFile = (ImageView) findViewById(R.id.iv_image_cloud);
        textRoot = (TextView) findViewById(R.id.text_root_cloud);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateFrwd = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_forward);
        rotateBckwd = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_backward);
        fab_cloud.setVisibility(View.GONE);
        getDirFromRoot(m_root);

        fab_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimateFab();
            }
        });
        fab_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimateFab();
                createNewFolder(1);
                m_listAdapter.notifyDataSetChanged();
            }
        });
        fab_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimateFab();
                AlertDialog.Builder dialog = new AlertDialog.Builder(CloudStorageActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.activity_cloud_storage, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Select File");
                rootList_upload = (ListView) dialogView.findViewById(R.id.lvListRoot_dialog);
                uploadFile(u_root);
                show = dialog.show();
                show.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        u_curDir = null;
                    }
                });
            }
        });
    }

    public void getDirFromRoot(String p_rootPath) {
        m_item = new ArrayList<String>();
        Boolean m_isRoot = true;
        m_path = new ArrayList<String>();
        m_files = new ArrayList<String>();
        m_filesPath = new ArrayList<String>();
        File m_file = new File(p_rootPath);
        File[] m_filesArray = m_file.listFiles();
        if (!p_rootPath.equals(m_root)) {
            m_item.add("../");
            m_path.add(m_file.getParent());
            m_isRoot = false;
        }
        m_curDir = p_rootPath;
        //sorting file list in alphabetical order
        Arrays.sort(m_filesArray);
        for (int i = 0; i < m_filesArray.length; i++) {
            File file = m_filesArray[i];
            if (file.isDirectory()) {
                m_item.add(file.getName());
                m_path.add(file.getPath());
            } else {
                m_files.add(file.getName());
                m_filesPath.add(file.getPath());
            }
        }
        for (String m_AddFile : m_files) {
            m_item.add(m_AddFile);
        }
        for (String m_AddPath : m_filesPath) {
            m_path.add(m_AddPath);
        }
        m_listAdapter = new ListAdapter(this, m_item, m_path, m_isRoot);
        m_RootList.setAdapter(m_listAdapter);
        m_RootList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                File m_isFile = new File(m_path.get(position));
                if (m_isFile.isDirectory()) {
                    getDirFromRoot(m_isFile.toString());
                } else {
                    Toast.makeText(CloudStorageActivity.this, "This is File" + m_path.get(position), Toast.LENGTH_SHORT).show();
                    prepareMerging(m_path.get(position));
                }
            }
        });
        textRoot();
    }

    //Method to delete selected files
    void deleteFile() {
        for (int m_delItem : m_listAdapter.m_selectedItem) {
            File m_delFile = new File(m_path.get(m_delItem));
            Log.d("file", m_path.get(m_delItem));
            boolean m_isDelete = m_delFile.delete();
            Toast.makeText(CloudStorageActivity.this, "File(s) Deledted", Toast.LENGTH_SHORT).show();
            getDirFromRoot(m_curDir);
        }
    }

    void createNewFolder(final int p_opt) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create new folder");

        // Set up the input
        final EditText m_edtinput = new EditText(this);
        // Specify the type of input expected;
        m_edtinput.setInputType(InputType.TYPE_CLASS_TEXT);
        // Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_text = m_edtinput.getText().toString();
                if (p_opt == 1) {
                    File m_newPath = new File(m_curDir, m_text);
                    Log.w("Path :", String.valueOf(m_newPath));
                    if (!m_newPath.exists()) {
                        m_newPath.mkdir();
                        m_listAdapter.notifyDataSetChanged();
                    }
                } else {
                    try {
                        FileOutputStream m_Output = new FileOutputStream((m_curDir + File.separator + m_text), false);
                        m_Output.close();
                        //  <!--<intent-filter>
                        //  <action android:name="android.intent.action.SEARCH" />
                        //  </intent-filter>
                        //  <meta-data android:name="android.app.searchable"
                        //  android:resource="@xml/searchable"/>-->

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                getDirFromRoot(m_curDir);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(m_edtinput);
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void AnimateFab() {
        // kalo baru di buka false
        if (!isOpen) {
            fab_cloud.startAnimation(rotateFrwd);
            fab_create.startAnimation(fabOpen);
            fab_upload.startAnimation(fabOpen);
            fab_create.setClickable(true);
            fab_upload.setClickable(true);
            isOpen = true;
            textCreate.startAnimation(fabOpen);
            textUpdate.startAnimation(fabOpen);
        } else {
            fab_cloud.startAnimation(rotateBckwd);
            fab_create.startAnimation(fabClose);
            fab_upload.startAnimation(fabClose);
            fab_create.setClickable(false);
            fab_upload.setClickable(false);
            isOpen = false;
            textCreate.startAnimation(fabClose);
            textUpdate.startAnimation(fabClose);
        }
    }

    @Override
    public void onBackPressed() {
        if (!m_curDir.equals(m_root)) {
            getDirFromRoot(m_path.get(0));
        } else if (u_curDir != null) {
            if (!u_curDir.equals(u_root)) {
                uploadFile(u_path.get(0));
            }
        } else {
            super.onBackPressed();
        }
    }

    public void textRoot() {
        try {
            int lastIndex = m_curDir.lastIndexOf("byonchat");
            String previewRoot = m_curDir.substring(lastIndex).replace("byonchat", "Home > Root");
            String root = previewRoot.replace("/", " > ");
            textRoot.setText(root);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void uploadFile(String rootPath) {
        u_item = new ArrayList<String>();
        Boolean u_isRoot = true;
        u_path = new ArrayList<String>();
        u_files = new ArrayList<String>();
        u_filesPath = new ArrayList<String>();
        File u_file = new File(rootPath);
        File[] u_filesArray = u_file.listFiles();
        if (!rootPath.equals(u_root)) {
            u_item.add("../");
            u_path.add(u_file.getParent());
            u_isRoot = false;
        }
        u_curDir = rootPath;
        //sorting file list in alphabetical order
        Arrays.sort(u_filesArray);
        for (int i = 0; i < u_filesArray.length; i++) {
            File file = u_filesArray[i];
            if (file.isDirectory()) {
                u_item.add(file.getName());
                u_path.add(file.getPath());
            } else {
                u_files.add(file.getName());
                u_filesPath.add(file.getPath());
            }
        }
        for (String m_AddFile : u_files) {
            u_item.add(m_AddFile);
        }
        for (String m_AddPath : u_filesPath) {
            u_path.add(m_AddPath);
        }
        u_listAdapter = new ListAdapter(this, u_item, u_path, u_isRoot);
        rootList_upload.setAdapter(u_listAdapter);
        rootList_upload.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                File u_isFile = new File(u_path.get(position));
                if (u_isFile.isDirectory()) {
                    uploadFile(u_isFile.toString());
                } else {
                    String input = u_path.get(position);
                    String inputPath = u_isFile.getParent();
                    String inputFile = input.substring(input.lastIndexOf("/"));
                    String outputPath = m_curDir;
                    copyFile(inputPath, inputFile, outputPath);
                    show.dismiss();
                    getDirFromRoot(m_curDir);
                }
            }
        });

    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("sini", "1 : " + fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("sini", "2 : " + e.getMessage());
        }
    }

    private void prepareMerging(String path) {
        String fileOne = path;
        String Nama_Merge = path.replace("/storage/emulated/0/ByonChatDoc/","");
        String fileTwo = CARD_PATH;
        File hasil = new File(Environment.getExternalStorageDirectory(), Nama_Merge);
        hasil.getParentFile().mkdirs();
        String fileHasil = hasil.getAbsolutePath();

        try {
            FileInputStream fisOne = new FileInputStream(fileOne);
            FileInputStream fisTwo = new FileInputStream(fileTwo);
            FileOutputStream fosHasil = new FileOutputStream(fileHasil);

            mergePdfFiles(fisOne, fisTwo, fosHasil);
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("sini", "Exc : " + e.getMessage());
        }

        File outputFile = hasil;

        Uri pdfUri = FileProvider.getUriForFile(CloudStorageActivity.this, getPackageName() + ".provider", outputFile);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, pdfUri);
        startActivity(share);

        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(outputFile.getName());
        String type = map.getMimeTypeFromExtension(ext);
    }

    private void mergePdfFiles(FileInputStream isOne, FileInputStream isTwo, FileOutputStream hasil) throws Exception {
        PdfReader one = new PdfReader(isOne);
        PdfReader two = new PdfReader(isTwo);
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, hasil);
        document.open();
        copy.addDocument(one);
        copy.addDocument(two);
        document.close();
        one.close();
        two.close();
    }
}