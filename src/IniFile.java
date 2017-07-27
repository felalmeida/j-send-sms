package soldigital_consultoria;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class IniFile {
    private String sDefaultCharset  = "US-ASCII";
    private String sFileName        = "IniFile.cfg";

    public IniFile() throws FileNotFoundException {
        setFileName(sFileName);
    }

    public IniFile(String v_sFileName) throws FileNotFoundException {
        setFileName(v_sFileName);
    }

    public void setFileName(String v_sFileName) throws FileNotFoundException {
        Path phFilePath     = new File(v_sFileName).toPath();
        Charset chCharset   = Charset.forName(sDefaultCharset);

        try (BufferedReader bufReader = Files.newBufferedReader(phFilePath, chCharset)) {
            sFileName = v_sFileName;
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            throw new FileNotFoundException("File not found: '"+v_sFileName+"'");
        }
    }

    private int Block(String v_BlockName) {
        Path    phFilePath  = new File(sFileName).toPath();
        Charset chCharset   = Charset.forName(sDefaultCharset);
        String  sLine       = "";
        int     nLineCnt    = 0;

        try (BufferedReader bufReader = Files.newBufferedReader(phFilePath, chCharset)) {
            while ((sLine = bufReader.readLine()) != null) {
                //Check commented lines
                if ((  sLine.trim().length() > 0) && 
                    (!(sLine.trim().substring(0, 1).equals("#")) && 
                     !(sLine.trim().substring(0, 2).equals("//")))) {

                    //Check Block Name
                    if ((sLine.trim().equals(v_BlockName)) ||
                        (sLine.trim().substring(1, sLine.trim().length()-1).trim().equals(v_BlockName))) {
                        return nLineCnt;
                    }
                }
                nLineCnt = nLineCnt + 1;
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return -1;
    }

    public int countBlocks() {
        Path    phFilePath  = new File(sFileName).toPath();
        Charset chCharset   = Charset.forName(sDefaultCharset);
        String  sLine       = "";
        int     nBlockCnt   = 0;

        try (BufferedReader bufReader = Files.newBufferedReader(phFilePath, chCharset)) {
            while ((sLine = bufReader.readLine()) != null) {
                //Check commented lines
                if ((  sLine.trim().length() > 0) && 
                    (!(sLine.trim().substring(0, 1).equals("#")) && 
                     !(sLine.trim().substring(0, 2).equals("//")))) {

                    //Check If Is Block
                    if (sLine.trim().substring(0, 1).equals("[")) {
                        nBlockCnt = nBlockCnt + 1;
                    }
                }
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return nBlockCnt;
    }

    public String Parameter(String v_BlockName, String v_Parameter) throws FileNotFoundException {
        Path    phFilePath      = new File(sFileName).toPath();
        Charset chCharset       = Charset.forName(sDefaultCharset);
        String  sLine           = "";
        int     nBlockLine      = Block(v_BlockName);
        int     nLineCnt        = 0;

        if (nBlockLine == -1) {
            System.err.println("Block ("+v_BlockName+") not found!");
            throw new FileNotFoundException("Block ("+v_BlockName+") not found!");
        } else {
            try (BufferedReader bufReader = Files.newBufferedReader(phFilePath, chCharset)) {
                while ((sLine = bufReader.readLine()) != null) {
                    if (nLineCnt > nBlockLine) {

                        //Check commented lines
                        if ((  sLine.trim().length() > 0) &&
                            (!(sLine.trim().substring(0, 1).trim().equals("#")) && 
                             !(sLine.trim().substring(0, 2).trim().equals("//")))) {
                            
                            if (sLine.trim().substring(0, 1).trim().equals("[")) {
                                System.err.println("Parameter ("+v_Parameter+") for block ("+v_BlockName+") not found!");
                                throw new FileNotFoundException("Parameter ("+v_Parameter+") for block ("+v_BlockName+") not found!");
                            }

                            if (sLine.trim().substring(0, sLine.trim().indexOf("=")).trim().equals(v_Parameter)) {
                                return sLine.trim().substring(sLine.trim().indexOf("=") + 1, sLine.trim().length()).trim();
                            }

                        }
                    }
                    nLineCnt = nLineCnt + 1;
                }
                System.err.println("Parameter ("+v_Parameter+") for block ("+v_BlockName+") not found!");
                throw new FileNotFoundException("Parameter ("+v_Parameter+") for block ("+v_BlockName+") not found!");
            } catch (IOException x) {
                System.err.format("IO Exception: %s%n", x);
                throw new FileNotFoundException("IOException");
            }
        }
    }
}