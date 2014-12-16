package soldigital_consultoria;

import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.Address;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.EnquireLink;
import org.smpp.pdu.EnquireLinkResp;
import org.smpp.pdu.SubmitSM;
import org.smpp.pdu.SubmitSMResp;
import org.smpp.pdu.UnbindResp;

public class SMSClass {

    private static final int    SEVERE      = 9;
    private static final int    WARNING     = 8;
    private static final int    INFO        = 7;
    private static final int    CONFIG      = 6;
    private static final int    FINE        = 5;
    private static final int    FINER       = 4;
    private static final int    FINEST      = 3;
    private static final int    DEBUG       = 2;
    private static final String sTimeFormat = "yyyy-MM-dd HH:mm:ss.SSS";

    public static class SMSCServer {
        String sName;
        String sDesc;
        String sIpAddr;
        int nPort;
        String sUser;
        String sPass;
        String sServiceType     = "CMT";
        String sSystemType      = "smpp";
        String sSyncMode        = "async";
        String sBindMode        = "t";
        String sAddressRange    = "11*";
        byte byAddrTon          = 1;
        byte byAddrNpi          = 1;
        byte bySourceTon        = 1;
        byte bySourceNpi        = 1;
        byte byDestinationTon   = 1;
        byte byDestinationNpi   = 1;
        int nReceiveTimeout     = 1;
    }

    private static class SMSMessage {
        String sNumA;
        String sNumB;
        String sStrMsg;

        public SMSMessage(String v_sNumA, String v_sNumB, String v_sStrMsg) {
            this.sNumA      = v_sNumA.trim();
            this.sNumB      = v_sNumB.trim();
            this.sStrMsg    = v_sStrMsg.trim();
        }
    }

    public          List<SMSCServer>    lstAllSMSCs     = new ArrayList<SMSCServer>();
    public          String              sLogFile        = "SendSMS.log";
    public          String              sDefaultNumA    = "QOSPerf";
    public          int                 nLogLevel       = SEVERE;
    private         boolean             bIsBounded      = false;
    private static  Session             tcpSession      = null;
    private         int                 nSuccessCnt     = 0;

    public String GetSMSCsInfo() {
        String sReturnStr = "";
        for (int n = 0; n < this.lstAllSMSCs.size(); n++) {
            sReturnStr = sReturnStr + 
                ((SMSCServer)this.lstAllSMSCs.get(n)).sName     + " ("  +
                ((SMSCServer)this.lstAllSMSCs.get(n)).sDesc     + ") [" +
                ((SMSCServer)this.lstAllSMSCs.get(n)).sUser     + "@"   +
                ((SMSCServer)this.lstAllSMSCs.get(n)).sIpAddr   + ":"   +
                ((SMSCServer)this.lstAllSMSCs.get(n)).nPort     + "]\n";
        }
        WriteToLog("'GetSMSCsInfo'\n\tsReturnStr:\n\t" + sReturnStr.trim());
        return sReturnStr.trim();
    }

    public int SendSMS(String v_sNumB, String v_sStrMsg) {
        List<String> lstNumA = new ArrayList<String>();
        List<String> lstNumB = new ArrayList<String>();
        List<String> lstMsgs = new ArrayList<String>();
        lstNumA.add(this.sDefaultNumA);
        lstNumB.add(v_sNumB);
        lstMsgs.add(v_sStrMsg);
        return SendSMS(lstNumA, lstNumB, lstMsgs);
    }

    public int SendSMS(String v_sNumB, List<String> v_lstStrMsg) {
        List<String> lstNumA = new ArrayList<String>();
        List<String> lstNumB = new ArrayList<String>();
        lstNumA.add(this.sDefaultNumA);
        lstNumB.add(v_sNumB);
        return SendSMS(lstNumA, lstNumB, v_lstStrMsg);
    }

    public int SendSMS(List<String> v_lstNumB, String v_sStrMsg) {
        List<String> lstNumA = new ArrayList<String>();
        List<String> lstMsgs = new ArrayList<String>();
        lstNumA.add(this.sDefaultNumA);
        lstMsgs.add(v_sStrMsg);
        return SendSMS(lstNumA, v_lstNumB, lstMsgs);
    }

    public int SendSMS(List<String> v_lstNumB, List<String> v_lstStrMsg) {
        List<String> lstNumA = new ArrayList<String>();
        lstNumA.add(this.sDefaultNumA);
        return SendSMS(lstNumA, v_lstNumB, v_lstStrMsg);
    }

