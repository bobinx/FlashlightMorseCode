package dchaos.flashlightmorsecode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button bflash,breset,bexit;
    EditText inedit,outedit;
    Toolbar toolbar;
    Switch s;
    int egg=0;
    //char [] English = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
    String [] Morse = { ".-" , "-..." , "-.-." , "-.." , "." , "..-." , "--." , "...." , ".." , ".---" , "-.-" , ".-.." , "--" , "-." , "---" , ".--." , "--.-" ,  ".-." , "..." , "-" , "..-" , "...-" , ".--" , "-..-" , "-.--" , "--.." , "|" };
    boolean flash=false;
    public volatile boolean loop = false;
    public static Camera camera = null;
    Parameters params;
    boolean isFlashOn=false;
    private CameraManager mCameraManager;
    private String mCameraId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("fMC");
        setSupportActionBar(toolbar);

        bflash = (Button) findViewById(R.id.button);
        breset = (Button) findViewById(R.id.button2);
        bexit = (Button) findViewById(R.id.button3);
        inedit=(EditText) findViewById(R.id.editText);
        outedit=(EditText) findViewById(R.id.editText2);
        s=(Switch) findViewById(R.id.switch1);
        flash=getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if(!flash) outedit.setText("Flash not available");
        else outedit.setText("Flash Available");
        bflash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String c=inedit.getText().toString();
                c = c.toLowerCase();
                c=c.replace(" ","");
                if(isFlashOn==true)Toast.makeText(getApplicationContext(), "Please Wait until Current Message is finished", Toast.LENGTH_SHORT).show();
                else{
                    getCamera();
                outedit.setText("Flashing Message \""+c+"\"");
                final String mes=c;
                    new Thread () {
                    public void run() {
                        Bundle b = new Bundle();
                        Message msg = Message.obtain(mhandler, 1);
                        mhandler.sendMessage(msg);
                        String temp="",out="";
                        do{
                    for ( int y = 0; y < mes.length (); y++ ) {
                        temp=Morse[((int) mes.charAt(y) - 97)];
                        b.putString("message", "Flashing letter -> "+mes.charAt(y)+"{ "+temp+" }");
                        msg = Message.obtain(mhandler, 3);
                        msg.setData(b);
                        mhandler.sendMessage(msg);
                        SendMessage(temp);
                        out+=" "+temp;
                        try {Thread.sleep(700);} catch (InterruptedException e) {e.printStackTrace();}
                    }}while (loop);
                    msg = Message.obtain(mhandler, 2);
                    mhandler.sendMessage(msg);
                    }}.start();
        }}
        });

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(s.isChecked())loop=true;
                else loop=false;
                ++egg;
                if(egg==10){inedit.setText("send nudes");egg=0;};
            }
        });
        inedit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inedit.setText("");
            }
        });
        bexit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
        breset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    inedit.setText("SOS");
                    outedit.setText("Enter a message and click \'Flash message\'");
                }catch (Exception e){e.printStackTrace();}
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id=item.getItemId();
        switch (res_id) {
            case R.id.action_appversion: {
                Toast.makeText(getApplicationContext(), "FlashLight Morse Code v1.0 | dC | 16045", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.action_close: {
                finish();
                System.exit(0);
                break;
            }
            case R.id.action_help: {
                Toast.makeText(getApplicationContext(), "Enter a message in the TextBox and click \"Flash Message\" to use flashlight to flash the message in Morse Code", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.action_contact:
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Contact");
                builder.setMessage("flashlightmorsecode@dch.com");
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "fmc@dch.com", Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alert=builder.create();
                alert.show();
            }
        }
        return true;
    }

    private void getCamera() {

        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
                //params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                //camera.setParameters(params);
            }catch (Exception e) {            }
        }
    }
    private void SendMessage(String s) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '.') {
                    //loop
                    params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    camera.startPreview();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    params.setFlashMode(Parameters.FLASH_MODE_OFF);
                    camera.setParameters(params);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    camera.startPreview();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    params.setFlashMode(Parameters.FLASH_MODE_OFF);
                    camera.setParameters(params);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {mCameraId = mCameraManager.getCameraIdList()[0];} catch (CameraAccessException e) {e.printStackTrace();}
            try{
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '.') {
                        //mCameraManager.setTorchMode(mCameraId, true);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mCameraManager.setTorchMode(mCameraId, false);
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mCameraManager.setTorchMode(mCameraId, true);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mCameraManager.setTorchMode(mCameraId, false);
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }catch (CameraAccessException e){e.printStackTrace();}

        }
    }

    Handler mhandler= new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1:
                    outedit.setText("Flashing Message...");
                    isFlashOn=true;
                    break;
                case 2:
                    outedit.setText("Done!");
                    isFlashOn=false;
                    break;
                case 3:
                    outedit.setText(msg.getData().getString("message"));
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "handler call not specified", Toast.LENGTH_SHORT).show();
            }
        }
    };


}
/*
        //inside oncreate
                    Thread t_one = new Thread(new Runnable()
                    {
                        public void run() {
                            //code
                        }
                    });
                    t_one.start();
*/
