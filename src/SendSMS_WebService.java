package soldigital_consultoria;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class SendSMS_WebService {
    private SMSClass SMS            = new SMSClass();
    private String sClassPath       = getClass().getProtectionDomain().getCodeSource().getLocation().toString().split(":")[1];
    private String sWebInfPath      = this.sClassPath.substring(0, this.sClassPath.indexOf("WEB-INF"));
    private String sParametersFile  = this.sWebInfPath + "SendSMS.cfg";

    public SendSMS_WebService() throws FileNotFoundException {
        IniFile iniParams   = new IniFile(this.sParametersFile);
        int nBlockId        = 0;

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
                this.SMS.lstAllSMSCs.add(SMSC);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                nBlockCnt--;
            }
        }
    }

    @WebMethod(exclude=true)
    public String GetSMSCsInfo() {
        return this.SMS.GetSMSCsInfo();
    }

    @WebMethod(exclude=false)
    public int Send_SMS(String v_sNumB, String v_sStrMsg) {
        if (v_sNumB.indexOf("|") > 0) {
            String sNumA = v_sNumB.substring(0, v_sNumB.indexOf("|"));
            String sNumB = v_sNumB.substring(v_sNumB.indexOf("|") + 1);
            return this.SMS.SendSMS(sNumA, sNumB, v_sStrMsg);
        }
        return this.SMS.SendSMS(v_sNumB, v_sStrMsg);
    }

    @WebMethod(exclude=true)
    public String SendSMSStr_OneOne(String v_sNumB, String v_sStrMsg) {
        List<String> lstNumA = new ArrayList<String>();
        List<String> lstNumB = new ArrayList<String>();
        List<String> lstMsgs = new ArrayList<String>();
        lstNumA.add(this.SMS.sDefaultNumA);
        lstNumB.add(v_sNumB);
        lstMsgs.add(v_sStrMsg);
        return this.SMS.SendSMSStr(lstNumA, lstNumB, lstMsgs);
    }

    @WebMethod(exclude=true)
    public int SendSMS_OneOne(String v_sNumB, String v_sStrMsg) {
        return this.SMS.SendSMS(v_sNumB, v_sStrMsg);
    }

    @WebMethod(exclude=true)
    public int SendSMS_OneMany(String v_sNumB, List<String> v_lstStrMsg) {
        return this.SMS.SendSMS(v_sNumB, v_lstStrMsg);
    }

    @WebMethod(exclude=true)
    public int SendSMS_ManyOne(List<String> v_lstNumB, String v_sStrMsg) {
        return this.SMS.SendSMS(v_lstNumB, v_sStrMsg);
    }

    @WebMethod(exclude=true)
    public int SendSMS_ManyMany(List<String> v_lstNumB, List<String> v_lstStrMsg) {
        return this.SMS.SendSMS(v_lstNumB, v_lstStrMsg);
    }

    @WebMethod(exclude=true)
    public int SendSMS_OneOneOne(String v_sNumA, String v_sNumB, String v_sStrMsg) {
        return this.SMS.SendSMS(v_sNumA, v_sNumB, v_sStrMsg);
    }

    @WebMethod(exclude=true)
    public int SendSMS_OneOneMany(String v_sNumA, String v_sNumB, List<String> v_lstStrMsg) {
        return this.SMS.SendSMS(v_sNumA, v_sNumB, v_lstStrMsg);
    }

    @WebMethod(exclude=true)
    public int SendSMS_OneManyOne(String v_sNumA, List<String> v_lstNumB, String v_sStrMsg) {
        return this.SMS.SendSMS(v_sNumA, v_lstNumB, v_sStrMsg);
    }

    @WebMethod(exclude=true)
    public int SendSMS_OneManyMany(String v_sNumA, List<String> v_lstNumB, List<String> v_lstStrMsg) {
        return this.SMS.SendSMS(v_sNumA, v_lstNumB, v_lstStrMsg);
    }

    @WebMethod(exclude=true)
    public int SendSMS_ManyOneOne(List<String> v_lstNumA, String v_sNumB, String v_sStrMsg) {
        return this.SMS.SendSMS(v_lstNumA, v_sNumB, v_sStrMsg);
    }

    @WebMethod(exclude=true)
    public int SendSMS_ManyOneMany(List<String> v_lstNumA, String v_sNumB, List<String> v_lstStrMsg) {
        return this.SMS.SendSMS(v_lstNumA, v_sNumB, v_lstStrMsg);
    }

    @WebMethod(exclude=true)
    public int SendSMS_ManyManyOne(List<String> v_lstNumA, List<String> v_lstNumB, String v_sStrMsg) {
        return this.SMS.SendSMS(v_lstNumA, v_lstNumB, v_sStrMsg);
    }

    @WebMethod(exclude=true)
    public int SendSMS_ManyManyMany(List<String> v_lstNumA, List<String> v_lstNumB, List<String> v_lstStrMsg) {
        return this.SMS.SendSMS(v_lstNumA, v_lstNumB, v_lstStrMsg);
    }
}