    public int SendSMS(String v_sNumA, String v_sNumB, String v_sStrMsg) {
        List<String> lstNumA = new ArrayList<String>();
        List<String> lstNumB = new ArrayList<String>();
        List<String> lstMsgs = new ArrayList<String>();
        lstNumA.add(v_sNumA);
        lstNumB.add(v_sNumB);
        lstMsgs.add(v_sStrMsg);
        return SendSMS(lstNumA, lstNumB, lstMsgs);
    }

    public int SendSMS(String v_sNumA, String v_sNumB, List<String> v_lstStrMsg) {
        List<String> lstNumA = new ArrayList<String>();
        List<String> lstNumB = new ArrayList<String>();
        lstNumA.add(v_sNumA);
        lstNumB.add(v_sNumB);
        return SendSMS(lstNumA, lstNumB, v_lstStrMsg);
    }

    public int SendSMS(String v_sNumA, List<String> v_lstNumB, String v_sStrMsg) {
        List<String> lstNumA = new ArrayList<String>();
        List<String> lstMsgs = new ArrayList<String>();
        lstNumA.add(v_sNumA);
        lstMsgs.add(v_sStrMsg);
        return SendSMS(lstNumA, v_lstNumB, lstMsgs);
    }

    public int SendSMS(String v_sNumA, List<String> v_lstNumB, List<String> v_lstStrMsg) {
        List<String> lstNumA = new ArrayList<String>();
        lstNumA.add(v_sNumA);
        return SendSMS(lstNumA, v_lstNumB, v_lstStrMsg);
    }

    public int SendSMS(List<String> v_lstNumA, String v_sNumB, String v_sStrMsg) {
        List<String> lstNumB = new ArrayList<String>();
        List<String> lstMsgs = new ArrayList<String>();
        lstNumB.add(v_sNumB);
        lstMsgs.add(v_sStrMsg);
        return SendSMS(v_lstNumA, lstNumB, lstMsgs);
    }

    public int SendSMS(List<String> v_lstNumA, String v_sNumB, List<String> v_lstStrMsg) {
        List<String> lstNumB = new ArrayList<String>();
        lstNumB.add(v_sNumB);
        return SendSMS(v_lstNumA, lstNumB, v_lstStrMsg);
    }

    public int SendSMS(List<String> v_lstNumA, List<String> v_lstNumB, String v_sStrMsg) {
        List<String> lstMsgs = new ArrayList<String>();
        lstMsgs.add(v_sStrMsg);
        return SendSMS(v_lstNumA, v_lstNumB, lstMsgs);
    }

    public int SendSMS(List<String> v_lstNumA, List<String> v_lstNumB, List<String> v_lstStrMsg) {
        SendSMSStr(v_lstNumA, v_lstNumB, v_lstStrMsg);
        return this.nSuccessCnt;
    }

