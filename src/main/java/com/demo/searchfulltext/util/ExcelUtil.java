package com.demo.searchfulltext.util;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.demo.searchfulltext.util.Element;


import net.sf.json.JSONObject;

/**
 * @author HungNV
 * <p>
 * This library use execute with excel file
 * <p/>
 * @author hungnv.iist@gmail.com
 * @date 19/7/2019
 */

/**
 * @author computer
 */
public class ExcelUtil {
  private static final char[] EXCEL_SHEET_NAME_INVALID_CHARS = {'/', '\\', '?', '*', ']', '[', ':'};
  private static final char INVALID_REPLACE_CHAR = '_';

  /**
   * @param row
   * @param colIndex
   * @return
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static String getStringCellValue(Row row, int colIndex) {
    String result = null;
    if (row != null) {
      Cell cell = row.getCell(colIndex);
      if (cell != null) {
        try {
          result = cell.getStringCellValue();
        } catch (IllegalStateException e) {
          result = String.valueOf(getNumericCellValue(row, colIndex));
        }
      }
    }
    return result;
  }

  /**
   * @param row
   * @param colIndex
   * @return
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static double getNumericCellValue(Row row, int colIndex) {
    double result = -1;
    if (row != null) {
      Cell cell = row.getCell(colIndex);
      if (cell != null) {
        try {
          result = cell.getNumericCellValue();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }

  /**
   * method get 1 row with index row
   *
   * @param sheet
   * @param rowIndex
   * @return Row
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static Row getRow(Sheet sheet, int rowIndex) {
    Row row = sheet.getRow(rowIndex);
    if (row == null) {
      row = sheet.createRow(rowIndex);
    }
    return row;
  }

  public static List<String> getRow(String excelFilePath, Object obj) {
    int indexBeginHeader = 0;
    List<String> rows = new ArrayList<String>();
    Class<?> clazz = obj.getClass();
    for (Field field : clazz.getDeclaredFields()) {
      Element element = field.getAnnotation(Element.class);
      if (element.name().equals("indexBeginHeader")) {
        indexBeginHeader = element.indexBeginHeader();
        break;
      }
    }
    rows = getListToExcel(excelFilePath, obj).get(indexBeginHeader);
    return rows;
  }

  public static List<List<String>> getListToExcel(String excelFilePath, Object obj) {
    List<List<String>> sheetDataTable = new ArrayList<List<String>>();
    try {
      Workbook excelWorkBook = getWorkbook(excelFilePath);
      // Get all excel sheet count.
      int totalSheetNumber = excelWorkBook.getNumberOfSheets();
      for (int i = 0; i < totalSheetNumber; i++) {
        // Get current sheet.
        Sheet sheet = excelWorkBook.getSheetAt(i);
        // Get sheet name.
        String sheetName = sheet.getSheetName();
        if (sheetName != null && sheetName.length() > 0) {
          sheetDataTable = getSheetDataList(sheet);
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sheetDataTable;
  }


  /**
   * method get cell with index row and index column
   *
   * @param row
   * @param columnIndex
   * @return cell
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static Cell getCell(Row row, int columnIndex) {
    Cell cell = row.getCell(columnIndex);
    if (cell == null) {
      cell = row.createCell(columnIndex);
    }
    return cell;
  }

  /**
   * set value double for cell
   *
   * @param row
   * @param columnIndex
   * @param value
   * @return cell value double
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static Cell setCellValue(Row row, int columnIndex, double value) {
    Cell cell = getCell(row, columnIndex);

    cell.setCellValue(value);

    return cell;
  }

  /**
   * @param row
   * @param columnIndex
   * @param value
   * @return
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static Cell setCellValue(Row row, int columnIndex, String value) {
    Cell cell = getCell(row, columnIndex);
    try {
      cell.setCellValue(value);
    } catch (IllegalArgumentException ex) {
      ex.printStackTrace();
      cell.setCellValue(value.substring(0, 32767));

    }
    return cell;
  }

  /**
   * @param sheet
   * @param fromRow
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void removeRows(Sheet sheet, int fromRow) {
    removeRows(sheet, fromRow, -1);
  }

  /**
   * @param sheet
   * @param fromRow
   * @param toRow
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void removeRows(Sheet sheet, int fromRow, int toRow) {

    while (true) {

      Row row = sheet.getRow(fromRow++);

      if (row == null) {
        break;
      } else if (toRow >= 0 && fromRow == toRow) {
        break;
      }
      sheet.removeRow(row);
    }

  }

  /**
   * @param workbook
   * @param sheetName
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void moveToLast(Workbook workbook, String sheetName) {
    workbook.setSheetOrder(sheetName, workbook.getNumberOfSheets() - 1);
  }

  /**
   * @param workbook
   * @param sheetName
   * @return
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static int removeSheet(Workbook workbook, String sheetName) {

    int sheetIndex = workbook.getSheetIndex(sheetName);

    if (sheetIndex >= 0) {
      workbook.removeSheetAt(sheetIndex);
    }

    return sheetIndex;
  }

  /**
   * @param workbook
   * @param sheetName
   * @param pos
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void moveTo(Workbook workbook, String sheetName, int pos) {
    workbook.setSheetOrder(sheetName, pos);
  }

  /**
   * @param rawSheetname
   * @return
   */
  public static String getSheetNameWithLimit(String rawSheetname) {
    return getSheetNameWithLimit(rawSheetname, false);
  }

