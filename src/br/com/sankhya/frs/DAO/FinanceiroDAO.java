package br.com.sankhya.frs.DAO;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import java.math.BigDecimal;

public class FinanceiroDAO {
    static EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
    public static DynamicVO get(BigDecimal nuFin) throws Exception {
        PersistentLocalEntity financeiro = dwfEntityFacade.findEntityByPrimaryKey("AD_VGFINTSHU", nuFin);
        EntityVO financeiroEVO = financeiro.getValueObject();
        return (DynamicVO)financeiroEVO;
    }
}
