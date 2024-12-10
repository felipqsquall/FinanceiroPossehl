package br.com.sankhya.frs.service;

import java.util.Map;

import br.com.sankhya.frs.DAO.ConfiguracaoDAO;
import br.com.sankhya.jape.vo.DynamicVO;

public class CremerAPIBkp {

    public static Map<String, String> sendFinanceiro(String request, String codEmp, String codParc, String nuFin) throws Exception{
        DynamicVO configVO = ConfiguracaoDAO.get(codEmp);
        String url = (String) configVO.getProperty("URL");
        CallServiceBkp service = new CallServiceBkp();
        service.setBody(request.toString());
        service.setMethod("POST");
        service.setUri(url + codParc + "/info/accountingdocument");
        service.setEmpresa(codEmp);
        Map<String, String> response = null;
        try {
            response = service.fire();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //response.put("status", nuFin)
            e.getMessage();
            System.out.println("@@ERRO CremerAPI");
        }

        return response;
    }

    public static Map<String, String> refreshFinanceiro(String request, String codEmp, String codParc, String nuFin) throws Exception{
        DynamicVO configVO = ConfiguracaoDAO.get(codEmp);
        String url = (String) configVO.getProperty("URL");
        CallServiceBkp service = new CallServiceBkp();
        service.setBody(request.toString());
        service.setMethod("PUT");
        service.setUri(url + codParc + "/info/accountingdocument/" + nuFin);
        service.setEmpresa(codEmp);
        Map<String, String> response = null;
        try {
            response = service.fire();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //response.put("status", nuFin)
            System.out.println("@@ERRO CremerAPI");
            e.getMessage();
        }

        return response;
    }
}