  /**
   * @param rawSheetname
   * @param right
   * @return
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static String getSheetNameWithLimit(String rawSheetname, boolean right) {

    String sheetname = right ? StringUtils.right(rawSheetname, 31) : StringUtils.left(rawSheetname, 31);

    // Replace invalid characters
    for (char c : EXCEL_SHEET_NAME_INVALID_CHARS) {
      sheetname = StringUtils.replaceChars(sheetname, c, INVALID_REPLACE_CHAR);
    }

    return sheetname;

  }

  /**
   * @param prefix
   * @param before
   * @return
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static String getSheetNameWithLimit(String prefix, String before) {
    String result = before;

    if (!StringUtils.isEmpty(before)) {
      before = before.trim();
      if (before.length() > 31) {
        if (before.indexOf(prefix) == 0) {
          result = before.substring(prefix.length());
        } else {
          result = before;
        }
      }
    }

    if (!StringUtils.isEmpty(result) && result.length() > 31) {
      result = result.substring(result.length() - 31);
    }
    return result;

  }

  public static void addValidationData(Sheet sheet, String listFormula, int firstRow, int lastRow, int colIndex) {
    addValidationData(sheet, listFormula, firstRow, lastRow, colIndex, colIndex);
  }

  /**
   * @param sheet
   * @param listFormula
   * @param firstRow
   * @param lastRow
   * @param firstCol
   * @param lastCol
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void addValidationData(Sheet sheet, String listFormula, int firstRow, int lastRow, int firstCol,
                                       int lastCol) {

    DataValidationHelper dataValidationHelper = null;
    if (sheet instanceof XSSFSheet) {

      dataValidationHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);

    } else if (sheet instanceof HSSFSheet) {

      dataValidationHelper = new HSSFDataValidationHelper((HSSFSheet) sheet);

    }

    DataValidationConstraint dvConstraint = dataValidationHelper.createFormulaListConstraint(listFormula);

    CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);

    DataValidation validation = dataValidationHelper.createValidation(dvConstraint, addressList);

    sheet.addValidationData(validation);
  }

  /**
   * @param sheet
   * @param firstRow
   * @param lastRow
   * @param firstCol
   * @param lastCol
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void mergeCell(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
    sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
  }

  /**
   * @param cellStyle
   */
  public static void setThinBorder(CellStyle cellStyle) {
    setBorder(cellStyle, BorderStyle.THIN);
  }

