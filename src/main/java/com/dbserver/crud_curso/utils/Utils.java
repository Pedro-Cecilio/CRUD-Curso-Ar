package com.dbserver.crud_curso.utils;

import java.util.regex.Pattern;

public class Utils {
    protected Utils() {
    }

    public static final String REGEX_EMAIL = "^[a-zA-Z0-9._%]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$";
   

    public static boolean validarRegex(String regex, String conteudo) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(conteudo).matches();
    }

}