    public String SendSMSStr(List<String> v_lstNumA, List<String> v_lstNumB, List<String> v_lstStrMsg) {
        String sNumA                    = "";
        String sNumB                    = "";
        String sTmpMsg                  = "";
        String sBindReturn              = "";
        String sUnBindReturn            = "";
        String sSubmitReturn            = "";
        String sPieceSubmitReturn       = "";
        String sReturnMsg               = "";
        boolean bSentPiece              = false;
        List<String> lstMsgArr          = new ArrayList<String>();
        List<SMSMessage> lstSMSMessages = new ArrayList<SMSMessage>();
        int[] nListsSizes = {v_lstNumA.size(), v_lstNumB.size(), v_lstStrMsg.size()};
        Arrays.sort(nListsSizes);

        this.nSuccessCnt = 0;

        for (int nListId = 0; nListId < nListsSizes[(nListsSizes.length - 1)]; nListId++) {
            try {
                sNumA = (String)v_lstNumA.get(nListId);
            } catch (IndexOutOfBoundsException IndxErr) {
                try {
                    sNumA = (String)v_lstNumA.get(v_lstNumA.size() - 1);
                } catch (Exception ExcpErr) {
                    sNumA = this.sDefaultNumA;
                }
            }

            try {
                sNumB = (String)v_lstNumB.get(nListId);
            } catch (IndexOutOfBoundsException IndxErr) {
                try {
                    sNumB = (String)v_lstNumB.get(v_lstNumB.size() - 1);
                } catch (Exception ExcpErr) {
                    ExcpErr.printStackTrace();
                    System.err.format("You must have at least one destination!", new Object[0]);
                    System.exit(1);
                }
            }

            try {
                sTmpMsg = (String)v_lstStrMsg.get(nListId);
            } catch (IndexOutOfBoundsException IndxErr) {
                try {
                    sTmpMsg = (String)v_lstStrMsg.get(v_lstStrMsg.size() - 1);
                } catch (Exception ExcpErr) {
                    ExcpErr.printStackTrace();
                    System.err.format("You must input a message to send!", new Object[0]);
                    System.exit(1);
                }
            }

            SMSMessage smsMsg = new SMSMessage(sNumA, sNumB, sTmpMsg);
            lstSMSMessages.add(smsMsg);
        }

        for (int nSMSCid = 0; nSMSCid < this.lstAllSMSCs.size(); nSMSCid++) {
            if (lstSMSMessages.size() == 0) {
                break;
            }

            SMSCServer SMSC = (SMSCServer)this.lstAllSMSCs.get(nSMSCid);
            sBindReturn     = bind(SMSC);
            sSubmitReturn   = "";

            if (!this.bIsBounded) {
                sReturnMsg = sReturnMsg + "Can not connect to SMSC " + SMSC.sName + "\n" + sBindReturn + "\n";
            } else {
                for (int nMsgid = 0; nMsgid < lstSMSMessages.size(); nMsgid++) {
                    sNumA   = ((SMSMessage)lstSMSMessages.get(nMsgid)).sNumA;
                    sNumB   = ((SMSMessage)lstSMSMessages.get(nMsgid)).sNumB;
                    sTmpMsg = ((SMSMessage)lstSMSMessages.get(nMsgid)).sStrMsg;
                    lstMsgArr.clear();

                    while (sTmpMsg.length() > 150) {
                        lstMsgArr.add(sTmpMsg.substring(0, 150));
                        sTmpMsg = sTmpMsg.substring(150);
                    }

                    lstMsgArr.add(sTmpMsg);

                    for (int nTmpMsgid = 0; nTmpMsgid < lstMsgArr.size(); nTmpMsgid++) {
                        sPieceSubmitReturn = "";
                        sPieceSubmitReturn = submit(sNumA, sNumB, (String)lstMsgArr.get(nTmpMsgid), SMSC);
                        if (sPieceSubmitReturn.length() > 0) {
                            bSentPiece = true;
                        } else {
                            bSentPiece = false;
                        }
                    }

                    if (bSentPiece) {
                        lstSMSMessages.remove(nMsgid);
                        nMsgid--;
                        sSubmitReturn = sSubmitReturn + sPieceSubmitReturn;
                        this.nSuccessCnt += 1;
                    }
                }
                sUnBindReturn = unbind();
                sReturnMsg = sReturnMsg + sBindReturn + "\n" + sSubmitReturn + "\n" + sUnBindReturn + "\n";
            }
        }

        return sReturnMsg;
    }

    private String bind(SMSCServer v_SMSC) {
        String sReturnMsg = "";

        try {
            if (this.bIsBounded) {
                return "Already bound, unbind first.";
            }

            BindRequest request         = new BindTransmitter();
            BindResponse response       = null;
            TCPIPConnection connection  = new TCPIPConnection(v_SMSC.sIpAddr, v_SMSC.nPort);

            connection.setReceiveTimeout(v_SMSC.nReceiveTimeout * 1);
            tcpSession = new Session(connection);

            request.setSystemId(v_SMSC.sUser);
            request.setPassword(v_SMSC.sPass);
            request.setSystemType(v_SMSC.sSystemType);
            request.setInterfaceVersion((byte)52);
            request.setAddressRange(v_SMSC.sAddressRange);

            sReturnMsg = "Bind request " + request.debugString();
            response = tcpSession.bind(request);
            sReturnMsg = sReturnMsg + "\nBind response " + response.debugString();

            if (response.getCommandStatus() == 0) {
                this.bIsBounded = true;
            } else {
                this.bIsBounded = false;
                sReturnMsg = sReturnMsg + "\nBind failed, code " + response.getCommandStatus();
            }
        } catch (Exception e) {
            sReturnMsg = "Bind operation failed. " + e;
        }
        return sReturnMsg;
    }

    private String unbind() {
        String sReturnMsg = "";

        try {
            if (!this.bIsBounded) {
                return "Not bound, cannot unbind.";
            }
            sReturnMsg = "Going to unbind.";
            if (tcpSession.getReceiver().isReceiver()) {
                sReturnMsg = sReturnMsg + "\nIt can take a while to stop the receiver.";
            }
            UnbindResp response = tcpSession.unbind();
            sReturnMsg = sReturnMsg + "\nUnbind response " + response.debugString();
            this.bIsBounded = false;
        } catch (Exception e) {
            sReturnMsg = sReturnMsg + "\nUnbind operation failed. " + e;
        }
        return sReturnMsg;
    }