  /**
   * @param cellStyle
   * @param borderStyle
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void setBorder(CellStyle cellStyle, BorderStyle borderStyle) {

    short borderColor = IndexedColors.BLACK.getIndex();

    setBorder(cellStyle, borderStyle, borderStyle, borderStyle, borderStyle, borderColor, borderColor, borderColor,
            borderColor);
  }

  public static CellStyle getCellStyle(Workbook workbook, Cell cell) {

    CellStyle newCellStyle = workbook.createCellStyle();
    CellStyle currentCellStyle = cell.getCellStyle();

    if (currentCellStyle != null) {
      newCellStyle.cloneStyleFrom(currentCellStyle);
    }

    return newCellStyle;

  }

  /**
   * @param cellStyle
   * @param top
   * @param right
   * @param bottom
   * @param left
   * @param topColor
   * @param rightColor
   * @param bottomColor
   * @param leftColor
   */
  public static void setBorder(CellStyle cellStyle, BorderStyle top, BorderStyle right, BorderStyle bottom,
                               BorderStyle left, short topColor, short rightColor, short bottomColor, short leftColor) {

    cellStyle.setBorderTop(top);
    cellStyle.setBorderRight(right);
    cellStyle.setBorderBottom(bottom);
    cellStyle.setBorderLeft(left);

    cellStyle.setTopBorderColor(topColor);
    cellStyle.setRightBorderColor(rightColor);
    cellStyle.setBottomBorderColor(bottomColor);
    cellStyle.setLeftBorderColor(leftColor);

  }

