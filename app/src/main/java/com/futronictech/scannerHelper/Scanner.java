package com.futronictech.scannerHelper;

import android.content.Context;

public class Scanner {
    public native boolean OpenDevice();
    public native boolean CloseDevice();
    public native String GetVersionInfo();
    public native boolean GetImageSize();
    public native boolean IsFingerPresent();
    public native boolean GetFrame(byte[] pImage);
    public native boolean GetImage2(int nDose, byte[] pImage);
    public native boolean SetOptions(int Mask, int Flag);
    public native boolean Save7Bytes(byte[] buffer);
    public native boolean Restore7Bytes(byte[] buffer);
    public native boolean SetNewAuthorizationCode(byte[] SevenBytesAuthorizationCode);
    public native boolean SaveSecret7Bytes(byte[] SevenBytesAuthorizationCode, byte[] buffer);
    public native boolean RestoreSecret7Bytes(byte[] SevenBytesAuthorizationCode, byte[] buffer);
    public native boolean SetDiodesStatus(int GreenDiodeStatus, int RedDiodeStatus );
    public native boolean GetDiodesStatus(byte[] Status); //2 bytes - 1st:Green, 2nd:Red
    public native boolean GetInterfaces(byte[] pInterfaceList);	//need to allocate 128 bytes
    public native boolean OpenDeviceOnInterface(int nInterface);
    public native boolean SetGlobalSyncDir(String SyncDir);
    //testing DL
    public native boolean GetImage(int nDose, byte[] pImage);
    public native boolean GetImageByVariableDose(int nVariableDose, byte[] pImage);
    
    public native boolean SetLogOptions(int mask, int level);
    
    private native boolean OpenDeviceCtx(Object io_ctx);
    
    // const
    static public final int FTR_MAX_INTERFACE_NUMBER = 128;
    static public final byte FTRSCAN_INTERFACE_STATUS_CONNECTED = 0;
    static public final byte FTRSCAN_INTERFACE_STATUS_DISCONNECTED = 1;
    // options
    public final int FTR_ERROR_NO_ERROR = 0;
    public final int FTR_OPTIONS_CHECK_FAKE_REPLICA = 0x00000001;
    public final int FTR_OPTIONS_DETECT_FAKE_FINGER = FTR_OPTIONS_CHECK_FAKE_REPLICA;
    public final int FTR_OPTIONS_IMPROVE_IMAGE = 0x00000020; // for PIV compatible devices
    public final int FTR_OPTIONS_INVERT_IMAGE = 0x00000040;
    // error code
    public final int FTR_ERROR_EMPTY_FRAME = 4306; /* ERROR_EMPTY */
    public final int FTR_ERROR_MOVABLE_FINGER = 0x20000001;
    public final int FTR_ERROR_NO_FRAME = 0x20000002;
    public final int FTR_ERROR_HARDWARE_INCOMPATIBLE = 0x20000004;
    public final int FTR_ERROR_FIRMWARE_INCOMPATIBLE = 0x20000005;
    public final int FTR_ERROR_INVALID_AUTHORIZATION_CODE = 0x20000006;
    public final int FTR_ERROR_WRITE_PROTECT = 19;
    public final int FTR_ERROR_NOT_READY = 21;
    public final int FTR_ERROR_NOT_ENOUGH_MEMORY = 8;
    
    private final int kDefaultDeviceInstance = 0;
    
    public final int  FTR_LOG_MASK_OFF = 0;
    public final int  FTR_LOG_MASK_TO_FILE = 0x00000001;
    public final int  FTR_LOG_MASK_TO_AUX = 0x00000002;
    public final int  FTR_LOG_MASK_TIMESTAMP = 0x00000004;
    public final int  FTR_LOG_MASK_THREAD_ID = 0x00000008;
    public final int  FTR_LOG_MASK_PROCESS_ID = 0x00000010;

    public final int  FTR_LOG_LEVEL_MIN = 0;
    public final int  FTR_LOG_LEVEL_OPTIMAL = 1;
    public final int  FTR_LOG_LEVEL_FULL = 2;

    public Scanner()
    {
    	m_hDevice = 0;
    	m_ImageWidth = m_ImageHeight = 0;
    }
    
    public int GetImageWidth()
    {
        return m_ImageWidth;
    }
    public int GetImageHeight()
    {
        return m_ImageHeight;
    }

    public int GetErrorCode()
    {
    	return m_ErrorCode;
    }

    public String GetErrorMessage()
    {
        String strErrMsg;
        switch(m_ErrorCode)
        {
            case FTR_ERROR_NO_ERROR:
                strErrMsg = "OK";
                break;
            case FTR_ERROR_EMPTY_FRAME:
                strErrMsg = "Empty Frame";
                break;
            case FTR_ERROR_MOVABLE_FINGER:
                strErrMsg = "Moveable Finger";
                break;
            case FTR_ERROR_NO_FRAME:
                strErrMsg = "Fake Finger";
                break;
            case FTR_ERROR_HARDWARE_INCOMPATIBLE:
                strErrMsg = "Hardware Incompatible";
                break;
            case FTR_ERROR_FIRMWARE_INCOMPATIBLE:
                strErrMsg = "Firmware Incompatible";
                break;
            case FTR_ERROR_INVALID_AUTHORIZATION_CODE:
                strErrMsg = "Invalid Authorization Code";
                break;
            case FTR_ERROR_WRITE_PROTECT:
                strErrMsg = "Write Protect";
                break;
            default:
                strErrMsg = String.format("Error code is %d", m_ErrorCode);
                break;
        }
        return strErrMsg;
    }
    
    public boolean OpenDeviceOnInterfaceUsbHost( UsbDeviceDataExchangeImpl usb_host_ctx )
    {
    	boolean res = false;
    	
    	synchronized(this)
    	{
    		res = OpenDeviceCtx(usb_host_ctx);
    	}
    	
    	return res;
    }
    
            
    public boolean GetInterfacesUsbHost(Context ctx, byte[] pInterfaceList)
    {
    	if( pInterfaceList.length < FTR_MAX_INTERFACE_NUMBER )
    	{
    		m_ErrorCode = FTR_ERROR_NOT_ENOUGH_MEMORY;
    		return false;
    	}
    	
    	UsbDeviceDataExchangeImpl.GetInterfaces(ctx, pInterfaceList);
    	
    	return true;
    }
    
    public void CloseDeviceUsbHost()
    {
    	synchronized(this)
    	{
    		CloseDevice();
    	}
    }
     
    public long GetDeviceHandle()
    {
    	return m_hDevice;
    }

    static {
    System.loadLibrary("usb-1.0");
    }
    static {
    System.loadLibrary("ftrScanAPI");
    }
    static {
    System.loadLibrary("ftrScanApiAndroidJni");
    }
    
    private int m_ImageWidth;
    private int m_ImageHeight;
    private long m_hDevice;
    private int m_ErrorCode;
   
}
