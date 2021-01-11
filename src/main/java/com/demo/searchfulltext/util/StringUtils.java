package com.demo.searchfulltext.util;

import java.text.Normalizer;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


import java.lang.reflect.Field;
import java.util.List;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtils {

  public static StringBuilder convertStringToVar(String rawString) {

    StringBuilder varConvert = new StringBuilder();
    String stringRemoveAccent = removeAccent(rawString);
    StringTokenizer varToken = new StringTokenizer(stringRemoveAccent);

    int j = 0;
    while (varToken.hasMoreTokens()) {
      char arr[] = varToken.nextToken().toCharArray();
      if (j == 0) {
        arr[0] = Character.toLowerCase(arr[0]);
      } else {
        arr[0] = Character.toUpperCase(arr[0]);
      }
      j++;
      for (int i = 1; i < arr.length; i++) {
        arr[i] = Character.toLowerCase(arr[i]);
      }
      varConvert.append(arr);

    }
    return varConvert;
  }


  public static String removeAccent(String s) {
    String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
  }

  public static String getPathOutput() {
    // Get current executing class working directory.
    String currentWorkingFolder = System.getProperty("user" + StringPool.PERIOD + "dir");
    // Get file path separator.
    String filePathSeperator = System.getProperty("file" + StringPool.PERIOD + "separator");
    // Get the output file absolute path.
    String filePath = currentWorkingFolder + filePathSeperator + "src" + StringPool.BACK_SLASH + "main" + StringPool.BACK_SLASH + "output" + StringPool.BACK_SLASH;
    return filePath;

  }

  public static final String SPECIAL_CHARACTERS = "àÀảẢãÃáÁạẠăĂằẰẳẲẵẴắẮặẶâÂầẦẩẨẫẪấẤậẬđĐèÈẻẺẽẼéÉẹẸêÊềỀểỂễỄếẾệỆìÌỉỈĩĨíÍịỊòÒỏỎõÕóÓọỌôÔồỒổỔỗỖốỐộỘơƠờỜởỞỡỠớỚợỢùÙủỦũŨúÚụỤưƯừỪửỬữỮứỨựỰýÝỹỸỷỶỵỴỳỲ";
  public static final String REPLACEMENTS = "aAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAdDeEeEeEeEeEeEeEeEeEeEeEiIiIiIiIiIoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOuUuUuUuUuUuUuUuUuUuUuUyYyYyYyYyY";
  private static final int INVOICE_MAX_LENGTH = 7;
  private static final String ZERO = "0";
  private static String alphabeUpCaseNumber = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  private StringUtils() {
  }

  public static String replaceVietnameseCharacter(String input) throws Exception {
    try {
      char[] c1 = SPECIAL_CHARACTERS.toCharArray();
      char[] c2 = REPLACEMENTS.toCharArray();
      for (int i = 0; i < c1.length; i++) {
        input = input.replaceAll(c1[i] + "", c2[i] + "");
      }

    } catch (Exception e) {
      log.error("Exception", e);
    }
    return input;
  }

  public static Long convertStringToLong(String key) {
    Long keyConvert = null;
    try {
      if (key != null && !"".equals(key)) {
        keyConvert = Long.parseLong(key);
      }
    } catch (Exception e) {
      log.error("Exception", e);
    }
    return keyConvert;
  }

  public static boolean compareString(String str1, String str2) {
    if (str1 == null) {
      str1 = "";
    }
    if (str2 == null) {
      str2 = "";
    }

    if (str1.equals(str2)) {
      return true;
    }
    return false;
  }

  public static String convertFromLongToString(Long lng) throws Exception {
    try {
      return Long.toString(lng);
    } catch (Exception ex) {
      log.error("Exception", ex);
      throw ex;
    }
  }

  public static String[] convertFromLongToString(Long[] arrLong) throws Exception {
    String[] arrResult = new String[arrLong.length];
    try {
      for (int i = 0; i < arrLong.length; i++) {
        arrResult[i] = convertFromLongToString(arrLong[i]);
      }
      return arrResult;
    } catch (Exception ex) {
      log.error("Exception", ex);
      throw ex;
    }
  }

  public static long[] convertFromStringToLong(String[] arrStr) throws Exception {
    long[] arrResult = new long[arrStr.length];
    try {
      for (int i = 0; i < arrStr.length; i++) {
        arrResult[i] = Long.parseLong(arrStr[i]);
      }
      return arrResult;
    } catch (Exception ex) {
      log.error("Exception", ex);
      throw ex;
    }
  }

  public static long convertFromStringToLong(String value) throws Exception {
    try {
      return Long.parseLong(value);
    } catch (Exception ex) {
      log.error("Exception", ex);
      throw ex;
    }
  }

  public static boolean checkAlphabeUpCaseNumber(String value) {
    boolean result = true;
    for (int i = 0; i < value.length(); i++) {
      String temp = value.substring(i, i + 1);
      if (alphabeUpCaseNumber.indexOf(temp) == -1) {
        result = false;
        return result;
      }
    }
    return result;
  }

  public static String standardInvoiceString(Long input) {
    String temp;
    if (input == null) {
      return "";
    }
    temp = input.toString();
    if (temp.length() <= INVOICE_MAX_LENGTH) {
      int count = INVOICE_MAX_LENGTH - temp.length();
      for (int i = 0; i < count; i++) {
        temp = ZERO + temp;
      }
    }
    return temp;
  }

  public static boolean validString(String temp) {
    if (temp == null || "".equals(temp.trim())) {
      return false;
    }
    return true;
  }

  public static String getSafeFileName(String input) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (c != '/' && c != '\\' && c != 0) {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  public static void escapeHTMLString(Object escapeObject) {
    String oldData = "";
    String newData = "";
    try {
      if (escapeObject != null) {
        Class escapeClass = escapeObject.getClass();

        Field fields[] = escapeClass.getDeclaredFields();
        Field superFields[] = escapeClass.getSuperclass().getDeclaredFields();

        Field allField[] = new Field[fields.length + superFields.length];
        System.arraycopy(fields, 0, allField, 0, fields.length);
        System.arraycopy(superFields, 0, allField, fields.length, superFields.length);

        for (Field f : allField) {
          f.setAccessible(true);
          if (f.getType().equals(String.class)) {
            if (f.get(escapeObject) != null) {
              oldData = f.get(escapeObject).toString();
              newData = escapeHTMLString(oldData);
              f.set(escapeObject, newData);
            }
          } else if (f.getType().isArray()) {
            if (f.getType().getComponentType().equals(String.class)) {
              String[] tmpArr = (String[]) f.get(escapeObject);
              if (tmpArr != null) {
                for (int i = 0; i < tmpArr.length; i++) {
                  tmpArr[i] = escapeHTMLString(tmpArr[i]);
                }
                f.set(escapeObject, tmpArr);
              }
            }
          } else if (f.get(escapeObject) instanceof List) {
            List<Object> tmpList = (List<Object>) f.get(escapeObject);
            for (int i = 0; i < tmpList.size(); i++) {
              if (tmpList.get(i) instanceof String) {
                tmpList.set(i, escapeHTMLString(tmpList.get(i).toString()));
              }
            }
            f.set(escapeObject, tmpList);
          }
        }
      }
    } catch (Exception e) {
      log.error("Exception", e);
    }
  }

  public static String escapeHTMLString(String str) {
    if (str != null) {
      str = str.replaceAll("<", "&lt;");
      str = str.replaceAll(">", "&gt;");
      str = str.replaceAll("\"", "&quot;");
    }
    return str;
  }

  public static void escapeHTMLStringListObject(List escapeObjectList) {
    Object obj;
    for (int i = 0; i < escapeObjectList.size(); i++) {
      obj = escapeObjectList.get(i);
      escapeHTMLString(obj);
      escapeObjectList.set(i, obj);
    }
  }

  public static boolean isNotNull(Object value) {
    if (value == null) {
      return false;
    } else if (value instanceof String && value.toString().trim().length() <= 0) {
      return false;
    }
    return true;

  }

  public static boolean isNumeric(String str) {
    return str.matches("-?\\d+(\\.\\d+)?");
  }

  public static boolean isLong(String str) {
    return str.matches("-?\\d+");
  }

  public static boolean validNumberFormat(String number, int integrantLength,
                                          int decimalLength, String regex) {

    boolean valid = true;

    String[] pieces = number.split(regex);

    if (pieces != null) {
      if (pieces.length > 0 && pieces[0].length() > integrantLength) {
        valid = false;
      } else if (pieces.length > 1 && pieces[1].length() > decimalLength) {
        valid = false;
      }
    } else {
      valid = false;
    }

    return valid;

  }

  public static String escapeHTML(String str) {
    str = str.replaceAll("<", "&lt;");
    str = str.replaceAll(">", "&gt;");
    str = str.replaceAll("\"", "&quot;");
    return str;
  }

  public static boolean validateSQLInjection(String input) {
    if (input != null && !input.trim().isEmpty()) {
      // TODO(tovin07): Remember to escape SQL query
      if (input.equals(input.trim())) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
}
