package br.com.sankhya.frs.service;

import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

import br.com.sankhya.frs.DAO.ConfiguracaoDAO;
import br.com.sankhya.jape.vo.DynamicVO;

public class CremerAPI {
    private static final Logger LOGGER = Logger.getLogger(CremerAPI.class.getName());
    private static final String ACCOUNTING_PATH = "/info/accountingdocument";

    public static Map<String, String> sendFinanceiro(String request, String codEmp, String codParc, String nuFin) throws Exception {
        return executeRequest(request, codEmp, codParc, nuFin, "POST", false);
    }

    public static Map<String, String> refreshFinanceiro(String request, String codEmp, String codParc, String nuFin) throws Exception {
        return executeRequest(request, codEmp, codParc, nuFin, "PUT", true);
    }

    private static Map<String, String> executeRequest(String request, String codEmp, String codParc, String nuFin,
                                                      String method, boolean isUpdate) throws Exception {
        try {
            DynamicVO configVO = ConfiguracaoDAO.get(codEmp);
            String baseUrl = buildUrl(configVO, codParc, nuFin, isUpdate);

            CallService service = new CallService.Builder()
                    .body(request)
                    .method(method)
                    .uri(baseUrl)
                    .empresa(codEmp)
                    .build();

            return service.fire();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao processar requisição para NUFIN: " + nuFin, e);
            throw new Exception("Falha ao processar requisição: " + e.getMessage(), e);
        }
    }

    private static String buildUrl(DynamicVO configVO, String codParc, String nuFin, boolean isUpdate) {
        String baseUrl = configVO.asString("URL");
        StringBuilder urlBuilder = new StringBuilder(baseUrl)
                .append(codParc)
                .append(ACCOUNTING_PATH);

        if (isUpdate) {
            urlBuilder.append("/").append(nuFin);
        }

        return urlBuilder.toString();
    }
}