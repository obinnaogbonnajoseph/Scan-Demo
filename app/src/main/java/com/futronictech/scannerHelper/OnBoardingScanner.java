package com.futronictech.scannerHelper;

import android.os.Handler;
import android.util.Log;

import com.futronictech.ui.AttendanceRegisterActivity;
import com.futronictech.ui.OnBoardingActivity;

import static com.futronictech.ui.AttendanceRegisterActivity.LOG_TAG;

public class OnBoardingScanner {
    private final Handler mHandler;
    private OnBoardScan mScanThread;
    private UsbDeviceDataExchangeImpl ctx;

    public OnBoardingScanner(UsbDeviceDataExchangeImpl context, Handler handler) {
        mHandler = handler;
        ctx = context;
    }

    /**
     * This method starts the thread to run the scanning
     * This is a synchronized method. It runs only one instance, all through the app
     */
    public synchronized void start() {
        if (mScanThread == null) {
            mScanThread = new OnBoardScan();
            mScanThread.start();
        }
    }

    public synchronized void stop() {
        if (mScanThread != null) {mScanThread.cancel(); mScanThread = null;}
    }

    private class OnBoardScan extends Thread {
        private boolean bGetInfo;
        private Scanner devScan;
        private String strInfo;
        private int errCode;
        private boolean bRet;

        OnBoardScan() {
            bGetInfo = false;
            devScan = new Scanner();
        }

        public void run() {
            while (!OnBoardingActivity.mStop)
            {
                if(!bGetInfo)
                {
                    boolean bRet;
                    bRet = devScan.OpenDeviceOnInterfaceUsbHost(ctx);
                    // If usb is closed for any reason, run the operation below
                    if( !bRet )
                    {
                        ctx.CloseDevice();
                        mHandler.obtainMessage(OnBoardingActivity.MESSAGE_SHOW_MSG, -1, -1,
                                devScan.GetErrorMessage()).sendToTarget();
                        mHandler.obtainMessage(OnBoardingActivity.MESSAGE_ERROR).sendToTarget();
                        return;
                    }

                    if( !devScan.GetImageSize() )
                    {
                        mHandler.obtainMessage(OnBoardingActivity.MESSAGE_SHOW_MSG,
                                -1, -1, devScan.GetErrorMessage()).sendToTarget();
                        devScan.CloseDeviceUsbHost();
                        mHandler.obtainMessage(OnBoardingActivity.MESSAGE_ERROR).sendToTarget();
                        return;
                    }
                    AttendanceRegisterActivity.InitFingerPictureParameters(devScan.GetImageWidth(), devScan.GetImageHeight());
                    Log.d(LOG_TAG,"Width: "+ devScan.GetImageWidth() + "Height: " + devScan.GetImageHeight());
                    bGetInfo = true;
                }

                bRet = devScan.GetFrame(OnBoardingActivity.mImageFP);
                if( !bRet )
                {
                    mHandler.obtainMessage(OnBoardingActivity.MESSAGE_SHOW_MSG, -1, -1,
                            devScan.GetErrorMessage()).sendToTarget();
                    errCode = devScan.GetErrorCode();
                    if( errCode != devScan.FTR_ERROR_EMPTY_FRAME &&
                            errCode != devScan.FTR_ERROR_MOVABLE_FINGER &&
                            errCode != devScan.FTR_ERROR_NO_FRAME )
                    {
                        devScan.CloseDeviceUsbHost();
                        mHandler.obtainMessage(OnBoardingActivity.MESSAGE_ERROR).sendToTarget();
                        return;
                    }
                }
                else
                {
                    strInfo = "Thumb print captured";
                    mHandler.obtainMessage(OnBoardingActivity.MESSAGE_SHOW_MSG, -1, -1, strInfo ).sendToTarget();
                }
                synchronized (OnBoardingActivity.mSyncObj)
                {
                    //show image
                    mHandler.obtainMessage(OnBoardingActivity.MESSAGE_SHOW_IMAGE).sendToTarget();
                    try {
                        OnBoardingActivity.mSyncObj.wait(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //close device
            devScan.CloseDeviceUsbHost();
        }

        void cancel() {
            OnBoardingActivity.mStop = true;
            try {
                synchronized (OnBoardingActivity.mSyncObj)
                {
                    OnBoardingActivity.mSyncObj.notifyAll();
                }
                this.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