  /**
   * @param cellStyle
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void setCellAlignmentTopCenter(CellStyle cellStyle) {
    setCellAlignment(cellStyle, VerticalAlignment.TOP, HorizontalAlignment.CENTER);
  }

  /**
   * @param cellStyle
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void setCellAlignmentCenterLeft(CellStyle cellStyle) {
    setCellAlignment(cellStyle, VerticalAlignment.CENTER, HorizontalAlignment.LEFT);
  }

  /**
   * @param cellStyle
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void setCellAlignmentCenter(CellStyle cellStyle) {
    setCellAlignment(cellStyle, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
  }

  /**
   * @param cellStyle
   * @param verticalAlignment
   * @param horizontalAlignment
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void setCellAlignment(CellStyle cellStyle, VerticalAlignment verticalAlignment,
                                      HorizontalAlignment horizontalAlignment) {

    cellStyle.setVerticalAlignment(verticalAlignment);
    cellStyle.setAlignment(horizontalAlignment);

  }

  /**
   * @param workbook
   * @param cellStyle
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void setHyperLinkStyle(Workbook workbook, CellStyle cellStyle) {

    Font font = workbook.createFont();

    font.setColor(IndexedColors.BLUE.getIndex());

    cellStyle.setFont(font);

  }

  /**
   * @param workbook
   * @param row
   * @param columnIndex
   * @param cellContent
   * @param filePath
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void setHyperLinkToFile(Workbook workbook, Row row, int columnIndex, String cellContent,
                                        String filePath) {

    // Get workbook creation helper
    CreationHelper creationHelper = workbook.getCreationHelper();

    // Create new hyperlink
    XSSFHyperlink hyperlink = (XSSFHyperlink) creationHelper.createHyperlink(HyperlinkType.FILE);

    // Set address value
    hyperlink.setAddress(filePath);

    // Set value and the link of cell
    Cell cell = setCellValue(row, columnIndex, cellContent);
    cell.setHyperlink(hyperlink);

    // Update hyperlink style
    CellStyle cellStyle = getCellStyle(workbook, cell);
    setHyperLinkStyle(workbook, cellStyle);
    cell.setCellStyle(cellStyle);

  }

  /**
   * @param workbook
   * @param row
   * @param columnIndex
   * @param cellContent
   * @param linkToSheetName
   * @param linkToCellIndex
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static void setHyperLinkToSheet(Workbook workbook, Row row, int columnIndex, String cellContent,
                                         String linkToSheetName, String linkToCellIndex) {

    // Get workbook creation helper
    CreationHelper creationHelper = workbook.getCreationHelper();

    // Create new hyperlink
    XSSFHyperlink hyperlink = (XSSFHyperlink) creationHelper.createHyperlink(HyperlinkType.DOCUMENT);

    // Build address value
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("'");
    stringBuilder.append(getSheetNameWithLimit(linkToSheetName));
    stringBuilder.append("'");
    stringBuilder.append("!");
    stringBuilder.append(linkToCellIndex);

    // Set address value
    String address = stringBuilder.toString();
    hyperlink.setAddress(address);

    // Set value and the link of cell
    Cell cell = setCellValue(row, columnIndex, cellContent);
    cell.setHyperlink(hyperlink);

    // Update hyperlink style
    CellStyle cellStyle = getCellStyle(workbook, cell);
    setHyperLinkStyle(workbook, cellStyle);
    cell.setCellStyle(cellStyle);
  }

  /**
   * @param colName
   * @return
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static int getColIndexByName(String colName) {
    int index = 0;
    String upper = colName.toUpperCase();
    char[] arr = upper.toCharArray();
    index = arr[0] - 'A';
    return index;
  }

  /**
   * @param excelFilePath
   * @return
   * @throws IOException
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static Workbook getWorkbook(String excelFilePath) throws IOException {
    Workbook workbook = null;
    if (excelFilePath.endsWith("xlsx")) {
      workbook = new XSSFWorkbook(new FileInputStream(excelFilePath));
    } else if (excelFilePath.endsWith("xls")) {
      workbook = new HSSFWorkbook(new FileInputStream(excelFilePath));
    } else {
      throw new IllegalArgumentException("The specified file is not Excel file");
    }
    return workbook;
  }

  /**
   * @param excelFilePath
   * @param beginRow
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static ArrayList<String> reading(String excelFilePath, int beginRow) {
    Workbook workbook;
    ArrayList<String> cells = new ArrayList<String>();
    try {
      workbook = getWorkbook(excelFilePath);

      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        Sheet sheet = workbook.getSheetAt(i);
        String rawSheetname = sheet.getSheetName();
        getSheetNameWithLimit(rawSheetname);

        for (Row row : sheet) {
          if (row.getRowNum() >= beginRow) {
            for (Cell cell : row) {
              cells.add(cell.toString().trim());
            }
          }
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return cells;

  }

  /**
   * @param excelFilePath
   * @param beginRow
   * @param endRow
   * @return
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static ArrayList<String> reading(String excelFilePath, int beginRow, int endRow) {
    Workbook workbook;
    ArrayList<String> cells = new ArrayList<String>();
    try {
      workbook = getWorkbook(excelFilePath);

      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        Sheet sheet = workbook.getSheetAt(i);
        String rawSheetname = sheet.getSheetName();
        getSheetNameWithLimit(rawSheetname);

        for (Row row : sheet) {
          if (row.getRowNum() >= beginRow && row.getRowNum() <= endRow) {
            for (Cell cell : row) {
              cells.add(cell.toString().trim());
            }
          }
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return cells;

  }

  /**
   * @param excelFilePath
   * @param oneRow
   * @return
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static ArrayList<String> readingOneRow(String excelFilePath, int oneRow) {
    Workbook workbook;
    ArrayList<String> cells = new ArrayList<String>();
    try {
      workbook = getWorkbook(excelFilePath);

      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        Sheet sheet = workbook.getSheetAt(i);
        String rawSheetname = sheet.getSheetName();
        getSheetNameWithLimit(rawSheetname);

        for (Row row : sheet) {
          if (row.getRowNum() == oneRow) {
            for (Cell cell : row) {
              cells.add(cell.toString().trim());
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cells;
  }


  /**
   * write string to file
   *
   * @param data
   * @param fileName
   */
  public static void writeStringToFile(String data, String fileName) {
    try {

      // Get the output file absolute path.
      String filePath = com.demo.searchfulltext.util.StringUtils.getPathOutput() + fileName;

      // Create File, FileWriter and BufferedWriter object.
      File file = new File(filePath);

      FileWriter fw = new FileWriter(file);

      BufferedWriter buffWriter = new BufferedWriter(fw);

      // Write string data to the output file, flush and close the buffered writer object.
      buffWriter.write(data);

      buffWriter.flush();

      buffWriter.close();

      System.out.println(filePath + " has been created.");

    } catch (IOException ex) {
      System.err.println(ex.getMessage());
    }
  }

