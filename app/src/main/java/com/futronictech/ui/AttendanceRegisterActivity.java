package com.futronictech.ui;


import java.util.Arrays;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.R;
import com.futronictech.scannerHelper.FPScan;
import com.futronictech.scannerHelper.Scanner;
import com.futronictech.scannerHelper.UsbDeviceDataExchangeImpl;
import com.futronictech.scannerHelper.ftrWsqAndroidHelper;

public class AttendanceRegisterActivity extends AppCompatActivity {
    public static final String LOG_TAG = AttendanceRegisterActivity.class.getSimpleName();
    /**
     * Called when the activity is first created.
     */
    private Button mButtonScan;
    private Button mButtonStop;
    private Button mButtonSave;
    private TextView mMessage;
    private static ImageView mFingerImage;

    public static boolean mStop = false;
    public static boolean mFrame = true;
    public static boolean mLFD = false;
    public static boolean mInvertImage = false;

    public static final int MESSAGE_SHOW_MSG = 1;
    public static final int MESSAGE_SHOW_SCANNER_INFO = 2;
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

    private FPScan mFPScan = null;
    //
    public static boolean mUsbHostMode = true;

    // Intent request codes
    private UsbDeviceDataExchangeImpl usb_host_ctx = null;

    /**
     * Initialize the finger scanner window
     * @param width of window
     * @param height of window
     */
    public static void InitFingerPictureParameters(int width, int height) {
        mImageWidth = width;
        mImageHeight = height;

        mImageFP = new byte[mImageWidth * mImageHeight];
        mPixels = new int[mImageWidth * mImageHeight];

        mBitmapFP = Bitmap.createBitmap(width, height, Config.RGB_565);

        mCanvas = new Canvas(mBitmapFP);
        mPaint = new Paint();

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        mPaint.setColorFilter(f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the booleans and the views
        mFrame = true;
        mUsbHostMode = true;
        mLFD = mInvertImage = false;
        mButtonScan = findViewById(R.id.btnScan);
        mButtonStop = findViewById(R.id.btnStop);
        mButtonSave = findViewById(R.id.btnSave);
        mMessage = findViewById(R.id.tvMessage);
        mFingerImage = findViewById(R.id.imageFinger);

        // Automatically starts the scanner screen here:



        // Initialize the usbDeviceDataExchange
        usb_host_ctx = new UsbDeviceDataExchangeImpl(this, mHandler);

        // Set up the on Click listeners
        mButtonScan.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Close any existing FPScan instance running
                if (mFPScan != null) {
                    mStop = true;
                    mFPScan.stop();

                }
                mStop = false;
                usb_host_ctx.CloseDevice();
                if(usb_host_ctx.OpenDevice(0,true)) {
                    if(StartScan()) {
                        // Disable the Scan button and enable the Stop button
                        mButtonScan.setEnabled(false);
                        mButtonStop.setEnabled(true);
                        mButtonSave.setEnabled(false);
                    }
                } else {
                    // If the device is not pending open do the operation below
                    if(!usb_host_ctx.IsPendingOpen()) {
                        mMessage.setText("Can not start scan operation.\nCan't open scanner device");
                    }
                }
            }
        });

        mButtonStop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mStop = true;
                if (mFPScan != null) {
                    mFPScan.stop();
                    mFPScan = null;

                }
                // Enable the scan and disable the stop buttons
                mButtonScan.setEnabled(true);
                mButtonStop.setEnabled(false);
                mButtonSave.setEnabled(true);
            }
        });

        mButtonSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageFP != null)
                    SaveImage();
                mFingerImage.setImageResource(R.drawable.logo);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStop = true;
        if (mFPScan != null) {
            mFPScan.stop();
            mFPScan = null;
        }
        usb_host_ctx.CloseDevice();
        usb_host_ctx.Destroy();
        usb_host_ctx = null;
    }

    private boolean StartScan() {
        // This is where the scanning starts.
        // this runs in a background thread
        mFPScan = new FPScan(usb_host_ctx, mHandler);
        mFPScan.start();
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
                case MESSAGE_SHOW_SCANNER_INFO:
                    String showInfo = (String) msg.obj;
                    Log.d(LOG_TAG,showInfo);
                    break;
                case MESSAGE_SHOW_IMAGE:
                    ShowBitmap();
                    break;
                case MESSAGE_ERROR:
                    //mFPScan = null;
                    mButtonScan.setEnabled(true);
                    mButtonStop.setEnabled(false);
                    break;
                case UsbDeviceDataExchangeImpl.MESSAGE_ALLOW_DEVICE:
                    if (usb_host_ctx.ValidateContext()) {
                        if (StartScan()) {
                            mButtonScan.setEnabled(false);
                            mButtonStop.setEnabled(true);
                            mButtonSave.setEnabled(false);
                        }
                    } else
                        mMessage.setText(R.string.error_open_scanner_device);
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
        mButtonScan.setEnabled(true);
        mButtonStop.setEnabled(false);
        mButtonSave.setEnabled(false);
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