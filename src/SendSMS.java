package soldigital_consultoria;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SendSMS {

    private static class Option {
        String sFlag;
        String sOption;

        public Option(String v_sFlag, String v_sOption) {
            this.sFlag      = v_sFlag.trim();
            this.sOption    = v_sOption.trim();
        }
    }

    private static SMSClass SMS                     = new SMSClass();
    private static String sDefaultCharset           = "US-ASCII";
    private static String sDefaultOrigin            = "QOSPerf";
    private static String sFieldSeparator           = ";";
    private static boolean bIsHiddenExecution       = false;
    private static String sParametersFile           = "SendSMS.cfg";
    private static List<String> lstNumOrigins       = new ArrayList<String>();
    private static List<String> lstNumDestinations  = new ArrayList<String>();
    private static List<String> lstMessages         = new ArrayList<String>();
    private static String sDestinationsFile         = "";
    private static String sMessagesFile             = "";
    private static String sDestMessagesFile         = "";
    private static String sOrigDestMessagesFile     = "";

    public static void main(String[] args) {
        List<String> lstArgsList    = new ArrayList<String>();
        List<Option> lstOptsList    = new ArrayList<Option>();
        List<String> lstDoubleList  = new ArrayList<String>();

        if (args.length == 0) {
            ShowHelp();
            throw new IllegalArgumentException("Must specify some arguments");
        }

        for (int nArgId = 0; nArgId < args.length; nArgId++) {
            switch (args[nArgId].charAt(0)) {
                case '-': 
                    if (args[nArgId].length() < 2) {
                        ShowHelp();
                        throw new IllegalArgumentException("Not a valid argument: " + args[nArgId]);
                    }
                    if (args[nArgId].charAt(1) == '-') {
                        if (args[nArgId].length() < 3) {
                            ShowHelp();
                            throw new IllegalArgumentException("Not a valid argument: " + args[nArgId]);
                        }
                        lstDoubleList.add(args[nArgId].substring(2, args[nArgId].length()));
                    } else {
                        if (args.length - 1 == nArgId) {
                            ShowHelp();
                            throw new IllegalArgumentException("Expected arg after: " + args[nArgId]);
                        }
                        lstOptsList.add(new Option(args[nArgId], args[(nArgId + 1)]));
                        nArgId++;
                    }
                    break;
                default: 
                    lstArgsList.add(args[nArgId]);
            }
        }

        for (int i = 0; i < lstOptsList.size(); i++) {
            switch (lstOptsList.get(i).sFlag) {
                case "-a":
                    lstNumOrigins.add(lstOptsList.get(i).sOption);
                    break;
                case "-b":
                    lstNumDestinations.add(lstOptsList.get(i).sOption);
                    break;
                case "-m":
                    lstMessages.add(lstOptsList.get(i).sOption);
                    break;
                case "-p":
                    sParametersFile = lstOptsList.get(i).sOption;
                    break;
                case "-fb":
                    sDestinationsFile = lstOptsList.get(i).sOption;
                    break;
                case "-fm":
                    sMessagesFile = lstOptsList.get(i).sOption;
                    break;
                case "-fbm":
                    sDestMessagesFile = lstOptsList.get(i).sOption;
                    break;
                case "-fabm":
                    sOrigDestMessagesFile = lstOptsList.get(i).sOption;
                    break;
            }
        }

        if (lstDoubleList.contains("hidden")) {
            bIsHiddenExecution = true;
        }

        if ((lstDoubleList.contains("h") | lstDoubleList.contains("help"))) {
            ShowHelp();
            System.exit(0);
        }

        try {
            PreapareEnvironment();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        if (!bIsHiddenExecution) {
            lstNumOrigins.clear();
        }

        if (lstNumOrigins.size() == 0) {
            lstNumOrigins.add(sDefaultOrigin);
        }

        System.out.println(SMS.SendSMS(lstNumOrigins, lstNumDestinations, lstMessages));
        System.exit(0);
    }

    public static void PreapareEnvironment() throws FileNotFoundException {
        loadParameters(sParametersFile);

        if (!sDestinationsFile.equals("")) {
            loadFileToLists(sDestinationsFile, lstNumDestinations);
        }
        if (!sMessagesFile.equals("")) {
            loadFileToLists(sMessagesFile, lstMessages);
        }
        if (!sDestMessagesFile.equals("")) {
            loadFileToLists(sDestMessagesFile, lstNumDestinations, lstMessages);
        }
        if ((!sOrigDestMessagesFile.equals("")) && (bIsHiddenExecution)) {
            loadFileToLists(sOrigDestMessagesFile, lstNumOrigins, lstNumDestinations, lstMessages);
        }
        if (lstNumDestinations.size() == 0) {
            ShowHelp();
            System.err.format("You must have at least one destiny!\n", new Object[0]);
            System.exit(1);
        }
        else if (lstMessages.size() == 0) {
            ShowHelp();
            System.err.format("You must input a message to send!\n", new Object[0]);
            System.exit(1);
        }
    }

    public static void loadParameters(String v_sParametersFile) throws FileNotFoundException {
        IniFile iniParams = new IniFile(v_sParametersFile);
        int nBlockId = 0;
        for (int nBlockCnt = 1; nBlockCnt <= iniParams.countBlocks(); nBlockCnt++) {
            nBlockId++;
            String sBlockStr = "SMSC_" + String.format("%03d", nBlockId);
            try {
                SMSClass.SMSCServer SMSC = new SMSClass.SMSCServer();
                SMSC.sName      = iniParams.Parameter(sBlockStr, "NAME");
                SMSC.sDesc      = iniParams.Parameter(sBlockStr, "DESC");
                SMSC.sIpAddr    = iniParams.Parameter(sBlockStr, "ADDR");
                SMSC.nPort      = Integer.parseInt(iniParams.Parameter(sBlockStr, "PORT"));
                SMSC.sUser      = iniParams.Parameter(sBlockStr, "USER");
                SMSC.sPass      = iniParams.Parameter(sBlockStr, "PASS");
                SMS.lstAllSMSCs.add(SMSC);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                nBlockCnt--;
            }
        }
    }

    public static void loadFileToLists(String v_sFile, List<String> v_lstList_01, List<String> v_lstList_02, List<String> v_lstList_03) throws FileNotFoundException {
        Path phFilePath     = new File(v_sFile).toPath();
        Charset chCharset   = Charset.forName(sDefaultCharset);
        String  sLine       = "";

        try (BufferedReader bufReader = Files.newBufferedReader(phFilePath, chCharset)) {
            while ((sLine = bufReader.readLine()) != null) {
                //Check commented lines
                if ((sLine.trim().length() > 0) && 
                    (!(sLine.trim().substring(0, 1).equals("#")) && 
                     !(sLine.trim().substring(0, 2).equals("//")))) {

                    v_lstList_01.add(sLine.split(";")[0].trim());
                    v_lstList_02.add(sLine.split(";")[1].trim());
                    v_lstList_03.add(sLine.split(";")[2].trim());
                }
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public static void loadFileToLists(String v_sFile, List<String> v_lstList_01, List<String> v_lstList_02) throws FileNotFoundException {
        Path phFilePath     = new File(v_sFile).toPath();
        Charset chCharset   = Charset.forName(sDefaultCharset);
        String  sLine       = "";

        try (BufferedReader bufReader = Files.newBufferedReader(phFilePath, chCharset)) {
            while ((sLine = bufReader.readLine()) != null) {
                //Check commented lines
                if ((sLine.trim().length() > 0) && 
                    (!(sLine.trim().substring(0, 1).equals("#")) && 
                     !(sLine.trim().substring(0, 2).equals("//")))) {

                    v_lstList_01.add(sLine.split(";")[0].trim());
                    v_lstList_02.add(sLine.split(";")[1].trim());
                }
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public static void loadFileToLists(String v_sFile, List<String> v_lstList_01) throws FileNotFoundException {
        Path phFilePath     = new File(v_sFile).toPath();
        Charset chCharset   = Charset.forName(sDefaultCharset);
        String  sLine       = "";

        try (BufferedReader bufReader = Files.newBufferedReader(phFilePath, chCharset)) {
            while ((sLine = bufReader.readLine()) != null) {
                //Check commented lines
                if ((sLine.trim().length() > 0) && 
                    (!(sLine.trim().substring(0, 1).equals("#")) && 
                     !(sLine.trim().substring(0, 2).equals("//")))) {

                    v_lstList_01.add(sLine.trim());
                }
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public static void ShowHelp() {
        String sHelp = "=== SendSMS Help ===\n";
        sHelp = sHelp + "\tSendSMS [--h | --help] | [-p] & <-b -m | -fb -m | -b -fm | -fbm>\n";
        sHelp = sHelp + "\t--h | --help   Mostra essa ajuda\n";
        sHelp = sHelp + "\t-p             \"<Caminho do arquivo de parametros. Atual: '" + sParametersFile + "'>\"" + "\n";
        sHelp = sHelp + "\t-b             <Numero de Destino no formato PPDDNNNNNNNNN (formato internacional sem o '+')>\n";
        sHelp = sHelp + "\t-m             \"<Mensagem>\"\n";
        sHelp = sHelp + "\t-fb            \"<Caminho do arquivo contendo lista com destinos>\"\n";
        sHelp = sHelp + "\t-fm            \"<Caminho do arquivo contendo a mensagem>\"\n";
        sHelp = sHelp + "\t-fbm           \"<Caminho do arquivo contendo a lista de destinos com suas mensagens, separados por '" + sFieldSeparator + "'>\"" + "\n";
        if (bIsHiddenExecution) {
            sHelp = sHelp + "\n";
            sHelp = sHelp + "\t=== Hidden Parameters ===\n";
            sHelp = sHelp + "\tSendSMS [--h | --help] | [-p] & < -a -b -m | -a -fb -m | -a -b -fm | -a -fbm | -fabm >\n";
            sHelp = sHelp + "\t\t-a      <Numero de Origem no formato PPDDNNNNNNNNN (formato internacional sem o '+')>\n";
            sHelp = sHelp + "\t\t-fabm   \"<Caminho do arquivo contendo a lista de origens, destinos com suas mensagens, separados por '" + sFieldSeparator + "'>\"" + "\n";
            sHelp = sHelp + "\n";
        }
        System.out.println(sHelp);
    }
}