  /**
   * get JSON from list
   *
   * @param dataTable
   * @return String
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static String getJSONStringFromList(List<List<String>> dataTable) {
    String ret = StringPool.BLANK;
    if (dataTable != null) {
      int rowCount = dataTable.size();
      if (rowCount > 1) {
        //Create a JSONObject to store table data.
        JSONObject tableJsonObject = new JSONObject();
        // The first row is the header row, store each column name.
        List<String> headerRowsRaw = dataTable.get(2);
        // The child header row
        List<String> childHeaderRowsRaw = dataTable.get(3);

        List<String> headerRows = new ArrayList<String>();
        for (String headerRowRaw : headerRowsRaw) {
          StringBuilder headerRow = com.demo.searchfulltext.util.StringUtils.convertStringToVar(headerRowRaw);
          headerRows.add(headerRow.toString());
        }

        List<String> childHeaderRows = new ArrayList<String>();
        for (String childHeaderRowRaw : childHeaderRowsRaw) {
          StringBuilder childHeaderRow = com.demo.searchfulltext.util.StringUtils.convertStringToVar(childHeaderRowRaw);
          childHeaderRows.add(childHeaderRow.toString());
        }

        // Loop in the row data list.
        for (int i = 5; i < rowCount; i++) {
          // Create a JSONObject object to store row data.

          JSONObject rowJsonObjectChild = new JSONObject();
          JSONObject rowJsonObject = new JSONObject();
          String nodeName = StringPool.BLANK;
          List<String> dataRow = dataTable.get(i);

          for (int j = 0; j < headerRows.size(); j++) {

            String columnKey = headerRows.get(j);
            String columnValue = dataRow.get(j);

            String columnObjectKey = childHeaderRows.get(j);
            String columnObjectValue = dataRow.get(j);
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

          tableJsonObject.putAll(rowJsonObjectChild);
        }
        ret = tableJsonObject.toString();
      }
    }
    return ret;
  }


  /**
   * get data to sheet from list nest list
   *
   * @param sheet
   * @return lists
   * @author hungnv.iist@gmail.com
   * @date 19/7/2019
   */
  public static List<List<String>> getSheetDataList(Sheet sheet) {
    List<List<String>> lists = new ArrayList<List<String>>();
    int firstRowNum = sheet.getFirstRowNum();
    int lastRowNum = sheet.getLastRowNum();
    if (lastRowNum > 0) {
      for (int i = firstRowNum; i < lastRowNum + 1; i++) {
        // Get current row object.
        Row row = sheet.getRow(i);
        // Get first and last cell number.
        int firstCellNum = row.getFirstCellNum();
        int lastCellNum = row.getLastCellNum();

        // Create a String list to save column data in a row.
        List<String> rowDataList = new ArrayList<String>();

        // Loop in the row cells.
        for (int j = firstCellNum; j < lastCellNum; j++) {
          // Get current cell.
          Cell cell = row.getCell(j);

          // Get cell type.
          CellType cellType = cell.getCellType();

          if (cellType == CellType.NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
              String stringCellValue = cell.toString();
              rowDataList.add(stringCellValue);
            } else {
              double numberValue = cell.getNumericCellValue();
              String stringCellValue = BigDecimal.valueOf(numberValue).toPlainString();
              rowDataList.add(stringCellValue);
            }

          } else if (cellType == CellType.STRING) {
            String cellValue = cell.getStringCellValue();
            rowDataList.add(cellValue);
          } else if (cellType == CellType.BOOLEAN) {
            boolean numberValue = cell.getBooleanCellValue();
            String stringCellValue = String.valueOf(numberValue);

            rowDataList.add(stringCellValue);

          } else if (cellType == CellType.BLANK) {
            rowDataList.add(StringPool.BLANK);
          }
        }
        lists.add(rowDataList);
      }
    }
    return lists;
  }

}