    private String submit(String v_sNumA, String v_sNumB, String v_sStrMsg, SMSCServer v_SMSC) {
        String sReturnMsg = "";

        byte bySenderTon                = v_SMSC.bySourceTon;
        byte bySenderNpi                = v_SMSC.bySourceNpi;
        byte byEsmClass                 = 0;
        byte byProtocolId               = 0;
        byte byPriorityFlag             = 0;
        byte byRegisteredDelivery       = 0;
        byte byReplaceIfPresentFlag     = 0;
        byte byDataCoding               = 0;
        byte bySmDefaultMsgId           = 0;
        String sScheduleDeliveryTime    = "";
        String sValidityPeriod          = "";
        String sMessageId               = "";

        try {
            SubmitSM request = new SubmitSM();
            request.setServiceType(v_SMSC.sServiceType);

            if (v_sNumA != null) {
                if (v_sNumA.startsWith("+")) {
                    v_sNumA = v_sNumA.substring(1);
                    bySenderTon = 1;
                    bySenderNpi = 1;
                }
                if (!v_sNumA.matches("\\d+")) {
                    bySenderTon = 5;
                    bySenderNpi = 0;
                }
                if (bySenderTon == 5) {
                    request.setSourceAddr(new Address(bySenderTon, bySenderNpi, v_sNumA, 11));
                } else {
                    request.setSourceAddr(new Address(bySenderTon, bySenderNpi, v_sNumA));
                }
            } else {
                request.setSourceAddr(new Address());
            }

            if (v_sNumB.startsWith("+")) {
                v_sNumB = v_sNumB.substring(1);
            }

            WriteToLog("Sending Message:\n\tNumA: '" + v_sNumA + "'\n\tNumB: '" + v_sNumB + "'\n\tsMsg: '" + v_sStrMsg + "'");
            request.setDestAddr(new Address((byte)1, (byte)1, v_sNumB));
            request.setReplaceIfPresentFlag(byReplaceIfPresentFlag);
            request.setShortMessage(v_sStrMsg, "ASCII");
            request.setScheduleDeliveryTime(sScheduleDeliveryTime);
            request.setValidityPeriod(sValidityPeriod);
            request.setEsmClass(byEsmClass);
            request.setProtocolId(byProtocolId);
            request.setPriorityFlag(byPriorityFlag);
            request.setRegisteredDelivery(byRegisteredDelivery);
            request.setDataCoding(byDataCoding);
            request.setSmDefaultMsgId(bySmDefaultMsgId);
            request.assignSequenceNumber(true);
            sReturnMsg = "Submit request " + request.debugString();
            SubmitSMResp response = tcpSession.submit(request);
            sReturnMsg = sReturnMsg + "\nSubmit response " + response.debugString();
            sMessageId = response.getMessageId();
            sReturnMsg = sReturnMsg + "\n" + sMessageId;
            sReturnMsg = sReturnMsg + "\n" + enquireLink();
        } catch (Exception e) {
            sReturnMsg = "Submit operation failed. " + e;
        }

        return sReturnMsg;
    }

    private String enquireLink() {
        String sReturnMsg = "";

        try {
            EnquireLink request = new EnquireLink();
            sReturnMsg = "Enquire Link request " + request.debugString();
            EnquireLinkResp response = tcpSession.enquireLink(request);
            sReturnMsg = sReturnMsg + "Enquire Link response " + response.debugString();
        } catch (Exception e) {
            sReturnMsg = "Enquire Link operation failed. " + e;
        }

        return sReturnMsg;
    }

    private void WriteToLog(String v_LogMessage) {
        WriteToLog(v_LogMessage, SEVERE);
    }

    private void WriteToLog(String v_LogMessage, Integer v_nLogLevel) {
        String dtString;
        String sLogLevelStr;

        switch(v_nLogLevel) {
            case SEVERE:    sLogLevelStr = "SEVERE";    break;
            case WARNING:   sLogLevelStr = "WARNING";   break;
            case INFO:      sLogLevelStr = "INFO";      break;
            case CONFIG:    sLogLevelStr = "CONFIG";    break;
            case FINE:      sLogLevelStr = "FINE";      break;
            case FINER:     sLogLevelStr = "FINER";     break;
            case FINEST:    sLogLevelStr = "FINEST";    break;
            case DEBUG:     sLogLevelStr = "DEBUG";     break;
            default:        sLogLevelStr = "N/A";       break;
        }

        if (v_nLogLevel >= this.nLogLevel) {
            try {
                File fLogFile = new File(this.sLogFile);

                if (!fLogFile.exists()) {
                    fLogFile.createNewFile();
                }

                FileWriter      fwLogFile   = new FileWriter(fLogFile.getAbsoluteFile(), true);
                BufferedWriter  bLogFile    = new BufferedWriter(fwLogFile);

                for (String sLine : v_LogMessage.split("\n")) {
                    dtString = new SimpleDateFormat(sTimeFormat).format(new Date());
                    bLogFile.write(dtString + ";" + sLogLevelStr + ";" + sLine + "\n");
                }

                bLogFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}