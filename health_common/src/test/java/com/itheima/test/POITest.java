package com.itheima.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;

public class POITest {
    @Test
    public void test1() throws Exception {
        //加载指定文件，创建一个Excel对象
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("G:\\hello.xlsx")));
        //读取Excel文件中第一个Sheet标签页
        XSSFSheet sheet = workbook.getSheetAt(0);
        //遍历标签页，获取每一行数据
        for (Row row:sheet){
            //遍历 行对象 获取单元格对象
            for (Cell cell:row){
                //获取单元格中的值
                String value = cell.getStringCellValue();
                System.out.println(value);

            }
        }
        workbook.close();
    }

    @Test
    public void test2() throws Exception {
        //加载指定文件，创建一个Excel对象
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("G:\\hello.xlsx")));
        //读取Excel文件中第一个Sheet标签页
        XSSFSheet sheet = workbook.getSheetAt(0);
        //获取当前工作表中的最后一个行号，需要注意行号从0开始
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 0; i <=lastRowNum; i++) {
            //根据行号获取每一行
            XSSFRow row = sheet.getRow(i);
            //获取当前行的最后一个单元格索引
            short lastCellNum = row.getLastCellNum();
            for (int j = 0; j < lastCellNum; j++) {
                //根据索引获取内容
                XSSFCell cell = row.getCell(j);
                System.out.println("cell = " + cell.getStringCellValue());
            }
        }

        workbook.close();
    }
    //Excel写数据
    @Test
    public void test3() throws IOException {
        //内存里创建Excel表
        XSSFWorkbook workbook = new XSSFWorkbook();
        //指定Excel名字
        XSSFSheet sheet = workbook.createSheet("转自博客");
        //创建行
        XSSFRow row = sheet.createRow(0);
        //设置行的每个单元格数据
        row.createCell(0).setCellValue("编号");
        row.createCell(1).setCellValue("名字");
        row.createCell(2).setCellValue("年龄");
        XSSFRow row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("01");
        row1.createCell(1).setCellValue("小米");
        row1.createCell(2).setCellValue("18");

        XSSFRow row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("02");
        row2.createCell(1).setCellValue("小为米");
        row2.createCell(2).setCellValue("23");

        //通过输出流写到硬盘中去
        FileOutputStream out = new FileOutputStream("G:\\itcast.xlsx");
        workbook.write(out);
        //执行
        out.flush();
        out.close();
        workbook.close();
    }
}
