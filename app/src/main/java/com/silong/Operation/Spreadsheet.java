package com.silong.Operation;

import static com.silong.Operation.Utility.dateToday;
import static com.silong.Operation.Utility.timeNow;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Spreadsheet {

    private Activity activity;
    private ArrayList<Object[]> entries;
    private Workbook workbook = new HSSFWorkbook();

    public Spreadsheet(Activity activity){
        this.activity = activity;
    }

    public void setEntries(ArrayList<Object[]> entries){
        this.entries = entries;
    }

    public Workbook create(){

        try {

            Sheet spreadsheet = workbook.createSheet("Sheet 1");

            Row row;

            Map<String, Object []> formData = new TreeMap<String, Object[]>();

            int ctrRow = 1;
            for (Object[] data : entries){
                formData.put(String.valueOf(ctrRow), data);
                ctrRow++;
            }


            Set<String> keyid = formData.keySet();

            int rowid = 0;

            for (String key : keyid){
                row = spreadsheet.createRow(rowid++);
                Object[] objArr = formData.get(key);
                int cellid = 0;

                for (Object obj : objArr) {
                    Cell cell = row.createCell(cellid++);
                    cell.setCellValue((String)obj);
                }
            }

            return workbook;
        }
        catch (Exception e){
            Utility.log("Spreadsheet.create: " + e.getMessage());
        }

        return null;
    }

    public boolean writeToFile(String filename, boolean internal){

        try {

            String path = "";

            if (internal)
                path = activity.getFilesDir().getPath();
            else
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();

            File dir = new File( path + (internal ? "" :"/Silong/"));

            if (!dir.exists()){
                dir.mkdirs();
            }

            FileOutputStream out = new FileOutputStream(new File(dir, filename));

            workbook.write(out);
            out.close();

            return true;

        } catch (Exception e) {
            Utility.log("Spreadsheet.eTD: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    public boolean sendAsEmail(String filename){

        try {

            String fileDir = filename + "-" + dateToday() + "-" + timeNow().replace("*", "") + ".xls";

            File tempfile = new File(activity.getFilesDir(), fileDir);

            boolean success = writeToFile(fileDir, true);

            if (success){

                EmailNotif emailNotif = new EmailNotif(activity, tempfile, filename);
                emailNotif.sendWithAttachment();

            }

            Toast.makeText(activity, "You will be notified shortly.", Toast.LENGTH_SHORT).show();

            return success;

        }
        catch (Exception e){
            Utility.log("Spreadsheet.sAE: " + e.getMessage());
        }

        return false;
    }
}
