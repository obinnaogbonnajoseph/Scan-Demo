package com.futronictech.scannerHelper;

import android.os.Handler;
import android.util.Log;

import com.futronictech.ui.AttendanceRegisterActivity;

import static com.futronictech.ui.AttendanceRegisterActivity.LOG_TAG;

public class FPScan {
    private final Handler mHandler;
    private ScanThread mScanThread;
    private UsbDeviceDataExchangeImpl ctx;
    
    public FPScan(UsbDeviceDataExchangeImpl context, Handler handler) {
        mHandler = handler;
        ctx = context;
    }

	/**
	 * This method starts the thread to run the scanning
	 * This is a synchronized method. It runs only one instance, all through the app
	 */
	public synchronized void start() {
        if (mScanThread == null) {
        	mScanThread = new ScanThread();
        	mScanThread.start();
        }
    }
    
    public synchronized void stop() {
        if (mScanThread != null) {mScanThread.cancel(); mScanThread = null;}
    }
    
    private class ScanThread extends Thread {
		// Field variables
    	private boolean bGetInfo;
    	private Scanner devScan;
    	private String strInfo;
    	private int mask, flag;
    	private int errCode;
    	private boolean bRet;
    	
        ScanThread() {
            // initialize some field variables
        	bGetInfo=false;
        	devScan = new Scanner();
        }

        // Thread operations are done here
        public void run() {
            while (!AttendanceRegisterActivity.mStop)
            {
            	if(!bGetInfo)
            	{
            		boolean bRet;
                    bRet = devScan.OpenDeviceOnInterfaceUsbHost(ctx);
         	        // If usb is closed for any reason, run the operation below
                    if( !bRet )
                    {
                        ctx.CloseDevice();
                        // Send a message via the handler to the ui thread
                        mHandler.obtainMessage(AttendanceRegisterActivity.MESSAGE_SHOW_MSG, -1, -1, devScan.GetErrorMessage()).sendToTarget();
                        mHandler.obtainMessage(AttendanceRegisterActivity.MESSAGE_ERROR).sendToTarget();
                        return;
                    }

                    // If the image view is not ready for any reason, report an error
            		if( !devScan.GetImageSize() )
	    	        {
	    	        	mHandler.obtainMessage(AttendanceRegisterActivity.MESSAGE_SHOW_MSG, -1, -1, devScan.GetErrorMessage()).sendToTarget();
                        devScan.CloseDeviceUsbHost();
                        mHandler.obtainMessage(AttendanceRegisterActivity.MESSAGE_ERROR).sendToTarget();
	    	            return;
	    	        }

	    	        // Initializes the view to show the scanner input
	    	        AttendanceRegisterActivity.InitFingerPictureParameters(devScan.GetImageWidth(), devScan.GetImageHeight());
                    Log.d(LOG_TAG,"Width: "+ devScan.GetImageWidth() + "Height: " + devScan.GetImageHeight());
	    	        bGetInfo = true;            	
             	}

             	// Get the frame for the capturing
                bRet = devScan.GetFrame(AttendanceRegisterActivity.mImageFP);
                if( !bRet )
                {
                    // Properly show the error messages
                	mHandler.obtainMessage(AttendanceRegisterActivity.MESSAGE_SHOW_MSG, -1, -1, devScan.GetErrorMessage()).sendToTarget();
                	errCode = devScan.GetErrorCode();
                	if( errCode != devScan.FTR_ERROR_EMPTY_FRAME && errCode != devScan.FTR_ERROR_MOVABLE_FINGER &&  errCode != devScan.FTR_ERROR_NO_FRAME )
                	{
                        devScan.CloseDeviceUsbHost();
                        mHandler.obtainMessage(AttendanceRegisterActivity.MESSAGE_ERROR).sendToTarget();
	    	            return;                		
                	}    	        	
                }
                else    // Thumb print captured successfully.
                {
                    strInfo = "Thumb print captured";
                	mHandler.obtainMessage(AttendanceRegisterActivity.MESSAGE_SHOW_MSG, -1, -1, strInfo ).sendToTarget();
                }
				synchronized (AttendanceRegisterActivity.mSyncObj)
                {
					//show image
					mHandler.obtainMessage(AttendanceRegisterActivity.MESSAGE_SHOW_IMAGE).sendToTarget();
					try {
						AttendanceRegisterActivity.mSyncObj.wait(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }
            }
            //close device
            devScan.CloseDeviceUsbHost();
        }

        void cancel() {
        	AttendanceRegisterActivity.mStop = true;
        	try {
        		synchronized (AttendanceRegisterActivity.mSyncObj)
		        {
        			AttendanceRegisterActivity.mSyncObj.notifyAll();
		        }        		
        		this.join();	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
       	      	           	
        }
    }    
}
