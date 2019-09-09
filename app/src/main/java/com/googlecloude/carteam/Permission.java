package com.googlecloude.carteam;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class Permission extends ActivityCompat{
    /*
     * 確認是否要權限(API > 23)
     * API < 23 一律不詢問
     */
    public static boolean needCheckPermission(String[] permissions){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //String[] perms = new String[]{PERMISSION_CAMERA};
            int permsRequestCode = 200;
            requestPermissions(MyContext.getContext(), permissions, permsRequestCode);
            return true;
        }
        return false;
    }

    /*
     * 檢查是否有權限
     * API < 23 一律回傳true
     */
    public static boolean hasPermission(String[] permissions){
        boolean permission = false;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for(int x = 0 ;x < permissions.length;x++)
                permission = permission && (checkSelfPermission(MyContext.getContext(), permissions[x]) == PackageManager.PERMISSION_GRANTED);
            return permission;
        }
        return true;
    }
    public static boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return (checkSelfPermission(MyContext.getContext(), permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    public static void RequestResult(int[] grantResults, String[] permissions_zh){
        boolean permission = false;
        if(grantResults.length > 0) {
            for(int x = 0; x < grantResults.length; x++) {
                if (grantResults[x] == PackageManager.PERMISSION_GRANTED) {
                    permission |= true;
                    if(Debug.ENABLE) Toast.makeText(MyContext.getContext(), "取得" + permissions_zh[x] + "權限", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[x] == PackageManager.PERMISSION_DENIED) {
                    permission |= false;
                    if(Debug.ENABLE) Toast.makeText(MyContext.getContext(), "未取得" + permissions_zh[x] + "權限", Toast.LENGTH_SHORT).show();
                }
            }

            if(permission){
                Toast.makeText(MyContext.getContext(), "取得所有權限", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MyContext.getContext(), "未取得所有權限，結束程式", Toast.LENGTH_SHORT).show();
                MyContext.getContext().finish();
            }
        }
        else{
            Toast.makeText(MyContext.getContext(), "未取得所有權限，結束程式", Toast.LENGTH_SHORT).show();
            MyContext.getContext().finish();
        }
    }
}
