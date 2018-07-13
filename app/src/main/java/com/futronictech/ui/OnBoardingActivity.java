package com.futronictech.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.R;
import com.futronictech.scannerHelper.OnBoardingScanner;
import com.futronictech.scannerHelper.Scanner;
import com.futronictech.scannerHelper.UsbDeviceDataExchangeImpl;
import com.futronictech.scannerHelper.ftrWsqAndroidHelper;

import java.util.Arrays;

import static com.futronictech.ui.AttendanceRegisterActivity.LOG_TAG;

public class OnBoardingActivity extends AppCompatActivity {

    private TextView firstNameView,lastNameView, emailView, gender;
    private TextView mMessage;
    private static ImageView mFingerImage;
    private OnBoardingScanner scanner = null;
    UsbDeviceDataExchangeImpl usb_host_ctx = null;

    public static final int MESSAGE_SHOW_MSG = 1;
    public static final int MESSAGE_SHOW_IMAGE = 3;
    public static final int MESSAGE_ERROR = 4;

    public static byte[] mImageFP = null;
    public static final Object mSyncObj = new Object();

    public static int mImageWidth = 0;
    public static int mImageHeight = 0;
    private static int[] mPixels = null;
    private static Bitmap mBitmapFP = null;
    private static Canvas mCanvas = null;
    private static Paint mPaint = null;

    public static boolean mStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Instantiate the fields
        firstNameView = findViewById(R.id.text_first_name);
        lastNameView = findViewById(R.id.text_last_name);
        emailView = findViewById(R.id.text_email);
        gender = findViewById(R.id.text_gender);

        mMessage = findViewById(R.id.text_message);
        mFingerImage = findViewById(R.id.image_finger_print);
        Button saveButton = findViewById(R.id.btn_save);


        // Initialize the usbDeviceDataExchange
        usb_host_ctx = new UsbDeviceDataExchangeImpl(this, mHandler);
        /// Close any existing FPScan instance running
        if (scanner != null) {
            mStop = true;
            scanner.stop();
        }
        mStop = false;
        usb_host_ctx.CloseDevice();
        if(usb_host_ctx.OpenDevice(0,true)) {
            // Start the scan process
            scanner = new OnBoardingScanner(usb_host_ctx, mHandler);
            scanner.start();
            // Note that in scanner.start(), we capture the fingerprint and also save it.
        } else {
            // If the device is not pending open do the operation below
            if(!usb_host_ctx.IsPendingOpen()) {
                mMessage.setText("Can not start scan operation.\nCan't open scanner device");
            }
        }
        SaveImage();

        // Save and exit
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputs()) {
                    // Push to API
                    Toast.makeText(OnBoardingActivity.this,"Saved Successfully",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else return;
            }
        });
    }

    private boolean validateInputs() {
        if(TextUtils.isEmpty(firstNameView.getText().toString())) {
            firstNameView.setError("Enter your first name");
            return false;
        }

        if(TextUtils.isEmpty(lastNameView.getText().toString())) {
            lastNameView.setError("Enter your last name");
            return false;
        }

        if(TextUtils.isEmpty(emailView.getText().toString())) {
            emailView.setError("Enter your position");
            return false;
        }
        return true;
    }

    // The Handler that gets information back from the FPScan
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_MSG:
                    String showMsg = (String) msg.obj;
                    mMessage.setText(showMsg);
                    break;
                case MESSAGE_SHOW_IMAGE:
                    ShowBitmap();
                    break;
                case UsbDeviceDataExchangeImpl.MESSAGE_ALLOW_DEVICE:
                    if (!usb_host_ctx.ValidateContext()) {
                            mMessage.setText(R.string.error_open_scanner_device);
                        }
                    break;
                case UsbDeviceDataExchangeImpl.MESSAGE_DENY_DEVICE:
                    mMessage.setText(R.string.error_user_deny_scanner_device);
                    break;
            }
        }
    };

    private void SaveImage() {
        Scanner devScan = new Scanner();
        boolean bRet;
        bRet = devScan.OpenDeviceOnInterfaceUsbHost(usb_host_ctx);
        if (!bRet) {
            mMessage.setText(devScan.GetErrorMessage());
            return;
        }
        byte[] wsqImg = new byte[mImageWidth * mImageHeight];
        ftrWsqAndroidHelper helper = new ftrWsqAndroidHelper();
        long hDevice = devScan.GetDeviceHandle();
        if(helper.ConvertRawToWsq(hDevice,mImageWidth,mImageHeight,2.25f,
                mImageFP,wsqImg)) {
            // Send wsqImg to a database
            mMessage.setText(R.string.msg_wsq_file_saved);
            Log.d(LOG_TAG, "Value of wsqImg: " + Arrays.toString(wsqImg));
        } else mMessage.setText(R.string.error_fail_save_scanner_image);
        devScan.CloseDeviceUsbHost();
    }

    private static void ShowBitmap() {
        for (int i = 0; i < mImageWidth * mImageHeight; i++) {
            mPixels[i] = Color.rgb(mImageFP[i], mImageFP[i], mImageFP[i]);
        }

        mCanvas.drawBitmap(mPixels, 0, mImageWidth, 0, 0, mImageWidth, mImageHeight, false, mPaint);

        mFingerImage.setImageBitmap(mBitmapFP);
        mFingerImage.invalidate();

        synchronized (mSyncObj) {
            mSyncObj.notifyAll();
        }
    }
}
