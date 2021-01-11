package com.demo.searchfulltext.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import net.sf.json.JSONObject;


/**
 * Json Util
 *
 * @author hungnv.iist@gmail.com
 * @date 19/7/2019
 */
public class JsonUtils {
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String INDEX_BEGIN_HEADER = "indexBeginHeader";
  private static ExcelUtil ExcelUtils;

  /**
   * pasrsing file json throw node
   *
   * @param pathFileExcel
   * @param fileName
   * @param obj
   */
  public static void parsingJsonFileForNode(String pathFileExcel, String fileName, Object obj) {
    String filePath = com.demo.searchfulltext.util.StringUtils.getPathOutput() + fileName;
    Class<?> clazz = obj.getClass();
    List<String> listNodeRemove = new ArrayList<String>();
    List<String> listNodeRow = ExcelUtils.getRow(pathFileExcel, obj);
    List<String> nodes = new ArrayList<String>();
    List<String> filterNode = new ArrayList<String>();
    ObjectMapper objectMapper = new ObjectMapper();

    for (String nodeRaw : listNodeRow) {
      StringBuilder node = com.demo.searchfulltext.util.StringUtils.convertStringToVar(nodeRaw);
      if (!node.toString().trim().equals(StringPool.BLANK)) {
        nodes.add(node.toString().trim());
      }

    }

    for (Field field : clazz.getDeclaredFields()) {
      Element element = field.getAnnotation(Element.class);
      if (element.equals(null)) {
        listNodeRemove.add(element.name());
      }
    }

    filterNode = nodes.stream().filter(e -> !listNodeRemove.contains(e)).collect(Collectors.toList());


    try {
      JsonNode rootNode = mapper.readTree(new File(filePath));
      for (JsonNode root : rootNode) {
        ((ObjectNode) root).remove(filterNode);
      }
      objectMapper.writeValue(new File(com.demo.searchfulltext.util.StringUtils.getPathOutput() + "outputfile.json"), rootNode);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * method get data return json
   *
   * @param dataTable: type List<List<String>>
   * @return string json
   * get data return json from list data
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static String getJSONStringFromList(List<List<String>> dataTable, Object obj) {
    String ret = StringPool.BLANK;
    String nodeName = StringPool.BLANK;
    int indexBeginHeader = 0;
    int rowCount = dataTable.size();
    Class<?> clazz = obj.getClass();

    for (Field field : clazz.getDeclaredFields()) {
      Element element = field.getAnnotation(Element.class);
      if (element.name().equals(INDEX_BEGIN_HEADER)) {
        indexBeginHeader = element.indexBeginHeader();
        break;
      }
    }

    if (!dataTable.equals(null)) {
      if (rowCount > 1) {
        //Create a JSONObject to store table data.
        JSONObject tableJsonObject = new JSONObject();
        // The first row is the header row, store each column name.
        List<String> headerRowsRaw = dataTable.get(indexBeginHeader);
        // The child header row
        List<String> childHeaderRowsRaw = dataTable.get(indexBeginHeader++);

        // convert vietkey to variable java
        List<String> headerRows = new ArrayList<String>();
        for (String headerRowRaw : headerRowsRaw) {
          StringBuilder headerRow = com.demo.searchfulltext.util.StringUtils.convertStringToVar(headerRowRaw);
          headerRows.add(headerRow.toString());
        }

        // convert vietkey to variable java
        List<String> childHeaderRows = new ArrayList<String>();
        for (String childHeaderRowRaw : childHeaderRowsRaw) {
          StringBuilder childHeaderRow = com.demo.searchfulltext.util.StringUtils.convertStringToVar(childHeaderRowRaw);
          childHeaderRows.add(childHeaderRow.toString());
        }

        JSONArray jsonArray = new JSONArray();
        for (int i = 5; i < rowCount; i++) {
          // Create a JSONObject object to store row data.
          JSONObject rowJsonObjectChild = new JSONObject();
          JSONObject rowJsonObject = new JSONObject();

          List<String> dataRow = dataTable.get(i);
          for (int j = 0; j < headerRows.size(); j++) {
            String columnObjectKey = childHeaderRows.get(j);
            String columnObjectValue = dataRow.get(j);
            String columnKey = headerRows.get(j);
            String columnValue = dataRow.get(j);

            if (!columnKey.equals(StringPool.BLANK)) {
              rowJsonObject.clear();
              nodeName = headerRows.get(j);
              rowJsonObjectChild.put(columnKey, columnValue);
            }

            if (!childHeaderRows.get(j).equals(StringPool.BLANK)) {
              rowJsonObject.put(columnObjectKey, columnObjectValue);
              rowJsonObjectChild.put(nodeName, rowJsonObject);
            }

          }

          tableJsonObject.put(i, rowJsonObjectChild);
          jsonArray.put(rowJsonObjectChild);
        }

        ret = jsonArray.toString();
      }
    }
    return ret;
  }

  /**
   * create json file from excel
   *
   * @param + excel file path <br>
   *          + object annotation use config index
   * @return sheetDataTable
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static List<List<String>> creteJSONFileFromExcel(String excelFilePath, Object obj) {
    List<List<String>> sheetDataTable = new ArrayList<List<String>>();
    try {
      Workbook excelWorkBook = ExcelUtils.getWorkbook(excelFilePath);
      // Get all excel sheet count.
      int totalSheetNumber = excelWorkBook.getNumberOfSheets();
      for (int i = 0; i < totalSheetNumber; i++) {
        // Get current sheet.
        Sheet sheet = excelWorkBook.getSheetAt(i);
        // Get sheet name.
        String sheetName = sheet.getSheetName();
        if (sheetName != null && sheetName.length() > 0) {
          sheetDataTable = ExcelUtils.getSheetDataList(sheet);
          // Generate JSON format of above sheet data and write to a JSON file.
          String jsonString = getJSONStringFromList(sheetDataTable, obj);
          String jsonFileName = ExcelUtils.getSheetNameWithLimit(sheetName) + StringPool.PERIOD + StringPool.SUFFIX_JSON;
          ExcelUtils.writeStringToFile(jsonString, jsonFileName);
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sheetDataTable;
  }

  static void jsonObjectClear(org.json.JSONObject jsonObject) {
    Iterator<String> keys = (Iterator<String>) jsonObject.keys();
    while (keys.hasNext()) {
      keys.next();
      keys.remove();
    }
  }

  static Iterator<String> jsonObjectListKey(org.json.JSONObject jsonObject) {
    Iterator<String> keys = (Iterator<String>) jsonObject.keys();
    while (keys.hasNext()) {
      keys.next();
    }
    return keys;
  }


  static boolean jsonObjectContainsValue(org.json.JSONObject jsonObject, Object value) {
    Iterator<String> keys = (Iterator<String>) jsonObject.keys();
    while (keys.hasNext()) {
      Object thisValue = jsonObject.opt(keys.next());
      if (thisValue != null && thisValue.equals(value)) {
        return true;
      }
    }
    return false;
  }

  private final static class JSONObjectEntry implements Map.Entry<String, Object> {
    private final String key;
    private final Object value;

    JSONObjectEntry(String key, Object value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return this.key;
    }

    public Object getValue() {
      return this.value;
    }

    public Object setValue(Object value) {
      throw new UnsupportedOperationException("JSONObjectEntry is immutable");
    }


  }

  static Set<Map.Entry<String, Object>> jsonObjectEntrySet(org.json.JSONObject jsonObject) {
    HashSet<Map.Entry<String, Object>> result = new HashSet<Map.Entry<String, Object>>();

    Iterator<String> keys = (Iterator<String>) jsonObject.keys();
    while (keys.hasNext()) {
      String key = keys.next();
      Object value = jsonObject.opt(key);
      result.add(new JSONObjectEntry(key, value));
    }

    return result;
  }

  static Set<String> jsonObjectKeySet(org.json.JSONObject jsonObject) {
    HashSet<String> result = new HashSet<String>();

    Iterator<String> keys = (Iterator<String>) jsonObject.keys();
    while (keys.hasNext()) {
      result.add(keys.next());
    }

    return result;
  }

  static void jsonObjectPutAll(org.json.JSONObject jsonObject, Map<String, Object> map) {
    Set<Map.Entry<String, Object>> entrySet = map.entrySet();
    for (Map.Entry<String, Object> entry : entrySet) {
      try {
        jsonObject.putOpt(entry.getKey(), entry.getValue());
      } catch (JSONException e) {
        throw new IllegalArgumentException(e);
      }
    }
  }

  static Collection<Object> jsonObjectValues(org.json.JSONObject jsonObject) {
    ArrayList<Object> result = new ArrayList<Object>();

    Iterator<String> keys = (Iterator<String>) jsonObject.keys();
    while (keys.hasNext()) {
      result.add(jsonObject.opt(keys.next()));
    }

    return result;
  }
}
