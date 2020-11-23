package com.xixi.sdk.utils.housekeeper;

public class ByteUtils {

   public static String getStringHWM(int[] bytes){
       StringBuilder sb = new StringBuilder();
       for (int aSearch : bytes) {
           sb.append(aSearch);
           sb.append(",");
       }
       sb.delete(sb.length() - 1, sb.length());
       return sb.toString();
   }
}
