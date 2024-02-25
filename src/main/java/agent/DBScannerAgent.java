package agent;

import source.DBScanner;

import java.lang.instrument.Instrumentation;

public class DBScannerAgent {
    public static void premain(String agentArguments, Instrumentation instrumentation) {
        try{
            DBScanner.getInstance().startWork();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
