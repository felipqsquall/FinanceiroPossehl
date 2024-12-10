package br.com.sankhya.frs.DAO;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class FilaDAO {
    public static void add(BigDecimal nuFin, String codEmp, String codParc ,String request, String method) throws Exception {
        EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
        Date dateobj = new Date();
        DynamicVO filaImp = (DynamicVO)dwfEntityFacade.getDefaultValueObjectInstance("AD_INTFINCRE");
        filaImp.setProperty("DTFILA", new Timestamp(dateobj.getTime()));
        filaImp.setProperty("REQUEST", request);
        filaImp.setProperty("STATUS", new String("A"));
        filaImp.setProperty("NUFIN", nuFin);
        filaImp.setProperty("METHOD", method);


        // Converte codEmp de String para BigDecimal
        BigDecimal codEmpBD = new BigDecimal(codEmp);
        filaImp.setProperty("CODEMP", codEmpBD);

        // Converte codParc de String para BigDecimal
        BigDecimal codParcBD = new BigDecimal(codParc);
        filaImp.setProperty("CODPARC", codParcBD);

        dwfEntityFacade.createEntity("AD_INTFINCRE", (EntityVO)filaImp);
    }
}