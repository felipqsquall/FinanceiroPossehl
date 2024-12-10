package br.com.sankhya.frs.utils;

import java.util.Base64;

import br.com.sankhya.frs.DAO.ConfiguracaoDAO;
import br.com.sankhya.jape.vo.DynamicVO;

public class CreateBasicAutorization {
    public static String getAutorization(String empresa) throws Exception {
        DynamicVO configVO = ConfiguracaoDAO.get(empresa);
        String user = (String) configVO.getProperty("USUARIO");
        String senha = (String) configVO.getProperty("SENHA");
        String original = user + ":" + senha;
        String encodedString = Base64.getEncoder().encodeToString(original.getBytes());
        return encodedString;
    }
}
